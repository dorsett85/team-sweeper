package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.game.dao.*;
import com.cphillipsdorsett.teamsweeper.game.websocket.UncoverCellMessage.UncoverCellPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GameService {
    private final GameDao gameDao;
    private final SessionGameDao sessionGameDao;
    private final ObjectMapper om = new ObjectMapper();

    public GameService(GameDao gameDao, SessionGameDao sessionGameDao) {
        this.gameDao = gameDao;
        this.sessionGameDao = sessionGameDao;
    }

    public GameDto newGame(String sessionId, String difficulty) throws JsonProcessingException {

        GameBuilder gameBuilder = new GameBuilder(difficulty);

        // Add records to the db, need to serialize the board for the db
        // insertion.
        Game gameToInsert = new Game(difficulty, gameBuilder.getSerializedBoard());
        Game game = gameDao.create(gameToInsert);

        // Also need a session_game record so we can match the game based on the
        // session for the websocket connect.
        SessionGame sessionGame = new SessionGame(sessionId, game.id);
        sessionGameDao.create(sessionGame);

        return new GameDto(game, GameBuilder.getBoardConfig(game.difficulty));
    }

    public void uncoverCell(
        String sessionId,
        UncoverCellPayload payload,
        UncoveredCellMessageCallback callback
    ) throws IOException {
        Game game = gameDao.findCurrent(sessionId, payload.gameId);

        // Early exit if the game is already over
        if (game.status != GameStatus.IN_PROGRESS) {
            return;
        }

        Cell[][] board = getDeSerializedBoard(game.board);
        Cell uncoveredCell = board[payload.rowIdx][payload.colIdx];

        boolean mineUncovered = uncoveredCell.value.equals("x");

        // It's possible that a mine was uncovered, but the game was already
        // won (lag in backend sending status to frontend), so we'll make sure
        // they can't lose after winning.
        if (mineUncovered && game.status == GameStatus.IN_PROGRESS) {
            game.status = GameStatus.LOST;
        }

        // Now that we have the initial covered cell, we'll uncover it and
        // repeat the process for all surrounding cells.
        uncoverCellCascade(uncoveredCell, board, game, callback, mineUncovered);

        // Once the cell cascade has finished, we'll check to see if the game
        // has been won.
        if (game.status == GameStatus.IN_PROGRESS) {
            BoardConfig boardConfig = GameBuilder.getBoardConfig(game.difficulty);

            // To see if the game has been won, we'll compare the total non-mine
            // cells on the board to the number of cells that have been
            // uncovered. The game is won if they're equal.
            int totalCellsToUncover = (boardConfig.cols * boardConfig.rows) - boardConfig.mines;
            int uncoveredCellCount = 0;
            for (Cell[] row : board) {
                for (Cell cell : row) {
                    if (!cell.covered) {
                        uncoveredCellCount++;
                    }
                    if (totalCellsToUncover == uncoveredCellCount) {
                        game.status = GameStatus.WON;
                    }
                }
            }
        }

        // Let the frontend know that the game is over
        if (game.status != GameStatus.IN_PROGRESS) {
            callback.endGame(game.status);
        }

        // Update the database with the latest status
        gameDao.update(game);
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
        UncoveredCellMessageCallback callback,
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
        game.board = om.writeValueAsString(board);
        new Thread(() -> gameDao.update(game)).start();
        callback.reveal(cell);

        // TODO - if multiplayer
        // Read updated game in case of changes from the other player

        // If the cell isn't near any mines we'll uncover the surrounding
        // cells as well.
        if (cell.value.equals("0") || uncoverAll) {
            for (int[] cellTuple : GameBuilder.surroundingCells) {
                int rIdx = cell.rowIdx + cellTuple[0];
                int cIdx = cell.colIdx + cellTuple[1];

                boolean rowInBounds = rIdx >= 0 && rIdx < board.length;
                boolean colInBounds = cIdx >= 0 && cIdx < board[0].length;
                if (rowInBounds && colInBounds) {
                    uncoverCellCascade(
                        board[rIdx][cIdx],
                        board,
                        game,
                        callback,
                        uncoverAll
                    );
                }
            }
        }
    }

    private Cell[][] getDeSerializedBoard(String board) throws JsonProcessingException {
        return om.readValue(board, new TypeReference<>() {
        });
    }

    public interface UncoveredCellMessageCallback {
        /**
         * Fired when the cell is uncovered and notifies the frontend that it
         * has been revealed.
         */
        void reveal(Cell cell) throws IOException;

        /**
         * Notifies the frontend that the game is no longer in progress
         */
        void endGame(GameStatus status) throws IOException;
    }
}
