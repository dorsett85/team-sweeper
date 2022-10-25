package com.cphillipsdorsett.teamsweeper.game.dao;

import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.Instant;

@Entity
@DynamicInsert
public class Game {
    @Id
    private int id;
    @Enumerated(EnumType.STRING)
    private GameDifficulty difficulty;
    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.IN_PROGRESS;
    /**
     * JSON string representing a 2 dimensional array of rows and columns that
     * contains cells.
     */
    private String board;
    @Column(name = "completion_pct")
    private float completionPct;
    @Column(name = "started_at")
    private Instant startedAt;
    @Column(name = "ended_at")
    private Instant endedAt;
    @Column(name = "created_at")
    private Instant createdAt;

    protected Game() {
    }

    public Game(GameDifficulty difficulty, String board) {
        this.difficulty = difficulty;
        this.board = board;
    }

    public int getId() {
        return id;
    }

    public GameDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(GameDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public float getCompletionPct() {
        return completionPct;
    }

    public void setCompletionPct(float completionPct) {
        this.completionPct = completionPct;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
