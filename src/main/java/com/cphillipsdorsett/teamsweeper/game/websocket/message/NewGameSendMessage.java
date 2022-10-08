package com.cphillipsdorsett.teamsweeper.game.websocket.message;

import com.cphillipsdorsett.teamsweeper.game.dto.GameStartResponseDto;

public class NewGameSendMessage extends GameSendMessage {

    public NewGameSendMessage(GameStartResponseDto gameStartResponseDto) {
        super(GameSendMessageType.NEW_GAME, gameStartResponseDto);
    }
}
