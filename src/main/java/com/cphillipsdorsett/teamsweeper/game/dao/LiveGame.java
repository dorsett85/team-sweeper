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
    private Instant endedAt;
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

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
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

    public int getUncoversBySession(String httpSessionId) {
        SessionInfo sessionInfo = sessions.get(httpSessionId);
        return sessionInfo.getUncovers();
    }

    public void incrementSessionUncovers(String httpSessionId) {
        incrementSessionUncovers(httpSessionId, 1);
    }

    public void incrementSessionUncovers(String httpSessionId, int uncovers) {
        SessionInfo sessionInfo = sessions.get(httpSessionId);
        if (sessionInfo != null) {
            sessionInfo.setUncovers(sessionInfo.getUncovers() + uncovers);
        }
    }

    public void removeSessionId(String httpSessionId) {
        sessions.remove(httpSessionId);
    }

    /**
     * Any session specific info associated with the live game
     */
    private static class SessionInfo {
        private int uncovers;

        public int getUncovers() {
            return uncovers;
        }

        public void setUncovers(int uncovers) {
            this.uncovers = uncovers;
        }
    }
}
