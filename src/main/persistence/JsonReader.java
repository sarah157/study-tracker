package persistence;

import model.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.stream.Stream;

// reference: JsonReader class in https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
// Represents a reader that reads a study tracker from JSON data stored in file
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    public StudyTracker read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseStudyTracker(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses study tracker from JSON object and returns it
    private StudyTracker parseStudyTracker(JSONObject jsonObject) {
        StudyTracker st = new StudyTracker();
        setTimerSettings(st, jsonObject);
        addActivities(st, jsonObject);
        addSessions(st, jsonObject);
        return st;
    }

    // MODIFIES: st
    // EFFECTS: parses pomodoro timer settings from JSON object and sets it to st
    private void setTimerSettings(StudyTracker st, JSONObject jsonObject) {
        PomodoroTimerSettings settings = parseTimerSettings(jsonObject);
        st.setTimerSettings(settings);
    }

    // EFFECTS: parses pomodoro timer settings from JSON object and returns it
    private PomodoroTimerSettings parseTimerSettings(JSONObject jsonObject) {
        JSONObject settingsObject = jsonObject.getJSONObject("timerSettings");
        int pomodoro = settingsObject.getInt("pomodoro");
        int shortBreak = settingsObject.getInt("shortBreak");
        int longBreak = settingsObject.getInt("longBreak");
        int pomodoroRepeats = settingsObject.getInt("pomodoroRepeats");
        return new PomodoroTimerSettings(pomodoro, shortBreak, longBreak, pomodoroRepeats);
    }

    // MODIFIES: st
    // EFFECTS: parses activities from JSON object and adds them to st
    private void addActivities(StudyTracker st, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("activities");
        for (Object json : jsonArray) {
            JSONObject nextActivity = (JSONObject) json;
            addActivity(st, nextActivity);
        }
    }

    // MODIFIES: st
    // EFFECTS: parses activity from JSON object and adds it to st
    private void addActivity(StudyTracker st, JSONObject jsonObject) {
        String title = jsonObject.getString("name");
        Activity activity = new Activity(title);
        st.addActivity(activity);
    }

    // MODIFIES: ac
    // EFFECTS: parses sessions from JSON object and adds them to ac
    private void addSessions(StudyTracker st, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("sessions");
        for (Object json : jsonArray) {
            JSONObject nextSession = (JSONObject) json;
            addSession(st, nextSession);
        }
    }

    // MODIFIES: ac
    // EFFECTS: parses session from JSON object and adds it to ac
    private void addSession(StudyTracker st, JSONObject sessionOb) {
        String details = sessionOb.getString("details");
        LocalDateTime start = LocalDateTime.parse(sessionOb.getString("start"));
        LocalDateTime end = LocalDateTime.parse(sessionOb.getString("end"));
        String activityName = sessionOb.getString("activity");
        // https://stackoverflow.com/questions/22940416/fetch-first-element-of-stream-matching-the-criteria
        Activity activity = st.getActivities()
                        .stream()
                        .filter(a -> a.getName().equals(activityName))
                        .findFirst()
                        .orElse(null);
        try {
            int pomodoroMinutes = sessionOb.getInt("pomodoroMinutes");
            PomodoroTimerSettings settings = parseTimerSettings(sessionOb);
            st.addSession(new PomodoroSession(details, start, end, settings, pomodoroMinutes, activity));
        } catch (JSONException e) {
            st.addSession(new Session(details, start, end, activity));
        }
    }

}
