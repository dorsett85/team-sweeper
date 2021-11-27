package com.cphillipsdorsett.teamsweeper;

import com.cphillipsdorsett.teamsweeper.game.GameService;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Component
public class WebSessionListener implements HttpSessionListener {
    private final Logger logger = Logger.getAnonymousLogger();
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
        logger.info("Expired session games deleted: " + countDeleted);
    }
}
