package com.cphillipsdorsett.teamsweeper.game.websocket;

import com.cphillipsdorsett.teamsweeper.game.Cell;
import com.cphillipsdorsett.teamsweeper.game.dao.Game;
import com.cphillipsdorsett.teamsweeper.game.GameBuilder;
import com.cphillipsdorsett.teamsweeper.game.dao.GameDao;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class GameSocketDispatch {
    private final ObjectMapper om = new ObjectMapper();
    private final GameDao gameDao;
    public final Map<String, MessageDispatch> dispatchMap;

    public GameSocketDispatch(GameDao gameDao) {
        this.gameDao = gameDao;
        dispatchMap = new HashMap<>() {{
            put(UncoverCellMessage.TYPE, (node, session, __) -> uncoverCell(node, session));
        }};
    }

    public void uncoverCell(JsonNode node, WebSocketSession session) throws IOException {
        UncoverCellMessage msg = om.treeToValue(node, UncoverCellMessage.class);
        int gameId = msg.payload.gameId;
        int rowIdx = msg.payload.rowIdx;
        int colIdx = msg.payload.colIdx;

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
                        uncoverCell(board[rIdx][cIdx], board, session);
                    }
                }
            }
        }
    }

    /**
     * Lambda expression value in our dispatch map.
     */
    interface MessageDispatch {
        void run(JsonNode node, WebSocketSession session, ArrayList<WebSocketSession> sessions) throws IOException;
    }
}
