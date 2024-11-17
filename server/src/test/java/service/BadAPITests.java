package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BadAPITests {
    private final DataAccess dataAccess;
    private AuthData auth;
    private int gameID;

    public BadAPITests() throws DataAccessException {
//        this.dataAccess = new MemoryDataAccess();
        this.dataAccess = new MySqlDataAccess();
    }

    @Test
    public void register() {
        assertThrows(DataAccessException.class, () -> {
            this.auth = dataAccess.register(null, "password", "email");
        }, "Expected register() to throw, but it didn't");
    }

    @Test
    public void login() {
        assertThrows(DataAccessException.class, () -> {
            this.auth = dataAccess.register("username", "password", "email");
            dataAccess.login("username", null);
        }, "Expected login() to throw, but it didn't");
    }

    @Test
    public void logout() {
        assertThrows(DataAccessException.class, () -> {
            this.auth = dataAccess.register("username", "password", "email");
            dataAccess.logout(null);
        }, "Expected logout() to throw, but it didn't");
    }

    @Test
    public void listGames() {
        assertThrows(DataAccessException.class, () -> {
            this.auth = dataAccess.register("username", "password", "email");
            dataAccess.listGames(null);
        }, "Expected listGames() to throw, but it didn't");
    }

    @Test
    public void createGame() {
        assertThrows(DataAccessException.class, () -> {
            this.auth = dataAccess.register("username", "password", "email");
            this.gameID = dataAccess.createGame(auth.authToken(), null);
        }, "Expected createGame() to throw, but it didn't");
    }

    @Test
    public void joinGame() {
        assertThrows(DataAccessException.class, () -> {
            this.auth = dataAccess.register("username", "password", "email");
            this.gameID = dataAccess.createGame(auth.authToken(), "gameName");
            dataAccess.joinGame(null, ChessGame.TeamColor.WHITE, gameID);
        }, "Expected joinGame() to throw, but it didn't");
    }
}
