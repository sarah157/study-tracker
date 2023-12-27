package ui;

import model.PomodoroTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;

// Represents a pomodoro timer panel
public class TimerPanel extends JPanel {
    private PomodoroTimer timer;
    private AddPomodoroSessionPanel psp;
    private JLabel interval;
    private JLabel timeRemaining;
    private JLabel cycleNumber;
    private JLabel repeatsRemaining;
    private JLabel totalPomodorosCompleted;
    private JPanel sunflowerField;  // number of sunflowers = number of pomodoros completed
    private ImageIcon sunflower;
    private ImageIcon seedling;

    // EFFECTS: constructs timer panel with countdown, cycle info, button panel, and sunflower field
    //          timer is set using given settings and is initially not running
    public TimerPanel(AddPomodoroSessionPanel psp) {
        this.psp = psp;
        timer = new PomodoroTimer(psp.getSettings());
        setName(StudyTrackerGUI.RUN_TIMER);
        setLayout(new BorderLayout());
        addCountdownAndCycleInfo();
        addButtonPanel();
        addPomodoroSunflowerField();
    }

    // MODIFIES: this
    // EFFECTS: starts the pomodoro timer, continues until user stops the timer
    public void runTimer() {
        TimerTask task = new TimerPanelTimerTask();
        timer.start(task);
    }

    // MODIFIES: this
    // EFFECTS: sets up timer interval, time remaining and cycle info graphics
    private void addCountdownAndCycleInfo() {
        interval = new JLabel();
        interval.setFont(new Font("Dialog", Font.ITALIC, 30));
        interval.setHorizontalAlignment(SwingConstants.CENTER);

        timeRemaining = new JLabel();
        timeRemaining.setFont(new Font("Dialog", Font.BOLD, 90));
        timeRemaining.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel cycleInfo = new JPanel();
        addCycleInfo(cycleInfo);

        JPanel countdown = new JPanel();
        countdown.setLayout(new BorderLayout());
        countdown.setBorder(BorderFactory.createEmptyBorder(75, 5, 25, 5));
        countdown.add(interval, BorderLayout.NORTH);
        countdown.add(timeRemaining, BorderLayout.CENTER);
        countdown.add(cycleInfo, BorderLayout.SOUTH);

        add(countdown, BorderLayout.NORTH);
    }

    // MODIFIES: this
    // EFFECTS: sets up cycle info fields and graphics
    private void addCycleInfo(JPanel cycleInfo) {
        cycleNumber = new JLabel();
        cycleNumber.setAlignmentX(CENTER_ALIGNMENT);

        repeatsRemaining = new JLabel();
        repeatsRemaining.setAlignmentX(CENTER_ALIGNMENT);

        totalPomodorosCompleted = new JLabel();
        totalPomodorosCompleted.setAlignmentX(CENTER_ALIGNMENT);


        cycleInfo.setLayout(new BoxLayout(cycleInfo, BoxLayout.Y_AXIS));
        cycleInfo.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        cycleInfo.add(cycleNumber);
        cycleInfo.add(repeatsRemaining);
        cycleInfo.add(totalPomodorosCompleted);
    }

