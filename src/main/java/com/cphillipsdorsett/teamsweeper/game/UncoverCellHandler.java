package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.game.dao.Cell;
import com.cphillipsdorsett.teamsweeper.game.dto.GameEndResponseDto;

import java.io.IOException;

/**
 * Event related methods to call when a cell is being uncovered
 */
public interface UncoverCellHandler {

    /**
     * Fired when the game starts
     */
    void onStartGame(boolean started) throws IOException;

    /**
     * Fired when a cell is uncovered
     */
    void onUncover(Cell cell) throws IOException;

    /**
     * Fired when the game is over (either won or lost)
     */
    void onEndGame(GameEndResponseDto gameEndDto) throws IOException;
}
