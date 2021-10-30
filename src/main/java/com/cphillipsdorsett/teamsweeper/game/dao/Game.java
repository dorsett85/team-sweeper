package com.cphillipsdorsett.teamsweeper.game.dao;

import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.Instant;

@Entity
@DynamicInsert
public class Game {
    @Id
    public int id;
    @Enumerated(EnumType.STRING)
    public GameDifficulty difficulty;
    @Enumerated(EnumType.STRING)
    public GameStatus status = GameStatus.IN_PROGRESS;
    /**
     * JSON string representing a 2 dimensional array of rows and columns that
     * contains cells.
     */
    public String board;
    @Column(name = "created_at")
    public Instant createdAt;
    @Column(name = "started_at")
    public Instant startedAt;

    protected Game() {}

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }
}
