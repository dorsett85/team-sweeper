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
    public int mines;
    @Column(name = "non_mines")
    public int nonMines;
    @Column(name = "total_cells")
    public int totalCells;
    /**
     * JSON string representing a 2 dimensional array of rows and columns that
     * contains cells.
     */
    public String board;
    @Column(name = "created_at")
    public Instant createdAt;

    protected Game() {}

    public Game(String difficulty, int mines, int nonMines, String board) {
        this.difficulty = difficulty;
        this.mines = mines;
        this.nonMines = nonMines;
        this.board = board;
        this.totalCells = mines + nonMines;
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

    public int getMines() {
        return mines;
    }

    public void setMines(int mines) {
        this.mines = mines;
    }

    public int getNonMines() {
        return nonMines;
    }

    public void setNonMines(int nonMines) {
        this.nonMines = nonMines;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public int getTotalCells() {
        return totalCells;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
