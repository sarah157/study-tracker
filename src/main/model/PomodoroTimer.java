package model;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

// Represents a pomodoro timer (units in seconds) with given pomodoro timer settings
public class PomodoroTimer extends Timer {
    public static final String POMODORO_INTERVAL = "pomodoro";
    public static final String SHORT_BREAK_INTERVAL = "short break";
    public static final String LONG_BREAK_INTERVAL = "long break";

    private boolean running;
    private LocalDateTime start;
    private LocalDateTime end;
    private final int pomodoroDuration;
    private final int shortBreakDuration;
    private final int longBreakDuration;
    private final int pomodoroRepeats;
    private String currentIntervalName;
    private int intervalTimeRemaining; // time remaining in current interval
    private int repeatsRemaining;      // pomodoro intervals remaining until long break
    private int totalPomodoroIntervals;

    // EFFECTS: Constructs pomodoro timer using given timer settings after conversion to seconds,
    //          and sets up timer at first pomodoro interval.
    //          Initially, total pomodoro intervals is 0, start and end are null, and timer is not running
    public PomodoroTimer(PomodoroTimerSettings settings) {
        pomodoroDuration = settings.getPomodoro() * 60;
        shortBreakDuration = settings.getShortBreak() * 60;
        longBreakDuration = settings.getLongBreak() * 60;
        pomodoroRepeats = settings.getPomodoroRepeats();
        totalPomodoroIntervals = 0;
        start = null;
        end = null;
        running = false;
        setUp();
    }

    // MODIFIES: this
    // EFFECTS: if timer is running, decreases current interval time by one second.
    //          if current interval time remaining == 0, starts next interval
    public void decrement() {
        if (isRunning()) {
            this.intervalTimeRemaining--;

            if (this.getTimeRemaining() == 0) {
                nextInterval();
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: runs the timer with given timer task; sets start to current timestamp
    public void start(TimerTask task) {
        running = true;
        start = LocalDateTime.now();

        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                decrement();
                if (task != null) {
                    task.run();
                }
            }
        };

        this.scheduleAtFixedRate(t, 1000, 1000);
    }

    // MODIFIES: this
    // EFFECTS: cancels the timer, sets running to false and end to current timestamp
    @Override
    public void cancel() {
        end = LocalDateTime.now();
        running = false;
        super.cancel();
    }

    // MODIFIES: this
    // EFFECTS: initializes next interval, updates repeats remaining and total cycles if required.
    //          if completed interval is pomodoro,
    //             - decrements repeats remaining and increments total pomodoro intervals
    //             - if repeatsRemaining == 0, sets interval to long break,
    //               else, sets interval to short break
    //          if completed interval is short break, sets interval to pomodoro
    //          if completed interval is long break, resets timer to start a new cycle
    public void nextInterval() {
        String completedInterval = this.getCurrentInterval();

        if (completedInterval.equals(POMODORO_INTERVAL)) {
            this.repeatsRemaining--;
            this.totalPomodoroIntervals++;
            if (this.getRepeatsRemaining() == 0) {
                initLongBreakInterval();
            } else {
                initShortBreakInterval();
            }
        } else if (completedInterval.equals(SHORT_BREAK_INTERVAL)) {
            initPomodoroInterval();
        } else { // (completedInterval.equals(LONG_BREAK_INTERVAL))
            setUp();
        }
    }

    public void pause() {
        running = false;
    }

    public void resume() {
        running = true;
    }

    public boolean isRunning() {
        return this.running;
    }

    public LocalDateTime getEnd() {
        return this.end;
    }

    public LocalDateTime getStart() {
        return this.start;
    }

    public int getPomodoroDuration() {
        return this.pomodoroDuration;
    }

    public int getShortBreakDuration() {
        return this.shortBreakDuration;
    }

    public int getLongBreakDuration() {
        return this.longBreakDuration;
    }

    public int getPomodoroRepeats() {
        return this.pomodoroRepeats;
    }

    public String getCurrentInterval() {
        return this.currentIntervalName;
    }

    public int getTimeRemaining() {
        return this.intervalTimeRemaining;
    }

    public int getRepeatsRemaining() {
        return this.repeatsRemaining;
    }

    public int getTotalPomodoroIntervals() {
        return this.totalPomodoroIntervals;
    }

    // EFFECTS: calculates then returns total number of timer cycles fully completed so far
    public int getTotalCycles() {
        return getTotalPomodoroIntervals() / getPomodoroRepeats();
    }

    // EFFECTS: calculates and returns total pomodoro minutes completed so far
    // including partially completed pomodoro intervals
    public int getTotalPomodoroMinutes() {
        int seconds = getTotalPomodoroIntervals() * getPomodoroDuration();
        if (currentIntervalName.equals(POMODORO_INTERVAL)) {
            seconds += getPomodoroDuration() - getTimeRemaining();
        }
        return seconds / 60;
    }

    // MODIFIES: this
    // EFFECTS: set/reset timer cycle by initializing current interval to pomodoro interval
    //          and repeats remaining to initial pomodoro repeats
    private void setUp() {
        initPomodoroInterval();
        this.repeatsRemaining = this.getPomodoroRepeats();
    }

    // MODIFIES: this
    // EFFECTS: sets current interval to pomodoro interval
    //          and resets time remaining to pomodoro duration
    private void initPomodoroInterval() {
        this.currentIntervalName = POMODORO_INTERVAL;
        this.intervalTimeRemaining = pomodoroDuration;
    }

    // MODIFIES: this
    // EFFECTS: sets current interval to short break interval
    //          and resets time remaining to short break duration
    private void initShortBreakInterval() {
        this.currentIntervalName = SHORT_BREAK_INTERVAL;
        this.intervalTimeRemaining = shortBreakDuration;
    }

    // MODIFIES: this
    // EFFECTS: sets current interval to long break interval
    //          and resets time remaining to long break duration
    private void initLongBreakInterval() {
        this.currentIntervalName = LONG_BREAK_INTERVAL;
        this.intervalTimeRemaining = longBreakDuration;
    }
}
