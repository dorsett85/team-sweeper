package com.cphillipsdorsett.teamsweeper.game.dao;

import java.util.Optional;

/**
 * Any games that are currently in-progress
 */
public interface LiveGameDao {
    /**
     * Create or update the game associated with a http session id
     */
    void updateSessionGame(String httpSessionId, LiveGame liveGame);

    /**
     * Get the game associated with a http session id
     */
    Optional<LiveGame> get(String httpSessionId);

    /**
     * Remove the game only if a single http session is associated with it. If
     * there are other sessions that means it's still active.
     */
    void remove(String httpSessionId);
}
