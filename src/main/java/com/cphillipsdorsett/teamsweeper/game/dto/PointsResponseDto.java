package com.cphillipsdorsett.teamsweeper.game.dto;

public class PointsResponseDto {
    private final int points;

    public PointsResponseDto(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }
}
