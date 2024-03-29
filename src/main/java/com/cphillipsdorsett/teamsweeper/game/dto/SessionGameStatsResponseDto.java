package com.cphillipsdorsett.teamsweeper.game.dto;

import com.cphillipsdorsett.teamsweeper.game.dao.GameDifficulty;
import com.cphillipsdorsett.teamsweeper.game.dao.GameStatus;
import com.cphillipsdorsett.teamsweeper.game.dao.SessionGameStats;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionGameStatsResponseDto {
    /**
     * Aggregate game statistics by difficulty and status
     */
    public final Map<String, GameDifficultyStats> games;

    public SessionGameStatsResponseDto(Map<String, GameDifficultyStats> games) {
        this.games = games;
    }

    /**
     * Maps a {@code List<SessionGameStats>} to a {@code SessGameStatsDto}
     */
    public static SessionGameStatsResponseDto fromSessionGameStatsList(List<SessionGameStats> sessionGameStatsList) {
        Map<String, GameDifficultyStats> gameStats = generateGameStats(sessionGameStatsList);
        return new SessionGameStatsResponseDto(gameStats);
    }

    /**
     * Creates the {@code SessionGameStatsDto.games} field from a
     * {@code List<SessionGameStats>}.
     */
    private static Map<String, GameDifficultyStats> generateGameStats(List<SessionGameStats> sessionGameStatsList) {
        Map<String, GameDifficultyStats> baseGamesStats = makeBaseGameStats();
        sessionGameStatsList.forEach(sessionGameStats -> {
            String difficultyKey = sessionGameStats.getDifficulty().getValue();
            GameStatus statusKey = sessionGameStats.getStatus();

            // Set values in the baseGamesStats map
            GameDifficultyStats gameDifficultyStats = baseGamesStats.get(difficultyKey);
            gameDifficultyStats.count += sessionGameStats.getCount();
            gameDifficultyStats.statuses.get(statusKey).count = sessionGameStats.getCount();
            gameDifficultyStats.statuses.get(statusKey).fastestTime = sessionGameStats.getFastestTime();
            gameDifficultyStats.statuses.get(statusKey).mostUncovers = sessionGameStats.getMostUncovers();
            gameDifficultyStats.statuses.get(statusKey).highestScore = sessionGameStats.getHighestScore();
            gameDifficultyStats.statuses.get(statusKey).avgCompletionPct = sessionGameStats.getAvgCompletionPct();
        });
        return baseGamesStats;
    }

    /**
     * Make a base map object that contains initial key/values for all
     * iterations of difficulties and statuses. This will be cloned and mutated
     * to fill in data for the DTO.
     */
    private static Map<String, GameDifficultyStats> makeBaseGameStats() {
        Map<String, GameDifficultyStats> baseMap = new HashMap<>();
        Arrays.stream(GameDifficulty.values()).forEach(gameDifficulty -> {
            GameDifficultyStats gameDifficultyStats = new GameDifficultyStats();
            baseMap.put(gameDifficulty.getValue(), gameDifficultyStats);
        });
        return baseMap;
    }

    private static class GameDifficultyStats {
        public int count = 0;
        public final Map<GameStatus, GameStatusStats> statuses;

        public GameDifficultyStats() {
            // Initiate the statuses map with all the GameStatus keys
            this.statuses = new HashMap<>();
            Arrays.stream(GameStatus.values()).forEach(status -> this.statuses.put(status, new GameStatusStats()));
        }
    }

    private static class GameStatusStats {
        public int count;
        /**
         * Fastest time from start to end in milliseconds
         */
        public Long fastestTime;
        public int mostUncovers;
        /**
         * Combined score taking into account uncovers and score
         */
        public float highestScore;
        /**
         * Calculated by uncovered cells divided by total non-mine cells
         */
        public float avgCompletionPct;

        public GameStatusStats() {
        }
    }
}
