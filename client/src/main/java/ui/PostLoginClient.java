
package ui;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import server.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;
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
        server.createGame(auth.authToken(), params[0]);
        return String.format("You created a chess game named %s.", params[0]);
    }

    private String listGames() throws ResponseException {
        GameData[] games = server.listGames(auth.authToken());
        StringBuilder result = new StringBuilder();

        // Determine column widths dynamically
        int maxGameNameLength = Arrays.stream(games)
                .mapToInt(game -> game.getGameName().length())
                .max()
                .orElse(10);
        int usernameColumnWidth = 10; // Fixed width for usernames

        // Format each game entry
        for (int i = 0; i < games.length; i++) {
            var white = games[i].getWhiteUsername() != null ? games[i].getWhiteUsername() : "";
            var black = games[i].getBlackUsername() != null ? games[i].getBlackUsername() : "";

            // Adjust usernames for the column width
            white = formatText(white, usernameColumnWidth);
            black = formatText(black, usernameColumnWidth);

            result.append(String.format(
                    "%-3d %-"+ maxGameNameLength +"s %s[%-"+ usernameColumnWidth +"s]%s %s[%-"+ usernameColumnWidth +"s]%s%n",
                    i + 1,                                   // Row number
                    games[i].getGameName(),                 // Game name
                    SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK, white, RESET_TEXT_COLOR + RESET_BG_COLOR, // White username
                    SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE, black, RESET_TEXT_COLOR + RESET_BG_COLOR  // Black username
            ));
        }

        return result.toString();
    }

    // Helper method to format text for the column
    private static String formatText(String text, int width) {
        if (text.length() > width) {
            // Truncate
            return text.substring(0, width);
        }
        // Center text within the column
        int padding = width - text.length();
        int padStart = padding / 2;
        int padEnd = padding - padStart;
        return " ".repeat(padStart) + text + " ".repeat(padEnd);
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
