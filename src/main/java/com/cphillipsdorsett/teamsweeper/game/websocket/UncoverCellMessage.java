package com.cphillipsdorsett.teamsweeper.game.websocket;

public class UncoverCellMessage extends GameMessage<GameMessageReceiveType, UncoverCellMessage.UncoverCellPayload> {
    public static GameMessageReceiveType TYPE = GameMessageReceiveType.UNCOVER_CELL;

    public static class UncoverCellPayload {
        public int gameId;
        public int rowIdx;
        public int colIdx;

        public int getGameId() {
            return gameId;
        }

        public int getRowIdx() {
            return rowIdx;
        }

        public int getColIdx() {
            return colIdx;
        }
    }
}