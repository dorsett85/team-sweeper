package com.cphillipsdorsett.teamsweeper.game.websocket;

/**
 * Allowed values for the type property when sending/receiving socket messages.
 * If we start having different values between sending and receiving then we can
 * have separate types.
 */
public enum GameSocketMessageType {
    UNCOVER_CELL, END_GAME
}
