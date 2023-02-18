package model;

import model.exception.DuplicateActivityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.time.LocalDateTime.parse;
import static org.junit.jupiter.api.Assertions.*;

public class StudyTrackerTest {
    StudyTracker testTracker;
    Activity a1;
    Activity a2;
    Session s1;
    Session s2;
    PomodoroSession s3;

    @BeforeEach
    void setUp() {
        testTracker = new StudyTracker();
        a1 = new Activity("CPSC210");
        a2 = new Activity("Read book");
        s1 = new Session("finished lab 5", parse("2022-02-07T14:00"), parse("2022-02-07T16:30"), a1);
        s2 = new Session("do lecture", parse("2022-02-07T14:00"), parse("2022-02-07T16:30"), a2);
        s3 = new PomodoroSession("read chapter 1", parse("2022-02-07T16:00"), parse("2022-02-07T18:30"),
                new PomodoroTimerSettings(), 2, a1);
    }

    @Test
    void testConstructor() {
        PomodoroTimerSettings defaultSettings = new PomodoroTimerSettings();

        assertTrue(testTracker.getActivities().isEmpty());
        assertEquals(defaultSettings.toString(), testTracker.getTimerSettings().toString());

        PomodoroTimerSettings customSettings = new PomodoroTimerSettings(30, 5, 22, 2);
        testTracker.setTimerSettings(customSettings);
        assertEquals(customSettings.toString(), testTracker.getTimerSettings().toString());
    }

    @Test
    void testAddActivity() {
        try {
            testTracker.addActivity(a1);
            assertEquals(1, testTracker.getActivities().size());
            assertEquals(a1, testTracker.getActivities().get(0));

            testTracker.addActivity(a2);
            assertEquals(2, testTracker.getActivities().size());
            assertEquals(a2, testTracker.getActivities().get(1));
        } catch (DuplicateActivityException e) {
            fail("DuplicateActivityException caught");
        }
    }

    @Test
    void testAddActivityDuplicate() {
        try {
            testTracker.addActivity(a1);
            assertEquals(1, testTracker.getActivities().size());
            assertEquals(a1, testTracker.getActivities().get(0));

            testTracker.addActivity(a1);
            fail("DuplicateActivityException was not thrown");
        } catch (DuplicateActivityException e) {
            // expected
        }
        assertEquals(1, testTracker.getActivities().size());
        assertEquals(a1, testTracker.getActivities().get(0));
    }

    @Test
    void testAddSession() {
        testTracker.addSession(s1);
        assertEquals(1, testTracker.getSessions().size());
        assertEquals(s1, testTracker.getSessions().get(0));

        testTracker.addSession(s2);
        assertEquals(2, testTracker.getSessions().size());
        assertEquals(s2, testTracker.getSessions().get(1));

        testTracker.addSession(s3);
        assertEquals(3, testTracker.getSessions().size());
        assertEquals(s3, testTracker.getSessions().get(2));
    }

    @Test
    void testRemoveSession() {
        testTracker.addSession(s1);
        testTracker.addSession(s2);

        testTracker.removeSession(s1);
        assertEquals(1, testTracker.getSessions().size());
        assertFalse(testTracker.getSessions().contains(s1));

        testTracker.removeSession(s2);
        assertEquals(0, testTracker.getSessions().size());
        assertFalse(testTracker.getSessions().contains(s2));
    }

    @Test
    void testFilterSessionByActivity() {
        testTracker.addSession(s1);
        testTracker.addSession(s2);
        testTracker.addSession(s3);

        List<Session> filtered = testTracker.filterSessionsByActivity(a1);
        assertEquals(2, filtered.size());
        assertTrue(filtered.contains(s1));
        assertTrue(filtered.contains(s3));

        s1.setActivity(null);
        s2.setActivity(null);

        filtered = testTracker.filterSessionsByActivity(null);
        assertEquals(2, filtered.size());
        assertTrue(filtered.contains(s1));
        assertTrue(filtered.contains(s2));
    }
}
