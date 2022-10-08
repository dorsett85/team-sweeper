package com.cphillipsdorsett.teamsweeper.game.websocket;

import org.springframework.web.socket.CloseStatus;

public class GameSocketException extends Exception {
    private final CloseStatus status;

    public GameSocketException(CloseStatus status, String message) {
        super(message);
        this.status = status;
    }

    public CloseStatus getStatus() {
        return status;
    }
}
