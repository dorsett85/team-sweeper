package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.game.dto.GameEndResponseDto;
import com.cphillipsdorsett.teamsweeper.game.dto.IncrementUncoversResponseDto;
import com.cphillipsdorsett.teamsweeper.game.dto.UncoverCellResponseDto;
import com.cphillipsdorsett.teamsweeper.game.websocket.SendableMessage;
import com.cphillipsdorsett.teamsweeper.game.websocket.UncoverCellHandler;
import com.cphillipsdorsett.teamsweeper.game.websocket.message.GameSendMessage;

import java.io.IOException;

public class UncoverCellSinglePlayerMessageHandler implements UncoverCellHandler {
    private final SendableMessage sm;

    public UncoverCellSinglePlayerMessageHandler(SendableMessage sm) {
        this.sm = sm;
    }

    @Override
    public void onStartGame(boolean started) throws IOException {
        sm.send(GameSendMessage.createStartGame(started));
    }

    @Override
    public void onUncover(UncoverCellResponseDto cellDto) throws IOException {
        sm.send(GameSendMessage.createUncoverCell(cellDto));
    }

    @Override
    public void onIncrementUncovers(IncrementUncoversResponseDto incrementUncoversDto) throws IOException {
        sm.send(GameSendMessage.createIncrementUncovers(incrementUncoversDto));
    }

    @Override
    public void onEndGame(GameEndResponseDto gameEndDto) throws IOException {
        sm.send(GameSendMessage.createEndGame(gameEndDto));
    }
}
