package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, GameData> games = new HashMap<>();
    private final Map<UUID, String> activeSessions = new HashMap<>(); // authToken to username mapping

    @Override
    public void clear() throws DataAccessException {
        try {
            users.clear();
            games.clear();
            activeSessions.clear();
        } catch (Exception e) {
            throw new DataAccessException("Unable to clear database");
        }
    }

    @Override
    public AuthData register(String username, String password, String email) throws DataAccessException {
        if (username == null || password == null || email == null) {
            throw new DataAccessException("Error: missing required field");
        }
        if (users.containsKey(username)) {
            throw new DataAccessException("Error: already taken");
        }
        AuthData auth;
        try {
            users.put(username, new UserData(username, password, email));
            auth = new AuthData(UUID.randomUUID(), username);
            activeSessions.put(auth.authToken(), auth.username());
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return auth;
    }

    @Override
    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = users.get(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new DataAccessException("Error: unauthorized");
        }
        AuthData auth = new AuthData(UUID.randomUUID(), username);
        activeSessions.put(auth.authToken(), auth.username());
        return auth;
    }

    @Override
    public void logout(UUID authToken) throws DataAccessException {
        if (!activeSessions.containsKey(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }
        activeSessions.remove(authToken);
    }

    @Override
    public Collection<GameData> listGames(UUID authToken) throws DataAccessException {
        if (!activeSessions.containsKey(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }
        return games.values();
    }

    @Override
    public int createGame(UUID authToken, String gameName) throws DataAccessException {
        if (!activeSessions.containsKey(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }
        int gameID = games.size() + 1;
        games.put("game" + gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }

    @Override
    public void joinGame(UUID authToken, String playerColor, int gameID) throws DataAccessException {
        if (!activeSessions.containsKey(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }

        // Check if the game exists
        GameData game = games.get("game" + gameID);
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        // Check if the color is already taken
        try {
            game.isColorTaken(playerColor);
        } catch (Exception e) {
            throw new DataAccessException("Error: bad request");
        }

        try {
            if (game.isColorTaken(playerColor)) {
                throw new DataAccessException("Error: already taken");
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: already taken");
        }

        // Get the username associated with the auth token
        String username = activeSessions.get(authToken);

        // Add the player to the game
        game.addPlayer(playerColor, username);
    }
}
