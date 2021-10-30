package com.cphillipsdorsett.teamsweeper.game.dao;

import java.util.Arrays;
import java.util.Optional;

/**
 * Values for the difficulty column in game table
 */
public enum GameDifficulty {
    E("e"),
    M("m"),
    H("h");

    private final String value;

    GameDifficulty(String value) {
        this.value = value;
    }

    /**
     * Reverse lookup to get an enum from a string value
     */
    public static Optional<GameDifficulty> getNameByValue(String value) {
        return Arrays
            .stream(values())
            .filter(gameDifficulty -> gameDifficulty.getValue().equals(value))
            .findFirst();
    }

    public String getValue() {
        return value;
    }
}
