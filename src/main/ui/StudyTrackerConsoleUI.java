package ui;

import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static java.time.LocalDateTime.parse;

// references:
// https://github.students.cs.ubc.ca/CPSC210/LongFormProblemStarters/blob/master/FitLifeGymChain/src/main/ui/InfoManager.java
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
// Followed structure and similar method names

// Represents a study session tracker and pomodoro timer application
public class StudyTrackerConsoleUI {
    private static final String ADD_ACTIVITY = "n";
    private static final String ADD_POMO_SESSION = "p";
    private static final String ADD_COMPLETED_SESSION = "s";
    private static final String VIEW_SESSIONS = "v";
    private static final String QUIT = "q";
    private static final String EDIT_TIMER = "e";
    private static final String QUICK_START_TIMER = "t";
    private static final String MAIN_MENU = "m";
    private static final String ADD_SESSION_MENU = "a";
    private static final String CONTINUE = "c";
    private static final String BACK = "b";
    private static final String LOAD_TRACKER = "l";
    private static final String SAVE_TRACKER = "s";
    private static final String JSON_STORE = "./data/myStudyTracker.json";

    private StudyTracker tracker;
    private PomodoroTimer timer;
    private boolean runApp;
    private Scanner input;
    private JsonReader jsonReader;
    private JsonWriter jsonWriter;

    // EFFECTS: Constructs the study tracker application. Opens user input, runs the program
    //          initializes tracker and loads it with sample data
    public StudyTrackerConsoleUI() {
        input = new Scanner(System.in);
        input.useDelimiter("\n");
        runApp = true;
        tracker = new StudyTracker();
        jsonReader = new JsonReader(JSON_STORE);
        jsonWriter = new JsonWriter(JSON_STORE);
        startTracker();
    }

    // EFFECTS: prints main menu and handles user input while program is running
    public void startTracker() {
        String selection;
        printMainMenu();

        while (runApp) {
            if (input.hasNext()) {
                selection = input.next().toLowerCase().trim();
                processUserInput(selection);
            }
        }
    }

    @SuppressWarnings("methodlength")
    // EFFECTS: prints options or handles action depending on user input
    private void processUserInput(String selection) {
        if (selection.length() > 0) {
            switch (selection) {
                case ADD_ACTIVITY:
                    handleAddActivity();
                    break;
                case ADD_SESSION_MENU:
                    printAddSessionMenu();
                    break;
                case VIEW_SESSIONS:
                    printAllSessions();
                    break;
                case MAIN_MENU:
                    printMainMenu();
                    break;
                case QUICK_START_TIMER:
                    runTimer();
                    printMainMenu();
                    break;
                case LOAD_TRACKER:
                    loadStudyTracker();
                    break;
                case SAVE_TRACKER:
                    saveStudyTracker();
                    break;
                case QUIT:
                    runApp = false;
                    quit();
                    break;
                default:
                    printInvalidSelectionMessage();
                    break;
            }
        }
    }

    // EFFECTS: displays main menu options to user
    private void printMainMenu() {
        printTitle("Main Menu");
        System.out.println("\t" + ADD_ACTIVITY + " → track a new activity");
        System.out.println("\t" + ADD_SESSION_MENU + " → add session to existing activity");
        System.out.println("\t" + VIEW_SESSIONS + " → view all session entries");
        System.out.println("\t" + QUICK_START_TIMER
                + " → quickly start pomodoro timer (session not saved)");
        System.out.println("\t" + LOAD_TRACKER + " → load tracker data from file");
        System.out.println("\t" + SAVE_TRACKER + " → save tracker data to file");
        System.out.println("\t" + QUIT + " → quit");
    }

