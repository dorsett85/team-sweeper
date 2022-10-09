package com.cphillipsdorsett.teamsweeper.game.dao;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class LiveGameDaoInMemory implements LiveGameDao {
    /**
     * Game ids indexed by http session id. This points to the active game for a
     * given session id.
     */
    private final Map<String, Integer> sessionGameId = new HashMap<>();
    /**
     * Active games indexed by game id
     */
    private final Map<Integer, LiveGame> games = new HashMap<>();

    @Override
    public void updateSessionGame(String sessionId, LiveGame liveGame) {
        remove(sessionId);
        int gameId = liveGame.getId();
        sessionGameId.put(sessionId, gameId);
        games.put(gameId, liveGame);
    }

    @Override
    public Optional<LiveGame> get(String sessionId) {
        Integer gameId = sessionGameId.get(sessionId);
        return Optional.ofNullable(games.get(gameId));
    }

    @Override
    public void remove(String sessionId) {
        Integer gameId = sessionGameId.get(sessionId);
        sessionGameId.remove(sessionId);
        LiveGame game = games.get(gameId);

        if (game == null) {
            return;
        }

        game.removeSessionId(sessionId);

        // Remove the game if there are no more sessions associated with it
        if (game.getSessionCount() == 0) {
            games.remove(gameId);
        }
    }
}
