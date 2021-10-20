package com.cphillipsdorsett.teamsweeper.game.dto;

import com.cphillipsdorsett.teamsweeper.game.BoardConfig;
import com.cphillipsdorsett.teamsweeper.game.dao.Game;

/**
 * Data sent to the frontend when a new game starts
 */
public class GameStartDto {
    public int id;
    public String difficulty;
    public int rows;
    public int cols;
    public int mines;

    public GameStartDto(Game game, BoardConfig boardConfig) {
        this.id = game.id;
        this.difficulty = game.difficulty;
        this.rows = boardConfig.rows;
        this.cols = boardConfig.cols;
        this.mines = boardConfig.mines;
    }
}
