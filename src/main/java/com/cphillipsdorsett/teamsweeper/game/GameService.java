package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.game.dao.Game;
import com.cphillipsdorsett.teamsweeper.game.dao.GameDao;
import com.cphillipsdorsett.teamsweeper.game.dao.SessionGame;
import com.cphillipsdorsett.teamsweeper.game.dao.SessionGameDao;
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
        Cell[][] board = getDeSerializedBoard(game.board);
        Cell cell = board[payload.rowIdx][payload.colIdx];

        boolean mineUncovered = cell.value.equals("x");

        // Now that we have the initial covered cell, we'll uncover it and
        // repeat the process for all surrounding cells
        uncoverCellCascade(cell, board, game, callback, mineUncovered);

        // It's possible that a mine was uncovered, but the game was already
        // won, so we'll make sure they can't lose after winning.
        if (mineUncovered && game.status.equals("in-progress")) {
            game.status = "lost";
        } else {
            // TODO check if the game has been won
        }

        // Let the frontend know that the game is over
        if (!game.status.equals("in-progress")) {
            callback.endGame(game.status);
        }
    }

    /**
     * Uncover a cell and recursively uncover surrounding cells if it isn't
     * near a mine.
     *
     * When the cell is covered we'll also call a handler notifying the the web
     * socket that we can add the cell to the message queue.
     */
    private void uncoverCellCascade(
        Cell cell,
        Cell[][] board,
        Game game,
        UncoveredCellMessageCallback callback,
        boolean uncoverAll
    ) throws IOException {
        if (cell.covered) {
            cell.covered = false;

            if (uncoverAll) {
                cell.checked = true;
            }

            callback.reveal(cell);

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
        void endGame(String status) throws IOException;
    }
}
