package com.cphillipsdorsett.teamsweeper.game.websocket;

import com.cphillipsdorsett.teamsweeper.game.dto.GameEndResponseDto;
import com.cphillipsdorsett.teamsweeper.game.dto.PointsResponseDto;
import com.cphillipsdorsett.teamsweeper.game.dto.UncoverCellResponseDto;

import java.io.IOException;

/**
 * Event related methods to call when a cell is being uncovered
 */
public interface UncoverCellMessageHandler {

    /**
     * Fired when the game starts
     */
    void onStartGame(boolean started) throws IOException;

    /**
     * Fired when a cell is uncovered
     */
    void onUncover(UncoverCellResponseDto cellDto) throws IOException;

    /**
     * Fired when points are adjusted
     */
    void onAdjustPoints(PointsResponseDto pointsDto) throws IOException;

    /**
     * Fired when the game is over (either won or lost)
     */
    void onEndGame(GameEndResponseDto gameEndDto) throws IOException;
}
