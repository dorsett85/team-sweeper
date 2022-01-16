package com.cphillipsdorsett.teamsweeper.game.websocket;

/**
 * Generic class for sending websocket messages
 *
 * @param <P> The messages' payload type
 */
public class GameMessageSend<P> extends GameMessage<GameMessageSendType, P> {

    public GameMessageSend(GameMessageSendType type, P payload) {
        this.setType(type);
        this.setPayload(payload);
    }

}
