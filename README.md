# FocusClock Readme

## Functional Abilities:

- **User Authentication:**
  - The user can log in to the app using a username and password.

- **Category Management:**
  - The user is able to create categories that the timesheet entries will belong to.

- **Timesheet Entry:**
  - The user can create a timesheet entry, specifying at least the date, start and end times, description, and category.
  - Optionally, the user may add a photograph to each timesheet entry.

- **Daily Goals:**
  - The user will set a minimum daily goal for hours worked, as well as a maximum daily goal.

- **Viewing Entries:**
  - The user can view the list of all the timesheet entries created during a user-selectable period.
  - If a photo was stored for an entry, the user may view it from this list.

- **Category Hours Tracking:**
  - The user must be able to view the total number of hours spent on each category during a user-selectable period.

# Video Link:
- https://youtu.be/rb2gscE2VQo?si=IDXBSj9qTxtREJny

  -----------------------------------------------------------------------------------------
  # Part 3 ReadMe
  # FocusClock Application
## Introduction
Welcome to FocusClock, your ultimate productivity companion designed to help you manage your time efficiently and achieve your goals. FocusClock is an intuitive Android application that leverages the Pomodoro Technique to enhance your focus and productivity. Whether you're working on a single task or juggling multiple projects, FocusClock offers a comprehensive set of tools to keep you on track and motivated.

## Features of FocusClock
### Feature 1
We have the Pomodoro Timer, which essentially breaks your work into intervals, traditionally 25 minutes in length, separated by short (5 minute) breaks. This technique is scientifically proven to improve concentration and reduce burnout. Here is how our one works: As a user, you select a project, then you can choose a task, or optionally add a new task, then you hit the ‘Start Session’ button, which will start a 25-minute timer, at the end of this timer a 5-minute timer will start (the break) and then after this timer, the 25-minute timer will start again. This process will go on and on until the user hits the ‘Stop Session’ button, once this button is hit, it will be saved as a Time Entry in the specified task and specified project. 

### Feature 2
Another one of our features, is separation of tasks. We have separated our task very thoroughly. First, we have a large overall project, then we have separate it further into tasks, these tasks fall under the separate projects. Then within these tasks, we create the separate time entries, which hold the hours, start and finish times.
### Feature 3
We also have implemented a few filters. We filter through our time entries in two ways: firstly, you can filter through all your entries by selecting a project, this will then display all the time entries within the project, regardless of their separate tasks. The second way we can filter through is by entering a start date and an end date. Then all the time entries, regardless of projects and tasks, between those two dates, will be displayed.  

## How to use this Application:
Firstly, the user has to sign up, here the user will have to enter their personal details, as well as their Goal Hours that they would like to accomplish, after they sign in, their information will be stored in Firebase, and they will go through the login process (using email/username and password). 
After they login, they will land on their homepage, which will show them their entries for the day and also be populated with the number of hours done for the day, as well as the number of total tasks completed. 
They can then go on to add a project, add a task, or add a time entry. They can also make use of the pomodoro timer, as well as the filtering page. They can also view their data visually with the use of graphs. There will be a button on the home page that will take the user to a page with a bar graph that will show them the hours they have logged for each day of the month. It will also display their maximum hours and minimum hours so they can see how they are doing in terms of the goal hours they set for themselves. There will be another button that will take the user to a page with a line graph, that will allow the user to select a date range and they can view their total hours with the minimum and maximum study hours highlighted. The user can also go onto their settings page to update any personal information, or log out.

## How to download and run this Application
As a user, you can either run this via Android Studio, or have one of us provide you with the APK, which you can use to download on an Android device.
If you choose to go the route of testing it via Android Studio, then you need at least the Hedgehog version of the IDE and it would be advised to run it on a Pixel 2, medium phone emulator. 
Next, take the code form this GitHub repo, and clone it into your Android Studio, and then you are almost ready to run this app. It will be recommended to create a Firebase project, then link it to the project, for the best results. You can then sign up with a new user, or message one of the team members for their login, to view a profile with some pre-existing data. You can also see this pre-existing data on the video that will be accompanying this submission. 
## YouTube Video Link
Here is our final submission link (Part 3 video): https://www.youtube.com/watch?v=gvUGbV03ctQ


### Please note the APK is available in the application labelled as:  app-debug-apk. This will be available to download via GitHub (as per our second to last GitHub commit)

