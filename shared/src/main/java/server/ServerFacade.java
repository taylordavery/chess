package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.util.*;
import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        var path = "/user";

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("password", password);
        userData.put("email", email);

        return this.makeRequest("POST", path, userData, AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException {
        var path = "/session";
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("password", password);
        return this.makeRequest("POST", path, userData, AuthData.class);
    }

    public void logout(UUID authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, authToken, null);
    }

    public GameData[] listGames(UUID authToken) throws ResponseException {
        var path = "/game";
        record listGameDataResponse(GameData[] games) {}
        var response = this.makeRequest("GET", path, null, listGameDataResponse.class);
        return response.games;
    }

    public int createGame(UUID authToken, String gameName) throws ResponseException {
        var path = "/game";
        Map<String, Object> userData = new HashMap<>();
        userData.put("authToken", authToken);
        userData.put("gameName", gameName);
        return this.makeRequest("POST", path, userData, int.class);
    }

    public void  joinGame(UUID authToken, String playerColor, int gameID) throws ResponseException {
        var path = "/game";
        Map<String, Object> userData = new HashMap<>();
        userData.put("authToken", authToken);
        userData.put("playerColor", playerColor);
        userData.put("gameID", gameID);
        this.makeRequest("PUT", path, userData, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

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

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
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
}