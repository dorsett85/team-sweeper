package com.cphillipsdorsett.teamsweeper.game.websocket.message;

import com.cphillipsdorsett.teamsweeper.game.dto.GameEndResponseDto;
import com.cphillipsdorsett.teamsweeper.game.dto.GameStartResponseDto;
import com.cphillipsdorsett.teamsweeper.game.dto.PointsResponseDto;
import com.cphillipsdorsett.teamsweeper.game.dto.UncoverCellResponseDto;

/**
 * Generate message objects for sending websocket messages.
 */
public class GameSendMessage extends BaseMessage<GameSendMessageType, Object> {

    public GameSendMessage(GameSendMessageType type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public static GameSendMessage createNewGame(GameStartResponseDto gameStartResponseDto) {
        return new GameSendMessage(GameSendMessageType.NEW_GAME, gameStartResponseDto);
    }

    public static GameSendMessage createStartGame(boolean started) {
        return new GameSendMessage(GameSendMessageType.START_GAME, started);
    }

    public static GameSendMessage createUncoverCell(UncoverCellResponseDto cellResponseDto) {
        return new GameSendMessage(GameSendMessageType.UNCOVER_CELL, cellResponseDto);
    }

    public static GameSendMessage createAdjustPoints(PointsResponseDto pointsResponseDto) {
        return new GameSendMessage(GameSendMessageType.ADJUST_POINTS, pointsResponseDto);
    }

    public static GameSendMessage createEndGame(GameEndResponseDto gameEndResponseDto) {
        return new GameSendMessage(GameSendMessageType.END_GAME, gameEndResponseDto);
    }

}
