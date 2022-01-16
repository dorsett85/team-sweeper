package com.cphillipsdorsett.teamsweeper.game.websocket;

import com.cphillipsdorsett.teamsweeper.game.Cell;
import com.cphillipsdorsett.teamsweeper.game.dto.GameEndResponseDto;
import com.cphillipsdorsett.teamsweeper.game.websocket.message.EndGameSendMessage;
import com.cphillipsdorsett.teamsweeper.game.websocket.message.StartGameSendMessage;
import com.cphillipsdorsett.teamsweeper.game.websocket.message.UncoverCellSendMessage;

import java.io.IOException;

/**
 * Contains callback methods to send messages to the client while a cell is
 * being uncovered.
 */
public class UncoverCellMessageCallback {
    private final SendableMessage sm;

    public UncoverCellMessageCallback(SendableMessage sm) {
        this.sm = sm;
    }

    /**
     * Fired when the first cell is uncovered
     */
    public void startGame(boolean started) throws IOException {
        sm.send(new StartGameSendMessage(started));
    }

    /**
     * Fired when the cell is uncovered and notifies the frontend that it
     * has been revealed.
     */
    public void uncover(Cell cell) throws IOException {
        sm.send(new UncoverCellSendMessage(cell));
    }

    /**
     * Notifies the frontend that the game is over (either won or lost)
     */
    public void endGame(GameEndResponseDto gameEndDto) throws IOException {
        sm.send(new EndGameSendMessage(gameEndDto));
    }
}
