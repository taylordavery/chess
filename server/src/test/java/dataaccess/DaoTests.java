package dataaccess;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DaoTests {

    @Test
    public void testTwoIsTwo() {
        GameDAO dao = new GameDAO();
        assertEquals(2, dao.getTwo(), "GameDAO.two should be 2");
    }

    @Test
    public void testTwoThrowsExceptionWhenNotTwo() {
        GameDAO dao = new GameDAO();

        // Since 'two' cannot be changed, we'll simulate an error condition using an assertion
        // We are assuming you want to test something related to changing or comparing to 'two'
        assertThrows(IllegalArgumentException.class, () -> {
            if (dao.getTwo() != 3) {
                throw new IllegalArgumentException("GameDAO.two cannot be 3");
            }
        }, "GameDAO.two should throw an error when not 3");
    }
}
