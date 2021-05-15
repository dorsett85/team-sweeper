package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.game.dao.Game;

public class GameDto {
    public int id;
    public String difficulty;
    public int rows;
    public int cols;
    public int mines;

    public GameDto(Game game, GameBuilder.BoardConfig boardConfig) {
        this.id = game.id;
        this.difficulty = game.difficulty;
        this.rows = boardConfig.rows;
        this.cols = boardConfig.cols;
        this.mines = boardConfig.mines;
    }
}
