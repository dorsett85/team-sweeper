package com.cphillipsdorsett.teamsweeper.game.websocket;

import com.cphillipsdorsett.teamsweeper.game.Cell;
import com.cphillipsdorsett.teamsweeper.game.dto.GameEndResponseDto;
import com.cphillipsdorsett.teamsweeper.game.websocket.message.EndGameSendMessage;
import com.cphillipsdorsett.teamsweeper.game.websocket.message.StartGameSendMessage;
import com.cphillipsdorsett.teamsweeper.game.websocket.message.UncoverCellSendMessage;

import java.io.IOException;

/**
 * Contains methods to call while a cell is being uncovered
 */
public class UncoverCellHandler {
    private final SendableMessage sm;

    public UncoverCellHandler(SendableMessage sm) {
        this.sm = sm;
    }

    /**
     * Fired when the game starts
     */
    public void onStartGame(boolean started) throws IOException {
        sm.send(new StartGameSendMessage(started));
    }

    /**
     * Fired when a cell is uncovered and notifies the frontend that it
     * has been revealed.
     */
    public void onUncover(Cell cell) throws IOException {
        sm.send(new UncoverCellSendMessage(cell));
    }

    /**
     * Fired when the game is over (either won or lost)
     */
    public void onEndGame(GameEndResponseDto gameEndDto) throws IOException {
        sm.send(new EndGameSendMessage(gameEndDto));
    }
}
