package model;

import model.exception.DuplicateActivityException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// References:
//  toJson method: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

// Represents a study session tracker with activities to track and pomodoro timer settings
public class StudyTracker implements Writable {
    private List<Activity> activities;
    private List<Session> sessions;
    private PomodoroTimerSettings timerSettings;

    // EFFECTS: constructs study tracker with empty list of activities and sessions and default pomodoro timer settings
    public StudyTracker() {
        activities = new ArrayList<>();
        sessions = new ArrayList<>();
        timerSettings = new PomodoroTimerSettings();
    }

    // MODIFIES: this
    // EFFECTS: adds given activity to list of activities,
    //          if activity with same name already exists, throws DuplicateActivityException
    public void addActivity(Activity a) throws DuplicateActivityException {
        for (Activity next : activities) {
            if (a.getName().equals(next.getName())) {
                throw new DuplicateActivityException("Activity with name '" + a.getName() + "' already exists.");
            }
        }
        EventLog.getInstance().logEvent(new Event("Activity added to study tracker"));
        this.activities.add(a);
    }

    // MODIFIES: this
    // EFFECTS: adds given session s to study tracker's list of sessions
    public void addSession(Session s) {
        String session = s instanceof PomodoroSession ? "Pomodoro session" : "Session";
        EventLog.getInstance().logEvent(new Event(session + " added to study tracker"));
        this.sessions.add(s);
    }

    // MODIFIES: this
    // EFFECTS: removes given session s from study tracker's list of sessions
    public void removeSession(Session s) {
        EventLog.getInstance().logEvent(new Event("Session removed from study tracker"));
        this.sessions.remove(s);
    }

    // EFFECTS: returns unmodifiable list of sessions filtered by given activity
    public List<Session> filterSessionsByActivity(Activity activity) {
        EventLog.getInstance().logEvent(new Event("Viewed sessions filtered by activity"));
        List<Session> filtered = sessions
                .stream()
                .filter(s -> s.getActivityName().equals(activity == null ? "" : activity.getName()))
                .collect(Collectors.toList());

        return Collections.unmodifiableList(filtered);
    }

    // EFFECTS: returns unmodifiable list of activities
    public List<Activity> getActivities() {
        return Collections.unmodifiableList(activities);
    }

    // EFFECTS: returns unmodifiable list of sessions
    public List<Session> getSessions() {
        EventLog.getInstance().logEvent(new Event("Viewed all sessions"));
        return Collections.unmodifiableList(sessions);
    }

    public PomodoroTimerSettings getTimerSettings() {
        return this.timerSettings;
    }

    public void setTimerSettings(PomodoroTimerSettings settings) {
        this.timerSettings = settings;
    }

    // referenced toJson and thingiesToJson method in WorkRoom class for the following two methods
    // https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("timerSettings", timerSettings.toJson());
        json.put("activities", activitiesToJson());
        json.put("sessions", sessionsToJson());
        return json;
    }

    // EFFECTS: returns activities in this study tracker as a JSON array
    private JSONArray activitiesToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Activity a : getActivities()) {
            jsonArray.put(a.toJson());
        }
        return jsonArray;
    }

    // EFFECTS: returns sessions of this activity as a JSON array
    private JSONArray sessionsToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Session s : getSessions()) {
            jsonArray.put(s.toJson());
        }
        return jsonArray;
    }
}
