package com.cphillipsdorsett.teamsweeper.game.websocket.message;

/**
 * Base class for sending/receiving websocket messages
 *
 * @param <T> The messages' type
 * @param <P> The messages' payload type
 */
public abstract class BaseMessage<T, P> {
    protected T type;
    protected P payload;

    public T getType() {
        return type;
    }

    public P getPayload() {
        return payload;
    }
}
