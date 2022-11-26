package com.cphillipsdorsett.teamsweeper.game.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

public class GameSocketInterceptor implements HandshakeInterceptor {
    /**
     * We'll use this override to check the session and possibly to authenticate
     * the user.
     */
    @Override
    public boolean beforeHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Map<String, Object> attributes
    ) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            // Always create a new http session for the app to use. This is NOT
            // the same as the websocket session id!
            HttpSession session = servletRequest.getServletRequest().getSession(true);
            attributes.put("httpSessionId", session.getId());
            return true;
        }
        // This should never happen coming from the client side
        return false;
    }

    @Override
    public void afterHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Exception exception
    ) {

    }
}
