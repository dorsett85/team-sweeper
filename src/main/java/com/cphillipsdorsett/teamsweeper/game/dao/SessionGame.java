package com.cphillipsdorsett.teamsweeper.game.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SessionGame {
    @Id
    private int id;
    @Column(name = "session_id")
    private String sessionId;
    @Column(name = "game_id")
    private int gameId;

    protected SessionGame() {
    }

    public SessionGame(String sessionId, int gameId) {
        this.sessionId = sessionId;
        this.gameId = gameId;
    }

    public int getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
