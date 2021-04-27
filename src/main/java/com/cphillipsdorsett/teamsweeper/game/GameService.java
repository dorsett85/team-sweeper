package com.cphillipsdorsett.teamsweeper.game;

import org.springframework.stereotype.Service;

@Service
public class GameService {

    GameDao gameDao;

    public GameService(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    public Board getBoard(String sessionId, String difficulty) {
        // TODO lookup if the game exists by session id

        Board board = new Board(difficulty);

        // Add record to the db
        Game insertGame = new Game(difficulty, board.mines, board.nonMines);
        gameDao.create(insertGame);

        return board;
    }
}
