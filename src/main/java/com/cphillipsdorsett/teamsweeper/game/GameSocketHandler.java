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
        String value = board[rowIdx][colIdx].value;

        String jsonMessage = String.format(
            "{ \"%s\": %s, \"%s\": %s, \"%s\": \"%s\" }",
            ROW_IDX,
            rowIdx,
            COL_IDX,
            colIdx,
            "value",
            value
        );
        TextMessage uncoveredCellMessage = new TextMessage(jsonMessage);

        session.sendMessage(uncoveredCellMessage);
    }
}