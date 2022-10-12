package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.game.dao.*;
import com.cphillipsdorsett.teamsweeper.game.dto.*;
import com.cphillipsdorsett.teamsweeper.game.websocket.UncoverCellHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class GameService {
    private final GameDao gameDao;
    private final SessionGameDao sessionGameDao;
    private final LiveGameDao liveGameDao;
    private final ObjectMapper om = new ObjectMapper();

    public GameService(GameDao gameDao, SessionGameDao sessionGameDao, LiveGameDao liveGameDao) {
        this.gameDao = gameDao;
        this.sessionGameDao = sessionGameDao;
        this.liveGameDao = liveGameDao;
    }

    public GameStartResponseDto newGame(String sessionId, GameDifficulty difficulty) throws JsonProcessingException {
        // Any current live games need to be saved before they're overwritten
        LiveGame currentLiveGame = liveGameDao.get(sessionId).orElse(null);
        if (currentLiveGame != null && currentLiveGame.isInProgress()) {
            saveLiveGame(sessionId, currentLiveGame);
        }

        GameBoard gameBoard = new GameBoard(difficulty);

        // Add records to the db, need to serialize the board for the db
        // insertion.
        Game gameToInsert = new Game(difficulty, gameBoard.getSerializedBoard());
        Game game = gameDao.create(gameToInsert);

        // Also need a session_game record so we can match the game based on the
        // session.
        SessionGame sessionGame = new SessionGame(sessionId, game.getId());
        sessionGameDao.create(sessionGame);

        // Create a live game
        LiveGame liveGame = new LiveGame(game.getId(), sessionId, gameBoard);
        liveGameDao.updateSessionGame(sessionId, liveGame);

        return new GameStartResponseDto(game, gameBoard.getBoardConfig());
    }

    public SessionGameStatsResponseDto findSessionGameStats(String sessionId) {
        List<SessionGameStats> sessionGameStatsList = sessionGameDao.findSessionGameStats(sessionId);
        return SessionGameStatsResponseDto.fromSessionGameStatsList(sessionGameStatsList);
    }

    /**
     * Delete related session game records, and any games that no longer have
     * any associated http session.
     *
     * @return tuple of session_games and games deleted
     */
    public int[] deleteExpiredSessionGames(String sessionId) {
        liveGameDao.remove(sessionId);
        int sessionGamesDeleted = sessionGameDao.deleteBySessionId(sessionId);
        int gamesDeleted = gameDao.deleteGamesWithoutSession();
        return new int[]{sessionGamesDeleted, gamesDeleted};
    }

    /**
     * Called when a user clicks/touches a cell on the board
     */
    public void uncoverCell(
        String sessionId,
        UncoverCellRequestDto payload,
        UncoverCellHandler eventHandler
    ) throws IOException {
        LiveGame game = liveGameDao.get(sessionId).orElseThrow(() -> {
            // TODO create a custom game service exception
            throw new NullPointerException("Couldn't find an active game for session id: " + sessionId);
        });

        // Early exit if the game is already over since all the cells have or
        // will be uncovered.
        if (!game.isInProgress()) {
            return;
        }

        // If this is the first click then set a started timestamp
        if (game.getStartedAt() == null) {
            game.setStartedAt(Instant.now());
            eventHandler.onStartGame(true);
        }

        Cell[][] board = game.getBoard();
        Cell uncoveredCell = board[payload.getRowIdx()][payload.getColIdx()];

        // Check if a mine was clicked
        if (uncoveredCell.isMine()) {
            game.setStatus(GameStatus.LOST);
            game.setEndedAt(Instant.now());
            endGame(sessionId, game);
            uncoverCellCascadeAll(uncoveredCell, game, eventHandler);
            sendEndGameMessage(sessionId, game, eventHandler);
            return;
        }

        // Mine was not clicked and the game is still in progress
        uncoverCellCascade(uncoveredCell, game, eventHandler, sessionId, 1);

        // Once the cell cascade has finished, we'll check to see if the game
        // has been won.
        if (game.getUncoveredCells() >= game.getUncoveredCellsNeededToWin()) {
            game.setStatus(GameStatus.WON);
            game.setEndedAt(Instant.now());
            endGame(sessionId, game);
            uncoverCellCascadeAll(uncoveredCell, game, eventHandler);
            sendEndGameMessage(sessionId, game, eventHandler);
        }
    }

    /**
     * Uncover a cell and recursively uncover surrounding cells if it isn't
     * near a mine.
     */
    private void uncoverCellCascade(
        Cell cell,
        LiveGame game,
        UncoverCellHandler eventHandler,
        String sessionId,
        Integer points
    ) throws IOException {
        // Early exit if the cell is already uncovered
        if (!cell.isCovered()) {
            return;
        }

        // TODO we'll need to update our live game cache with the updated board
        //  state.
        cell.setCovered(false);
        eventHandler.onUncover(new UncoverCellResponseDto(cell));
        if (points != null) {
            game.adjustSessionPoints(sessionId, points);
            eventHandler.onAdjustPoints(new PointsResponseDto(points));
        }
        game.incrementUncoveredCells();

        if (cell.isNearMine()) {
            return;
        }

        GameBoard.nearbyCellsForEach(cell, game.getBoard(), (nearbyCell) -> {
            if (!nearbyCell.isCovered()) {
                return;
            }

            try {
                uncoverCellCascade(
                    nearbyCell,
                    game,
                    eventHandler,
                    sessionId,
                    null
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Uncover all the cells
     */
    private void uncoverCellCascadeAll(
        Cell cell,
        LiveGame game,
        UncoverCellHandler handler
    ) throws IOException {
        // Early exit if we've already checked this cell
        if (cell.isChecked()) {
            return;
        }

        // TODO we'll need to update our live game cache with the updated board
        //  state.
        cell.setChecked(true);
        cell.setCovered(false);
        handler.onUncover(new UncoverCellResponseDto(cell));

        GameBoard.nearbyCellsForEach(cell, game.getBoard(), (nearbyCell) -> {
            try {
                uncoverCellCascadeAll(
                    nearbyCell,
                    game,
                    handler
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Update the db game and session game
     */
    private void endGame(String sessionId, LiveGame liveGame) throws JsonProcessingException {
        // Update the game
        Game dbGame = gameDao.findCurrent(sessionId, liveGame.getId());
        dbGame.setStatus(liveGame.getStatus());
        dbGame.setStartedAt(liveGame.getStartedAt());
        dbGame.setEndedAt(liveGame.getEndedAt());
        dbGame.setBoard(om.writeValueAsString(liveGame.getBoard()));
        gameDao.update(dbGame);

        // Update the session game
        updateSessionGame(sessionId, dbGame.getId(), liveGame);
    }

    /**
     * Save an in-progress live game. This enables data to be persisted before
     * a new game overrides the session live game.
     */
    private void saveLiveGame(String sessionId, LiveGame liveGame) throws JsonProcessingException {
        // Update the game
        Game dbGame = gameDao.findCurrent(sessionId, liveGame.getId());
        dbGame.setStartedAt(liveGame.getStartedAt());
        dbGame.setBoard(om.writeValueAsString(liveGame.getBoard()));
        gameDao.update(dbGame);

        // Update the session game
        updateSessionGame(sessionId, dbGame.getId(), liveGame);
    }

    private void updateSessionGame(String sessionId, int gameId, LiveGame liveGame) {
        SessionGame sessionGame = sessionGameDao.findCurrent(sessionId, gameId);
        sessionGame.setUncovers(liveGame.getUncoversBySession(sessionId));
        sessionGameDao.update(sessionGame);
    }

    /**
     * Sends message to the frontend when a game has been won or lost
     */
    private void sendEndGameMessage(String sessionId, LiveGame game, UncoverCellHandler callback) throws IOException {
        long duration = Duration.between(game.getStartedAt(), game.getEndedAt()).toMillis();
        int uncovers = game.getUncoversBySession(sessionId);
        float score = duration == 0 ? 0 : ((float) uncovers / 10) + ((float) 1000 / duration);
        GameEndResponseDto gameEndDto = new GameEndResponseDto(game.getStatus(), duration, uncovers, score);
        callback.onEndGame(gameEndDto);
    }
}
