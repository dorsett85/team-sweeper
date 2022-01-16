package com.cphillipsdorsett.teamsweeper.game.websocket.message;

import com.cphillipsdorsett.teamsweeper.game.dto.GameEndResponseDto;

public class EndGameSendMessage extends GameSendMessage {

    public EndGameSendMessage(GameEndResponseDto gameEndResponseDto) {
        super(GameSendMessageType.END_GAME, gameEndResponseDto);
    }
}