    // EFFECTS: if tracker does not have any activities, prints error message and then main menu
    //          otherwise, displays add session menu and handles user selection
    private void printAddSessionMenu() {
        if (tracker.getActivities().isEmpty()) {
            System.out.println("You have no activities to add a session to. Add an activity first. :)");
            printMainMenu();
            return;
        }
        printTitle("Add Session Menu");
        System.out.println("\t" + ADD_COMPLETED_SESSION + " → add previously completed session");
        System.out.println("\t" + ADD_POMO_SESSION + " → start and add a pomodoro session");
        System.out.println("\t" + BACK + " → go back");

        String selection = getSelectionFromOptions(Arrays.asList(ADD_COMPLETED_SESSION, ADD_POMO_SESSION,
                BACK, MAIN_MENU));

        if (selection.equals(ADD_COMPLETED_SESSION)) {
            handleAddSession();
        } else if (selection.equals(ADD_POMO_SESSION)) {
            printPomodoroMenu();
        } else {
            printMainMenu();
        }
    }

    // EFFECTS: displays pomodoro session menu and processes user input
    private void printPomodoroMenu() {
        printTitle("Pomodoro Session Menu");
        System.out.println("Current timer:\n" + tracker.getTimerSettings() + "\n");
        System.out.println("\t" + CONTINUE + " → continue with current timer");
        System.out.println("\t" + EDIT_TIMER + " → edit timer settings");
        System.out.println("\t" + BACK + " → back");

        String selection = getSelectionFromOptions(Arrays.asList(CONTINUE, EDIT_TIMER, BACK, MAIN_MENU));

        if (selection.equals(CONTINUE)) {
            handleStartSession();
        } else if (selection.equals(EDIT_TIMER)) {
            printEditTimerOptions();
            handleEditTimer();
            printPomodoroMenu();
        } else if (selection.equals(BACK)) {
            printAddSessionMenu();
        } else {
            printMainMenu();
        }
    }

    // EFFECTS: prompts user to select from one of the given options
    //          until a valid selection is made then turns selection
    private String getSelectionFromOptions(List<String> options) {
        String selection = input.next();
        while (!options.contains(selection)) {
            printInvalidSelectionMessage();
            selection = input.next().toLowerCase().trim();
        }
        return selection;
    }

    // MODIFIES: this
    // EFFECTS: adds new activity to study tracker using title given by user
    private void handleAddActivity() {
        printTitle("Add New Activity");
        System.out.print("Title: ");
        String title = getNonEmptyStringInput();

        Activity activity = new Activity(title);
        tracker.addActivity(activity);

        System.out.println("\n\nActivity successfully added!");
        printMainMenu();
    }

    // MODIFIES: this
    // EFFECTS: adds new session entry to selected activity based on user input
    private void handleAddSession() {
        printTitle("Add session entry");
        Activity activity = selectActivity();

        System.out.print("Tasks completed: ");
        String task = getNonEmptyStringInput();

        LocalDateTime[] interval = getStartAndEndDatetime();
        Session session = new Session(task, interval[0], interval[1], activity);
        tracker.addSession(session);

        System.out.println("\n\nSession successfully added!");
        printMainMenu();
    }

    // EFFECTS: asks user for start date-time and end date-time until valid input is given,
    //          then returns input as an array {start, end}
    private LocalDateTime[] getStartAndEndDatetime() {
        while (true) {
            System.out.println("Start: ");
            String startDate = getValidDate();
            LocalDateTime start = getValidTime(startDate);

            String ans = ".";
            System.out.println("End: ");
            while (!ans.isEmpty() && !ans.equals("y") && !ans.equals("n")) {
                System.out.print(" Date same as start? [y]/n: ");
                ans = input.next().trim().toLowerCase();
            }

            LocalDateTime end = ans.equals("n") ? getValidTime(getValidDate()) : getValidTime(startDate);

            if (start.isBefore(end)) {
                return new LocalDateTime[]{start, end};
            }

            System.out.println("End date-time must be later than start date-time. Please try again.");
        }
    }

