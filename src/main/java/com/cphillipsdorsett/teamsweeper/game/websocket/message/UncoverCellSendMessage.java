package com.cphillipsdorsett.teamsweeper.game.websocket.message;

import com.cphillipsdorsett.teamsweeper.game.Cell;

public class UncoverCellSendMessage extends GameSendMessage {

    public UncoverCellSendMessage(Cell cell) {
        super(GameSendMessageType.UNCOVER_CELL, cell);
    }
}
