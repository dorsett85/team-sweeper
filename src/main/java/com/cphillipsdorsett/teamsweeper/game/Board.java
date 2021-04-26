package com.cphillipsdorsett.teamsweeper.game;

import java.util.Random;

public class Board {
    public final Cell[][] cells2d;
    public final int rows;
    public final int cols;
    public final int mines;
    public final int nonMines;
    public final int totalCells;

    /**
     * 2d array of surrounding cells starting in top left corner moving
     * clockwise.
     */
    private final int[][] surroundingCells = {
        { -1, -1 },
        { -1, 0},
        { -1, 1},
        { 0, 1},
        { 1, 1},
        { 1, 0},
        { 1, -1},
        { 0, -1},
    };

    public Board(String difficulty) {
        if (difficulty.equals("e")) {
            rows = 9;
            cols = 9;
            mines = 10;
        } else if (difficulty.equals("m")) {
            rows = 16;
            cols = 16;
            mines = 40;
        } else {
            rows = 16;
            cols = 30;
            mines = 99;
        }

        cells2d = new Cell[rows][cols];
        totalCells = rows * cols;
        nonMines = totalCells - mines;

        // Adds cells to the rows and columns
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                cells2d[row][col] = new Cell(row, col);
            }
        }

        // Add random mines
        int mineCount = mines;
        while (mineCount > 0) {
            int rowIndex = new Random().nextInt(rows);
            int colIndex = new Random().nextInt(cols);
            Cell cell = cells2d[rowIndex][colIndex];
            if (!cell.value.equals("x")) {
                cell.value = "x";

                // Now that we've made this cell a mine, add to the mine count
                // value of all surrounding cells.
                addNearbyMineCount(cell);

                mineCount--;
            }
        }
    }

    /**
     * Add 1 to value of all surrounding cells of the passed in Cell. The
     * passed in Cell must be one has a mine on it.
     */
    private void addNearbyMineCount(Cell cellWithMine) {

        for (int[] cellTuple : surroundingCells) {
            int rIdx = cellWithMine.rowIdx + cellTuple[0];
            int cIdx = cellWithMine.colIdx + cellTuple[1];

            // Check that the indexes are not out of bounds
            boolean isRowOob = rIdx < 0 || rIdx + 1 > cells2d.length;
            boolean isColOob = cIdx < 0 || cIdx + 1 > cells2d[0].length;
            if (!isRowOob && !isColOob) {
                Cell nearbyCell = cells2d[rIdx][cIdx];
                if (!nearbyCell.value.equals("x")) {
                    int newValue = Integer.parseInt(nearbyCell.value) + 1;
                    nearbyCell.value = Integer.toString(newValue);
                }
            }
        }
    }

}
