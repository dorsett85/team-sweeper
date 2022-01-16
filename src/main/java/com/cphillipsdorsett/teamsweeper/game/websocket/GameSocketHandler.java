package com.cphillipsdorsett.teamsweeper.game.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class GameSocketHandler extends TextWebSocketHandler {
    private final ArrayList<WebSocketSession> sessions = new ArrayList<>();
    private final ObjectMapper om = new ObjectMapper();
    private final GameSocketDispatch gameSocketDispatch;

    public GameSocketHandler(GameSocketDispatch gameSocketDispatch) {
        this.gameSocketDispatch = gameSocketDispatch;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        JsonNode msgNode = om.readTree(message.getPayload());
        GameMessageReceiveType messageType = GameMessageReceiveType.valueOf(msgNode.get("type").asText());

        // We'll parse/dispatch the payload property based on the message type
        gameSocketDispatch.dispatchMap
            .get(messageType)
            .call(msgNode, session, sessions);
    }
}