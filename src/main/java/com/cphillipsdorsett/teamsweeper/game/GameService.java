package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.game.dao.*;
import com.cphillipsdorsett.teamsweeper.game.dto.GameEndResponseDto;
import com.cphillipsdorsett.teamsweeper.game.dto.GameStartResponseDto;
import com.cphillipsdorsett.teamsweeper.game.dto.SessionGameStatsResponseDto;
import com.cphillipsdorsett.teamsweeper.game.dto.UncoverCellRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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

        // Create a live game (this will replace any existing session game)
        LiveGame liveGame = new LiveGame(gameBoard);
        liveGameDao.add(sessionId, liveGame);

        // Add records to the db, need to serialize the board for the db
        // insertion.
        Game gameToInsert = new Game(difficulty, gameBoard.getSerializedBoard());
        Game game = gameDao.create(gameToInsert);

        // Also need a session_game record so we can match the game based on the
        // session for the websocket connect.
        SessionGame sessionGame = new SessionGame(sessionId, game.getId());
        sessionGameDao.create(sessionGame);

        return new GameStartResponseDto(game, gameBoard.getBoardConfig());
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
        String sessionId,
        UncoverCellRequestDto payload,
        UncoverCellHandler handler
    ) throws IOException {
        Game game = gameDao.findCurrent(sessionId, payload.gameId);

        // If this is the first click then set a started timestamp
        if (game.getStartedAt() == null) {
            game.setStartedAt(Instant.now());
            handler.onStartGame(true);
        }

        // Early exit if the game is already over since all the cells have or
        // will be uncovered.
        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            return;
        }

        Cell[][] board = getDeSerializedBoard(game.getBoard());
        Cell uncoveredCell = board[payload.rowIdx][payload.colIdx];

        // Check if a mine was clicked
        // TODO It's possible that a mine was uncovered while the backend was
        //  still processing if the game had been won. We may want to add status
        //  queue to make sure the clicks and processing get handled in order.
        if (uncoveredCell.value.equals("x")) {
            game.setStatus(GameStatus.LOST);
            game.setEndedAt(Instant.now());
            uncoverCellCascade(uncoveredCell, board, game, handler, true);
            sendEndGame(game, handler);
            return;
        }

        // Mine was not clicked and the game is still in progress
        uncoverCellCascade(uncoveredCell, board, game, handler, false);

        // Once the cell cascade has finished, we'll check to see if the game
        // has been won.
        BoardConfig boardConfig = GameBoard.getBoardConfig(game.getDifficulty());

        // To see if the game has been won, we'll compare the total non-mine
        // cells on the board to the number of cells that have been
        // uncovered. The game is won if they're equal.
        int totalCellsToUncover = (boardConfig.getRows() * boardConfig.getRows()) - boardConfig.getMines();
        int uncoveredCellCount = 0;
        for (Cell[] row : board) {
            for (Cell cell : row) {
                if (!cell.covered) {
                    uncoveredCellCount++;
                }

                // Whoop, game is won, uncover the rest of the cells and let
                // the frontend know.
                if (totalCellsToUncover == uncoveredCellCount) {
                    game.setStatus(GameStatus.WON);
                    game.setEndedAt(Instant.now());
                    gameDao.update(game);
                    uncoverCellCascade(cell, board, game, handler, true);
                    sendEndGame(game, handler);
                }
            }
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
        Cell[][] board,
        Game game,
        UncoverCellHandler handler,
        boolean uncoverAll
    ) throws IOException {
        // Early exit if the cell is already uncovered or if we're not
        // uncovering everything and the cell has already been checked.
        if ((!cell.covered && !uncoverAll) || (uncoverAll && cell.checked)) {
            return;
        }

        cell.covered = false;

        if (uncoverAll) {
            cell.checked = true;
        }

        // Store the updated game in the database and tell the frontend that
        // the cell has been revealed.
        game.setBoard(om.writeValueAsString(board));
        new Thread(() -> gameDao.update(game)).start();
        handler.onUncover(cell);

        // TODO - if multiplayer
        // Read updated game in case of changes from the other player

        // If the cell isn't near any mines we'll uncover the surrounding
        // cells as well.
        if (cell.value.equals("0") || uncoverAll) {
            for (int[] cellTuple : GameBoard.getSurroundingCells()) {
                int rIdx = cell.rowIdx + cellTuple[0];
                int cIdx = cell.colIdx + cellTuple[1];

                boolean rowInBounds = rIdx >= 0 && rIdx < board.length;
                boolean colInBounds = cIdx >= 0 && cIdx < board[0].length;
                if (rowInBounds && colInBounds) {
                    uncoverCellCascade(
                        board[rIdx][cIdx],
                        board,
                        game,
                        handler,
                        uncoverAll
                    );
                }
            }
        }
    }

    public SessionGameStatsResponseDto findSessionGameStats(String sessionId) {
        List<SessionGameStats> sessionGameStatsList = sessionGameDao.findSessionGameStats(sessionId);
        return SessionGameStatsResponseDto.fromSessionGameStatsList(sessionGameStatsList);
    }

    private void sendEndGame(Game game, UncoverCellHandler callback) throws IOException {
        long duration = Duration.between(game.getStartedAt(), game.getEndedAt()).toMillis();
        GameEndResponseDto gameEndDto = new GameEndResponseDto(game.getStatus(), duration);
        callback.onEndGame(gameEndDto);
    }

    private Cell[][] getDeSerializedBoard(String board) throws JsonProcessingException {
        return om.readValue(board, new TypeReference<>() {
        });
    }
}
