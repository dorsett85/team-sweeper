package com.cphillipsdorsett.teamsweeper;

import com.cphillipsdorsett.teamsweeper.game.GameService;
import com.cphillipsdorsett.teamsweeper.game.websocket.GameSocketSessionStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Component
public class WebSessionListener implements HttpSessionListener {
    private final Logger logger = LogManager.getLogger(WebSessionListener.class);
    private final GameService gameService;
    private final GameSocketSessionStore gameSocketSessionStore;

    public WebSessionListener(
        GameService gameService,
        GameSocketSessionStore gamesocketSessionStore
    ) {
        this.gameService = gameService;
        this.gameSocketSessionStore = gamesocketSessionStore;
    }

    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        String sessionId = sessionEvent.getSession().getId();

        try {
            int[] deletedCounts = gameService.deleteExpiredSessionGames(sessionId);
            logger.info("Deleted {} session_games, {} games", deletedCounts[0], deletedCounts[1]);
        } catch (Exception e) {
            logger.error("Error deleting expired session games with session id: {}", sessionId, e);
        }

        // Close related websocket connections
        try {
            gameSocketSessionStore.getByHttpId(sessionId).close();
        } catch (Exception e) {
            logger.error("Error closing websocket session", e);
        }
    }
}
