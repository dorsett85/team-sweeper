package com.cphillipsdorsett.teamsweeper.game.dao;

import com.cphillipsdorsett.teamsweeper.game.GameBoard;

import java.time.Instant;

public class LiveGame {
    private final int id;
    private GameStatus status = GameStatus.IN_PROGRESS;
    private final Cell[][] board;
    private Instant startedAt;
    private int uncoveredCells = 0;
    private final int uncoveredCellsNeededToWin;

    public LiveGame(int id, GameBoard gameBoard) {
        this.id = id;
        this.board = gameBoard.getBoard();
        this.uncoveredCellsNeededToWin = gameBoard.getUncoveredCellsNeededToWin();
    }

    public int getId() {
        return id;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public Cell[][] getBoard() {
        return board;
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

    public int getUncoveredCellsNeededToWin() {
        return uncoveredCellsNeededToWin;
    }

    public void incrementUncoveredCells() {
        uncoveredCells++;
    }

    public boolean isInProgress() {
        return status == GameStatus.IN_PROGRESS;
    }
}
