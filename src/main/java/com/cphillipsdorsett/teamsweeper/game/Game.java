package com.cphillipsdorsett.teamsweeper.game;

import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
@DynamicInsert
public class Game {

    @Id
    public int id;
    public String difficulty;
    /**
     * JSON string representing a 2 dimensional array of rows and columns that
     * contains cells.
     */
    public String board;
    @Column(name = "created_at")
    public Instant createdAt;

    protected Game() {}

    public Game(String difficulty, String board) {
        this.difficulty = difficulty;
        this.board = board;
    }

    public int getId() {
        return id;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
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
}
