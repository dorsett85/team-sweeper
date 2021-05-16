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
        UncoveredCellReadyForQueue readyForQueue
    ) throws IOException {
        Game game = gameDao.findCurrent(sessionId, payload.gameId);
        Cell[][] board = getDeSerializedBoard(game.board);
        Cell cell = board[payload.rowIdx][payload.colIdx];

        // Now that we have the initial covered cell, we'll uncover it and
        // repeat the process for all surrounding cells
        uncoverCellCascade(cell, board, readyForQueue, cell.value.equals("x"));
    }

    /**
     * Uncover a cell and recursively uncover surrounding cells if it isn't
     * near a mine.
     * <p>
     * When the cell is covered we'll also call a handler notifying the the web
     * socket that we can add the cell to the message queue.
     */
    private void uncoverCellCascade(
        Cell cell,
        Cell[][] board,
        UncoveredCellReadyForQueue readyForQueue,
        boolean uncoverAll
    ) throws IOException {
        if (cell.covered) {
            cell.covered = false;
            readyForQueue.call(cell);

            if (uncoverAll) {
                cell.checked = true;
            }

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
                            readyForQueue,
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

    public interface UncoveredCellReadyForQueue {
        void call(Cell cell) throws IOException;
    }
}
