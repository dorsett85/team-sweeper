package com.cphillipsdorsett.teamsweeper.game.dao;

import com.cphillipsdorsett.teamsweeper.game.GameBoard;

import java.time.Instant;

public class LiveGame {
    private final Cell[][] board;
    private final int uncoveredCellsNeededToWin;
    private Instant startedAt;
    private int uncoveredCells = 0;

    public LiveGame(GameBoard gameBoard) {
        this.board = gameBoard.getBoard();
        this.uncoveredCellsNeededToWin = gameBoard.getUncoveredCellsNeededToWin();
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public int getUncoveredCells() {
        return uncoveredCells;
    }

    public int incrementUncoveredCells() {
        return ++uncoveredCells;
    }
}
