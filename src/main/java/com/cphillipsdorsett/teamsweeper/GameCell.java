package com.cphillipsdorsett.teamsweeper;

import org.springframework.lang.NonNull;

public class GameCell {
    /**
     * Row index on the board
     */
    public final int rowIdx;
    /**
     * Column index on the board
     */
    public final int colIdx;
    /**
     * Surrounding mine count ('0' to '8' or 'x' if it's a mine)
     */
    @NonNull
    public String value = "0";
    /**
     * Whether or not the cell is covered
     */
    public boolean covered = true;

    public GameCell(int rIdx, int cIdx) {
        this.rowIdx = rIdx;
        this.colIdx = cIdx;
    }

    @Override
    public String toString() {
        return "{ value: " + value + ", covered: " + covered + " }";
    }
}
