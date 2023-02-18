package ui;

import model.*;
import model.exception.NonPositiveValueException;

import javax.swing.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

// Represents a form to add a new pomodoro session with timer settings fields, details text area, activity selection,
// and option to add new activity
public class AddPomodoroSessionPanel extends AddSessionPanel {
    private PomodoroTimerSettings settings;

    // EFFECTS: constructs panel containing a Pomodoro session form with activity combo box, details text area,
    //          and fields for timer settings
    public AddPomodoroSessionPanel(StudyTrackerGUI parent) {
        super(parent, "New Pomodoro Session", generateLabels(), generateFields(parent.getTracker().getTimerSettings()));
        settings = copyTimerSettings(parent.getTracker().getTimerSettings());
        setName(StudyTrackerGUI.ADD_POMODORO);
        setUpTimerSettingsFields();
        submitButton.setText("Start timer");
        submitButton.addActionListener(new StartTimerListener());
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // MODIFIES: this
                // EFFECTS: updates tracker timer settings if settings were changed in the pomodoro session panel
                if (!parent.getTracker().getTimerSettings().equals(getSettings())) {
                    parent.getTracker().setTimerSettings(getSettings());
                }
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: adds pomodoro session with given start, end and pomodorosCompleted to st
    public void addPomodoroSession(LocalDateTime start, LocalDateTime end, int pomodorosCompleted) {
        try {
            PomodoroSession session = new PomodoroSession(details.getText(), start, end, getSettings(),
                    pomodorosCompleted, combo.getSelectedActivity());
            parent.getTracker().addSession(session);
            addSessionSuccessPane();
            parent.setCurrentPanel(StudyTrackerGUI.ADD_POMODORO);
        } catch (DateTimeParseException err) {
            errorPane(err.getMessage(), "Datetime parse error");
        }
    }

    public PomodoroTimerSettings getSettings() {
        return copyTimerSettings(settings);
    }

    // EFFECTS: generates and returns array of formatted text fields for start and end date times
    private static JTextField[] generateFields(PomodoroTimerSettings settings) {
        JTextField pomodoro = new JTextField("" + settings.getPomodoro());
        JTextField repeats = new JTextField("" + settings.getPomodoroRepeats());
        JTextField shortBreak = new JTextField("" + settings.getShortBreak());
        JTextField longBreak = new JTextField("" + settings.getLongBreak());
        return new JTextField[]{pomodoro, repeats, shortBreak, longBreak};
    }

    // EFFECTS: generates and returns array of labels for start and end date times
    private static JLabel[] generateLabels() {
        JLabel pomodoro = new JLabel("Pomodoro duration (min): ");
        JLabel repeats = new JLabel("Pomodoro repeats: ");
        JLabel shortBreak = new JLabel("Short break duration (min): ");
        JLabel longBreak = new JLabel("Long break duration (min): ");
        return new JLabel[]{pomodoro, repeats, shortBreak, longBreak};
    }

    // MODIFIES: this
    // EFFECTS: sets up pomodoro timer settings fields
    private void setUpTimerSettingsFields() {
        JTextField pomodoro = (JTextField) extraFields[0];
        JTextField repeats = (JTextField) extraFields[1];
        JTextField shortBreak = (JTextField) extraFields[2];
        JTextField longBreak = (JTextField) extraFields[3];

        pomodoro.setName("pomodoro");
        repeats.setName("repeats");
        shortBreak.setName("short break");
        longBreak.setName("long break");

        pomodoro.addFocusListener(new UpdateSettingsListener());
        repeats.addFocusListener(new UpdateSettingsListener());
        shortBreak.addFocusListener(new UpdateSettingsListener());
        longBreak.addFocusListener(new UpdateSettingsListener());
    }

    // EFFECTS: returns copy of ts
    private PomodoroTimerSettings copyTimerSettings(PomodoroTimerSettings ts) {
        return new PomodoroTimerSettings(ts.getPomodoro(), ts.getShortBreak(), ts.getLongBreak(),
                ts.getPomodoroRepeats());
    }

    // Represents action taken when timer to start timer
    private class StartTimerListener implements ActionListener {
        // MODIFIES: this
        // EFFECTS: starts the timer
        @Override
        public void actionPerformed(ActionEvent e) {
            parent.setCurrentPanel(StudyTrackerGUI.RUN_TIMER);
        }
    }

    // Represents action taken when timer settings are changed
    private class UpdateSettingsListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
        }

        // MODIFIES: this
        // EFFECTS: sets timer settings field to the integer inputted at the updated field after losing focus
        // if the input cannot be parsed to an integer or integer less than zero, a pane pops up with the error
        @Override
        public void focusLost(FocusEvent e) {
            JTextField field = (JTextField) e.getSource();
            try {
                switch (field.getName()) {
                    case "pomodoro":
                        settings.setPomodoro(Integer.parseInt(field.getText()));
                        break;
                    case "repeats":
                        settings.setPomodoroRepeats(Integer.parseInt(field.getText()));
                        break;
                    case "short break":
                        settings.setShortBreak(Integer.parseInt(field.getText()));
                        break;
                    default:
                        settings.setLongBreak(Integer.parseInt(field.getText()));
                        break;
                }
            } catch (NumberFormatException err) {
                errorPane(field.getName() + " value must be an integer", "Invalid input");
            } catch (NonPositiveValueException err) {
                errorPane("Value must be greater than 0", "Invalid input");
            }
        }
    }

}
