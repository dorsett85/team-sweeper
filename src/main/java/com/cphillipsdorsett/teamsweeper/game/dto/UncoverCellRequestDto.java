package com.cphillipsdorsett.teamsweeper.game.dto;

public class UncoverCellRequestDto {
    private int gameId;
    private int rowIdx;
    private int colIdx;

    public int getGameId() {
        return gameId;
    }

    public int getRowIdx() {
        return rowIdx;
    }

    public int getColIdx() {
        return colIdx;
    }
}
