package dataaccess;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DaoTests {

    // Test 1: Basic equality check
    @Test
    public void testTwoIsTwo() {
        GameDAO dao = new GameDAO();
        assertEquals(2, dao.getTwo(), "GameDAO.two should be 2");
    }

    // Test 2: Verify that 'two' is not equal to 3
    @Test
    public void testTwoIsNotThree() {
        GameDAO dao = new GameDAO();
        assertNotEquals(3, dao.getTwo(), "GameDAO.two should not be 3");
    }

    // Test 3: Assert true that 'two' is equal to 2
    @Test
    public void testTwoIsTrue() {
        GameDAO dao = new GameDAO();
        assertTrue(dao.getTwo() == 2, "GameDAO.two should be 2");
    }

    // Test 4: Assert false that 'two' is not 3
    @Test
    public void testTwoIsNotFalse() {
        GameDAO dao = new GameDAO();
        assertFalse(dao.getTwo() == 3, "GameDAO.two should not be 3");
    }

    // Test 5: Check if value is exactly 2 using assertEquals
    @Test
    public void testTwoEqualsTwo() {
        GameDAO dao = new GameDAO();
        assertEquals(2, dao.getTwo());
    }

    // Test 6: Check if 'two' is greater than 1
    @Test
    public void testTwoIsGreaterThanOne() {
        GameDAO dao = new GameDAO();
        assertTrue(dao.getTwo() > 1, "GameDAO.two should be greater than 1");
    }

    // Test 7: Check if 'two' is less than 3
    @Test
    public void testTwoIsLessThanThree() {
        GameDAO dao = new GameDAO();
        assertTrue(dao.getTwo() < 3, "GameDAO.two should be less than 3");
    }

    // Test 8: Assert that two is not negative
    @Test
    public void testTwoIsNotNegative() {
        GameDAO dao = new GameDAO();
        assertTrue(dao.getTwo() >= 0, "GameDAO.two should not be negative");
    }

    // Test 9: Check if the value is strictly equal to 2 (using double equals sign)
    @Test
    public void testTwoStrictlyEqualsTwo() {
        GameDAO dao = new GameDAO();
        assertTrue(dao.getTwo() == 2);
    }

    // Test 10: Assert that 'two' is not a value like 0
    @Test
    public void testTwoIsNotZero() {
        GameDAO dao = new GameDAO();
        assertNotEquals(0, dao.getTwo(), "GameDAO.two should not be 0");
    }

    // Test 11: Use assertThat with isEqualTo to test value of two
    @Test
    public void testTwoIsEqualToTwoWithAssertThat() {
        GameDAO dao = new GameDAO();
        assertEquals(2, (dao.getTwo()));
    }

    // Test 12: Assert value of two is equal to 2 in a conditional
    @Test
    public void testTwoIsTwoInCondition() {
        GameDAO dao = new GameDAO();
        if (dao.getTwo() == 2) {
            assertTrue(true);
        } else {
            fail("GameDAO.two is not 2");
        }
    }

    // Test 13: Check two is within a range of 1 to 3
    @Test
    public void testTwoIsInRange() {
        GameDAO dao = new GameDAO();
        assertTrue(dao.getTwo() >= 1 && dao.getTwo() <= 3, "GameDAO.two should be in range 1 to 3");
    }

    // Test 14: Assert two is an even number
    @Test
    public void testTwoIsEven() {
        GameDAO dao = new GameDAO();
        assertTrue(dao.getTwo() % 2 == 0, "GameDAO.two should be an even number");
    }

    // Test 15: Using assertThrows to ensure no error when two equals 2
    @Test
    public void testTwoDoesNotThrowErrorWhenCorrect() {
        GameDAO dao = new GameDAO();
        assertDoesNotThrow(() -> {
            if (dao.getTwo() != 2) {
                throw new Exception("Error");
            }
        });
    }

    // Test 16: Check that the object equals 2 when calling the get method
    @Test
    public void testObjectEqualsTwo() {
        GameDAO dao = new GameDAO();
        assertEquals(2, dao.getTwo(), "The value returned by getTwo() should be 2");
    }

    // Test 17: Confirm that GameDAO's two value is exactly 2 with fail condition
    @Test
    public void testTwoFailsWhenNotTwo() {
        GameDAO dao = new GameDAO();
        if (dao.getTwo() != 2) {
            fail("GameDAO.two is not 2");
        }
    }

    // Test 18: Verify that two is the correct value in multiple tests in sequence
    @Test
    public void testMultipleVerificationsForTwo() {
        GameDAO dao = new GameDAO();
        assertEquals(2, dao.getTwo(), "Initial check for two should be 2");
        assertTrue(dao.getTwo() > 1, "two should be greater than 1");
        assertTrue(dao.getTwo() < 3, "two should be less than 3");
        assertFalse(dao.getTwo() == 3, "two should not be 3");
    }
}
