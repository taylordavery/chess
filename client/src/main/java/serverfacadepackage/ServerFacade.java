package serverfacadepackage;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        var path = "/user";

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("password", password);
        userData.put("email", email);

        return this.makeRequest("POST", path, userData, AuthData.class, null);
    }

    public AuthData login(String username, String password) throws ResponseException {
        var path = "/session";
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("password", password);
        return this.makeRequest("POST", path, userData, AuthData.class, null);
    }

    public void logout(UUID authToken) throws ResponseException {
        var path = "/session";
        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", authToken.toString());
        this.makeRequest("DELETE", path, null, null, headers);
    }


    public GameData[] listGames(UUID authToken) throws ResponseException {
        var path = "/game";
        record ListGameDataResponse(GameData[] games) {}
        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", authToken.toString());
        var response = this.makeRequest("GET", path, null, ListGameDataResponse.class, headers);
        return response.games;
    }

    public void createGame(UUID authToken, String gameName) throws ResponseException {
        var path = "/game";
        Map<String, Object> body = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", authToken.toString());
        body.put("gameName", gameName);
        class GameCreationResponse {
            int gameId;
        }

        var response = this.makeRequest("POST", path, body, GameCreationResponse.class, headers);
//        return response.gameId;
    }

    public void joinGame(UUID authToken, ChessGame.TeamColor playerColor, int gameID) throws ResponseException {
        var path = "/game";
        Map<String, Object> userData = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", authToken.toString());
        userData.put("playerColor", playerColor);
        userData.put("gameID", gameID);
        this.makeRequest("PUT", path, userData, null, headers);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T>
            responseClass, Map<String, String> headers) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            writeHeaders(headers, http);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static void writeHeaders(Map<String, String> headers, HttpURLConnection http) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                http.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }


    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        var msg = http.getResponseMessage();
        if (!isSuccessful(status)) {
            msg = "Error, try again.";
            throw new ResponseException(status, msg);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    public GameData getGame(UUID authToken, int gameID) throws ResponseException {
        var path = "/board";
        Map<String, Object> body = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", authToken.toString());
        body.put("gameID", gameID);
        return this.makeRequest("GET", path, body, GameData.class, headers);
    }

    public void observeGame(UUID authToken, int gameID) throws ResponseException {
        var path = "/observe";
        Map<String, Object> userData = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", authToken.toString());
        userData.put("gameID", gameID);
        this.makeRequest("PUT", path, userData, null, headers);
    }
}