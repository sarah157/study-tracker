package ui;

import model.Event;
import model.EventLog;
import model.StudyTracker;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;

// Represents the main frame for the study tracker app
public class StudyTrackerGUI extends JFrame {
    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;
    public static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final String JSON_STORE = "./data/myStudyTracker.json";
    public static final String VIEW = "View sessions";
    public static final String ADD_COMPLETED = "Add completed";
    public static final String ADD_POMODORO = "Add pomodoro";
    public static final String RUN_TIMER = "Run timer";
    public static final String LOAD = "Load";
    public static final String SAVE = "Save";

    private StudyTracker tracker;
    private JsonReader jsonReader;
    private JsonWriter jsonWriter;
    private JComponent currentPanel;

    public StudyTrackerGUI() {
        super("Study Tracker");
        addWindowListener(new ExitProgramListener());
        initializeFields();
        initializeGraphics();
        centreOnScreen();
        addMenu();
        setCurrentPanel(VIEW);
        setVisible(true);
    }

    public StudyTracker getTracker() {
        return tracker;
    }

    // MODIFIES: this
    // EFFECTS: sets current panel to given panel
    public void setCurrentPanel(String panel) {
        switch (panel) {
            case VIEW:
                ViewSessionsPanel viewSessionsPanel = new ViewSessionsPanel(this);
                currentPanel = viewSessionsPanel.getSplitPane();
                break;
            case ADD_COMPLETED:
                currentPanel = new AddRegularSessionPanel(this);
                break;
            case ADD_POMODORO:
                currentPanel = new AddPomodoroSessionPanel(this);
                break;
            default:
                TimerPanel timerPanel = new TimerPanel((AddPomodoroSessionPanel) currentPanel);
                currentPanel = timerPanel;
                timerPanel.runTimer();
        }
        setContentPane(currentPanel);
        revalidate();
        repaint();
    }

    // Reference: AlarmControllerUI.addMenu https://github.students.cs.ubc.ca/CPSC210/AlarmSystem
    // MODIFIES: this
    // EFFECTS: adds menu bar to main frame
    private void addMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        JMenu fileMenu = new JMenu("File");
        addMenuItem(fileMenu, LOAD, new FileMenuAction(), KeyStroke.getKeyStroke("control l"));
        addMenuItem(fileMenu, SAVE, new FileMenuAction(), KeyStroke.getKeyStroke("control s"));
        fileMenu.setMnemonic('F');
        menuBar.add(fileMenu);

        JMenu sessionMenu = new JMenu("Session");
        sessionMenu.setMnemonic('S');
        addMenuItem(sessionMenu, VIEW, new SessionMenuAction(), KeyStroke.getKeyStroke("control a"));
        addMenuItem(sessionMenu, ADD_COMPLETED, new SessionMenuAction(), KeyStroke.getKeyStroke("control r"));
        addMenuItem(sessionMenu, ADD_POMODORO, new SessionMenuAction(), KeyStroke.getKeyStroke("control t"));
        menuBar.add(sessionMenu);

        setJMenuBar(menuBar);
    }

    // References: AlarmControllerUi.addMenuItem https://github.students.cs.ubc.ca/CPSC210/AlarmSystem
    // MODIFIES: theMenu
    // EFFECTS: adds menu item with given action and accelerator to theMenu
    private void addMenuItem(JComponent theMenu, String name, AbstractAction action, KeyStroke accelerator) {
        JMenuItem menuItem = new JMenuItem(action);
        menuItem.setName(name);
        menuItem.setText(name);
        menuItem.setAccelerator(accelerator);
        theMenu.add(menuItem);
    }

    // Reference: method extract from https://github.students.cs.ubc.ca/CPSC210/SimpleDrawingPlayer-Complete
    // MODIFIES: this
    // EFFECTS: initializes fields
    private void initializeFields() {
        tracker = new StudyTracker();
        jsonReader = new JsonReader(JSON_STORE);
        jsonWriter = new JsonWriter(JSON_STORE);
    }

    // Reference: method from https://github.students.cs.ubc.ca/CPSC210/SimpleDrawingPlayer-Complete
    // MODIFIES: this
    // EFFECTS: initializes graphics
    private void initializeGraphics() {
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
    }

    // Reference: method extracted from https://github.students.cs.ubc.ca/CPSC210/AlarmSystem
    // EFFECTS: centre main application window on desktop
    private void centreOnScreen() {
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        setLocation((SCREEN_WIDTH - getWidth()) / 2, (height - getHeight()) / 2);
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

    // Represents action to be taken when user clicks on a session menu item
    private class SessionMenuAction extends AbstractAction {

        // MODIFIES: this
        // EFFECTS: sets current panel to the jMenuItem that was clicked on
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem) e.getSource();
            setCurrentPanel(item.getName());
        }
    }

    // Represents action to be taken when user clicks on a file menu item
    private class FileMenuAction extends AbstractAction {

        // MODIFIES: this
        // EFFECTS: if menu item clicked is LOAD, loads tracker with data from file and resets current panel
        //          if menu item clicked is SAVE, saves tracker to file
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem) e.getSource();
            if (item.getName().equals(LOAD)) {
                loadStudyTracker();
                setCurrentPanel(currentPanel.getName());
            } else {
                String temp = currentPanel.getName();
                setContentPane(new JPanel());
                saveStudyTracker();
                setCurrentPanel(temp);
            }
        }
    }

    // Represents action to be taken when user exits the program
    private class ExitProgramListener implements WindowListener {

        @Override
        public void windowOpened(WindowEvent e) {
        }

        // EFFECTS: checks if timer settings changed
        // then prints all events in event log before exiting system / ending program
        @Override
        public void windowClosing(WindowEvent e) {
            for (Event next : EventLog.getInstance()) {
                System.out.println(next);
                System.out.println();
            }
        }

        // EFFECTS: exits the system / ends the program
        @Override
        public void windowClosed(WindowEvent e) {
            System.exit(0);
        }

        @Override
        public void windowIconified(WindowEvent e) {
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
        }
    }

    // reference: https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon
    // EFFECTS: resizes images at given path to given width and height and returns as ImageIcon
    public static ImageIcon resizeImageIcon(String path, int width, int height) {
        ImageIcon image = new ImageIcon(path);
        Image resizedImage = image.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    public static void main(String[] args) {
        new StudyTrackerGUI();
    }
}
