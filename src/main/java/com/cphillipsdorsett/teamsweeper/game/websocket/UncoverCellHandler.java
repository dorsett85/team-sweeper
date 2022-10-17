package com.cphillipsdorsett.teamsweeper.game.websocket;

import com.cphillipsdorsett.teamsweeper.game.dto.GameEndResponseDto;
import com.cphillipsdorsett.teamsweeper.game.dto.IncrementUncoversResponseDto;
import com.cphillipsdorsett.teamsweeper.game.dto.UncoverCellResponseDto;

import java.io.IOException;

/**
 * Event related callbacks to call when a cell is being uncovered
 */
public interface UncoverCellHandler {

    /**
     * Fired when the game starts
     */
    void onStartGame(boolean started) throws IOException;

    /**
     * Fired when a cell is uncovered
     */
    void onUncover(UncoverCellResponseDto cellDto) throws IOException;

    /**
     * Fired when uncovers are incremented
     */
    void onIncrementUncovers(IncrementUncoversResponseDto incrementUncoversDto) throws IOException;

    /**
     * Fired when the game is over (either won or lost)
     */
    void onEndGame(GameEndResponseDto gameEndDto) throws IOException;
}
