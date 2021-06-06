package com.cphillipsdorsett.teamsweeper.game.websocket;

public class UncoverCellMessage extends GameMessage<UncoverCellMessage.UncoverCellPayload> {
    public static GameSocketMessageType TYPE = GameSocketMessageType.UNCOVER_CELL;
    public UncoverCellPayload payload;

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