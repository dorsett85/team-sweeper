package com.cphillipsdorsett.teamsweeper.game.websocket;

import com.cphillipsdorsett.teamsweeper.game.GameService;
import com.cphillipsdorsett.teamsweeper.game.UncoverCellSinglePlayerMessageHandler;
import com.cphillipsdorsett.teamsweeper.game.dao.GameDifficulty;
import com.cphillipsdorsett.teamsweeper.game.dto.GameStartResponseDto;
import com.cphillipsdorsett.teamsweeper.game.websocket.message.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class GameSocketDispatch {
    private final ObjectMapper om = new ObjectMapper();
    private final GameService gameService;
    public final Map<GameReceiveMessageType, MessageDispatch> dispatchMap;

    public GameSocketDispatch(GameService gameService) {
        this.gameService = gameService;

        // This is key. Our socket handler will call the values in this map
        // based on the type property in the json message.
        this.dispatchMap = new HashMap<>() {{
            put(GameReceiveMessageType.NEW_GAME, (node, session) -> newGame(node, session));
            put(GameReceiveMessageType.UNCOVER_CELL, (node, session) -> uncoverCell(node, session));
        }};
    }

    public void newGame(JsonNode node, WebSocketSession session) throws IOException, GameSocketException {
        NewGameReceiveMessage msg = om.treeToValue(node, NewGameReceiveMessage.class);
        String httpSessionId = GameSocketUtil.getHttpSessionId(session);
        GameDifficulty difficulty = GameDifficulty
            .getNameByValue(msg.getPayload().getDifficulty())
            .orElseThrow(() -> new GameSocketException(CloseStatus.BAD_DATA, "difficulty param must be (e|m|h)"));

        GameStartResponseDto responseDto = gameService.newGame(httpSessionId, difficulty);
        sendMessage(GameSendMessage.createNewGame(responseDto), session);
    }

    public void uncoverCell(JsonNode node, WebSocketSession session) throws IOException {
        UncoverCellReceiveMessage msg = om.treeToValue(node, UncoverCellReceiveMessage.class);
        String httpSessionId = GameSocketUtil.getHttpSessionId(session);

        SendableMessage sm = (gameSendMessage) -> sendMessage(gameSendMessage, session);
        gameService.uncoverCell(httpSessionId, msg.getPayload(), new UncoverCellSinglePlayerMessageHandler(sm));
    }

    /**
     * Send a socket message to the client with "type" and "payload" properties
     */
    private void sendMessage(GameSendMessage gameSendMessage, WebSocketSession session) throws IOException {
        TextMessage textMessage = new TextMessage(om.writeValueAsString(gameSendMessage));
        session.sendMessage(textMessage);
    }

    /**
     * Lambda expression as our dispatch map value
     */
    interface MessageDispatch {
        void dispatch(JsonNode node, WebSocketSession session) throws IOException, GameSocketException;
    }
}
