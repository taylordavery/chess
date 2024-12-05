
package ui;

import chess.ChessGame;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import exception.ResponseException;
import model.AuthData;
import serverfacadepackage.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
//import client.websocket.WebSocketFacade;

public class GameplayClient implements Client{
    private final ServerFacade server;
    private final String serverUrl;
    private final AuthData auth;

    public GameplayClient(String serverUrl, AuthData auth, String gameID) throws ResponseException {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.auth = auth;
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
                - help
                """;
    }

    public static class ChessBoardFormatter {

        public static String formatChessBoard(String boardString, boolean isBlack) {
            boardString = convertToStringFormat(boardString);

            Map<String, String> pieceMap = getBoardMap();

            JsonArray rows = JsonParser.parseString(boardString).getAsJsonArray();
            StringBuilder formattedBoard = new StringBuilder();

            for (int i = 0; i < 8; i++) {
                int rowIndex = isBlack ? i : 7 - i;
                JsonArray row = rows.get(rowIndex).getAsJsonArray();

                // numbers on left
                formattedBoard.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY)
                        .append(EscapeSequences.SET_TEXT_COLOR_BLACK)
                        .append(" ")
                        .append(!isBlack ? 8 - i : 1 + i).append(" ")
                        .append(EscapeSequences.RESET_BG_COLOR)
                        .append(EscapeSequences.RESET_TEXT_COLOR);

                // Reverse the columns if isBlack is true
                if (isBlack) {
                    for (int j = 7; j >= 0; j--) {
                        boolean isBlackSquare = (i + (7 - j)) % 2 == 1;
                        String bgColor = isBlackSquare ? EscapeSequences.SET_BG_COLOR_BLACK : EscapeSequences.SET_BG_COLOR_WHITE;
                        String piece = row.get(j).isJsonNull() ? "null" : row.get(j).getAsString();

                        formattedBoard.append(bgColor)
                                .append(pieceMap.getOrDefault(piece, EscapeSequences.EMPTY))
                                .append(EscapeSequences.RESET_BG_COLOR);
                    }
                } else {
                    for (int j = 0; j < row.size(); j++) {
                        boolean isBlackSquare = (i + j) % 2 == 1;
                        String bgColor = isBlackSquare ? EscapeSequences.SET_BG_COLOR_BLACK : EscapeSequences.SET_BG_COLOR_WHITE;
                        String piece = row.get(j).isJsonNull() ? "null" : row.get(j).getAsString();

                        formattedBoard.append(bgColor)
                                .append(pieceMap.getOrDefault(piece, EscapeSequences.EMPTY))
                                .append(EscapeSequences.RESET_BG_COLOR);
                    }
                }

                formattedBoard.append("\n");
            }

            // bottom letters
            formattedBoard.append("   ");
            if (!isBlack) {
                for (char col = 'a'; col <= 'h'; col++) {
                    formattedBoard.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY)
                            .append(EscapeSequences.SET_TEXT_COLOR_BLACK)
                            .append(" ").append(col).append(" ")
                            .append(EscapeSequences.RESET_BG_COLOR)
                            .append(EscapeSequences.RESET_TEXT_COLOR);
                }
            } else {
                for (char col = 'h'; col >= 'a'; col--) {
                    formattedBoard.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY)
                            .append(EscapeSequences.SET_TEXT_COLOR_BLACK)
                            .append(" ").append(col).append(" ")
                            .append(EscapeSequences.RESET_BG_COLOR)
                            .append(EscapeSequences.RESET_TEXT_COLOR);
                }
            }
            formattedBoard.append("\n");

            return formattedBoard.toString();
        }

        private static Map<String, String> getBoardMap() {
            Map<String, String> pieceMap = new HashMap<>();
            pieceMap.put("WHITE KING", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + EscapeSequences.WHITE_KING + EscapeSequences.RESET_TEXT_COLOR);
            pieceMap.put("WHITE QUEEN", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + EscapeSequences.WHITE_QUEEN + EscapeSequences.RESET_TEXT_COLOR);
            pieceMap.put("WHITE BISHOP", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + EscapeSequences.WHITE_BISHOP + EscapeSequences.RESET_TEXT_COLOR);
            pieceMap.put("WHITE KNIGHT", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + EscapeSequences.WHITE_KNIGHT + EscapeSequences.RESET_TEXT_COLOR);
            pieceMap.put("WHITE ROOK", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + EscapeSequences.WHITE_ROOK + EscapeSequences.RESET_TEXT_COLOR);
            pieceMap.put("WHITE PAWN", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + EscapeSequences.WHITE_PAWN + EscapeSequences.RESET_TEXT_COLOR);
            pieceMap.put("BLACK KING", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + EscapeSequences.BLACK_KING + EscapeSequences.RESET_TEXT_COLOR);
            pieceMap.put("BLACK QUEEN", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + EscapeSequences.BLACK_QUEEN + EscapeSequences.RESET_TEXT_COLOR);
            pieceMap.put("BLACK BISHOP", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + EscapeSequences.BLACK_BISHOP + EscapeSequences.RESET_TEXT_COLOR);
            pieceMap.put("BLACK KNIGHT", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + EscapeSequences.BLACK_KNIGHT + EscapeSequences.RESET_TEXT_COLOR);
            pieceMap.put("BLACK ROOK", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + EscapeSequences.BLACK_ROOK + EscapeSequences.RESET_TEXT_COLOR);
            pieceMap.put("BLACK PAWN", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + EscapeSequences.BLACK_PAWN + EscapeSequences.RESET_TEXT_COLOR);
            pieceMap.put("null", EscapeSequences.EMPTY);
            return pieceMap;
        }

        public static String convertToStringFormat(String input) {
            input = input.replaceAll("(WHITE|BLACK) (ROOK|KNIGHT|BISHOP|QUEEN|KING|PAWN)", "\"$0\"");
            return input;
        }
    }

    public String startMsg() {
        var game = new ChessGame().getBoard().toString();
        System.out.println("White:");
        System.out.print(ChessBoardFormatter.formatChessBoard(game, false));
        System.out.println("\n");
        System.out.println("Black:");
        System.out.print(ChessBoardFormatter.formatChessBoard(game, true));
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
        System.out.print("You left the game.");
        return "%quit%";
    }

}
