package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.game.dao.GameDifficulty;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains a map of all the preset board configurations
 */
public class BoardConfigPreset {
    private static final Map<GameDifficulty, BoardConfig> boardConfigMap = new HashMap<>() {{
        put(GameDifficulty.E, new BoardConfig(9, 9, 10));
        put(GameDifficulty.M, new BoardConfig(16, 16, 40));
        put(GameDifficulty.H, new BoardConfig(16, 30, 99));
    }};

    public static BoardConfig getByDifficulty(GameDifficulty difficulty) {
        return boardConfigMap.get(difficulty);
    }

    /**
     * Calculate the completion percentage of a game given its difficulty and
     * how many cells have been uncovered so far.
     */
    public static float getCompletionPct(GameDifficulty difficulty, int uncoveredCells) {
        BoardConfig boardConfig = boardConfigMap.get(difficulty);
        float totalCellsToUncover = (boardConfig.getRows() * boardConfig.getCols()) - boardConfig.getMines();
        return (uncoveredCells / totalCellsToUncover) * 100;
    }
}
