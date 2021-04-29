package com.cphillipsdorsett.teamsweeper.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameDao gameDao;
    private final SessionGameDao sessionGameDao;

    public GameService(GameDao gameDao, SessionGameDao sessionGameDao) {
        this.gameDao = gameDao;
        this.sessionGameDao = sessionGameDao;
    }

    public GameDto newGame(String sessionId, String difficulty) throws JsonProcessingException {

        GameBuilder gameBuilder = new GameBuilder(difficulty);

        // Add records to the db, need to serialize the board for the db
        // insertion.
        Game gameToInsert = new Game(difficulty, gameBuilder.getSerializedBoard());
        Game game = gameDao.create(gameToInsert);

        // Also need a session_game record so we can match the game based on the
        // session for the websocket connect.
        SessionGame sessionGame = new SessionGame(sessionId, game.id);
        sessionGameDao.create(sessionGame);

        return new GameDto(game.difficulty, GameBuilder.getBoardConfig(game.difficulty));
    }
}
