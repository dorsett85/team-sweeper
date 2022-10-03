package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.game.dao.Cell;
import com.cphillipsdorsett.teamsweeper.game.dto.GameEndResponseDto;
import com.cphillipsdorsett.teamsweeper.game.websocket.SendableMessage;
import com.cphillipsdorsett.teamsweeper.game.websocket.message.EndGameSendMessage;
import com.cphillipsdorsett.teamsweeper.game.websocket.message.StartGameSendMessage;
import com.cphillipsdorsett.teamsweeper.game.websocket.message.UncoverCellSendMessage;

import java.io.IOException;

public class UncoverCellSinglePlayerHandler implements UncoverCellHandler {
    private final SendableMessage sm;

    public UncoverCellSinglePlayerHandler(SendableMessage sm) {
        this.sm = sm;
    }

    public void onStartGame(boolean started) throws IOException {
        sm.send(new StartGameSendMessage(started));
    }

    public void onUncover(Cell cell) throws IOException {
        sm.send(new UncoverCellSendMessage(cell));
    }

    public void onEndGame(GameEndResponseDto gameEndDto) throws IOException {
        sm.send(new EndGameSendMessage(gameEndDto));
    }
}
