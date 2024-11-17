package dataaccess;

import com.google.gson.Gson;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
        statement = "TRUNCATE games";
        executeUpdate(statement);
        statement = "TRUNCATE activeSessions";
        executeUpdate(statement);
    }

    @Override
    public AuthData register(String username, String password, String email) throws DataAccessException, SQLException {
        if (username == null || password == null || email == null) {
            throw new DataAccessException("Error: missing required field");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        UserData user = new UserData(username, hashedPassword, email);
        AuthData auth;

        try (var conn = DatabaseManager.getConnection();
             var checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?")) {

            checkStmt.setString(1, username);
            try (var rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new DataAccessException("Error: already taken");
                }
            }

            String insertUserSql = "INSERT INTO users (username, password, email, json) VALUES (?, ?, ?, ?)";
            try (var ps = conn.prepareStatement(insertUserSql)) {
                String json = new Gson().toJson(user);
                ps.setString(1, username);
                ps.setString(2, hashedPassword);
                ps.setString(3, email);
                ps.setString(4, json);
                ps.executeUpdate();
            }

            auth = new AuthData(UUID.randomUUID(), username);
            String insertSessionSql = "INSERT INTO activeSessions (authToken, username) VALUES (?, ?)";
            try (var ps = conn.prepareStatement(insertSessionSql)) {
                ps.setString(1, auth.authToken().toString());
                ps.setString(2, username);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return auth;
    }

    @Override
    public AuthData login(String username, String password) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {

            stmt.setString(1, username);
            try (var rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    throw new DataAccessException("Error: unauthorized");
                }

                // Retrieve the hashed password from the database
                String storedHashedPassword = rs.getString("password");

                // Verify the provided password against the stored hash
                if (!BCrypt.checkpw(password, storedHashedPassword)) {
                    throw new DataAccessException("Error: unauthorized");
                }
            }

            AuthData auth = new AuthData(UUID.randomUUID(), username);
            String insertSessionSql = "INSERT INTO activeSessions (authToken, username) VALUES (?, ?)";
            try (var ps = conn.prepareStatement(insertSessionSql)) {
                ps.setString(1, auth.authToken().toString());
                ps.setString(2, username);
                ps.executeUpdate();
            }

            return auth;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void logout(UUID authToken) throws DataAccessException {

        System.out.println(authToken.toString());
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("DELETE FROM activeSessions WHERE authToken = ?")) {
            ps.setString(1, authToken.toString());
            if (ps.executeUpdate() == 0) {
                throw new DataAccessException("Error: unauthorized");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public Collection<GameData> listGames(UUID authToken) throws DataAccessException, SQLException {
        if (notValidSession(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("SELECT json FROM games")) {

            try (var rs = ps.executeQuery()) {
                Collection<GameData> gamesList = new ArrayList<>();
                while (rs.next()) {
                    String json = rs.getString("json");
                    gamesList.add(new Gson().fromJson(json, GameData.class));
                }
                return gamesList;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public int createGame(UUID authToken, String gameName) throws DataAccessException, SQLException {
        if (notValidSession(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (gameName == null) {
            throw new DataAccessException("Error: missing required field");
        }

        GameData gameData = new GameData(null, null, gameName, new ChessGame());
        String json = new Gson().toJson(gameData);

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("INSERT INTO games (username, gameName, game, json) VALUES (?, ?, ?, ?)", RETURN_GENERATED_KEYS)) {

            String username = getUsernameByAuthToken(authToken);
            ps.setString(1, username);
            ps.setString(2, gameName);
            ps.setString(3, "");
            ps.setString(4, json);
            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int gameID = rs.getInt(1);

                    // update JSON with correct gameID
                    String updateQuery = "UPDATE games SET json = JSON_SET(json, '$.gameID', ?) WHERE gameID = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setInt(1, gameID);
                    updateStmt.setInt(2, gameID);
                    updateStmt.executeUpdate();
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        throw new DataAccessException("I don't know what's wrong.");
//        return 0;
    }

    @Override
    public void joinGame(UUID authToken, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException, SQLException {
        if (notValidSession(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }

        String username = getUsernameByAuthToken(authToken);

        try (var conn = DatabaseManager.getConnection();
             var selectGame = conn.prepareStatement("SELECT json FROM games WHERE gameID = ?");
             var updateGame = conn.prepareStatement("UPDATE games SET json = ? WHERE gameID = ?")) {

            selectGame.setInt(1, gameID);
            try (var rs = selectGame.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Error: game not found");
                }

                GameData game = new Gson().fromJson(rs.getString("json"), GameData.class);

                if (playerColor != null) {
                    if (!playerColor.equals(ChessGame.TeamColor.WHITE)) {
                        if (!playerColor.equals(ChessGame.TeamColor.BLACK)) {
                            throw new DataAccessException("Error: bad request");
                        }
                    }
                    if (game.isColorTaken(playerColor)) {
                        throw new DataAccessException("Error: already taken");
                    }
                    game.addPlayer(playerColor, username);
                } //else {
                    //throw new DataAccessException("Error: bad request");
                //}

                updateGame.setString(1, new Gson().toJson(game));
                updateGame.setInt(2, gameID);
                updateGame.executeUpdate();
            } catch (Exception e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void observeGame(UUID authToken, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException, SQLException {
        if (notValidSession(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }

        String username = getUsernameByAuthToken(authToken);

        try (var conn = DatabaseManager.getConnection();
             var selectGame = conn.prepareStatement("SELECT json FROM games WHERE gameID = ?");
             var updateGame = conn.prepareStatement("UPDATE games SET json = ? WHERE gameID = ?")) {

            selectGame.setInt(1, gameID);
            try (var rs = selectGame.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Error: game not found");
                }

                GameData game = new Gson().fromJson(rs.getString("json"), GameData.class);

                if (playerColor != null) {
                    if (!playerColor.equals(ChessGame.TeamColor.WHITE)) {
                        if (!playerColor.equals(ChessGame.TeamColor.BLACK)) {
                            throw new DataAccessException("Error: bad request");
                        }
                    }
                    if (game.isColorTaken(playerColor)) {
                        throw new DataAccessException("Error: already taken");
                    }
                    game.addPlayer(playerColor, username);
                } else {
                    throw new DataAccessException("Error: bad request");
                }

                updateGame.setString(1, new Gson().toJson(game));
                updateGame.setInt(2, gameID);
                updateGame.executeUpdate();
            } catch (Exception e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @param authToken
     * @param gameID
     * @return
     */
    @Override
    public GameData getGame(UUID authToken, int gameID) throws SQLException, DataAccessException {
        if (notValidSession(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }

        String username = getUsernameByAuthToken(authToken);

        try (var conn = DatabaseManager.getConnection();
             var selectGame = conn.prepareStatement("SELECT json FROM games WHERE gameID = ?");
             var updateGame = conn.prepareStatement("UPDATE games SET json = ? WHERE gameID = ?")) {

            try (var rs = selectGame.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Error: game not found");
                }

                return new Gson().fromJson(rs.getString("json"), GameData.class);

            } catch (Exception e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

// Helper methods

    private boolean notValidSession(UUID authToken) throws SQLException, DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Auth token cannot be null");
        }

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("SELECT COUNT(*) FROM activeSessions WHERE authToken = ?")) {

            ps.setString(1, authToken.toString());
            try (var rs = ps.executeQuery()) {
                return !rs.next() || rs.getInt(1) <= 0;
            }
        }
    }


    private String getUsernameByAuthToken(UUID authToken) throws SQLException, DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("SELECT username FROM activeSessions WHERE authToken = ?")) {

            ps.setString(1, authToken.toString());
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                } else {
                    throw new DataAccessException("Error: unauthorized");
                }
            }
        }
    }


    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case UserData p -> ps.setString(i + 1, p.toString());
                        case GameData p -> ps.setString(i + 1, p.toString());
                        case AuthData p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default -> throw new IllegalStateException("Unexpected value: " + param);
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }

            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private final String[] createStatements = {

    """
    CREATE TABLE IF NOT EXISTS users (
      `username` VARCHAR(255) NOT NULL,
      `password` VARCHAR(255) NOT NULL,
      `email` VARCHAR(255) NOT NULL,
      `json` TEXT DEFAULT NULL,
      PRIMARY KEY (`username`),
      INDEX (`username`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """,



    """
    CREATE TABLE IF NOT EXISTS games (
      `gameID` INT NOT NULL AUTO_INCREMENT,
      `username` VARCHAR(255) NOT NULL,
      `gameName` VARCHAR(255) NOT NULL,
      `game` VARCHAR(255) NOT NULL,
      `json` TEXT DEFAULT NULL,
      PRIMARY KEY (`gameID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """,


    """
    CREATE TABLE IF NOT EXISTS activeSessions (
      `username` VARCHAR(255) NOT NULL,
      `authToken` VARCHAR(255) NOT NULL,
      PRIMARY KEY (`authToken`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
