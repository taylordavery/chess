package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class MemoryGoodAPITests {
    private final DataAccess dataAccess;
    private AuthData auth;
    private int gameID;

    public MemoryGoodAPITests() throws DataAccessException {
        this.dataAccess = new MemoryDataAccess();
    }

    @Test
    public void clear() throws DataAccessException, SQLException {
        dataAccess.clear();
    }

    @Test
    public void register() throws DataAccessException, SQLException {
        this.auth = dataAccess.register("username", "password", "email");
    }

    @Test
    public void login() throws DataAccessException, SQLException {
        this.auth = dataAccess.register("username", "password", "email");
        dataAccess.login("username", "password");
    }

    @Test
    public void logout() throws DataAccessException, SQLException {
        this.auth = dataAccess.register("username", "password", "email");
        dataAccess.logout(auth.authToken());
    }

    @Test
    public void listGames() throws DataAccessException, SQLException {
        this.auth = dataAccess.register("username", "password", "email");
        dataAccess.listGames(auth.authToken());
    }

    @Test
    public void createGame() throws DataAccessException, SQLException {
        this.auth = dataAccess.register("username", "password", "email");
        this.gameID = dataAccess.createGame(auth.authToken(), "gameName");
    }

    @Test
    public void joinGame() throws DataAccessException, SQLException {
        this.auth = dataAccess.register("username", "password", "email");
        this.gameID = dataAccess.createGame(auth.authToken(), "gameName");
        dataAccess.joinGame(auth.authToken(), "white", gameID);
    }
}
