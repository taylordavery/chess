package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.ChessService;
import spark.*;

import java.io.Reader;
import java.util.Collection;
import java.util.UUID;

public class Server {
    public ChessService service;

    public Server() {;
        this.service = null;
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

        // Initialize the server
        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        this.service.clear(); // Call the method to clear the database
        res.status(200); // Set the response status to 200
        return "";
    }

    private Object register(Request req, Response res) throws DataAccessException {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData = this.service.register(userData.username(), userData.password(), userData.email());
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
        String gameName = jsonBody.get("gameName").getAsString(); // Adjust according to your JSON structure
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







//package server;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
//import dataaccess.DataAccessException;
//import model.UserData;
//import service.*;
//import spark.*;
//
//public class Server {
//
//    private final UserService service;
//    private static final Gson gson = new Gson();
//
//    public Server(UserService service) {
//        this.service = service;
//    }
//
//    public int run(int desiredPort) {
//        Spark.port(desiredPort);
//
//        Spark.staticFiles.location("web");
//
//        // Register your endpoints and handle exceptions here.
//        Spark.post("/user", (req, res) -> registerUser(req, res));
//
//        //This line initializes the server and can be removed once you have a functioning endpoint
//        Spark.init();
//
//        Spark.awaitInitialization();
//        return Spark.port();
//    }
//
//    private Object registerUser(Request req, Response res) {
//        UserData user;
//        try {
//            user = gson.fromJson(req.body(), UserData.class);
//        } catch (JsonSyntaxException e) {
//            res.status(400);
//            return gson.toJson(new DataAccessException("Error: bad request"));
//        }
//
//        // Here, you would add your logic to check if the username/email is already taken
//        // and save the new user. For demonstration, I'll use dummy conditions.
//
//        if (user.getUsername().isEmpty() || user.getPassword().isEmpty() || user.getEmail().isEmpty()) {
//            res.status(400);
//            return gson.toJson(new ErrorResponse("Error: bad request"));
//        }
//
//        // Dummy logic to check if the username is already taken
//        if (isUsernameTaken(user.getUsername())) {
//            res.status(403);
//            return gson.toJson(new ErrorResponse("Error: already taken"));
//        }
//
//        // Simulate successful registration and return an authToken
//        String authToken = generateAuthToken(user);
//        res.status(200);
//        return gson.toJson(new SuccessResponse(user.getUsername(), authToken));
//    }
//
//    // Dummy method to check if a username is already taken
//    private boolean isUsernameTaken(String username) {
//        // Implement actual logic to check against a database or data store
//        return false; // Assume not taken for this example
//    }
//
//    // Dummy method to generate an auth token
//    private String generateAuthToken(User user) {
//        // Implement your logic to generate an auth token
//        return "dummy_auth_token"; // Return a dummy token for this example
//    }
//
//}
//
//    public void stop() {
//        Spark.stop();
//        Spark.awaitStop();
//    }
//}
