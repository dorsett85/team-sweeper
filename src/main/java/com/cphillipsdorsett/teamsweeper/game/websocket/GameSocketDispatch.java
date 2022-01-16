package com.cphillipsdorsett.teamsweeper.game.websocket;

import com.cphillipsdorsett.teamsweeper.game.Cell;
import com.cphillipsdorsett.teamsweeper.game.GameService;
import com.cphillipsdorsett.teamsweeper.game.dto.GameEndDto;
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
    public final Map<GameMessageReceiveType, MessageDispatch> dispatchMap;

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

        gameService.uncoverCell(sessionId, msg.getPayload(), new GameService.UncoveredCellMessageCallback() {
            @Override
            public void startGame(boolean started) throws IOException {
                sendMessage(GameMessageSendType.START_GAME, started, session);
            }

            @Override
            public void reveal(Cell cell) throws IOException {
                sendMessage(GameMessageSendType.UNCOVER_CELL, cell, session);
            }

            @Override
            public void endGame(GameEndDto gameEndDto) throws IOException {
                sendMessage(GameMessageSendType.END_GAME, gameEndDto, session);
            }
        });
    }

    /**
     * Send a socket message to the client with "type" and "payload" properties
     */
    private <P> void sendMessage(GameMessageSendType type, P payload, WebSocketSession session) throws IOException {
        GameMessage<GameMessageSendType, P> gameMessage = new GameMessageSend<>(type, payload);
        TextMessage textMessage = new TextMessage(om.writeValueAsString(gameMessage));
        session.sendMessage(textMessage);
    }

    /**
     * Lambda expression as our dispatch map value
     */
    interface MessageDispatch {
        void call(JsonNode node, WebSocketSession session, ArrayList<WebSocketSession> sessions) throws IOException;
    }
}
