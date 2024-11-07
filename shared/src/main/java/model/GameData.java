package model;

import chess.ChessGame;

public class GameData {
    private final int gameID;
    private String whiteUsername;
    private String blackUsername;
    private final String gameName;
    private final ChessGame game;

    public GameData(String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this.gameID = 0;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }

    public boolean isColorTaken(String playerColor) throws Exception {
        if (playerColor.equalsIgnoreCase("black")) {
            return blackUsername != null;
        } else if (playerColor.equalsIgnoreCase("white")) {
            return whiteUsername != null;
        } else {
            throw new Exception("Invalid player color");
        }
    }

    public void addPlayer(String playerColor, String username) {
        if (playerColor.equalsIgnoreCase("black")) {
            blackUsername = username;
        } else if (playerColor.equalsIgnoreCase("white")) {
            whiteUsername = username;
        }
    }

    // Getters for fields (optional)
    public int getGameID() {
        return gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public String getGameName() {
        return gameName;
    }

    public ChessGame getGame() {
        return game;
    }
}
