package com.cphillipsdorsett.teamsweeper.game.dao;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LiveGameDaoInMemory implements LiveGameDao {
    /**
     * Any active games with a http session id as key
     */
    private final Map<String, LiveGame> games = new HashMap<>();

    @Override
    public LiveGame add(String sessionId, LiveGame liveGame) {
        return games.put(sessionId, liveGame);
    }

    @Override
    public LiveGame get(String sessionId) {
        return games.get(sessionId);
    }

    @Override
    public LiveGame remove(String sessionId) {
        return games.remove(sessionId);
    }
}
