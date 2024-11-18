
package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import serverfacadepackage.ServerFacade;

import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;
//import client.websocket.WebSocketFacade;

public class PostLoginClient implements Client{
//    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final AuthData auth;

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
                case "logout" -> logout();
                case "list" -> listGames();
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "%quit%" -> "%quit%";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String observeGame(String[] params) throws ResponseException {
        if (params.length == 1) {
            var game = new ChessGame().getBoard().toString();
            server.observeGame(auth.authToken(), Integer.parseInt(params[0]));
            System.out.printf("You joined game %s as Observer\n", params[0]);
            new Repl(new GameplayClient(serverUrl, auth, game)).run(); //I'll need to add a game parameter here or something.
        }
        return this.help();
    }

    private String joinGame(String[] params) throws ResponseException {
        if (params.length > 1) {
            var game = new ChessGame().getBoard().toString();

            var teamColor = Objects.equals(params[1], "white") ? ChessGame.TeamColor.WHITE :
                    Objects.equals(params[1], "black") ? ChessGame.TeamColor.BLACK : null;

            server.joinGame(auth.authToken(), teamColor, Integer.parseInt(params[0]));
            System.out.printf("You joined game %s as %s\n", params[0], params[1]);
            new Repl(new GameplayClient(serverUrl, auth, game)).run(); //I'll need to add a game parameter here or something.
        }
        return this.help();
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

    public String help() {
        return """
                - logout
                - list
                - create <gameName>
                - join <gameNumber> <white/black>
                - observe <gameNumber>
                - help
                """;
    }

    public String startMsg() {
        return "";
    }
}
