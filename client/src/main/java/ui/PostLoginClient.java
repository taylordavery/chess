
package ui;

import exception.ResponseException;
import model.AuthData;
import server.ServerFacade;

import java.util.Arrays;
//import client.websocket.WebSocketFacade;

public class PostLoginClient implements Client{
//    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final AuthData auth;
    //    private final NotificationHandler notificationHandler;
//    private WebSocketFacade ws;
//    private State state = State.SIGNEDOUT;

    public PostLoginClient(String serverUrl, AuthData auth) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.auth = auth;
        //        this.notificationHandler = notificationHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "clear" -> clear();
                case "logout" -> logout();
                case "list" -> listGames();
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "%quit%" -> "%quit%";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String joinGame(String[] params) throws ResponseException {
        if (params.length > 1) {
            server.joinGame(auth.authToken(), Integer.parseInt(params[0]), params[1]);
            return String.format("You joined game %s as %s", params[0], params[1]);
        } else {
            server.joinGame(auth.authToken(), Integer.parseInt(params[0]), null);
            return String.format("You joined game %s as Observer", params[0]);
        }
    }

    private String createGame(String[] params) throws ResponseException {
        var gameID = server.createGame(auth.authToken(), params[0]);
        return String.format("You created a chess game named %s. GameID: %s", params[0], gameID);
    }

    private String listGames() throws ResponseException {
        var gamesList = Arrays.toString(server.listGames(auth.authToken()));
        System.out.println(gamesList); // For debugging
        return gamesList;
    }

    private String logout() throws ResponseException {
        server.logout(auth.authToken());
        System.out.println("You logged out.");
        return "%quit%";
    }

    public String clear() throws ResponseException {
        server.clear();
        return "Database has been cleared.";
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 1) {
//            ws = new WebSocketFacade(serverUrl, notificationHandler);
//            ws.enterPetShop(visitorName);
            server.register(params[0], params[1], params[2]);
            System.out.printf("Account created.\nYou are signed in as %s.", params[0]);
            new Repl(new PostLoginClient(serverUrl, auth)).run();
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String help() {
        return """
                - clear
                - logout
                - list
                - create <gameName>
                - join <gameID>
                """;
    }

    public String startMsg() {
        return "";
    }
}
