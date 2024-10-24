package dataaccess;

import model.*;

import java.util.Collection;
import java.util.UUID;

public interface DataAccess {
    void clear() throws DataAccessException;

    AuthData register(String username, String password, String email) throws DataAccessException;

    AuthData login(String username, String password) throws DataAccessException;

    void logout(UUID authToken) throws DataAccessException;

    Collection<GameData> listGames(UUID authToken) throws DataAccessException;

    int createGame(UUID authToken, String gameName) throws DataAccessException;

    void  joinGame(UUID authToken, String playerColor, int gameID) throws DataAccessException;
}