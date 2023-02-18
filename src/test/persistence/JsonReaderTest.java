package persistence;

import model.Activity;
import model.PomodoroSession;
import model.PomodoroTimerSettings;
import model.StudyTracker;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

// reference: JsonReaderTest class in https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonReaderTest extends JsonTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/nonexistentFile.json");
        try {
            StudyTracker st = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    void testReaderEmptyStudyTracker() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyStudyTracker.json");
        try {
            StudyTracker st = reader.read();
            checkTimerSettings(25, 5, 25, 4, st.getTimerSettings());
            assertEquals(0, st.getActivities().size());
        } catch (IOException e) {
            fail("Unable to read file");
        }
    }

    @Test
    void testReaderGeneralStudyTracker() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralStudyTracker.json");
        try {
            StudyTracker st = reader.read();
            checkTimerSettings(25, 5, 25, 4, st.getTimerSettings());
            assertEquals(2, st.getActivities().size());
            assertEquals(5, st.getSessions().size());

            assertEquals("MyCourse", st.getActivities().get(0).getName());
            assertEquals("Reading time", st.getActivities().get(1).getName());

            checkPomoSession("lab", "2022-02-07T14:00", "2022-02-07T16:30",
                    new PomodoroTimerSettings(50, 10, 35, 2), 4,
                    new Activity("MyCourse"), (PomodoroSession) st.getSessions().get(0));
            checkSession("watch lecture", "2022-02-07T14:00", "2022-02-07T16:30",
                    new Activity("MyCourse"), st.getSessions().get(1));

            checkSession("A book title: chapters 1 - 3", "2022-02-07T16:30", "2022-02-07T18:00",
                    new Activity("Reading time"), st.getSessions().get(2));

            checkSession("null activity", "2022-02-07T16:30", "2022-02-07T18:00", st.getSessions().get(3));

            checkPomoSession("null activity", "2022-02-07T14:00", "2022-02-07T16:30",
                    new PomodoroTimerSettings(50, 10, 35, 2), 4,
                    (PomodoroSession) st.getSessions().get(4));
        } catch (IOException e) {
            fail("Unable to read file");
        }
    }

}