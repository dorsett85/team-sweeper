package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.Board;

import org.springframework.stereotype.Service;

@Service
public class GameService {

    public Board createBoard(String difficulty) {
        Board board = new Board(difficulty);
        // TODO add row to DB
        return board;
    }
}
