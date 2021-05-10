package com.cphillipsdorsett.teamsweeper.game;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Component
public class GameSocketHandler extends TextWebSocketHandler {

    private ArrayList<WebSocketSession> sessions = new ArrayList<>();
    private final GameDao gameDao;

    public GameSocketHandler(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        ObjectMapper om = new ObjectMapper();
        Map<String, Integer> clickedCellInfo = om.readValue(message.getPayload(), new TypeReference<>() {
        });

        String GAME_ID = "gameId";
        String ROW_IDX = "rowIdx";
        String COL_IDX = "colIdx";

        int gameId = clickedCellInfo.get(GAME_ID);
        int rowIdx = clickedCellInfo.get(ROW_IDX);
        int colIdx = clickedCellInfo.get(COL_IDX);
        String sessionId = (String) session.getAttributes().get("sessionId");

        Game game = gameDao.findCurrent(sessionId, gameId);
        Cell[][] board = om.readValue(game.board, new TypeReference<>() {
        });
        Cell cell = board[rowIdx][colIdx];

        uncoverCell(cell, board, session);
    }

    private void uncoverCell(Cell cell, Cell[][] board, WebSocketSession session) throws IOException {
        ObjectMapper om = new ObjectMapper();
        if (cell.covered) {
            cell.covered = false;
            String cellMessage = om.writeValueAsString(cell);
            session.sendMessage(new TextMessage(cellMessage));

            // If the cell isn't near any mines we'll uncover the surrounding
            // cells as well.
            if (cell.value.equals("0")) {
                for (int[] cellTuple : GameBuilder.surroundingCells) {
                    int rIdx = cell.rowIdx + cellTuple[0];
                    int cIdx = cell.colIdx + cellTuple[1];

                    boolean rowInBounds = rIdx >= 0 && rIdx < board.length;
                    boolean colInBounds = cIdx >= 0 && cIdx < board[0].length;
                    if (rowInBounds && colInBounds) {
                        uncoverCell(board[rIdx][cIdx], board, session);                    }
                }
            }
        }
    }
}