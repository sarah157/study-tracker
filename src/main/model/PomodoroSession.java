package model;

import model.exception.InvalidDateTimeInterval;
import org.json.JSONObject;

import java.time.LocalDateTime;

// Represents a pomodoro session entry
public class PomodoroSession extends Session {
    private final PomodoroTimerSettings timerSettings;    // timer settings used during session
    private final int pomodoroMinutes;

    // REQUIRES: details is non-empty string, and end is later than start
    // EFFECTS: constructs pomodoro study session with given details, start, end, timer settings used, and activity
    //          cycles completed and pomodoro intervals completed
    public PomodoroSession(String details, LocalDateTime start, LocalDateTime end,
                           PomodoroTimerSettings timerSettings, int pomodorosMinutes, Activity activity)
            throws InvalidDateTimeInterval {
        super(details, start, end, activity);
        this.timerSettings = timerSettings;
        this.pomodoroMinutes = pomodorosMinutes;
    }

    public PomodoroTimerSettings getTimerSettings() {
        return timerSettings;
    }

    public int getPomodoroMinutes() {
        return pomodoroMinutes;
    }

    // EFFECTS: returns the number of timer cycles completed (i.e., count of completion of all pomodoro repeats)
    public int getCyclesCompleted() {
        return (getPomodoroMinutes() / getTimerSettings().getPomodoro()) / getTimerSettings().getPomodoroRepeats();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("timerSettings", getTimerSettings().toJson());
        json.put("pomodoroMinutes", getPomodoroMinutes());
        return json;
    }
}
