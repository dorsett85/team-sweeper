package com.cphillipsdorsett.teamsweeper;

import com.cphillipsdorsett.teamsweeper.game.GameService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Component
public class WebSessionListener implements HttpSessionListener {
    private final Logger logger = LogManager.getLogger(WebSessionListener.class);
    private final GameService gameService;

    public WebSessionListener(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        String sessionId = sessionEvent.getSession().getId();
        int countDeleted = gameService.deleteExpiredSessionGames(sessionId);
        logger.info("Expired session games deleted: {}", countDeleted);
    }
}
