package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

import static model.PomodoroTimer.*;
import static model.PomodoroTimerSettings.*;
import static org.junit.jupiter.api.Assertions.*;

public class PomodoroTimerTest {
    PomodoroTimer testTimer;

    @BeforeEach
    void setUp() {
        testTimer = new PomodoroTimer(new PomodoroTimerSettings());
    }

    @Test
    void testConstructor() {
        assertEquals(DEFAULT_POMODORO * 60, testTimer.getPomodoroDuration());
        assertEquals(DEFAULT_SHORT_BREAK * 60, testTimer.getShortBreakDuration());
        assertEquals(DEFAULT_LONG_BREAK * 60, testTimer.getLongBreakDuration());
        assertEquals(DEFAULT_REPEATS, testTimer.getPomodoroRepeats());
        assertEquals(0, testTimer.getTotalPomodoroIntervals());
        assertEquals(DEFAULT_REPEATS, testTimer.getRepeatsRemaining());
        assertNull(testTimer.getStart());
        assertNull(testTimer.getEnd());
        assertFalse(testTimer.isRunning());

        testPomodoroIntervalInitialized();

        assertEquals(0, testTimer.getTotalCycles());
        assertEquals(0, testTimer.getTotalPomodoroMinutes());
    }

    @Test
    void testStart() {
        testTimer.start(new TimerTask() {
            @Override
            public void run() { }
        });
        testTimestamp(testTimer.getStart());
        assertTrue(testTimer.isRunning());
    }

    @Test
    void testStartAndRun() throws InterruptedException {
        testTimer.start(new TimerTask() {
            @Override
            public void run() { }
        });
        Thread.sleep(1200L);
        assertEquals(testTimer.getPomodoroDuration() - 1, testTimer.getTimeRemaining());
    }

    @Test
    void testStartAndRunNullTask() throws InterruptedException {
        testTimer.start(null);
        Thread.sleep(1000L);
        assertEquals(testTimer.getPomodoroDuration() - 1, testTimer.getTimeRemaining());
    }

    @Test
    void testPauseResume() {
        testTimer.start(null);
        testTimer.pause();
        assertFalse(testTimer.isRunning());
        testTimer.resume();
        assertTrue(testTimer.isRunning());
    }

    @Test
    void testCancel() {
        testTimer.start(null);
        testTimer.cancel();
        testTimestamp(testTimer.getEnd());
        assertFalse(testTimer.isRunning());
    }

    @Test
    void testDecrementTimerNotPaused() {
        int seconds = testTimer.getTimeRemaining();
        testTimer.start(null);

        testTimer.decrement();
        assertEquals(seconds - 1, testTimer.getTimeRemaining());

        decrementNSeconds(testTimer, 120);
        assertEquals(seconds - 121, testTimer.getTimeRemaining());
    }

    @Test
    void testDecrementTimerIsPaused() {
        int seconds = testTimer.getTimeRemaining();

        testTimer.decrement();
        assertEquals(seconds, testTimer.getTimeRemaining());

        decrementNSeconds(testTimer, 120);
        assertEquals(seconds, testTimer.getTimeRemaining());
    }

    @Test
    void testNextInterval() {
        for (int i = DEFAULT_REPEATS; i > 1; i--) {
            assertEquals(i, testTimer.getRepeatsRemaining());
            testPomodoroIntervalInitialized();

            testTimer.nextInterval(); // pomodoro to short break

            assertEquals(i - 1, testTimer.getRepeatsRemaining());
            testShortBreakIntervalInitialized();

            testTimer.nextInterval(); // short break to pomodoro
        }

        assertEquals(1, testTimer.getRepeatsRemaining());
        assertEquals(0, testTimer.getTotalCycles());
        testPomodoroIntervalInitialized();

        testTimer.nextInterval(); // pomodoro to long break

        assertEquals(0, testTimer.getRepeatsRemaining());
        assertEquals(1, testTimer.getTotalCycles());
        testLongBreakIntervalInitialized();

        testTimer.nextInterval(); // long break to pomodoro

        assertEquals(DEFAULT_REPEATS, testTimer.getRepeatsRemaining());
        testPomodoroIntervalInitialized();
    }

    @Test
    void testGetTotalPomodoroMinutesAtPomodoroInterval() {
        testTimer.start(null);
        performOneCycle();
        assertEquals(DEFAULT_REPEATS * DEFAULT_POMODORO, testTimer.getTotalPomodoroMinutes());

        decrementNSeconds(testTimer, 120);
        assertEquals(DEFAULT_REPEATS * DEFAULT_POMODORO + 2, testTimer.getTotalPomodoroMinutes());
    }

    @Test
    void testGetTotalPomodoroMinutesNotAtPomodoroInterval() {
        testTimer.nextInterval(); // pomodoro to short break
        assertEquals(DEFAULT_POMODORO, testTimer.getTotalPomodoroMinutes());

        performOneCycle();
        assertEquals((DEFAULT_REPEATS + 1) * DEFAULT_POMODORO, testTimer.getTotalPomodoroMinutes());

        decrementNSeconds(testTimer, 120);
        assertEquals((DEFAULT_REPEATS + 1) * DEFAULT_POMODORO, testTimer.getTotalPomodoroMinutes());
    }

    @Test
    void testManyCycles() {
        performOneCycle();

        assertEquals(1, testTimer.getTotalCycles());

        int numCycles = 5;
        for (int i = 0; i < numCycles; i++) {
            performOneCycle();
        }

        assertEquals(numCycles + 1, testTimer.getTotalCycles());
    }

    @Test
    void testDecrementToZero() {
        PomodoroTimer shortTimer = new PomodoroTimer(new PomodoroTimerSettings(2, 1, 2, 2));
        assertEquals(120, shortTimer.getTimeRemaining());
        shortTimer.start(null);

        decrementNSeconds(shortTimer, 119);
        assertEquals(1, shortTimer.getTimeRemaining());

        shortTimer.decrement();

        assertEquals(60, shortTimer.getTimeRemaining());
        assertEquals(SHORT_BREAK_INTERVAL, shortTimer.getCurrentInterval());
    }

    private void decrementNSeconds(PomodoroTimer timer, int n) {
        for (int i = 0; i < n; i++) {
            timer.decrement();
        }
    }

    private void performOneCycle() {
        int totalIntervals = testTimer.getPomodoroRepeats() * 2; // one cycle + long break interval
        for (int i = 0; i < totalIntervals; i++) {
            testTimer.nextInterval();
        }
    }

    private void testTimestamp(LocalDateTime timestamp) {
        int seconds = (int) ChronoUnit.SECONDS.between(LocalDateTime.now(), timestamp);
        assertTrue(seconds < 10);
    }

    private void testPomodoroIntervalInitialized() {
        assertEquals(POMODORO_INTERVAL, testTimer.getCurrentInterval());
        assertEquals(testTimer.getPomodoroDuration(), testTimer.getTimeRemaining());
    }

    private void testShortBreakIntervalInitialized() {
        assertEquals(SHORT_BREAK_INTERVAL, testTimer.getCurrentInterval());
        assertEquals(testTimer.getShortBreakDuration(), testTimer.getTimeRemaining());
    }

    private void testLongBreakIntervalInitialized() {
        assertEquals(LONG_BREAK_INTERVAL, testTimer.getCurrentInterval());
        assertEquals(testTimer.getLongBreakDuration(), testTimer.getTimeRemaining());
    }
}
