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
    /**
     * Whether or not the cell has been checked to uncover. This is important
     * when a user loses/wins the game and we uncover all of the remaining,
     * but don't want infinite recursion.
     */
    public boolean checked = false;

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
