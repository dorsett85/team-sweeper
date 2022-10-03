package com.cphillipsdorsett.teamsweeper.game.dao;

/**
 * Any games that are currently in-progress
 */
public interface LiveGameDao {
    LiveGame add(String httpSessionId, LiveGame liveGame);

    LiveGame get(String httpSessionId);

    LiveGame remove(String httpSessionId);
}
