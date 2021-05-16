package com.cphillipsdorsett.teamsweeper.game.websocket;

import com.cphillipsdorsett.teamsweeper.game.websocket.GameSocketDispatch.MessageDispatch;
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
    private ArrayList<WebSocketSession> sessions = new ArrayList<>();
    private final ObjectMapper om = new ObjectMapper();
    private final GameSocketDispatch gameSocketDispatch;

    public GameSocketHandler(GameSocketDispatch gameSocketDispatch) {
     this.gameSocketDispatch = gameSocketDispatch;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        JsonNode msgNode = om.readTree(message.getPayload());
        String messageType = msgNode.get("type").asText();

        // We'll parse/dispatch the payload property based on the message type
        dispatch(messageType).call(msgNode, session, sessions);
    }

    /**
     * Convenience method to return the appropriate dispatch handler based on
     * the type.
     */
    private MessageDispatch dispatch(String type) {
        return gameSocketDispatch.dispatchMap.get(type);
    }
}