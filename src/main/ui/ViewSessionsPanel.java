package ui;

import model.Activity;
import model.PomodoroSession;
import model.Session;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Represents a view session panel that displays a study tracker's sessions
public class ViewSessionsPanel extends SessionPanel implements ListSelectionListener {
    private static final int DIVIDER_LOC = 400;
    
    private JSplitPane splitPane;

    // left panel (sessions list)
    private JPanel view;
    private DefaultListModel<String> listModel;
    private JList list;
    private List<Session> sessions;

    // right panel (view selected session)
    private JLabel date;
    private JLabel time;
    private JLabel activity;
    private JTextArea details;
    private JScrollPane detailsScrollPane;
    private JLabel timerSettings;
    private JLabel pomodoroMinutes;

    // reference: https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/SplitPaneDemoProject/src/components/SplitPaneDemo.java
    // EFFECTS: constructs a split pane that contains a session list scroll pane and a session view scroll pane
    public ViewSessionsPanel(StudyTrackerGUI parent) {
        super(parent);
        initializeListPaneFields();
        initializeViewPaneFields();

        JPanel listPane = new JPanel(new BorderLayout());
        listPane.add(combo, BorderLayout.NORTH);
        listPane.add(list, BorderLayout.CENTER);

        JScrollPane viewScrollPane = new JScrollPane(view);
        JScrollPane listScrollPane = new JScrollPane(listPane);

        Dimension minimumSize = new Dimension(100, 50);
        listScrollPane.setMinimumSize(minimumSize);
        viewScrollPane.setMinimumSize(minimumSize);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, viewScrollPane);
        splitPane.setName(StudyTrackerGUI.VIEW);
        splitPane.setDividerLocation(DIVIDER_LOC);
        add(splitPane);
        updateViewPanel();
    }

    public JSplitPane getSplitPane() {
        return splitPane;
    }

    // EFFECTS: returns name of splitPane
    @Override
    public String getName() {
        return splitPane.getName();
    }

    // MODIFIES: this
    // EFFECTS: listens to the changes in list selection and updates the view panel
    public void valueChanged(ListSelectionEvent e) {
        updateViewPanel();
    }

    // MODIFIES: this
    // EFFECTS: if sessions is empty or no session index is selected, a no sessions message is displayed in view panel
    //          otherwise, selected session is displayed in the view panel
    private void updateViewPanel() {
        view.removeAll();
        if (sessions.isEmpty() || list.getSelectedIndex() == -1) {
            showNoSessionsMessage();
        } else {
            showSelectedSession(sessions.get(list.getSelectedIndex()));
        }
        view.revalidate();
        view.repaint();
    }

    // MODIFIES: this
    // EFFECTS: renders the selected session in the view panel
    private void showSelectedSession(Session s) {
        date.setText(parseDate(s.getStart()));
        time.setText(parseTime(s.getStart()) + " - " + parseTime(s.getEnd()));
        activity.setText(s.getActivityName().isEmpty() ? "--" : s.getActivityName());
        details.setText(s.getDetails());

        List<JComponent> rows = new ArrayList<>();
        rows.add(createRow(null, date));
        rows.add(createRow(null, time));
        rows.add(new JSeparator());
        rows.add(createRow(new JLabel("Activity:"), activity));

        if (s instanceof PomodoroSession) {
            timerSettings.setText(((PomodoroSession) s).getTimerSettings().toString());
            pomodoroMinutes.setText("" + ((PomodoroSession) s).getPomodoroMinutes());
            rows.add(createRow(new JLabel("Timer settings used:"), timerSettings));
            rows.add(createRow(new JLabel("Pomodoro minutes completed:"), pomodoroMinutes));
        }

        rows.add(createRow(new JLabel("Details:"), null));
        addRows(rows);
        JButton delete = new JButton("Delete");
        delete.addActionListener(new DeleteSessionListener());
        delete.setBounds(400, 500, 75, 30);
        view.add(delete);
    }

    // MODIFIES: this
    // EFFECTS: renders a "no sessions" message in the view panel
    private void showNoSessionsMessage() {
        JLabel info = new JLabel("No sessions here. Add your first one. •ᴗ•");
        info.setFont(new Font("Serif", Font.PLAIN, 15));
        info.setBounds(10, 0, 500, 30);
        view.add(info);
    }

    // reference: https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/SplitPaneDemoProject/src/components/SplitPaneDemo.java
    // MODIFIES: this
    // EFFECTS: initializes fields in list scroll pane
    private void initializeListPaneFields() {
        sessions = new ArrayList<>();
        sessions.addAll(parent.getTracker().getSessions());

        combo = new ActivityComboBox(parent.getTracker().getActivities());
        combo.addActivity(new Activity("All Activities"));
        combo.addActionListener(new FilterActivityListener());

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setModel(listModel);
        loadListModel();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
    }

    // MODIFIES: this
    // EFFECTS: initializes fields in view scroll pane
    private void initializeViewPaneFields() {
        view = new JPanel(null);
        view.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        date = new JLabel();
        date.setFont(new Font("Serif", Font.PLAIN, 20));

        time = new JLabel();
        time.setFont(new Font("Serif", Font.PLAIN, 15));

        activity = new JLabel();
        timerSettings = new JLabel();
        pomodoroMinutes = new JLabel();

        details = new JTextArea();
        details.setDisabledTextColor(Color.BLACK);
        details.setEnabled(false);
        detailsScrollPane = new JScrollPane(details);
        detailsScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    // EFFECTS: combines given label and field into a panel and returns it
    private JPanel createRow(JLabel label, JComponent field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if (label != null) {
            label.setFont(new Font("Serif", Font.BOLD, 12));
            row.add(label);
        }
        if (field != null) {
            row.add(field);
            field.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        }
        return row;
    }

    // MODIFIES: this
    // EFFECTS: adds given JComponent rows to the view scroll pane followed by details scroll pane and delete button
    private void addRows(List<JComponent> rows) {
        int y = 10;
        for (JComponent row : rows) {
            row.setBounds(10, y, 500, 30);
            view.add(row);
            y += 30;
        }
        // get details label (last index) and make width smaller
        rows.get(rows.size() - 1).setBounds(10, y - 30, 55, 30);
        // add details scroll pane beside details label
        detailsScrollPane.setBounds(70, y - 23, 400, 125);
        view.add(detailsScrollPane);
    }

    // MODIFIES: this
    // EFFECTS: populates list model with given sessions
    private void loadListModel() {
        listModel.removeAllElements();
        for (Session s : sessions) {
            String str = parseShortDate(s.getStart())
                    + ", " + parseTime(s.getStart()) + ", " + s.getDetails();
            if (str.length() > 55) {
                str = str.substring(0, 55) + "...";
            }
            listModel.addElement(str);
        }
        list.setSelectedIndex(0);
    }

    // EFFECTS: returns time in string format h:mm a from given datetime. e.g. 1:30 PM, 11:00 AM
    private String parseTime(LocalDateTime datetime) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        return timeFormatter.format(datetime);
    }

    // EFFECTS: returns date in string format EEE MMM-dd-yyyy from given datetime. e.g. Mon Feb-07-2022
    private String parseDate(LocalDateTime datetime) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        return dateFormatter.format(datetime);
    }

    // EFFECTS: returns date in string format EEE MMM-dd-yyyy from given datetime. e.g. Mon Feb-07-2022
    private String parseShortDate(LocalDateTime datetime) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM-dd-yyyy");
        return dateFormatter.format(datetime);
    }

    // Represents action to be taken when user deletes a session
    private class DeleteSessionListener implements ActionListener {

        // MODIFIES: this
        // EFFECTS: removes selected sessions from list model and study tracker sessions
        @Override
        public void actionPerformed(ActionEvent e) {
            int idx = list.getSelectedIndex();
            Session session = sessions.get(idx);
            listModel.removeElementAt(idx);
            parent.getTracker().removeSession(session);
            if (list.getComponentCount() > 0) {
                list.setSelectedIndex(Math.max(0, idx - 1));
            }
        }
    }

    // Represents action to be taken when user selects an activity to filter sessions
    private class FilterActivityListener implements ActionListener {

        // MODIFIES: this
        // EFFECTS: filters sessions based on select activity then updates listModel
        @Override
        public void actionPerformed(ActionEvent e) {
            int idx = combo.getSelectedIndex();
            if (idx == combo.getItemCount() - 1) { // selection is "All activities"
                sessions = parent.getTracker().getSessions();
            } else {
                Activity activity = idx == 0 ? null : parent.getTracker().getActivities().get(idx - 1);
                sessions = parent.getTracker().filterSessionsByActivity(activity);
            }
            loadListModel();
        }
    }
}