    //  EFFECTS: prints edit timer menu
    private void printEditTimerOptions() {
        System.out.println("Select field to edit or '5' to go back: ");
        System.out.println("\t1. pomodoro duration");
        System.out.println("\t2. short break duration");
        System.out.println("\t3. long break duration");
        System.out.println("\t4. pomodoro repeats");
        System.out.println("\t5. save and go back");
    }

    // MODIFIES: this
    // EFFECTS: edits the tracker's timer settings based on user input
    private void handleEditTimer() {
        PomodoroTimerSettings ts = currentTimerCopy();
        int selection = getValidIntegerInput();
        while (selection != 5) {
            if (selection == 1) {
                System.out.print("new pomodoro duration (min): ");
                ts.setPomodoro(getValidIntegerInput());
            } else if (selection == 2) {
                System.out.print("new short break duration (min): ");
                ts.setShortBreak(getValidIntegerInput());
            } else if (selection == 3) {
                System.out.print("new long break duration (min): ");
                ts.setLongBreak(getValidIntegerInput());
            } else if (selection == 4) {
                System.out.print("new number of pomodoro repeats: ");
                ts.setPomodoroRepeats(getValidIntegerInput());
            }
            if (selection >= 1 && selection <= 4) {
                System.out.print("Updated! ");
            }
            System.out.print("Select from 1 - 5: ");
            selection = getValidIntegerInput();
        }
        tracker.setTimerSettings(ts);
    }

    // MODIFIES: this
    // EFFECTS: starts and ends the pomodoro timer then adds session to the selected activity
    private void handleStartSession() {
        printTitle("New Pomodoro session");
        Activity activity = selectActivity();
        System.out.print("What will you be working on? ");
        String details = getNonEmptyStringInput();
        LocalDateTime start = LocalDateTime.now();
        runTimer();
        LocalDateTime end = LocalDateTime.now();
        PomodoroSession session = new PomodoroSession(details, start, end, tracker.getTimerSettings(),
                timer.getTotalPomodoroIntervals(), activity);
        tracker.addSession(session);

        System.out.println("Session successfully added!");
        printMainMenu();
    }

    // EFFECTS: starts pomodoro timer based on tracker's timer settings,
    //          continues until user stops the timer then returns {cyclesCompleted, pomodorosCompleted}
    private void runTimer() {
        timer = new PomodoroTimer(tracker.getTimerSettings());
        TimerTask task = new TimerTask() {
            public void run() {
                System.out.println(timer.getCurrentInterval() + "\t" + prettyTime(timer.getTimeRemaining()));
                timer.decrement();
            }
        };

        System.out.println("\nStarting timer now...");
        System.out.println("Enter 's' to stop timer and end session early\n");
        System.out.println("INTERVAL\tTIME REMAINING (mm:ss)");

        timer.scheduleAtFixedRate(task, 1000, 1000);

        String str = "";
        while (!str.equals("s")) {
            str = input.next();
        }

        timer.cancel();
        System.out.println("\n\nSession ended.");
    }

    // EFFECTS: if tracker has no activities, prints error message,
    //          otherwise, prints activity title and its session entries for all activities in study tracker
    private void printAllSessions() {
        if (tracker.getActivities().isEmpty()) {
            System.out.println("You have no activities. Add your first one!");
            printMainMenu();
        } else {
            int i = 1;
            for (Session s : tracker.getSessions()) {
                System.out.println(i + ". " + s.getActivityName() + " - " + s.getDetails());
                System.out.println("\t" + parseDate(s.getStart()) + ", "
                        + parseTime(s.getStart()) + " - " + parseTime(s.getEnd()));
                i++;
            }
        }
        System.out.println();
        printLine();
        System.out.println(MAIN_MENU + " - main menu");
    }


