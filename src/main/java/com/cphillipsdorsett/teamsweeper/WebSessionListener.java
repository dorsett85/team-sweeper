package com.cphillipsdorsett.teamsweeper;

import com.cphillipsdorsett.teamsweeper.game.GameService;
import com.cphillipsdorsett.teamsweeper.game.websocket.GameSocketSessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Component
public class WebSessionListener implements HttpSessionListener {
    private final Logger logger = LogManager.getLogger(WebSessionListener.class);
    private final GameService gameService;
    private final GameSocketSessionManager gameSocketSessionManager;

    public WebSessionListener(GameService gameService, GameSocketSessionManager gameSocketSessionManager) {
        this.gameService = gameService;
        this.gameSocketSessionManager = gameSocketSessionManager;
    }

    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        String sessionId = sessionEvent.getSession().getId();

        // Remove all relevant games to the session
        try {
            int countDeleted = gameService.deleteExpiredSessionGames(sessionId);
            logger.info("Expired session games deleted: {}", countDeleted);
        } catch (Exception e) {
            logger.error("Error deleting expired session games", e);
        }

        // Close related websocket connections
        try {
            gameSocketSessionManager.getByHttpId(sessionId).close();
        } catch (Exception e) {
            logger.error("Error closing websocket session", e);
        }
    }
}
