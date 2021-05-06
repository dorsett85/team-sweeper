package com.cphillipsdorsett.teamsweeper.game;

import org.springframework.lang.NonNull;

public class Cell {
    /**
     * Row index on the board
     */
    public int rowIdx;
    /**
     * Column index on the board
     */
    public int colIdx;
    /**
     * Surrounding mine count ('0' to '8' or 'x' if it's a mine)
     */
    @NonNull
    public String value = "0";
    /**
     * Whether or not the cell is covered
     */
    public boolean covered = true;

    public Cell() {}

    public Cell(int rIdx, int cIdx) {
        this.rowIdx = rIdx;
        this.colIdx = cIdx;
    }

    @Override
    public String toString() {
        return "{ value: " + value + ", covered: " + covered + " }";
    }
}
