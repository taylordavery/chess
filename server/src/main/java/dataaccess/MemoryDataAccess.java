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
    // Sample data storage
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, GameData> games = new HashMap<>();
    private final Map<UUID, String> activeSessions = new HashMap<>(); // authToken to username mapping

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        games.clear();
        activeSessions.clear();
    }

    @Override
    public AuthData register(String username, String password, String email) throws DataAccessException {
        // Check if the user already exists
        if (users.containsKey(username)) {
            throw new DataAccessException("User already exists");
        }
        // Store the user and create an AuthData object
        users.put(username, new UserData(username, password, email));
        AuthData auth = new AuthData(UUID.randomUUID(), username);
        activeSessions.put(auth.authToken(), auth.username());
        return auth;
    }

    @Override
    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = users.get(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new DataAccessException("Invalid username or password");
        }
        AuthData auth = new AuthData(UUID.randomUUID(), username);
        activeSessions.put(auth.authToken(), auth.username());
        return auth;
    }

    @Override
    public void logout(UUID authToken) throws DataAccessException {
        if (!activeSessions.containsKey(authToken)) {
            throw new DataAccessException("Invalid auth token");
        }
        activeSessions.remove(authToken);
    }

    @Override
    public Collection<GameData> listGames(UUID authToken) throws DataAccessException {
        if (!activeSessions.containsKey(authToken)) {
            throw new DataAccessException("Invalid auth token");
        }
        return games.values(); // Return all games
    }

    @Override
    public int createGame(UUID authToken, String gameName) throws DataAccessException {
        if (!activeSessions.containsKey(authToken)) {
            throw new DataAccessException("Invalid auth token");
        }
        int gameID = games.size() + 1; // Generate a simple game ID
        games.put("game" + gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }

    @Override
    public void joinGame(UUID authToken, String playerColor, int gameID) throws DataAccessException {
        if (!activeSessions.containsKey(authToken)) {
            throw new DataAccessException("Invalid auth token");
        }

        // Check if the game exists
        GameData game = games.get("game" + gameID);
        if (game == null) {
            throw new DataAccessException("Game does not exist");
        }

        // Check if the color is already taken
        try {
            if (game.isColorTaken(playerColor)) {
                throw new DataAccessException("Color is already taken");
            }
        } catch (Exception e) {
            throw new DataAccessException("Invalid color");
        }

        // Get the username associated with the auth token
        String username = activeSessions.get(authToken);

        // Add the player to the game
        game.addPlayer(playerColor, username);

        // Add player to game (you'll need to implement GameData to support this)
        // Example: games.get("game" + gameID).addPlayer(authToken, playerColor);
    }
}
