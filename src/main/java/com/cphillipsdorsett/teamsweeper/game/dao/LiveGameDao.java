package com.cphillipsdorsett.teamsweeper.game.dao;

/**
 * Any games that are currently in-progress
 */
public interface LiveGameDao {
    LiveGame add(String sessionId, LiveGame liveGame);

    LiveGame get(String sessionId);

    LiveGame remove(String sessionId);
}
