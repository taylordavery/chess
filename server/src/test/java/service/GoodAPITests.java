package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import org.junit.jupiter.api.Test;

public class GoodAPITests {
    private final DataAccess dataAccess;
    private AuthData auth;
    private int gameID;

    public GoodAPITests() {
//        this.dataAccess = new MemoryDataAccess();
        this.dataAccess = new DatabaseManager();
    }

    @Test
    public void clear() throws DataAccessException {
        dataAccess.clear();
    }

    @Test
    public void register() throws DataAccessException {
        this.auth = dataAccess.register("username", "password", "email");
    }

    @Test
    public void login() throws DataAccessException {
        this.auth = dataAccess.register("username", "password", "email");
        dataAccess.login("username", "password");
    }

    @Test
    public void logout() throws DataAccessException {
        this.auth = dataAccess.register("username", "password", "email");
        dataAccess.logout(auth.authToken());
    }

    @Test
    public void listGames() throws DataAccessException {
        this.auth = dataAccess.register("username", "password", "email");
        dataAccess.listGames(auth.authToken());
    }

    @Test
    public void createGame() throws DataAccessException {
        this.auth = dataAccess.register("username", "password", "email");
        this.gameID = dataAccess.createGame(auth.authToken(), "gameName");
    }

    @Test
    public void joinGame() throws DataAccessException {
        this.auth = dataAccess.register("username", "password", "email");
        this.gameID = dataAccess.createGame(auth.authToken(), "gameName");
        dataAccess.joinGame(auth.authToken(), "white", gameID);
    }
}
