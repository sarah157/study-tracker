package model;

import model.exception.NonPositiveValueException;
import org.json.JSONObject;
import persistence.Writable;

import java.util.Objects;

// Represents settings for a pomodoro timer with a pomodoro duration, short break duration
// long break duration and number of pomodoro repeats
public class PomodoroTimerSettings implements Writable {
    public static final int DEFAULT_POMODORO = 25;
    public static final int DEFAULT_SHORT_BREAK = 5;
    public static final int DEFAULT_LONG_BREAK = 25;
    public static final int DEFAULT_REPEATS = 4;

    private int pomodoro;       // duration of pomodoro/work interval in minutes
    private int shortBreak;     // duration of short break interval in minutes
    private int longBreak;      // duration of long break interval in minutes
    private int pomodoroRepeats;        // number of pomodoro repeats before long break

    // REQUIRES: pomodoro, shortBreak, longBreak, repeats >= 1
    //           and longBreak >= shortBreak
    // EFFECTS: constructs pomodoro timer settings with given pomodoro duration (min),
    //          short break duration (min), long break duration (min), and number of pomodoro repeats
    public PomodoroTimerSettings(int pomodoro, int shortBreak, int longBreak, int pomodoroRepeats)
            throws NonPositiveValueException {
        setPomodoro(pomodoro);
        setShortBreak(shortBreak);
        setLongBreak(longBreak);
        setPomodoroRepeats(pomodoroRepeats);
    }

    // EFFECTS: constructs pomodoro timer settings with default timer settings
    public PomodoroTimerSettings() {
        this.pomodoro = DEFAULT_POMODORO;
        this.shortBreak = DEFAULT_SHORT_BREAK;
        this.longBreak = DEFAULT_LONG_BREAK;
        this.pomodoroRepeats = DEFAULT_REPEATS;
    }

    public int getPomodoro() {
        return this.pomodoro;
    }

    public int getShortBreak() {
        return this.shortBreak;
    }

    public int getLongBreak() {
        return this.longBreak;
    }

    public int getPomodoroRepeats() {
        return this.pomodoroRepeats;
    }

    // MODIFIES: this
    // EFFECTS: if given value is positive, sets pomodoro to it
    public void setPomodoro(int minutes) throws NonPositiveValueException {
        checkPositiveValue(minutes);
        this.pomodoro = minutes;
    }

    // MODIFIES: this
    // EFFECTS: if given value is positive, sets shortBreak to it
    public void setShortBreak(int minutes) throws NonPositiveValueException {
        checkPositiveValue(minutes);
        this.shortBreak = minutes;
    }

    // MODIFIES: this
    // EFFECTS: if given value is positive, sets longBreak to it
    public void setLongBreak(int minutes) throws NonPositiveValueException {
        checkPositiveValue(minutes);
        this.longBreak = minutes;
    }

    // MODIFIES: this
    // EFFECTS: if given value is positive, sets pomodoroRepeats to it
    public void setPomodoroRepeats(int pomodoroRepeats) throws NonPositiveValueException {
        checkPositiveValue(pomodoroRepeats);
        this.pomodoroRepeats = pomodoroRepeats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PomodoroTimerSettings settings = (PomodoroTimerSettings) o;
        return this.toString().equals(settings.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(pomodoro, shortBreak, longBreak, pomodoroRepeats);
    }

    // EFFECTS: returns timer settings in format: pomodoro: 25 min, short: 5m in, long: 25 min, pomodoro repeats: 4
    @Override
    public String toString() {
        return "pomodoro: " + getPomodoroRepeats() + " x " + getPomodoro() + " min, short: " + getShortBreak()
                + " min, long: " + getLongBreak() + " min";
    }

    // referenced toJson method in Thingy class in https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("pomodoro", getPomodoro());
        json.put("shortBreak", getShortBreak());
        json.put("longBreak", getLongBreak());
        json.put("pomodoroRepeats", getPomodoroRepeats());
        return json;
    }

    // EFFECTS: if given value is non-positive, throws a NonPositiveValueException
    private void checkPositiveValue(int value) throws NonPositiveValueException {
        if (value <= 0) {
            throw new NonPositiveValueException();
        }
    }
}
