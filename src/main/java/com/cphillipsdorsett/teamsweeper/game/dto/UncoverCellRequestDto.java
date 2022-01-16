package com.cphillipsdorsett.teamsweeper.game.dto;

public class UncoverCellRequestDto {
    public int gameId;
    public int rowIdx;
    public int colIdx;

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
