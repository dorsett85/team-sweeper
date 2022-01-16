package com.cphillipsdorsett.teamsweeper.game.websocket.message;

import com.cphillipsdorsett.teamsweeper.game.dto.UncoverCellRequestDto;

public class UncoverCellReceiveMessage extends GameReceiveMessage<UncoverCellRequestDto> {
    public static GameReceiveMessageType TYPE = GameReceiveMessageType.UNCOVER_CELL;

    private UncoverCellReceiveMessage() {
    }

}