    // EFFECTS: prints all activities and returns the user selected activity
    private Activity selectActivity() {
        System.out.println("Please select an activity or '0' for no activity: ");

        for (int i = 0; i < tracker.getActivities().size(); i++) {
            System.out.println((i + 1) + ". " + tracker.getActivities().get(i).getName());
        }

        int selection = getValidIntegerInput();
        while (selection < 0 || selection > tracker.getActivities().size()) {
            System.out.print("Index out of range. Please try again: ");
            selection = getValidIntegerInput();
        }
        if (selection == 0) {
            return null;
        }

        return tracker.getActivities().get(selection - 1);
    }

    // EFFECTS: quits the application (stops receiving user input)
    private void quit() {
        System.out.println("Quitting Study Tracker...");
        System.out.println("Have a good day!");
        input.close();
    }

    // EFFECTS: gets user input until user enters a valid integer then returns input
    private int getValidIntegerInput() {
        int num;
        while (true) {
            try {
                num = input.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.print("Please enter a number: ");
                input.next();
            }
        }
        return num;
    }

    // EFFECTS: asks user to input date in format yyyy-mm-dd until valid input is given, then returns input date
    private String getValidDate() {
        System.out.print(" Date (yyyy-mm-dd): ");
        String date = input.next();
        while (true) {
            try {
                parse(date + "T00:00");
                break;
            } catch (DateTimeParseException e) {
                System.out.print(" Invalid date. Please try again: ");
                date = input.next();
            }
        }
        return date;
    }

    // EFFECTS: asks user to input time in format HH:mm until valid input given,
    //          then constructs LocalDateTime with the given date and returns it
    private LocalDateTime getValidTime(String date) {
        System.out.print(" Time (24-hour hh:mm): ");
        String time = input.next();
        LocalDateTime datetime;
        while (true) {
            try {
                datetime = parse(date + "T" + time);
                break;
            } catch (DateTimeParseException e) {
                System.out.print(" Invalid time. Please try again: ");
                time = input.next();
            }
        }
        return datetime;
    }

    // EFFECTS: gets user input until user enters a non-empty string then returns input
    private String getNonEmptyStringInput() {
        String str = "";
        while (str.length() == 0) {
            str = input.next();
        }
        return str.trim();
    }

    // EFFECTS: returns time in string format h:mm a from given datetime. e.g. 1:30 PM, 11:00 AM
    private String parseTime(LocalDateTime datetime) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        return timeFormatter.format(datetime);
    }

    // EFFECTS: returns copy of current timer settings
    private PomodoroTimerSettings currentTimerCopy() {
        PomodoroTimerSettings ts = tracker.getTimerSettings();
        return new PomodoroTimerSettings(ts.getPomodoro(), ts.getShortBreak(), ts.getLongBreak(),
                ts.getPomodoroRepeats());
    }

    // EFFECTS: returns date in string format EEE MMM-dd-yyyy from given datetime. e.g. Mon Feb-07-2022
    private String parseDate(LocalDateTime datetime) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE MMM-dd-yyyy");
        return dateFormatter.format(datetime);
    }

    // EFFECTS: prints given str with top and bottom borders
    private void printTitle(String str) {
        System.out.println();
        System.out.println(str);
        printLine();
    }

    // EFFECTS: prints horizontal line
    private void printLine() {
        System.out.println("---------------------------------------");
    }

    // EFFECTS: returns given seconds to a string in the format mm:ss
    private String prettyTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds - (min * 60);
        return String.format("%02d:%02d", min, sec);
    }

    // EFFECTS: prints error message if user selection is invalid
    private void printInvalidSelectionMessage() {
        System.out.println("Invalid Selection. Please try again.");
    }

    // EFFECTS: save the tracker to file
    private void saveStudyTracker() {
        try {
            jsonWriter.open();
            jsonWriter.write(tracker);
            jsonWriter.close();
            System.out.println("Saved tracker to " + JSON_STORE + "!");
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads tracker from file
    private void loadStudyTracker() {
        try {
            tracker = jsonReader.read();
            System.out.println("Loaded study tracker from " + JSON_STORE + "!");
        } catch (IOException e) {
            System.out.println("Unable to read " + JSON_STORE);
        }
    }

}

