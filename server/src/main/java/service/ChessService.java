package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public class ChessService {
    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) throws DataAccessException{
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException, SQLException {
        dataAccess.clear();
    }

    public AuthData register(String username, String password, String email) throws DataAccessException, SQLException {
        return dataAccess.register(username, password, email);
    }

    public AuthData login(String username, String password) throws DataAccessException {
        return dataAccess.login(username, password);
    }

    public void logout(UUID authToken) throws DataAccessException {
        dataAccess.logout(authToken);
    }

    public Collection<GameData> listGames(UUID authToken) throws DataAccessException, SQLException {
        return dataAccess.listGames(authToken);
    }

    public int createGame(UUID authToken, String gameName) throws DataAccessException, SQLException {
        return dataAccess.createGame(authToken, gameName);
    }

    public void  joinGame(UUID authToken, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException, SQLException {
        dataAccess.joinGame(authToken, playerColor, gameID);
    }

    public GameData getGame(UUID authToken, int gameID) throws SQLException, DataAccessException {
        return dataAccess.getGame(authToken, gameID);
    }
}
