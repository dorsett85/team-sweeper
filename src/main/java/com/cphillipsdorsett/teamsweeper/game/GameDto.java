package com.cphillipsdorsett.teamsweeper.game;

public class GameDto {
    public String difficulty;
    public int rows;
    public int cols;
    public int mines;

    public GameDto(String difficulty, GameBuilder.BoardConfig boardConfig) {
        this.difficulty = difficulty;
        this.rows = boardConfig.rows;
        this.cols = boardConfig.cols;
        this.mines = boardConfig.mines;
    }
}
