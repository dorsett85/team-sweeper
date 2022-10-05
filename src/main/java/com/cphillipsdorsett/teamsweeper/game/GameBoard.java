package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.game.dao.Cell;
import com.cphillipsdorsett.teamsweeper.game.dao.GameDifficulty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class GameBoard {
    private final Cell[][] board;
    private final GameDifficulty difficulty;
    private final int uncoveredCellsNeededToWin;
    private static final Map<GameDifficulty, BoardConfig> boardConfigMap = new HashMap<>() {{
        put(GameDifficulty.E, new BoardConfig(9, 9, 10));
        put(GameDifficulty.M, new BoardConfig(16, 16, 40));
        put(GameDifficulty.H, new BoardConfig(16, 30, 99));
    }};
    /**
     * 2d array of surrounding cells starting in top left corner moving
     * clockwise.
     */
    private static final int[][] nearbyCells = {
        {-1, -1},
        {-1, 0},
        {-1, 1},
        {0, 1},
        {1, 1},
        {1, 0},
        {1, -1},
        {0, -1},
    };

    public GameBoard(GameDifficulty difficulty) {
        BoardConfig boardConfig = boardConfigMap.get(difficulty);
        int rows = boardConfig.getRows();
        int cols = boardConfig.getCols();
        int mines = boardConfig.getMines();

        this.board = new Cell[rows][cols];
        this.difficulty = difficulty;
        this.uncoveredCellsNeededToWin = rows * cols - mines;

        // Adds cells to the rows and columns
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                board[row][col] = new Cell(row, col);
            }
        }

        // Add random mines
        int mineCount = mines;
        while (mineCount > 0) {
            int rowIndex = new Random().nextInt(rows);
            int colIndex = new Random().nextInt(cols);
            Cell cell = board[rowIndex][colIndex];
            if (!cell.isMine()) {
                cell.setValue("x");

                // Now that we've made this cell a mine, add to the mine count
                // value of all surrounding cells.
                addNearbyMineCount(cell, board);

                mineCount--;
            }
        }
    }

    /**
     * Add 1 to value of all surrounding cells of the passed in Cell. The
     * passed in Cell must be one with a mine on it.
     */
    private void addNearbyMineCount(Cell cellWithMine, Cell[][] board) {
        nearbyCellsForEach(cellWithMine, board, (nearbyCell) -> {
            if (!nearbyCell.isMine()) {
                int newValue = Integer.parseInt(nearbyCell.getValue()) + 1;
                nearbyCell.setValue(Integer.toString(newValue));
            }
        });
    }

    public Cell[][] getBoard() {
        return board;
    }

    public BoardConfig getBoardConfig() {
        return boardConfigMap.get(difficulty);
    }

    public int getUncoveredCellsNeededToWin() {
        return uncoveredCellsNeededToWin;
    }

    public String getSerializedBoard() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(board);
    }

    /**
     * For a given row and column index, check if the cell would be in bounds.
     */
    public static boolean isInBounds(int rIdx, int cIdx, Cell[][] board) {
        boolean rowInBounds = rIdx >= 0 && rIdx < board.length;
        boolean colInBounds = cIdx >= 0 && cIdx < board[0].length;
        return rowInBounds && colInBounds;
    }

    /**
     * Loop through surrounding cells. The BiConsumer function returns the cell
     * indexes of the surrounding cells.
     */
    public static void nearbyCellsForEach(Cell cell, Cell[][] board, Consumer<Cell> biConsumer) {
        for (int[] cellIndexes : nearbyCells) {
            int rIdx = cell.getRowIdx() + cellIndexes[0];
            int cIdx = cell.getColIdx() + cellIndexes[1];

            if (isInBounds(rIdx, cIdx, board)) {
                biConsumer.accept(board[rIdx][cIdx]);
            }
        }
    }

}
