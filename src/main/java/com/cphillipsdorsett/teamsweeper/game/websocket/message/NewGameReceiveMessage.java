package com.cphillipsdorsett.teamsweeper.game.websocket.message;

import com.cphillipsdorsett.teamsweeper.game.dto.NewGameRequestDto;

public class NewGameReceiveMessage extends GameReceiveMessage<NewGameRequestDto> {
    public static GameReceiveMessageType TYPE = GameReceiveMessageType.NEW_GAME;

    private NewGameReceiveMessage() {
    }

}