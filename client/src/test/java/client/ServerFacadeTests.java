package client;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacadepackage.ServerFacade;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var serverUrl = "http://localhost:" + port;
        serverFacade = new ServerFacade(serverUrl);
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void clearPositive() {
        assertDoesNotThrow(() -> serverFacade.clear());
    }

    @Test
    void registerPositive() {
        assertDoesNotThrow(() -> {
            AuthData auth = serverFacade.register("testUser", "password", "test@example.com");
            assertNotNull(auth);
        });
    }

    @Test
    void registerNegative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.register("", "password", "invalid-email");
        });
    }

    @Test
    void loginPositive() {
        assertDoesNotThrow(() -> {
            serverFacade.register("testUser2", "password", "test2@example.com");
            AuthData auth = serverFacade.login("testUser2", "password");
            assertNotNull(auth);
        });
    }

    @Test
    void loginNegative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.login("nonExistentUser", "wrongPassword");
        });
    }

    @Test
    void logoutPositive() {
        assertDoesNotThrow(() -> {
            AuthData auth = serverFacade.register("testUser3", "password", "test3@example.com");
            serverFacade.logout(auth.authToken());
        });
    }

    @Test
    void logoutNegative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.logout(UUID.randomUUID()); // Invalid token
        });
    }

    @Test
    void listGamesPositive() {
        assertDoesNotThrow(() -> {
            AuthData auth = serverFacade.register("testUser4", "password", "test4@example.com");
            GameData[] games = serverFacade.listGames(auth.authToken());
            assertNotNull(games);
        });
    }

    @Test
    void listGamesNegative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.listGames(UUID.randomUUID()); // Invalid token
        });
    }

    @Test
    void createGamePositive() {
        assertDoesNotThrow(() -> {
            AuthData auth = serverFacade.register("testUser5", "password", "test5@example.com");
            serverFacade.createGame(auth.authToken(), "Test Game");
        });
    }

    @Test
    void createGameNegative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.createGame(UUID.randomUUID(), "Invalid Game"); // Invalid token
        });
    }

    @Test
    void joinGamePositive() {
        assertDoesNotThrow(() -> {
            AuthData auth = serverFacade.register("testUser6", "password", "test6@example.com");
            serverFacade.createGame(auth.authToken(), "Test Game 2");
            serverFacade.joinGame(auth.authToken(), ChessGame.TeamColor.WHITE, 1);
        });
    }

    @Test
    void joinGameNegative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.joinGame(UUID.randomUUID(), null, -1); // Invalid inputs
        });
    }
}
