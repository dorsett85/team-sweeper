package com.cphillipsdorsett.teamsweeper.game.websocket;

import com.cphillipsdorsett.teamsweeper.game.Cell;
import com.cphillipsdorsett.teamsweeper.game.GameService;
import com.cphillipsdorsett.teamsweeper.game.dao.GameStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final GameService gameService;
    public final Map<GameSocketMessageType, MessageDispatch> dispatchMap;

    public GameSocketDispatch(GameService gameService) {
        this.gameService = gameService;

        // This is key. Our socket handler will call the values in this map
        // based on the type property in the json message.
        dispatchMap = new HashMap<>() {{
            put(UncoverCellMessage.TYPE, (node, session, sessions) -> uncoverCell(node, session));
        }};
    }

    public void uncoverCell(JsonNode node, WebSocketSession session) throws IOException {
        UncoverCellMessage msg = om.treeToValue(node, UncoverCellMessage.class);
        String sessionId = (String) session.getAttributes().get("sessionId");

        gameService.uncoverCell(sessionId, msg.payload, new GameService.UncoveredCellMessageCallback() {
            @Override
            public void reveal(Cell cell) throws IOException {
                TextMessage cellMessage = transformToPublish(UncoverCellMessage.TYPE, cell);
                session.sendMessage(cellMessage);
            }

            @Override
            public void endGame(GameStatus status) throws IOException {
                TextMessage endGameMessage = transformToPublish(GameSocketMessageType.END_GAME, status);
                session.sendMessage(endGameMessage);
            }
        });
    }

    /**
     * Create a message object that's ready to be sent to the client with "type"
     * and "payload" properties
     */
    private TextMessage transformToPublish(GameSocketMessageType type, Object payload) throws JsonProcessingException {
        var message = new HashMap<>() {{
            put("type", type);
            put("payload", payload);
        }};
        return new TextMessage(om.writeValueAsString(message));
    }

    /**
     * Lambda expression as our dispatch map value
     */
    interface MessageDispatch {
        void call(JsonNode node, WebSocketSession session, ArrayList<WebSocketSession> sessions) throws IOException;
    }
}
