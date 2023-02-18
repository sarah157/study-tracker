package ui;

import model.Session;
import model.exception.InvalidDateTimeInterval;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static java.time.LocalDateTime.parse;

// Represents a form to add a new session with start time, end time, details text area, activity selection,
//  and option to add new activity
public class AddRegularSessionPanel extends AddSessionPanel {
    private JTextField start;
    private JTextField end;

    // EFFECTS: constructs panel containing add session form with an activity combo box, details text area,
    //          and fields for start time and end time
    public AddRegularSessionPanel(StudyTrackerGUI parent) {
        super(parent, "Add Completed Session", generateLabels(), generateFields());
        setName(StudyTrackerGUI.ADD_COMPLETED);
        start = (JTextField) extraFields[0];
        end = (JTextField) extraFields[1];
        submitButton.addActionListener(new AddRegularSessionHandler());
    }

    // EFFECTS: generates and returns list of formatted text fields for start and end date times
    private static JTextField[] generateFields() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        JTextField start = new JTextField(df.format(LocalDateTime.now()));
        JTextField end = new JTextField(df.format(LocalDateTime.now()));
        start.setActionCommand("start");
        end.setActionCommand("end");
        return new JTextField[]{start, end};
    }

    // EFFECTS: generates and returns list of labels for start and end date times
    private static JLabel[] generateLabels() {
        JLabel start = new JLabel("Start: ");
        JLabel end = new JLabel("End: ");
        return new JLabel[]{start, end};
    }

    // Represents the action to be taken when the user wants to add a new activity to the study tracker.
    protected class AddRegularSessionHandler implements ActionListener {

        // MODIFIES: this
        // EFFECTS: adds new session to st with details, start, end, and selected activity
        //          if start or end cannot be parsed, an error pane pops up with a datetime parse error
        //          if start is later than end, an error pane pops with an invalid interval error
        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                Session session = new Session(details.getText(), parse(start.getText(), df),
                        parse(end.getText(), df), combo.getSelectedActivity());
                parent.getTracker().addSession(session);
                parent.setCurrentPanel(StudyTrackerGUI.ADD_COMPLETED);
                addSessionSuccessPane();
            } catch (DateTimeParseException e) {
                errorPane(e.getMessage(), "Datetime parse error");
            } catch (InvalidDateTimeInterval e) {
                errorPane(e.getMessage(), "Invalid datetime interval");
            }
        }
    }

}
