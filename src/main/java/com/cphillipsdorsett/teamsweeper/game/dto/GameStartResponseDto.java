package com.cphillipsdorsett.teamsweeper.game.dto;

import com.cphillipsdorsett.teamsweeper.game.BoardConfig;
import com.cphillipsdorsett.teamsweeper.game.dao.Game;

/**
 * Data sent to the frontend when a new game starts
 */
public class GameStartResponseDto {
    private final int id;
    private final String difficulty;
    private final int rows;
    private final int cols;
    private final int mines;

    public GameStartResponseDto(Game game, BoardConfig boardConfig) {
        this.id = game.getId();
        this.difficulty = game.getDifficulty().getValue();
        this.rows = boardConfig.rows;
        this.cols = boardConfig.cols;
        this.mines = boardConfig.mines;
    }

    public int getId() {
        return id;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getMines() {
        return mines;
    }
}
