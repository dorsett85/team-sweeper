package com.cphillipsdorsett.teamsweeper.game.dto;

public class IncrementUncoversResponseDto {
    private final int uncovers;

    public IncrementUncoversResponseDto(int uncovers) {
        this.uncovers = uncovers;
    }

    public int getUncovers() {
        return uncovers;
    }
}
