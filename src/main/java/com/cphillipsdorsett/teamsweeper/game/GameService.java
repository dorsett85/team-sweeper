package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.game.dao.*;
import com.cphillipsdorsett.teamsweeper.game.dto.GameEndResponseDto;
import com.cphillipsdorsett.teamsweeper.game.dto.GameStartResponseDto;
import com.cphillipsdorsett.teamsweeper.game.dto.SessionGameStatsResponseDto;
import com.cphillipsdorsett.teamsweeper.game.dto.UncoverCellRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

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
        GameBoard gameBoard = new GameBoard(difficulty);

        // Add records to the db, need to serialize the board for the db
        // insertion.
        Game gameToInsert = new Game(difficulty, gameBoard.getSerializedBoard());
        Game game = gameDao.create(gameToInsert);

        // Also need a session_game record so we can match the game based on the
        // session for the websocket connect.
        SessionGame sessionGame = new SessionGame(sessionId, game.getId());
        sessionGameDao.create(sessionGame);

        // Create a live game (this will replace any existing session game)
        LiveGame liveGame = new LiveGame(game.getId(), gameBoard);
        liveGameDao.add(sessionId, liveGame);

        return new GameStartResponseDto(game, gameBoard.getBoardConfig());
    }

    public SessionGameStatsResponseDto findSessionGameStats(String sessionId) {
        List<SessionGameStats> sessionGameStatsList = sessionGameDao.findSessionGameStats(sessionId);
        return SessionGameStatsResponseDto.fromSessionGameStatsList(sessionGameStatsList);
    }

    /**
     * Delete related game table records when a session expires
     *
     * @return number of games that were deleted
     */
    public int deleteExpiredSessionGames(String sessionId) {
        int deletedCount = gameDao.deleteBySingleSessionReference(sessionId);

        // Clean up the session_game records as well
        sessionGameDao.deleteBySessionId(sessionId);

        return deletedCount;
    }

    /**
     * Called when a user clicks/touches a cell on the board
     */
    public void uncoverCell(
        String httpSessionId,
        UncoverCellRequestDto payload,
        UncoverCellHandler handler
    ) throws IOException {
        LiveGame game = liveGameDao.get(httpSessionId);

        // Early exit if the game is already over since all the cells have or
        // will be uncovered.
        if (game == null || game.getStatus() != GameStatus.IN_PROGRESS) {
            return;
        }

        // If this is the first click then set a started timestamp
        if (game.getStartedAt() == null) {
            game.setStartedAt(Instant.now());
            handler.onStartGame(true);
        }

        Cell[][] board = game.getBoard();
        Cell uncoveredCell = board[payload.rowIdx][payload.colIdx];

        // Check if a mine was clicked
        if (uncoveredCell.getValue().equals("x")) {
            Game dbGame = endGame(httpSessionId, GameStatus.LOST, game);
            uncoverCellCascade(uncoveredCell, game, handler, true);
            sendEndGame(dbGame, handler);
            return;
        }

        // Mine was not clicked and the game is still in progress
        uncoverCellCascade(uncoveredCell, game, handler, false);

        // Once the cell cascade has finished, we'll check to see if the game
        // has been won.
        if (game.getUncoveredCells() >= game.getUncoveredCellsNeededToWin()) {
            Game dbGame = endGame(httpSessionId, GameStatus.WON, game);
            uncoverCellCascade(uncoveredCell, game, handler, true);
            sendEndGame(dbGame, handler);
        }
    }

    /**
     * Uncover a cell and recursively uncover surrounding cells if it isn't
     * near a mine.
     * <p>
     * When the cell is covered we'll also call a handler notifying the web
     * socket that we can add the cell to the message queue.
     */
    private void uncoverCellCascade(
        Cell cell,
        LiveGame game,
        UncoverCellHandler handler,
        boolean uncoverAll
    ) throws IOException {
        // Early exit if the cell is already uncovered or if we're not
        // uncovering everything and the cell has already been checked.
        if ((!cell.isCovered() && !uncoverAll) || (uncoverAll && cell.isChecked())) {
            return;
        }

        cell.setCovered(false);

        if (uncoverAll) {
            cell.setChecked(true);
        }

        // TODO we'll need to update our live game cache with the updated board
        //  state.
        handler.onUncover(cell);
        game.incrementUncoveredCells();

        Cell[][] board = game.getBoard();

        // If the cell isn't near any mines we'll uncover the surrounding
        // cells as well.
        if (cell.getValue().equals("0") || uncoverAll) {
            for (int[] cellTuple : GameBoard.getSurroundingCells()) {
                int rIdx = cell.getRowIdx() + cellTuple[0];
                int cIdx = cell.getColIdx() + cellTuple[1];

                boolean rowInBounds = rIdx >= 0 && rIdx < board.length;
                boolean colInBounds = cIdx >= 0 && cIdx < board[0].length;
                if (rowInBounds && colInBounds) {
                    uncoverCellCascade(
                        board[rIdx][cIdx],
                        game,
                        handler,
                        uncoverAll
                    );
                }
            }
        }
    }

    /**
     * Update the db game and remove the live game
     */
    private Game endGame(String httpSessionId, GameStatus newStatus, LiveGame game) throws JsonProcessingException {
        // Update end of game fields in db
        Game dbGame = gameDao.findCurrent(httpSessionId, game.getId());
        dbGame.setStatus(newStatus);
        dbGame.setStartedAt(game.getStartedAt());
        dbGame.setEndedAt(Instant.now());
        dbGame.setBoard(om.writeValueAsString(game.getBoard()));
        gameDao.update(dbGame);

        // Game is persisted, safe to remove the live game
        liveGameDao.remove(httpSessionId);

        return dbGame;
    }

    private void sendEndGame(Game game, UncoverCellHandler callback) throws IOException {
        long duration = Duration.between(game.getStartedAt(), game.getEndedAt()).toMillis();
        GameEndResponseDto gameEndDto = new GameEndResponseDto(game.getStatus(), duration);
        callback.onEndGame(gameEndDto);
    }
}
