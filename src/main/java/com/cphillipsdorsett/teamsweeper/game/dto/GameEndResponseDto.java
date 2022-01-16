package com.cphillipsdorsett.teamsweeper.game.dto;

import com.cphillipsdorsett.teamsweeper.game.dao.GameStatus;

/**
 * Data sent to the frontend when a game ends
 */
public class GameEndResponseDto {
    private final GameStatus status;
    /**
     * Amount of time the game lasted (in milliseconds)
     */
    private final long duration;

    public GameEndResponseDto(GameStatus status, long duration) {
        this.status = status;
        this.duration = duration;
    }

    public GameStatus getStatus() {
        return status;
    }

    public long getDuration() {
        return duration;
    }
}
