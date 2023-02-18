package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.time.LocalDateTime.parse;
import static org.junit.jupiter.api.Assertions.*;

public class PomodoroSessionTest {
    PomodoroSession testSession;

    @BeforeEach
    void setUp() {
        testSession = new PomodoroSession("read 2 chapters", parse("2022-02-07T14:00"), parse("2022-02-07T16:30"),
                new PomodoroTimerSettings(), 120, null);
    }

    @Test
    void testConstructorNoActivity() {
        assertEquals("read 2 chapters", testSession.getDetails());
        assertEquals("2022-02-07T14:00", testSession.getStart().toString());
        assertEquals("2022-02-07T16:30", testSession.getEnd().toString());
        assertTrue(testSession.getActivityName().isEmpty());
        assertEquals(new PomodoroTimerSettings().toString(), testSession.getTimerSettings().toString());
        assertEquals(1, testSession.getCyclesCompleted());
        assertEquals(120, testSession.getPomodoroMinutes());
    }

    @Test
    void testConstructorWithActivity() {
        testSession = new PomodoroSession("read 2 chapters", parse("2022-02-07T14:00"), parse("2022-02-07T16:30"),
                new PomodoroTimerSettings(), 25, new Activity("hello"));
        assertEquals("read 2 chapters", testSession.getDetails());
        assertEquals("2022-02-07T14:00", testSession.getStart().toString());
        assertEquals("2022-02-07T16:30", testSession.getEnd().toString());
        assertEquals("hello", testSession.getActivityName());
        assertEquals(new PomodoroTimerSettings().toString(), testSession.getTimerSettings().toString());
        assertEquals(0, testSession.getCyclesCompleted());
        assertEquals(25, testSession.getPomodoroMinutes());
    }
}

