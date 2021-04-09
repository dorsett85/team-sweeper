package com.cphillipsdorsett.teamsweeper.singlePlayer;

import org.springframework.stereotype.Service;

import com.cphillipsdorsett.teamsweeper.GameBoard;

@Service
public class SinglePlayerService {

    public GameBoard createBoard(String difficulty) {
        GameBoard board = new GameBoard(difficulty);
        // TODO add row to DB
        return board;
    }
}
