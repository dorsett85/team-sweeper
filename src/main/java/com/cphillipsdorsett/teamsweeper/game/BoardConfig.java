package com.cphillipsdorsett.teamsweeper.game;

public class BoardConfig {
    public int rows;
    public int cols;
    public int mines;

    public BoardConfig(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
    }
}
