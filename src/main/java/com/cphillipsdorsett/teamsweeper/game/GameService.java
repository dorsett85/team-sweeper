package com.cphillipsdorsett.teamsweeper.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    GameDao gameDao;
    SessionGameDao sessionGameDao;

    public GameService(GameDao gameDao, SessionGameDao sessionGameDao) {
        this.gameDao = gameDao;
        this.sessionGameDao = sessionGameDao;
    }

    public Game getGame(String sessionId, String difficulty) throws JsonProcessingException {
        // TODO lookup if the game exists by session id
        Game game = gameDao.findBySessionId(sessionId);

        if (game == null) {
            GameBuilder gameBuilder = new GameBuilder(difficulty);

            // Add records to the db, need to serialize the board for the db
            // insertion.
            Game gameToInsert = new Game(difficulty, gameBuilder.mines, gameBuilder.nonMines, gameBuilder.getSerializedBoard());
            game = gameDao.create(gameToInsert);

            // Also need a session_game record so we can match the game based
            // on the session next time around.
            SessionGame sessionGame = new SessionGame(sessionId, game.id);
            sessionGameDao.create(sessionGame);
        }

        return game;
    }
}
