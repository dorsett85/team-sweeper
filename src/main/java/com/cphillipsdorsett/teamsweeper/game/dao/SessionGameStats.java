package com.cphillipsdorsett.teamsweeper.game.dao;

import javax.persistence.*;

@Entity
public class SessionGameStats {
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private GameDifficulty difficulty;
    @Enumerated(EnumType.STRING)
    private GameStatus status;
    private int count;
    @Column(name = "fastest_time")
    private Long fastestTime;
    @Column(name = "most_uncovers")
    private int mostUncovers;
    @Column(name = "highest_score")
    private float highestScore;
    @Column(name = "avg_completion_pct")
    private float avgCompletionPct;

    protected SessionGameStats() {
    }

    public Long getId() {
        return id;
    }

    public GameDifficulty getDifficulty() {
        return difficulty;
    }

    public GameStatus getStatus() {
        return status;
    }

    public int getCount() {
        return count;
    }

    public Long getFastestTime() {
        return fastestTime;
    }

    public int getMostUncovers() {
        return mostUncovers;
    }

    public float getHighestScore() {
        return highestScore;
    }

    public float getAvgCompletionPct() {
        return avgCompletionPct;
    }
}
