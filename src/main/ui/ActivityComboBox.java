package ui;

import model.Activity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

// Represents an activity combo box
public class ActivityComboBox extends JComboBox<String> {
    Activity selectedActivity;
    List<Activity> activities;

    // EFFECTS: constructs an activity combo box using the given list of activity titles and sets activities list
    public ActivityComboBox(List<Activity> activities) {
        this.activities = new ArrayList<>();
        this.activities.addAll(activities);
        addActivityTitles();
        addActionListener(new SelectionListener());
        setPreferredSize(new Dimension(175, 30));
    }

    public Activity getSelectedActivity() {
        return selectedActivity;
    }

    // MODIFIES: this
    // EFFECTS: adds given activity to activities list and its title to comboBox items,
    // and sets selected index to the index of activity
    public void addActivity(Activity a) {
        activities.add(a);
        addItem(a.getName());
        setSelectedIndex(activities.size());
    }

    // EFFECTS: returns array of activities' titles
    private void addActivityTitles() {
        addItem("--"); // null activity
        for (Activity a : activities) {
            addItem(a.getName());
        }
    }

    // Represents action to be taken when an activity is selected
    private class SelectionListener implements ActionListener {

        // MODIFIES: this
        // EFFECTS: If selected index is 0, selectedActivity is set to null
        //          otherwise, sets selectedActivity to activities.get(selected index minus one)
        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            int idx = cb.getSelectedIndex();
            selectedActivity = idx == 0 ? null : activities.get(idx - 1);
        }
    }
}
