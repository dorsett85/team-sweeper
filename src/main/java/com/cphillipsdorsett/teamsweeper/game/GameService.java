package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.game.dao.*;
import com.cphillipsdorsett.teamsweeper.game.dto.GameEndDto;
import com.cphillipsdorsett.teamsweeper.game.dto.GameStartDto;
import com.cphillipsdorsett.teamsweeper.game.dto.SessionGameStatsDto;
import com.cphillipsdorsett.teamsweeper.game.websocket.UncoverCellMessage.UncoverCellPayload;
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
    private final ObjectMapper om = new ObjectMapper();

    public GameService(GameDao gameDao, SessionGameDao sessionGameDao) {
        this.gameDao = gameDao;
        this.sessionGameDao = sessionGameDao;
    }

    public GameStartDto newGame(String sessionId, GameDifficulty difficulty) throws JsonProcessingException {

        GameBuilder gameBuilder = new GameBuilder(difficulty);

        // Add records to the db, need to serialize the board for the db
        // insertion.
        Game gameToInsert = new Game(difficulty, gameBuilder.getSerializedBoard());
        Game game = gameDao.create(gameToInsert);

        // Also need a session_game record so we can match the game based on the
        // session for the websocket connect.
        SessionGame sessionGame = new SessionGame(sessionId, game.id);
        sessionGameDao.create(sessionGame);

        return new GameStartDto(game, GameBuilder.getBoardConfig(game.difficulty));
    }

    /**
     * Called when a user clicks/touches a cell on the board
     */
    public void uncoverCell(
        String sessionId,
        UncoverCellPayload payload,
        UncoveredCellMessageCallback callback
    ) throws IOException {
        Game game = gameDao.findCurrent(sessionId, payload.gameId);

        // If this is the first click then set a started timestamp
        if (game.startedAt == null) {
            game.startedAt = Instant.now();
        }

        // Early exit if the game is already over since all of the cells have or
        // will be uncovered.
        if (game.status != GameStatus.IN_PROGRESS) {
            return;
        }

        Cell[][] board = getDeSerializedBoard(game.board);
        Cell uncoveredCell = board[payload.rowIdx][payload.colIdx];

        // Check if a mine was clicked
        // TODO It's possible that a mine was uncovered while the backend was
        //  still processing if the game had been won. We may want to add status
        //  queue to make sure the clicks and processing get handled in order.
        if (uncoveredCell.value.equals("x")) {
            game.status = GameStatus.LOST;
            uncoverCellCascade(uncoveredCell, board, game, callback, true);
            sendEndGame(game, callback);
            return;
        }

        // Mine was not clicked and the game is still in progress
        uncoverCellCascade(uncoveredCell, board, game, callback, false);

        // Once the cell cascade has finished, we'll check to see if the game
        // has been won.
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

                // Whoop, game is won, set the status and uncover the rest
                // of the cells.
                if (totalCellsToUncover == uncoveredCellCount) {
                    game.status = GameStatus.WON;
                    uncoverCellCascade(cell, board, game, callback, true);
                    sendEndGame(game, callback);
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

    public SessionGameStatsDto findSessionGameStats(String sessionId) {
        List<SessionGameStats> sessionGameStatsList = sessionGameDao.findSessionGameStats(sessionId);
        return SessionGameStatsDto.toSessionGameStatsDto(sessionGameStatsList);
    }

    private void sendEndGame(Game game, UncoveredCellMessageCallback callback) throws IOException {
        long duration = Duration.between(game.startedAt, Instant.now()).toMillis();
        GameEndDto gameEndDto = new GameEndDto(game.status, duration);
        callback.endGame(gameEndDto);
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
         * Notifies the frontend that the game is over (either won or lost)
         */
        void endGame(GameEndDto gameEndDto) throws IOException;
    }
}
