package com.cphillipsdorsett.teamsweeper.game;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SessionGame {

    @Id
    public int id;
    @Column(name = "session_id")
    public String sessionId;
    @Column(name = "game_id")
    public int gameId;

    protected SessionGame() {}

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
