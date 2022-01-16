package com.cphillipsdorsett.teamsweeper.game.websocket.message;

/**
 * Generic class for sending websocket messages
 */
public class GameSendMessage extends BaseMessage<GameSendMessageType, Object> {

    public GameSendMessage(GameSendMessageType type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

}
