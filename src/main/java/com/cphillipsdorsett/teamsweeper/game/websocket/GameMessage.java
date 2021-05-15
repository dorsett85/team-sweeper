package com.cphillipsdorsett.teamsweeper.game.websocket;

public abstract class GameMessage<T> {
    public String type;
    public T payload;

    public String getType() {
        return type;
    }

    public T getPayload() {
        return payload;
    }
}
