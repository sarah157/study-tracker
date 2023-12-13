# Study Session Tracker with Pomodoro Timer

Term Project for UBC's CPSC 210 Software Construction Course (Winter 2022)

## Introduction
This application keeps track of your study or focus sessions with the help of a customizable *pomodoro* timer. 
You may add a session that you have already completed or start a new session with the pomodoro timer. 
The timer follows the [Pomodoro technique](https://en.wikipedia.org/wiki/Pomodoro_Technique) which works by alternating 
between pomodoros (work intervals) and rest intervals. A cycle typically consists of four 25-minute pomodoros separated 
by 5-minute short breaks. After each cycle, take a longer 25-minute break then repeat! The number of pomodoro repeats 
and interval durations may be customized in this app.

If you're looking to improve the consistency and efficiency of your study sessions, tracking your sessions and/or using 
a pomodoro timer may be helpful. This app can be used for any routine or long-term activity that requires 
focus and attention such as studying for a course or reading a book in your spare time.

The Pomodoro technique has greatly improved the efficiency of my study sessions. I thought it would be interesting and 
motivating to keep track of these sessions to see how they vary depending on the time, day of the week and other factors.
With this application, I can quickly start a pomodoro timer and track a study session all in one go. 

## User Stories
- As a user, I want to add a completed study session entry to my study tracker.
- As a user, I want to start and end a pomodoro timer then save the pomodoro session to my study tracker.
- As a user, I want to group sessions by activity type.
- As a user, I want to view a list of my study session entries and filter them by activity.
- As a user, I want to edit the pomodoro timer settings in terms of interval durations and repetitions.
- As a user, I want to save my study tracker's activities, sessions, and timer settings.
- As a user, I want to load my study tracker from file.

## Demo

https://github.com/sarah157/study-tracker/assets/47197893/f1c4e84e-a3a2-4a03-b910-2052a483517b


<!-- ## Phase 4: Task 2
Fri Apr 01 12:32:46 PDT 2022  
Viewed all sessions

Fri Apr 01 12:32:54 PDT 2022  
Session added to study tracker

Fri Apr 01 12:32:57 PDT 2022  
Activity added to study tracker

Fri Apr 01 12:33:06 PDT 2022  
Pomodoro session added to study tracker

Fri Apr 01 12:33:08 PDT 2022  
Viewed all sessions

Fri Apr 01 12:33:09 PDT 2022  
Viewed sessions filtered by activity

Fri Apr 01 12:33:15 PDT 2022  
Session removed from study tracker

## Phase 4: Task 3
Ways to refactor:
- AddSessionPanel and ViewSessionsPanel both have fields for StudyTrackerGUI and ActivityComboBox. 
Duplication can be reduced by creating an abstract class with those two fields and have AddSessionPanel and  ViewSessionsPanel extend it.
- Currently, StudyTrackerGUI has an association with AddPomodoroSessionPanel (APSP) so it can check for changes in 
the APSP timer settings then update its tracker's timer settings. I can remove this association to reduce coupling by adding a 
PropertyChangeListener to the APSP. This listener will listen to any changes in the APSP timer settings and update the tracker's timer settings if required.
 -->
