package com.cphillipsdorsett.teamsweeper.game.dao;

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
     * Whether the cell is covered
     */
    public boolean covered = true;
    /**
     * Whether the cell has been checked to uncover. This is important when a
     * user loses/wins the game and we uncover all the remaining cells, but
     * don't want infinite recursion.
     */
    public boolean checked = false;

    public Cell() {
    }

    public Cell(int rIdx, int cIdx) {
        this.rowIdx = rIdx;
        this.colIdx = cIdx;
    }

    @Override
    public String toString() {
        return "{ value: " + value + ", covered: " + covered + ", checked: " + checked + " }";
    }
}
