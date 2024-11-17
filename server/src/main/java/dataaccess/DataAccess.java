package dataaccess;

import chess.ChessGame;
import model.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public interface DataAccess {
    void clear() throws DataAccessException, SQLException;

    AuthData register(String username, String password, String email) throws DataAccessException, SQLException;

    AuthData login(String username, String password) throws DataAccessException;

    void logout(UUID authToken) throws DataAccessException;

    Collection<GameData> listGames(UUID authToken) throws DataAccessException, SQLException;

    int createGame(UUID authToken, String gameName) throws DataAccessException, SQLException;

    void  joinGame(UUID authToken, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException, SQLException;

    GameData getGame(UUID authToken, int gameID) throws SQLException, DataAccessException;
}