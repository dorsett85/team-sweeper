package com.cphillipsdorsett.teamsweeper.game.dao;

import org.springframework.lang.NonNull;

public class Cell {
    /**
     * Row index on the board
     */
    private int rowIdx;
    /**
     * Column index on the board
     */
    private int colIdx;
    /**
     * Surrounding mine count ('0' to '8' or 'x' if it's a mine)
     */
    @NonNull
    private String value = "0";
    /**
     * Whether the cell is covered
     */
    private boolean covered = true;
    /**
     * Whether the cell has been checked to uncover. This is important when a
     * user loses/wins the game and we uncover all the remaining cells, but
     * don't want infinite recursion.
     */
    private boolean checked = false;

    public Cell() {
    }

    public Cell(int rIdx, int cIdx) {
        this.rowIdx = rIdx;
        this.colIdx = cIdx;
    }

    public int getRowIdx() {
        return rowIdx;
    }

    public int getColIdx() {
        return colIdx;
    }

    @NonNull
    public String getValue() {
        return value;
    }

    public void setValue(@NonNull String value) {
        this.value = value;
    }

    public boolean isCovered() {
        return covered;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isMine() {
        return this.value.equals("x");
    }

    public boolean isNearMine() {
        return !this.value.equals("0");
    }

    @Override
    public String toString() {
        return "{ value: " + value + ", covered: " + covered + ", checked: " + checked + " }";
    }
}
