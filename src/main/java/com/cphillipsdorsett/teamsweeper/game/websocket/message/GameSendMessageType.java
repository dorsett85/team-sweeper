package com.cphillipsdorsett.teamsweeper.game.websocket.message;

/**
 * Values for the type property when sending socket messages.
 */
public enum GameSendMessageType {
    NEW_GAME,
    START_GAME,
    UNCOVER_CELL,
    INCREMENT_UNCOVERS,
    END_GAME
}
