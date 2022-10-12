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
    private final int uncovers;
    private final float score;

    public GameEndResponseDto(GameStatus status, long duration, int uncovers, float score) {
        this.status = status;
        this.duration = duration;
        this.uncovers = uncovers;
        this.score = score;
    }

    public GameStatus getStatus() {
        return status;
    }

    public long getDuration() {
        return duration;
    }

    public int getUncovers() {
        return uncovers;
    }

    public float getScore() {
        return score;
    }
}
