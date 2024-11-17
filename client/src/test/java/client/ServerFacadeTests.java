package client;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        var serverUrl = "http://localhost:8080";
        serverFacade = new ServerFacade(serverUrl);
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void clear_positive() {
        assertDoesNotThrow(() -> serverFacade.clear());
    }

    @Test
    void register_positive() {
        assertDoesNotThrow(() -> {
            AuthData auth = serverFacade.register("testUser", "password", "test@example.com");
            assertNotNull(auth);
        });
    }

    @Test
    void register_negative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.register("", "password", "invalid-email");
        });
    }

    @Test
    void login_positive() {
        assertDoesNotThrow(() -> {
            serverFacade.register("testUser2", "password", "test2@example.com");
            AuthData auth = serverFacade.login("testUser2", "password");
            assertNotNull(auth);
        });
    }

    @Test
    void login_negative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.login("nonExistentUser", "wrongPassword");
        });
    }

    @Test
    void logout_positive() {
        assertDoesNotThrow(() -> {
            AuthData auth = serverFacade.register("testUser3", "password", "test3@example.com");
            serverFacade.logout(auth.authToken());
        });
    }

    @Test
    void logout_negative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.logout(UUID.randomUUID()); // Invalid token
        });
    }

    @Test
    void listGames_positive() {
        assertDoesNotThrow(() -> {
            AuthData auth = serverFacade.register("testUser4", "password", "test4@example.com");
            GameData[] games = serverFacade.listGames(auth.authToken());
            assertNotNull(games);
        });
    }

    @Test
    void listGames_negative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.listGames(UUID.randomUUID()); // Invalid token
        });
    }

    @Test
    void createGame_positive() {
        assertDoesNotThrow(() -> {
            AuthData auth = serverFacade.register("testUser5", "password", "test5@example.com");
            int gameId = serverFacade.createGame(auth.authToken(), "Test Game");
            assertTrue(gameId > 0);
        });
    }

    @Test
    void createGame_negative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.createGame(UUID.randomUUID(), "Invalid Game"); // Invalid token
        });
    }

    @Test
    void joinGame_positive() {
        assertDoesNotThrow(() -> {
            AuthData auth = serverFacade.register("testUser6", "password", "test6@example.com");
            int gameId = serverFacade.createGame(auth.authToken(), "Test Game 2");
            serverFacade.joinGame(auth.authToken(), "white", gameId);
        });
    }

    @Test
    void joinGame_negative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.joinGame(UUID.randomUUID(), "invalidColor", -1); // Invalid inputs
        });
    }
}
