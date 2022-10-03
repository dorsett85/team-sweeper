package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.game.dao.Cell;
import com.cphillipsdorsett.teamsweeper.game.dao.GameDifficulty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
    private static final int[][] surroundingCells = {
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
            boolean isRowOob = rIdx < 0 || rIdx + 1 > board.length;
            boolean isColOob = cIdx < 0 || cIdx + 1 > board[0].length;
            if (!isRowOob && !isColOob) {
                Cell nearbyCell = board[rIdx][cIdx];
                if (!nearbyCell.value.equals("x")) {
                    int newValue = Integer.parseInt(nearbyCell.value) + 1;
                    nearbyCell.value = Integer.toString(newValue);
                }
            }
        }
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
     * TODO Remove this method once live game is implemented
     */
    public static BoardConfig getBoardConfig(GameDifficulty difficulty) {
        return boardConfigMap.get(difficulty);
    }


    public static int[][] getSurroundingCells() {
        return surroundingCells;
    }

}
