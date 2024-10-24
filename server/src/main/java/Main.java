import chess.*;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import server.Server;
import service.ChessService;

import java.util.UUID;

public class Main {
    public static void main(String[] args) throws DataAccessException {

        DataAccess dataAccess = new MemoryDataAccess();

        var server = new Server();
        server.service = new ChessService(dataAccess);
        server.run(8080);

    }
}