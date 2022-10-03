package com.cphillipsdorsett.teamsweeper.game;

/**
 * Basic configuration of the game board
 */
public class BoardConfig {
    private final int rows;
    private final int cols;
    private final int mines;

    public BoardConfig(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
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
