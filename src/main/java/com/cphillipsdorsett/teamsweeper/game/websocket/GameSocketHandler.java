package com.cphillipsdorsett.teamsweeper.game.websocket;

import com.cphillipsdorsett.teamsweeper.game.websocket.message.GameReceiveMessageType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class GameSocketHandler extends TextWebSocketHandler {
    private final Logger logger = LogManager.getLogger(GameSocketHandler.class);
    private final ObjectMapper om = new ObjectMapper();
    private final GameSocketDispatch gameSocketDispatch;
    private final GameSocketSessionManager gameSocketSessionManager;

    public GameSocketHandler(GameSocketDispatch gameSocketDispatch, GameSocketSessionManager gameSocketSessionManager) {
        this.gameSocketDispatch = gameSocketDispatch;
        this.gameSocketSessionManager = gameSocketSessionManager;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        JsonNode msgNode = om.readTree(message.getPayload());
        GameReceiveMessageType messageType = GameReceiveMessageType.valueOf(msgNode.get("type").asText());

        // We'll dispatch the payload property based on the message type
        try {
            gameSocketDispatch.dispatchMap
                .get(messageType)
                .dispatch(msgNode, session);
        } catch (Exception e) {
            String errMsg = "An error occurred handling your message";
            logger.error(errMsg, e);
            String reason = errMsg + ": " + e.getMessage();
            session.close(CloseStatus.SERVER_ERROR.withReason(reason));
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String key = GameSocketUtil.getHttpSessionId(session);
        gameSocketSessionManager.putByHttpSessionId(key, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String key = GameSocketUtil.getHttpSessionId(session);
        gameSocketSessionManager.removeByHttpSessionId(key);
    }
}