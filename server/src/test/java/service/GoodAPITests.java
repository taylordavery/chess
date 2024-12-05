package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;

public class GoodAPITests {
    private final DataAccess dataAccess;
    private AuthData auth;
    private int gameID;

    public GoodAPITests() throws DataAccessException {
        this.dataAccess = new MySqlDataAccess();
    }

    @Test
    public void clear() throws DataAccessException, SQLException {
        dataAccess.clear();
    }

    @Test
    public void register() throws DataAccessException, SQLException {
        this.clear();
        this.auth = dataAccess.register("username", "password", "email");
        dataAccess.clear();
    }

    @Test
    public void login() throws DataAccessException, SQLException {
        this.auth = dataAccess.register("username", "password", "email");
        dataAccess.login("username", "password");
        dataAccess.clear();
    }

    @Test
    public void logout() throws DataAccessException, SQLException {
        this.clear();
        this.auth = dataAccess.register("username", "password", "email");
        dataAccess.logout(auth.authToken());
        dataAccess.clear();
    }

    @Test
    public void listGames() throws DataAccessException, SQLException {
        this.clear();
        this.auth = dataAccess.register("username", "password", "email");
        dataAccess.listGames(auth.authToken());
        dataAccess.clear();
    }

    @Test
    public void createGame() throws DataAccessException, SQLException {
        this.auth = dataAccess.register("username", "password", "email");
        this.gameID = dataAccess.createGame(auth.authToken(), "gameName");
        dataAccess.clear();
    }

    @Test
    public void joinGame() throws DataAccessException, SQLException {
        this.clear();
        this.auth = dataAccess.register("username", "password", "email");
        this.gameID = dataAccess.createGame(auth.authToken(), "gameName");
        dataAccess.joinGame(auth.authToken(), ChessGame.TeamColor.WHITE, gameID);
        dataAccess.clear();
    }
}
