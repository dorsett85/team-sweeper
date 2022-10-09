package com.cphillipsdorsett.teamsweeper.game.dao;

import com.cphillipsdorsett.teamsweeper.game.GameBoard;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class LiveGame {
    private final int id;
    private GameStatus status = GameStatus.IN_PROGRESS;
    private final Cell[][] board;
    private Instant startedAt;
    private int uncoveredCells;
    private final int uncoveredCellsNeededToWin;
    private final Map<String, SessionInfo> sessions = new HashMap<>();

    public LiveGame(int id, String httpSessionId, GameBoard gameBoard) {
        this.id = id;
        this.board = gameBoard.getBoard();
        this.uncoveredCellsNeededToWin = gameBoard.getUncoveredCellsNeededToWin();
        sessions.put(httpSessionId, new SessionInfo());
    }

    public int getId() {
        return id;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public Cell[][] getBoard() {
        return board;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public int getUncoveredCells() {
        return uncoveredCells;
    }

    public int getUncoveredCellsNeededToWin() {
        return uncoveredCellsNeededToWin;
    }

    public void incrementUncoveredCells() {
        uncoveredCells++;
    }

    public boolean isInProgress() {
        return status == GameStatus.IN_PROGRESS;
    }

    public int getSessionCount() {
        return sessions.size();
    }

    public int getSessionPoints(String httpSessionId) {
        SessionInfo sessionInfo = sessions.get(httpSessionId);
        return sessionInfo.getPoints();
    }

    public void adjustSessionPoints(String httpSessionId, int points) {
        SessionInfo sessionInfo = sessions.get(httpSessionId);
        if (sessionInfo != null) {
            sessionInfo.setPoints(sessionInfo.getPoints() + points);
        }
    }

    public void removeSessionId(String httpSessionId) {
        sessions.remove(httpSessionId);
    }

    /**
     * Any session specific info associated with the live game
     */
    private static class SessionInfo {
        private int points;

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }
    }
}
