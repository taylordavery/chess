package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.ChessService;

import spark.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.UUID;

public class Server {
    public ChessService service;
    public DataAccess dataAccess = new MemoryDataAccess();

    public Server() {;
        this.service = new ChessService(dataAccess);
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

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request req, Response res) throws DataAccessException {
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
            return new Gson().toJson(e.getMessage());
        }
        AuthData authData = null;
        try {
            authData = this.service.register(userData.username(), userData.password(), userData.email());
        } catch (DataAccessException e) {
            switch (e.getMessage()) {
                case "Error: bad request":
                    res.status(400);
                    break;
                case "Error: already taken":
                    res.status(403);
                    return new Gson().toJson(e.getMessage());
                default:
                    res.status(500);
                    return new Gson().toJson(e.getMessage());
            }
        }
        res.status(200);
        return new Gson().toJson(authData);
    }

    private Object login(Request request, Response response) throws DataAccessException {
        UserData userData = new Gson().fromJson(request.body(), UserData.class);
        AuthData authData = this.service.login(userData.username(), userData.password());
        response.status(200);
        return new Gson().toJson(authData);
    }

    private Object logout(Request request, Response response) throws DataAccessException {
        UUID authToken = new Gson().fromJson(request.headers("authorization"), UUID.class);
        this.service.logout(authToken);
        response.status(200);
        return "";
    }

    private Object listGames(Request request, Response response) throws DataAccessException {
        UUID authToken = new Gson().fromJson(request.headers("authorization"), UUID.class);
        Collection<GameData> games = this.service.listGames(authToken);
        response.status(200);
        return new Gson().toJson(games);
    }

    private Object createGame(Request request, Response response) throws DataAccessException {
        UUID authToken = new Gson().fromJson(request.headers("authorization"), UUID.class);
        JsonObject jsonBody = JsonParser.parseString(request.body()).getAsJsonObject();
        String gameName = jsonBody.get("gameName").getAsString();
        int gameID = this.service.createGame(authToken, gameName);
        response.status(200);
        return gameID;
    }

    private Object joinGame(Request request, Response response) throws DataAccessException {
        UUID authToken = new Gson().fromJson(request.headers("authorization"), UUID.class);
        JsonObject body = JsonParser.parseString(request.body()).getAsJsonObject();
        String playerColor = body.get("playerColor").getAsString();
        int gameID = body.get("gameID").getAsInt();
        this.service.joinGame(authToken, playerColor, gameID);
        response.status(200);
        return "";
    }
}