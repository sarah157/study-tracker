package ui;

import javax.swing.*;

public abstract class SessionPanel extends JPanel {
    protected StudyTrackerGUI parent;
    protected ActivityComboBox combo;

    public SessionPanel(StudyTrackerGUI parent) {
        this.parent = parent;
        combo = new ActivityComboBox(parent.getTracker().getActivities());
    }
}
