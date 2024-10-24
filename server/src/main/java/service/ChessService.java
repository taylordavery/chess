package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;

import java.util.Collection;
import java.util.UUID;

public class ChessService {
    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

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

    public int createGame(AuthData authToken, String gameName) throws DataAccessException {
        return dataAccess.createGame(authToken, gameName);
    }

    public void  joinGame(AuthData authToken, String playerColor, int gameID) throws DataAccessException {
        dataAccess.joinGame(authToken, playerColor, gameID);
    }
}
