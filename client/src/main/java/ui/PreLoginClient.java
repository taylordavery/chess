
package ui;

import java.util.Arrays;

import exception.ResponseException;
import ui.websocket.NotificationHandler;
import serverfacadepackage.ServerFacade;
import ui.websocket.WebSocketFacade;

public class PreLoginClient implements Client{
//    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;

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
        if (params.length == 3) {
//            ws = new WebSocketFacade(serverUrl, notificationHandler);
//            ws.enterPetShop(visitorName);
            var auth = server.register(params[0], params[1], params[2]);
            System.out.printf("Account created.\nYou are signed in as %s.\n", params[0]);
            new Repl(new PostLoginClient(serverUrl, auth)).run();
            this.help();
        } else {
            throw new ResponseException(400, "Expected: <username> <password> <email>");
        }
        return this.help();
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
//            ws = new WebSocketFacade(serverUrl, notificationHandler);
//            ws.enterPetShop(visitorName);
            var auth = server.login(params[0], params[1]);
            System.out.printf("You signed in as %s.", params[0]);
            new Repl(new PostLoginClient(serverUrl, auth)).run();
        } else {
        throw new ResponseException(400, "Expected: <username> <password>");
        }
        return this.help();
    }

    public String help() {
        return """
                - register <username> <password> <email>
                - login <username> <password>
                - quit
                - help
                """;
    }

    public String startMsg() {
        return "Welcome to Chess. Sign in to start.";
    }
}
