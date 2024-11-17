
package ui;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import ServerFacadePackage.ServerFacade;

import java.util.Arrays;
//import client.websocket.WebSocketFacade;

public class GameplayClient implements Client{
//    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final AuthData auth;
    private final GameData game;
    //    private final NotificationHandler notificationHandler;
//    private WebSocketFacade ws;
//    private State state = State.SIGNEDOUT;

    public GameplayClient(String serverUrl, AuthData auth, String gameID) throws ResponseException {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.auth = auth;
        //        this.notificationHandler = notificationHandler;
        this.game = null;  // server.getGame(auth.authToken(), Integer.parseInt(gameID));
    }

    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "move" -> move(params);
            case "chat" -> chat(params);
            case "resign" -> resign();
            case "leave" -> leave();
            case "%quit%" -> "%quit%";
            default -> help();
        };
    }

    public String help() {
        return """
                - move <start> <end>
                - chat <message>
                - resign
                - leave
                """;
    }

    public String startMsg() {
//        assert game != null;
//        return game.toString();
        return "";
    }

    public String move(String[] params) {
        System.out.print("Not implemented yet.");
        return "";
    }

    public String chat(String[] params) {
        System.out.print("Not implemented yet.");
        return "";
    }

    public String resign() {
        System.out.print("Not implemented yet.");
        return "";
    }

    public String leave() {
        assert game != null;
//        System.out.printf("You left %s.", game.getGameName());
        System.out.print("You left the game.");
        return "%quit%";
    }

}
