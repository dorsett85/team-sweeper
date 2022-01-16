package com.cphillipsdorsett.teamsweeper.game.websocket;

/**
 * Base class for sending and receiving websocket messages
 *
 * @param <T> The messages' type
 * @param <P> The messages' payload type
 */
public abstract class GameMessage<T, P> {
    private T type;
    private P payload;

    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    public P getPayload() {
        return payload;
    }

    public void setPayload(P payload) {
        this.payload = payload;
    }
}
