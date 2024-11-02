package dataaccess;

import model.AuthData;
import model.GameData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class DatabaseManager implements DataAccess{
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @throws DataAccessException
     */
    @Override
    public void clear() throws DataAccessException {

    }

    /**
     * @param username
     * @param password
     * @param email
     * @return
     * @throws DataAccessException
     */
    @Override
    public AuthData register(String username, String password, String email) throws DataAccessException {
        return null;
    }

    /**
     * @param username
     * @param password
     * @return
     * @throws DataAccessException
     */
    @Override
    public AuthData login(String username, String password) throws DataAccessException {
        return null;
    }

    /**
     * @param authToken
     * @throws DataAccessException
     */
    @Override
    public void logout(UUID authToken) throws DataAccessException {

    }

    /**
     * @param authToken
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<GameData> listGames(UUID authToken) throws DataAccessException {
        return List.of();
    }

    /**
     * @param authToken
     * @param gameName
     * @return
     * @throws DataAccessException
     */
    @Override
    public int createGame(UUID authToken, String gameName) throws DataAccessException {
        return 0;
    }

    /**
     * @param authToken
     * @param playerColor
     * @param gameID
     * @throws DataAccessException
     */
    @Override
    public void joinGame(UUID authToken, String playerColor, int gameID) throws DataAccessException {

    }
}
