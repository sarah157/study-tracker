package persistence;

import model.Activity;
import model.PomodoroSession;
import model.PomodoroTimerSettings;
import model.Session;

import static org.junit.jupiter.api.Assertions.*;

// reference: JsonTest class in https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonTest {
    protected void checkSession(String details, String start, String end, Activity activity, Session session) {
        assertEquals(details, session.getDetails());
        assertEquals(activity.getName(), session.getActivityName());
        assertEquals(start, session.getStart().toString());
        assertEquals(end, session.getEnd().toString());
    }

    protected void checkSession(String details, String start, String end, Session session) {
        assertEquals(details, session.getDetails());
        assertTrue(session.getActivityName().isEmpty());
        assertEquals(start, session.getStart().toString());
        assertEquals(end, session.getEnd().toString());
    }

    protected void checkPomoSession(String details, String start, String end, PomodoroTimerSettings settings,
                                    int pomodoros, Activity activity, PomodoroSession session) {
        checkSession(details, start, end, activity, session);
        assertEquals(settings.toString(), session.getTimerSettings().toString());
        assertEquals(pomodoros, session.getPomodoroMinutes());
    }

    protected void checkPomoSession(String details, String start, String end, PomodoroTimerSettings settings,
                                    int pomodoros, PomodoroSession session) {
        checkSession(details, start, end, session);
        assertEquals(settings.toString(), session.getTimerSettings().toString());
        assertEquals(pomodoros, session.getPomodoroMinutes());
    }

    protected void checkTimerSettings(int pomodoro, int shortBreak, int longBreak, int repeats, PomodoroTimerSettings ts) {
        PomodoroTimerSettings expected = new PomodoroTimerSettings(pomodoro, shortBreak, longBreak, repeats);
        assertEquals(expected.toString(), ts.toString());
    }
}
