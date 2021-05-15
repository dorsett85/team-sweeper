package com.cphillipsdorsett.teamsweeper.game.websocket;

public class UncoverCellMessage extends GameMessage<UncoverCellMessage.UncoverCellPayload> {
    public static final String TYPE = "uncoverCell";
    public String type = UncoverCellMessage.TYPE;
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