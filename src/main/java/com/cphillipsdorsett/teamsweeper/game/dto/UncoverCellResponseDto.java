package com.cphillipsdorsett.teamsweeper.game.dto;

import com.cphillipsdorsett.teamsweeper.game.dao.Cell;

public class UncoverCellResponseDto {
    private final int rowIdx;
    private final int colIdx;
    private final String value;

    public UncoverCellResponseDto(Cell cell) {
        this.rowIdx = cell.getRowIdx();
        this.colIdx = cell.getColIdx();
        this.value = cell.getValue();
    }

    public int getRowIdx() {
        return rowIdx;
    }

    public int getColIdx() {
        return colIdx;
    }

    public String getValue() {
        return value;
    }
}
