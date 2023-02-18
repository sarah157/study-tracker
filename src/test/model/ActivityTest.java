package model;

import model.exception.EmptyNameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.time.LocalDateTime.parse;
import static org.junit.jupiter.api.Assertions.*;

public class ActivityTest {
    private Activity testActivity;

    @BeforeEach
    void runBefore() {
        testActivity = null;
    }

    @Test
    void testConstructorEmptyName() {
        try {
            testActivity = new Activity("");
            fail("EmptyNameException was not thrown");
            assertEquals("CPSC210", testActivity.getName());
        } catch (EmptyNameException e) {
            // expected
        }
        assertNull(testActivity);
    }

    @Test
    void testConstructorWithName() {
        try {
            testActivity = new Activity("CPSC210");
        } catch (EmptyNameException e) {
            fail("EmptyNameException caught");
        }
        assertEquals("CPSC210", testActivity.getName());
    }

}
