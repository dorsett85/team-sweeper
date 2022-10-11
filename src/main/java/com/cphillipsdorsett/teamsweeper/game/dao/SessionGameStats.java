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
    @Column(name = "most_points")
    private int mostPoints;

    protected SessionGameStats() {
    }

    public SessionGameStats(
        GameDifficulty difficulty,
        GameStatus status,
        int count,
        Long fastestTime,
        int mostPoints
    ) {
        this.difficulty = difficulty;
        this.status = status;
        this.count = count;
        this.fastestTime = fastestTime;
        this.mostPoints = mostPoints;
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

    public int getMostPoints() {
        return mostPoints;
    }
}
