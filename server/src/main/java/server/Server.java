package server;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.ChessService;

import spark.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.SQLException;
import java.util.*;

public class Server {
    public ChessService service;
    public DataAccess dataAccess;

    {
        try {
            dataAccess = new MySqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Server() {;
        try {
            this.service = new ChessService(dataAccess);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.put("/observe", this::observeGame);
        Spark.get("/board", this::getGame);

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object observeGame(Request request, Response response) {
        UUID authToken;
        try {
            authToken = new Gson().fromJson(request.headers("authorization"), UUID.class);
        } catch (Exception e) {
            response.status(401);
            Map<String, String> jsonResponse = new HashMap<>();
            jsonResponse.put("message", "Error: unauthorized");
            return new Gson().toJson(jsonResponse);
        }
        JsonObject body = JsonParser.parseString(request.body()).getAsJsonObject();


        int gameID;
        if (body.get("gameID") != null) {
            gameID = body.get("gameID").getAsInt();
        } else {
            response.status(400);
            Map<String, String> jsonResponse = new HashMap<>();
            jsonResponse.put("message", "Error: bad request");
            return new Gson().toJson(jsonResponse);
        }

        try {
            this.service.joinGame(authToken, null, gameID);
        } catch (Exception e) {
            // Map exception messages to status codes
            Map<String, Integer> statusCodes = new HashMap<>();
            statusCodes.put("Error: bad request", 400);
//            statusCodes.put("Name is null", 400);
            statusCodes.put("Error: unauthorized", 401);
            statusCodes.put("Error: already taken", 403);

            int statusCode = statusCodes.getOrDefault(e.getMessage(), 500);
            response.status(statusCode);

            // Return JSON error message
            Map<String, String> jsonResponse = new HashMap<>();
            jsonResponse.put("message", e.getMessage());

            return new Gson().toJson(jsonResponse);
        }

        // Success status
        response.status(200);
        return "";
    }

    private Object getGame(Request request, Response response) throws DataAccessException {
        UUID authToken;
        try {
            authToken = new Gson().fromJson(request.headers("authorization"), UUID.class);
        } catch (Exception e) {
            response.status(401);
            Map<String, String> jsonResponse = new HashMap<>();
            jsonResponse.put("message", "Error: unauthorized");
            return new Gson().toJson(jsonResponse);
        }
        JsonObject body = JsonParser.parseString(request.body()).getAsJsonObject();

        // Check if gameID is present and not null
        int gameID;
        if (body.get("gameID") != null) {
            gameID = body.get("gameID").getAsInt();
        } else {
            response.status(400);
            Map<String, String> jsonResponse = new HashMap<>();
            jsonResponse.put("message", "Error: bad request");
            return new Gson().toJson(jsonResponse);
        }

        try {
            this.service.getGame(authToken, gameID);
        } catch (Exception e) {
            // Map exception messages to status codes
            Map<String, Integer> statusCodes = new HashMap<>();
            statusCodes.put("Error: bad request", 400);
            statusCodes.put("Error: unauthorized", 401);
            statusCodes.put("Error: already taken", 403);

            int statusCode = statusCodes.getOrDefault(e.getMessage(), 500);
            response.status(statusCode);

            // Return JSON error message
            Map<String, String> jsonResponse = new HashMap<>();
            jsonResponse.put("message", e.getMessage());

            return new Gson().toJson(jsonResponse);
        }

        // Success status
        response.status(200);
        return response;
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request req, Response res) {
        try {
            this.service.clear();
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(e.getMessage());
        }
        res.status(200);
        return "";
    }

    private Object register(Request req, Response res) throws DataAccessException {
        UserData userData;
        try {
            userData = new Gson().fromJson(req.body(), UserData.class);
        } catch (Exception e) {
            res.status(500);
            Map<String, String> jsonResponse = new HashMap<>();
            jsonResponse.put("message", "Error");
            return new Gson().toJson(jsonResponse);
        }

        if (userData.username() == null || userData.username().isEmpty() ||
                userData.password() == null || userData.password().isEmpty() ||
                userData.email() == null || userData.email().isEmpty()) {
            res.status(400);
            Map<String, String> jsonResponse = new HashMap<>();
            jsonResponse.put("message", "Error: missing required field");
            return new Gson().toJson(jsonResponse);
        }

        AuthData authData;
        try {
            authData = this.service.register(userData.username(), userData.password(), userData.email());
        } catch (DataAccessException e) {
            Map<String, String> jsonResponse;
            switch (e.getMessage()) {
                case "Error: missing required field":
                case "Error: bad request":
                    res.status(400);
                    jsonResponse = new HashMap<>();
                    jsonResponse.put("message", e.getMessage());
                    return new Gson().toJson(jsonResponse);
                case "Error: already taken":
                    res.status(403);
                    jsonResponse = new HashMap<>();
                    jsonResponse.put("message", e.getMessage());
                    return new Gson().toJson(jsonResponse);
                default:
                    res.status(500);
                    jsonResponse = new HashMap<>();
                    jsonResponse.put("message", e.getMessage());
                    return new Gson().toJson(jsonResponse);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        res.status(200);
        return new Gson().toJson(authData);
    }

    private Object login(Request request, Response response) throws DataAccessException {
        UserData userData = new Gson().fromJson(request.body(), UserData.class);
        AuthData authData;
        try {
            authData = this.service.login(userData.username(), userData.password());
        } catch (Exception e) {
            if (e.getMessage().equals("Error: unauthorized")) {
                Map<String, String> jsonResponse = getStringStringMap(response, e);
                return new Gson().toJson(jsonResponse);
            }
            response.status(500);
            return new Gson().toJson(e.getMessage());
        }
        response.status(200);
        return new Gson().toJson(authData);
    }

    private Object logout(Request request, Response response) throws DataAccessException {
        UUID authToken = new Gson().fromJson(request.headers("authorization"), UUID.class);
        try {
            this.service.logout(authToken);
        } catch (Exception e) {
            if (e.getMessage().equals("Error: unauthorized")) {
                Map<String, String> jsonResponse = getStringStringMap(response, e);
                return new Gson().toJson(jsonResponse);
            }
        }
        response.status(200);
        return "";
    }

    private static Map<String, String> getStringStringMap(Response response, Exception e) {
        response.status(401);
        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("message", e.getMessage());
        return jsonResponse;
    }

    private Object listGames(Request request, Response response) throws DataAccessException {
        UUID authToken = new Gson().fromJson(request.headers("authorization"), UUID.class);
        Collection<GameData> games = List.of();
        try {
            games = this.service.listGames(authToken);
        } catch (DataAccessException g) {
            if (g.getMessage().equals("Error: unauthorized")) {
                Map<String, String> jsonResponse = getStringStringMap(response, g);
                return new Gson().toJson(jsonResponse);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        // Wrap the games list in a JSON object
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("games", games);

        response.status(200);
        return new Gson().toJson(responseBody);  // Return the games wrapped in a key
    }


    private Object createGame(Request request, Response response) throws DataAccessException {
        UUID authToken = new Gson().fromJson(request.headers("authorization"), UUID.class);
        JsonObject jsonBody = JsonParser.parseString(request.body()).getAsJsonObject();
        String gameName = jsonBody.get("gameName").getAsString();
        int gameID = 0;
        try {
            gameID = this.service.createGame(authToken, gameName);
        } catch (DataAccessException f) {
            if (f.getMessage().equals("Error: unauthorized")) {
                Map<String, String> jsonResponse = getStringStringMap(response, f);
                return new Gson().toJson(jsonResponse);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        response.status(200);
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("gameID", gameID);
        return new Gson().toJson(jsonResponse);
    }

    private Object joinGame(Request request, Response response) throws DataAccessException {
        UUID authToken;
        try {
            authToken = new Gson().fromJson(request.headers("authorization"), UUID.class);
        } catch (Exception e) {
            response.status(401);
            Map<String, String> jsonResponse = new HashMap<>();
            jsonResponse.put("message", "Error: unauthorized");
            return new Gson().toJson(jsonResponse);
        }
        JsonObject body = JsonParser.parseString(request.body()).getAsJsonObject();

        // Check if playerColor is present and not null
        ChessGame.TeamColor playerColor = null;
        if (body.has("playerColor") && !body.get("playerColor").isJsonNull()) {
            String colorString = body.get("playerColor").getAsString(); // Extract the string value
            playerColor = ChessGame.TeamColor.valueOf(colorString.toUpperCase()); // Convert to TeamColor (case-insensitive)
        } else {
            response.status(400);
            Map<String, String> jsonResponse = new HashMap<>();
            jsonResponse.put("message", "Error: bad request");
            return new Gson().toJson(jsonResponse);
        }


        int gameID;
        if (body.get("gameID") != null) {
            gameID = body.get("gameID").getAsInt();
        } else {
            response.status(400);
            Map<String, String> jsonResponse = new HashMap<>();
            jsonResponse.put("message", "Error: bad request");
            return new Gson().toJson(jsonResponse);
        }
        
        ChessGame.TeamColor playerTeamColor = null;
        
        if (Objects.equals(playerColor, "black")) {
            playerTeamColor = ChessGame.TeamColor.BLACK;
        }
        if (Objects.equals(playerColor, "white")) {
            playerTeamColor = ChessGame.TeamColor.WHITE;
        }

        try {
            this.service.joinGame(authToken, playerColor, gameID);
        } catch (Exception e) {
            // Map exception messages to status codes
            Map<String, Integer> statusCodes = new HashMap<>();
            statusCodes.put("Error: bad request", 400);
//            statusCodes.put("Name is null", 400);
            statusCodes.put("Error: unauthorized", 401);
            statusCodes.put("Error: already taken", 403);

            int statusCode = statusCodes.getOrDefault(e.getMessage(), 500);
            response.status(statusCode);

            // Return JSON error message
            Map<String, String> jsonResponse = new HashMap<>();
            jsonResponse.put("message", e.getMessage());

            return new Gson().toJson(jsonResponse);
        }

        // Success status
        response.status(200);
        return "";
    }

}