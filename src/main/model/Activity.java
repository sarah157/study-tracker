package model;

import model.exception.EmptyNameException;
import org.json.JSONObject;
import persistence.Writable;

// Represents an activity with a title and list of study sessions
public class Activity implements Writable {
    private String name;

    // EFFECTS: constructs activity with given name and empty list of sessions
    //         if name is empty string, throws EmptyNameException
    public Activity(String name) throws EmptyNameException {
        if (name.isEmpty()) {
            throw new EmptyNameException("Empty name not valid");
        }
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    // referenced toJson and thingiesToJson method in WorkRoom class for the following two methods
    // https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", this.getName());
        return json;
    }

}
