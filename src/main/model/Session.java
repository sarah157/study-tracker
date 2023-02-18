package model;

import model.exception.InvalidDateTimeInterval;
import org.json.JSONObject;
import persistence.Writable;

import java.time.Duration;
import java.time.LocalDateTime;

// Represents a study session entry with session details, date, start time, and end time
public class Session implements Writable {
    private String details;
    private LocalDateTime start;
    private LocalDateTime end;
    private Activity activity;

    // REQUIRES: details is non-empty string, and end is later than start
    // EFFECTS: constructs study session with given details, start and end, and given activity
    public Session(String details, LocalDateTime start, LocalDateTime end, Activity activity)
            throws InvalidDateTimeInterval {
        if (start.isAfter(end)) {
            throw new InvalidDateTimeInterval();
        }
        this.details = details;
        this.start = start;
        this.end = end;
        this.activity = activity;
    }

    public String getDetails() {
        return this.details;
    }

    public LocalDateTime getStart() {
        return this.start;
    }

    public LocalDateTime getEnd() {
        return this.end;
    }

    // EFFECTS: returns activity title if activity is not null, else ""
    public String getActivityName() {
        return activity == null ? "" : this.activity.getName();
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    // EFFECTS: returns duration of session in minutes
    public long getDuration() {
        return Duration.between(getStart(), getEnd()).toMinutes();
    }

    // referenced toJson method in Thingy class in https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("details", getDetails());
        json.put("start", getStart().toString());
        json.put("end", getEnd().toString());
        json.put("activity", getActivityName());
        return json;
    }
}