    // MODIFIES: this
    // EFFECTS: sets up pause/resume and end session buttons
    private void addButtonPanel() {
        JPanel buttons = new JPanel();

        JButton endButton = new JButton("End session");
        endButton.addActionListener(new EndTimerListener());
        endButton.setPreferredSize(new Dimension(100, 40));

        JButton pauseResumeButton = new JButton("Pause/Resume");
        pauseResumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer.isRunning()) {
                    timer.pause();
                } else {
                    timer.resume();
                }
            }
        });
        pauseResumeButton.setPreferredSize(new Dimension(125, 40));
        buttons.add(pauseResumeButton);
        buttons.add(endButton, BorderLayout.SOUTH);
        add(buttons, BorderLayout.CENTER);
    }

    // MODIFIES: this
    // EFFECTS: sets up seedling and sunflower image icons and sunflower field graphics
    private void addPomodoroSunflowerField() {
        seedling = StudyTrackerGUI.resizeImageIcon("data/pomodoro-seedling.png", 40, 40);
        sunflower = StudyTrackerGUI.resizeImageIcon("data/pomodoro-sunflower.png", 60, 60);

        sunflowerField = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JScrollPane sp = new JScrollPane(sunflowerField);
        sp.setPreferredSize(new Dimension(800, 100));
        sp.setBorder(null);
        add(sp, BorderLayout.SOUTH);
    }

    // EFFECTS: returns given seconds to a string in the format mm:ss
    private String prettyTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds - (min * 60);
        return String.format("%02d:%02d", min, sec);
    }

    // Represents action taken to end timer and save session
    private class EndTimerListener implements ActionListener {

        // MODIFIES: this
        // EFFECTS: cancels the timer, adds pomodoro session to study tracker
        //          and sets the current panel to pomodoro session panel
        @Override
        public void actionPerformed(ActionEvent e) {
            timer.cancel();
            psp.addPomodoroSession(timer.getStart(), timer.getEnd(), timer.getTotalPomodoroMinutes());
        }
    }

    // Represents task to be executed when the pomodoro timer is running
    private class TimerPanelTimerTask extends TimerTask {

        // EFFECTS: adds seedling to sunflower field and sets JLabel fields to initial timer values
        public TimerPanelTimerTask() {
            addSeedling();
            updateTextFields();
        }

        // MODIFIES: this
        // EFFECTS: if timer is not paused, decrements timer, updates JLabel fields
        // and sunflower field according to current timer
        @Override
        public void run() {
            updateTextFields();
            updateSunflowerField();
        }

        // Seedling is added to the sunflower field at the start of a pomodoro interval.
        // At the end the pomodoro, the seedling is replaced with a sunflower
        // MODIFIES: this
        // EFFECTS: if current interval is pomodoro and last component in field is a sunflower,
        //              add seedling to field
        //          if current interval is not pomodoro and the last component in the field is a seedling,
        //              last component is replaced with a sunflower
        private void updateSunflowerField() {
            JLabel last = (JLabel) sunflowerField.getComponent(sunflowerField.getComponentCount() - 1);

            if (timer.getCurrentInterval().equals(PomodoroTimer.POMODORO_INTERVAL)
                    && last.getName().equals("sunflower")) {
                addSeedling();
                return;
            }

            if (!timer.getCurrentInterval().equals(PomodoroTimer.POMODORO_INTERVAL)
                    && last.getName().equals("seedling")) {
                sunflowerField.remove(last);
                addSunflower();
            }
        }

        // MODIFIES: this
        // EFFECTS: sets fields according to current timer
        private void updateTextFields() {
            interval.setText(timer.getCurrentInterval());
            timeRemaining.setText(prettyTime(timer.getTimeRemaining()));
            cycleNumber.setText("Cycle #" + (timer.getTotalCycles() + 1));
            repeatsRemaining.setText("Pomodoros left until long break: " + timer.getRepeatsRemaining());
            totalPomodorosCompleted.setText("Total pomodoros completed: " + timer.getTotalPomodoroIntervals()
                    + " x " + timer.getPomodoroDuration() / 60 + " min");
        }

        // MODIFIES: this
        // EFFECTS: adds JLabel with seedling icon and name set to "seedling" to sunflowerField
        private void addSeedling() {
            JLabel seedlingLabel = new JLabel(seedling);
            seedlingLabel.setName("seedling");
            sunflowerField.add(seedlingLabel);
        }

        // MODIFIES: this
        // EFFECTS: adds JLabel with sunflower icon and name set to "sunflower" to sunflowerField
        private void addSunflower() {
            JLabel sunflowerLabel = new JLabel(sunflower);
            sunflowerLabel.setName("sunflower");
            sunflowerField.add(sunflowerLabel);
        }
    }
}
