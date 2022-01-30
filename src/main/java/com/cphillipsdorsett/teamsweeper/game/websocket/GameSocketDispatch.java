package com.cphillipsdorsett.teamsweeper.game.websocket;

import com.cphillipsdorsett.teamsweeper.game.GameService;
import com.cphillipsdorsett.teamsweeper.game.websocket.message.GameReceiveMessageType;
import com.cphillipsdorsett.teamsweeper.game.websocket.message.GameSendMessage;
import com.cphillipsdorsett.teamsweeper.game.websocket.message.UncoverCellReceiveMessage;
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
    public final Map<GameReceiveMessageType, MessageDispatch> dispatchMap;

    public GameSocketDispatch(GameService gameService) {
        this.gameService = gameService;

        // This is key. Our socket handler will call the values in this map
        // based on the type property in the json message.
        dispatchMap = new HashMap<>() {{
            put(UncoverCellReceiveMessage.TYPE, (node, session, sessions) -> uncoverCell(node, session));
        }};
    }

    public void uncoverCell(JsonNode node, WebSocketSession session) throws IOException {
        UncoverCellReceiveMessage msg = om.treeToValue(node, UncoverCellReceiveMessage.class);
        String sessionId = (String) session.getAttributes().get("sessionId");

        SendableMessage sm = (gameSendMessage) -> sendMessage(gameSendMessage, session);
        gameService.uncoverCell(sessionId, msg.getPayload(), new UncoverCellHandler(sm));
    }

    /**
     * Send a socket message to the client with "type" and "payload" properties
     */
    private void sendMessage(GameSendMessage gameSendMessage, WebSocketSession session) throws IOException {
        TextMessage textMessage = new TextMessage(om.writeValueAsString(gameSendMessage));
        session.sendMessage(textMessage);
    }

    /**
     * Lambda expression as our dispatch map value
     */
    interface MessageDispatch {
        void dispatch(JsonNode node, WebSocketSession session, ArrayList<WebSocketSession> sessions) throws IOException;
    }
}
