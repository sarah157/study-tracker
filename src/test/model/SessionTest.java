package model;

import model.exception.InvalidDateTimeInterval;
import org.junit.jupiter.api.Test;

import static java.time.LocalDateTime.parse;

import static org.junit.jupiter.api.Assertions.*;

public class SessionTest {
    private Session testSession;

    @Test
    void testConstructorNoActivity() {
        try {
            testSession = new Session("read 2 chapters", parse("2022-02-07T14:00"), parse("2022-02-07T16:30"), null);
        } catch (InvalidDateTimeInterval e) {
            fail("InvalidDateTimeInterval was caught");
        }
        assertEquals("read 2 chapters", testSession.getDetails());
        assertEquals("2022-02-07T14:00", testSession.getStart().toString());
        assertEquals("2022-02-07T16:30", testSession.getEnd().toString());
        assertTrue(testSession.getActivityName().isEmpty());
        assertEquals(150, testSession.getDuration());

        testSession.setDetails("read 15 pages");
        testSession.setStart(parse("2022-02-09T15:00"));
        testSession.setEnd(parse("2022-02-09T18:00"));
        testSession.setActivity(new Activity("a2"));

        assertEquals("read 15 pages", testSession.getDetails());
        assertEquals("2022-02-09T15:00", testSession.getStart().toString());
        assertEquals("2022-02-09T18:00", testSession.getEnd().toString());
        assertEquals("a2", testSession.getActivityName());
        assertEquals(180, testSession.getDuration());
    }

    @Test
    void testConstructorWithActivity() {
        try {
            testSession = new Session("read 2 chapters", parse("2022-02-07T14:00"), parse("2022-02-07T16:30"), new Activity("a1"));
        } catch (InvalidDateTimeInterval e) {
            fail("InvalidDateTimeInterval was caught");
        }
        assertEquals("read 2 chapters", testSession.getDetails());
        assertEquals("2022-02-07T14:00", testSession.getStart().toString());
        assertEquals("2022-02-07T16:30", testSession.getEnd().toString());
        assertEquals("a1", testSession.getActivityName());
        assertEquals(150, testSession.getDuration());

        testSession.setDetails("read 15 pages");
        testSession.setStart(parse("2022-02-09T15:00"));
        testSession.setEnd(parse("2022-02-09T18:00"));
        testSession.setActivity(new Activity("a2"));

        assertEquals("read 15 pages", testSession.getDetails());
        assertEquals("2022-02-09T15:00", testSession.getStart().toString());
        assertEquals("2022-02-09T18:00", testSession.getEnd().toString());
        assertEquals("a2", testSession.getActivityName());
        assertEquals(180, testSession.getDuration());
    }

    @Test
    void testConstructorStartAfterEndDateTime() {
        try {
            testSession = new Session("read 2 chapters", parse("2022-02-07T16:30"), parse("2022-02-07T14:00"), new Activity("a1"));
            fail("InvalidDateTimeInterval was not thrown");
        } catch (InvalidDateTimeInterval e) {
            // expected
        }
        assertNull(testSession);
    }

}
