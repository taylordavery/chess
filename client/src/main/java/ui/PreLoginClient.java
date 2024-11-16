
package ui;

import java.util.Arrays;

import com.google.gson.Gson;
import exception.ResponseException;
//import client.websocket.NotificationHandler;
import server.ServerFacade;
//import client.websocket.WebSocketFacade;

public class PreLoginClient implements Client{
//    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    //    private final NotificationHandler notificationHandler;
//    private WebSocketFacade ws;
//    private State state = State.SIGNEDOUT;

    public PreLoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        //        this.notificationHandler = notificationHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "clear" -> clear();
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "%quit%";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String clear() throws ResponseException {
        server.clear();
        return "Database has been cleared.";
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 1) {
//            ws = new WebSocketFacade(serverUrl, notificationHandler);
//            ws.enterPetShop(visitorName);
            var auth = server.register(params[0], params[1], params[2]);
            System.out.printf("Account created.\nYou are signed in as %s.\n", params[0]);
            new Repl(new PostLoginClient(serverUrl, auth)).run();
        } else {
        throw new ResponseException(400, "Expected: <username> <password> <email>");
        }
        return "";
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 1) {
//            ws = new WebSocketFacade(serverUrl, notificationHandler);
//            ws.enterPetShop(visitorName);
            var auth = server.login(params[0], params[1]);
            System.out.printf("You signed in as %s.", params[0]);
            new Repl(new PostLoginClient(serverUrl, auth)).run();
        } else {
        throw new ResponseException(400, "Expected: <username> <password>");
    }
        return "";
    }

    public String help() {
        return """
                - clear
                - register <username> <password> <email>
                - login <username> <password>
                - quit
                """;
    }

    public String startMsg() {
        return "Welcome to Chess. Sign in to start.";
    }
}
