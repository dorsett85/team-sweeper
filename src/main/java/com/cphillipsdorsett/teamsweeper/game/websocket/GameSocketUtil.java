package com.cphillipsdorsett.teamsweeper.game.websocket;

import org.springframework.web.socket.WebSocketSession;

/**
 * Helper functions related to websocket connections
 */
public class GameSocketUtil {
    static public String getHttpSessionId(WebSocketSession session) {
        return (String) session.getAttributes().get("httpSessionId");
    }
}
