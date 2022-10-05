package com.cphillipsdorsett.teamsweeper.game.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains all open websocket connections. Enables other components in the DI
 * container to access them as well (e.g., when a http session closes, this
 * allows us to also close the associated websocket session).
 * <p>
 * Most importantly for multiplayer, this will enable sessions to communicate
 * with one another.
 */
@Component
public class GameSocketSessionStore {
    /**
     * Map whose key is the http session id and the value is the associated
     * websocket session.
     */
    private final Map<String, WebSocketSession> socketsByHttpSessionId = new HashMap<>();

    public WebSocketSession getByHttpId(String httpSessionId) {
        return socketsByHttpSessionId.get(httpSessionId);
    }

    public WebSocketSession putByHttpSessionId(String httpSessionId, WebSocketSession webSocketSession) {
        return socketsByHttpSessionId.put(httpSessionId, webSocketSession);
    }

    public WebSocketSession removeByHttpSessionId(String httpSessionId) {
        return socketsByHttpSessionId.remove(httpSessionId);
    }
}
