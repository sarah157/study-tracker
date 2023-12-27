package ui;

import model.Activity;
import model.exception.InvalidActivityException;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import static ui.StudyTrackerGUI.SCREEN_WIDTH;

// Represents a form to add a new session with a details text area, activity selection, and option to add new activity
public abstract class AddSessionPanel extends SessionPanel {
    protected JTextArea details;
    protected GridBagConstraints gridBagConstraints;
    protected JPanel sessionForm;
    protected JLabel[] extraLabels;
    protected JComponent[] extraFields;
    protected JButton submitButton;

    // EFFECTS: constructs panel containing form with the given title, activity combo box, details text area,
    //          and given extra labels and extra fields
    public AddSessionPanel(StudyTrackerGUI parent, String title, JLabel[] extraLabels,
                           JComponent[] extraFields) {
        super(parent);
        this.extraLabels = extraLabels;
        this.extraFields = extraFields;
        details = new JTextArea();
        submitButton = new JButton("Submit");
        gridBagConstraints = new GridBagConstraints();
        sessionForm = new JPanel(new GridBagLayout());
        sessionForm.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(20, 10, 10, 10)));
        sessionForm.setMaximumSize(new Dimension(400, 600));

        // add components
        setLayout(new FlowLayout(FlowLayout.CENTER));
        addTitle(title);
        addActivityArea();
        addLabelTextRows(extraLabels, extraFields, gridBagConstraints, sessionForm);
        addDetailsArea();
        sessionForm.add(new JLabel(" "), gridBagConstraints);
        sessionForm.add(submitButton, gridBagConstraints);
        add(sessionForm);
    }

    // MODIFIES: this
    // EFFECTS: adds given title to this panel
    private void addTitle(String title) {
        JLabel label = new JLabel(title);
        label.setBorder(BorderFactory.createEmptyBorder(50, SCREEN_WIDTH / 2, 10, SCREEN_WIDTH / 2));
        label.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(label);
    }

    // references: https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TextSamplerDemoProject/src/components/TextSamplerDemo.java
    // MODIFIES: this
    // EFFECTS: adds labels and fields pairs as a row to container with grid bag constraints c
    protected void addLabelTextRows(JLabel[] labels, JComponent[] fields, GridBagConstraints c, Container container) {
        int numLabels = labels.length;
        c.weighty = 0.1;
        for (int i = 0; i < numLabels; i++) {
            c.gridwidth = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;
            c.weightx = 0.0;
            container.add(labels[i], c);

            c.gridwidth = GridBagConstraints.REMAINDER;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            container.add(fields[i], c);
        }
    }

    // MODIFIES: this
    // EFFECTS: adds activity combo box and add activity button to form
    protected void addActivityArea() {
        JButton addAct = new JButton("new activity");
        addAct.addActionListener(new AddActivityHandler());

        JPanel activityArea = new JPanel(new BorderLayout());
        activityArea.add(combo, BorderLayout.WEST);
        activityArea.add(addAct, BorderLayout.EAST);

        JLabel activityLabel = new JLabel("Activity: ");
        activityLabel.setLabelFor(activityArea);

        gridBagConstraints.anchor = GridBagConstraints.EAST;
        addLabelTextRows(new JLabel[]{activityLabel}, new JComponent[]{activityArea}, gridBagConstraints, sessionForm);
    }

    // EFFECTS: displays an add session success pane
    protected void addSessionSuccessPane() {
        // image source: https://emojis.wiki/raising-hands/
        ImageIcon icon = StudyTrackerGUI.resizeImageIcon("data/success-icon.png", 40, 40);
        JOptionPane.showMessageDialog(parent,
                "Session successfully added!", "Success", JOptionPane.INFORMATION_MESSAGE, icon);
    }

    // EFFECTS: displays an error pane with the given message and title
    protected void errorPane(String message, String title) {
        // image source: https://emojis.wiki/confused-face/
        ImageIcon icon = StudyTrackerGUI.resizeImageIcon("data/error-icon.png", 40, 40);
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE, icon);
    }

    // MODIFIES: this
    // EFFECTS: adds details text area to form
    protected void addDetailsArea() {
        details = new JTextArea();
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        JScrollPane areaScrollPane = new JScrollPane(details);

        JPanel detailsArea = new JPanel(new BorderLayout());
        detailsArea.add(areaScrollPane, BorderLayout.CENTER);
        detailsArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        detailsArea.setMinimumSize(new Dimension(150, 125));
        detailsArea.setPreferredSize(new Dimension(150, 125));

        JLabel detailsLabel = new JLabel("Details: ");
        detailsLabel.setLabelFor(detailsArea);

        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        addLabelTextRows(new JLabel[]{detailsLabel}, new JComponent[]{detailsArea}, gridBagConstraints, sessionForm);
    }

    // references: AlarmSystem AddSensorAction class https://github.students.cs.ubc.ca/CPSC210/AlarmSystem
    // Represents the action to be taken when the user wants to add a new activity to the study tracker.
    protected class AddActivityHandler implements ActionListener {

        // MODIFIES: this
        // EFFECTS: prompts user to enter name for new activity and adds new activity to st and combo
        //          if name given is an empty string or a duplicate, pane pops up with the error
        //          if user presses cancel, nothing happens
        @Override
        public void actionPerformed(ActionEvent evt) {
            String name = (String) JOptionPane.showInputDialog(
                    parent,
                    "Enter activity name",
                    "New Activity",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null);
            try {
                Activity activity = new Activity(name);
                parent.getTracker().addActivity(activity);
                combo.addActivity(activity);
            } catch (InvalidActivityException e) {
                errorPane(e.getMessage(), "Unable to add activity");
            } catch (NullPointerException e) {
                // catch and do nothing if user presses cancel
            }
        }
    }
}
