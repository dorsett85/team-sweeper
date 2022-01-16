package com.cphillipsdorsett.teamsweeper.game.websocket;

import com.cphillipsdorsett.teamsweeper.game.websocket.message.GameSendMessage;

import java.io.IOException;

/**
 * Callback method to prepare and send messages to the client
 */
interface SendableMessage {
    void send(GameSendMessage message) throws IOException;
}
