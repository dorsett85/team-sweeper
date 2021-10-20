package com.cphillipsdorsett.teamsweeper.game.dto;

import com.cphillipsdorsett.teamsweeper.game.dao.GameStatus;

/**
 * Data sent to the frontend when a game ends
 */
public class GameEndDto {
    public GameStatus status;
    /**
     * Amount of time the game lasted (in milliseconds)
     */
    public long duration;

    public GameEndDto(GameStatus status, long duration) {
        this.status = status;
        this.duration = duration;
    }
}
