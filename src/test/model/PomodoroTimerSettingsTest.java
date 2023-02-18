package model;

import model.exception.NonPositiveValueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static model.PomodoroTimerSettings.DEFAULT_POMODORO;
import static model.PomodoroTimerSettings.DEFAULT_SHORT_BREAK;
import static model.PomodoroTimerSettings.DEFAULT_LONG_BREAK;
import static model.PomodoroTimerSettings.DEFAULT_REPEATS;

import static org.junit.jupiter.api.Assertions.*;

public class PomodoroTimerSettingsTest {
    PomodoroTimerSettings testSettings;

    @BeforeEach
    void setUp() {
        testSettings = new PomodoroTimerSettings();
    }

    @Test
    void testConstructorDefaultTimer() {
        assertEquals(DEFAULT_POMODORO, testSettings.getPomodoro());
        assertEquals(DEFAULT_SHORT_BREAK, testSettings.getShortBreak());
        assertEquals(DEFAULT_LONG_BREAK, testSettings.getLongBreak());
        assertEquals(DEFAULT_REPEATS, testSettings.getPomodoroRepeats());
    }

    @Test
    void testConstructorCustomTimer() {
        try {
            PomodoroTimerSettings custom = new PomodoroTimerSettings(50, 10, 30, 2);
            assertEquals(50, custom.getPomodoro());
            assertEquals(10, custom.getShortBreak());
            assertEquals(30, custom.getLongBreak());
            assertEquals(2, custom.getPomodoroRepeats());
        } catch (NonPositiveValueException e) {
            fail("NonPositiveValueException caught");
        }
    }

    @Test
    void testEquals() {
        PomodoroTimerSettings s1 = new PomodoroTimerSettings();
        assertFalse(s1.equals(null));
        assertEquals(s1, s1);
        assertFalse(s1.equals(new Activity("a")));

        PomodoroTimerSettings s2 = new PomodoroTimerSettings(5, 5, 5, 5);
        assertNotEquals(s2, s1);
        assertNotEquals(s2.hashCode(), s1.hashCode());
        s2.setPomodoro(DEFAULT_POMODORO);
        assertNotEquals(s2, s1);
        assertNotEquals(s2.hashCode(), s1.hashCode());
        s2.setShortBreak(DEFAULT_SHORT_BREAK);
        assertNotEquals(s2, s1);
        assertNotEquals(s2.hashCode(), s1.hashCode());
        s2.setLongBreak(DEFAULT_LONG_BREAK);
        assertNotEquals(s2, s1);
        assertNotEquals(s2.hashCode(), s1.hashCode());
        s2.setPomodoroRepeats(DEFAULT_REPEATS);
        assertEquals(s2, s1);
        assertEquals(s2.hashCode(), s1.hashCode());
    }

    @Test
    void testConstructorNonPositiveValues() {
        try {
            PomodoroTimerSettings custom = new PomodoroTimerSettings(0, 10, 30, 2);
            fail("NonPositiveValueException was not thrown");
        } catch (NonPositiveValueException e) {
            // expected
        }
    }

    @Test
    void testSettersWithPositiveValues() {
        testSettings.setPomodoro(50);
        testSettings.setShortBreak(15);
        testSettings.setLongBreak(15);
        testSettings.setPomodoroRepeats(2);

        assertEquals(50, testSettings.getPomodoro());
        assertEquals(15, testSettings.getShortBreak());
        assertEquals(15, testSettings.getLongBreak());
        assertEquals(2, testSettings.getPomodoroRepeats());
    }

    @Test
    void testSetPomodoroWithNonPositiveValues() {
        try {
            testSettings.setPomodoro(0);
            fail();
        } catch (NonPositiveValueException e) {
            // expected
        }
        assertEquals(DEFAULT_POMODORO, testSettings.getPomodoro());
    }

    @Test
    void testSetShortBreakWithNonPositiveValues() {
        try {
            testSettings.setShortBreak(-1);
            fail();
        } catch (NonPositiveValueException e) {
            // expected
        }
        assertEquals(DEFAULT_SHORT_BREAK, testSettings.getShortBreak());
    }

    @Test
    void testSetLongBreakWithNonPositiveValues() {
        try {
            testSettings.setLongBreak(-1);
            fail();
        } catch (NonPositiveValueException e) {
            // expected
        }
        assertEquals(DEFAULT_LONG_BREAK, testSettings.getLongBreak());
    }

    @Test
    void testSetPomodoroRepeatsWithNonPositiveValues() {
        try {
            testSettings.setPomodoroRepeats(-1);
            fail();
        } catch (NonPositiveValueException e) {
            // expected
        }
        assertEquals(DEFAULT_REPEATS, testSettings.getPomodoroRepeats());
    }

    @Test
    void testToString() {
        testSettings.setPomodoro(45);
        testSettings.setShortBreak(10);
        testSettings.setLongBreak(30);
        testSettings.setPomodoroRepeats(2);

        String expected = "pomodoro: 2 x 45 min, short: 10 min, long: 30 min";
        assertEquals(expected, testSettings.toString());
    }
}
