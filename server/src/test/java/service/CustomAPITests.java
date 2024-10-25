package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.UUID;

public class CustomAPITests {
    private final DataAccess dataAccess;

    public CustomAPITests() {
        this.dataAccess = new MemoryDataAccess();
    }

    @Test
    public void clear() throws DataAccessException {
        dataAccess.clear();
    }

    public AuthData register(String username, String password, String email) throws DataAccessException {
        return dataAccess.register(username, password, email);
    }

    public AuthData login(String username, String password) throws DataAccessException {
        return dataAccess.login(username, password);
    }

    public void logout(UUID authToken) throws DataAccessException {
        dataAccess.logout(authToken);
    }

    public Collection<GameData> listGames(UUID authToken) throws DataAccessException {
        return dataAccess.listGames(authToken);
    }

    public int createGame(UUID authToken, String gameName) throws DataAccessException {
        return dataAccess.createGame(authToken, gameName);
    }

    public void  joinGame(UUID authToken, String playerColor, int gameID) throws DataAccessException {
        dataAccess.joinGame(authToken, playerColor, gameID);
    }
}
