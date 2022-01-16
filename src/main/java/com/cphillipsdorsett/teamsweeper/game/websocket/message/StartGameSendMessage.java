package com.cphillipsdorsett.teamsweeper.game.websocket.message;

public class StartGameSendMessage extends GameSendMessage {

    public StartGameSendMessage(boolean started) {
        super(GameSendMessageType.START_GAME, started);
    }
}
