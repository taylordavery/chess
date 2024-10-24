package model;
import java.util.UUID;

public record AuthData(UUID authToken, String username){
    public String getUsername() {
        return username;
    }
}
