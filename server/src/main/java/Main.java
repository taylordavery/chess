import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import server.Server;
import service.ChessService;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        DataAccess dataAccess = new MySqlDataAccess();
        var server = new Server();
        server.service = new ChessService(dataAccess);
        server.run(8080);
    }
}