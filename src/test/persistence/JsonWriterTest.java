package persistence;

import model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static java.time.LocalDateTime.parse;
import static org.junit.jupiter.api.Assertions.*;

// reference: JsonWriterTest class in https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonWriterTest extends JsonTest {

    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/invalid\0:fileName.json");
            writer.open();
            fail("IOException was not thrown");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    void testWriterEmptyStudyTracker() {
        try {
            StudyTracker st = new StudyTracker();
            String settings = st.getTimerSettings().toString();
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyStudyTracker.json");
            writer.open();
            writer.write(st);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyStudyTracker.json");
            st = reader.read();
            assertEquals(settings, st.getTimerSettings().toString());
            assertTrue(st.getActivities().isEmpty());
        } catch (IOException e) {
            fail("IOException was caught");
        }
    }

    @Test
    void testWriterGeneralStudyTracker() {
        try {
            StudyTracker st = new StudyTracker();
            st.setTimerSettings(new PomodoroTimerSettings(50, 10, 30, 2));
            String settings = st.getTimerSettings().toString();
            loadTracker(st);
            JsonWriter writer = new JsonWriter("./data/testWriterGeneralStudyTracker.json");
            writer.open();
            writer.write(st);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralStudyTracker.json");
            st = reader.read();
            assertEquals(settings, st.getTimerSettings().toString());
            assertEquals(2, st.getActivities().size());
            assertEquals("a1", st.getActivities().get(0).getName());
            assertEquals("a2", st.getActivities().get(1).getName());

            assertEquals(4, st.getSessions().size());
            checkSession("s1", "2022-02-22T12:40", "2022-02-22T13:40", new Activity("a1"), st.getSessions().get(0));
            checkPomoSession("s2", "2022-02-22T15:40", "2022-02-22T16:40",
                    new PomodoroTimerSettings(), 2, new Activity("a2"), (PomodoroSession) st.getSessions().get(1));
            checkSession("s3", "2022-02-22T12:40", "2022-02-22T13:40", st.getSessions().get(2));
            checkPomoSession("s2", "2022-02-22T15:40", "2022-02-22T16:40",
                    new PomodoroTimerSettings(), 2, (PomodoroSession) st.getSessions().get(3));
        } catch (IOException e) {
            fail("IOException was caught");
        }
    }

    private void loadTracker(StudyTracker st) {
        Activity a1 = new Activity("a1");
        Activity a2 = new Activity("a2");
        Session s = new Session("s1", parse("2022-02-22T12:40"), parse("2022-02-22T13:40"), a1);
        PomodoroSession ps = new PomodoroSession("s2", parse("2022-02-22T15:40"), parse("2022-02-22T16:40"),
                new PomodoroTimerSettings(), 2, a2);
        Session ns = new Session("s3", parse("2022-02-22T12:40"), parse("2022-02-22T13:40"), null);
        PomodoroSession nps = new PomodoroSession("s2", parse("2022-02-22T15:40"), parse("2022-02-22T16:40"),
                new PomodoroTimerSettings(), 2, null);
        st.addSession(s);
        st.addSession(ps);
        st.addSession(ns);
        st.addSession(nps);
        st.addActivity(a1);
        st.addActivity(a2);
    }
}
