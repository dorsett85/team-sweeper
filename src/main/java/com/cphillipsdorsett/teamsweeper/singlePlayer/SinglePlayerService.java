package com.cphillipsdorsett.teamsweeper.singlePlayer;

import org.springframework.stereotype.Service;

@Service
public class SinglePlayerService {

    public void createBoard(String sessionId) {
        System.out.println(sessionId);
    }
}
