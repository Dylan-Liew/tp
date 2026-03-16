gg---
layout: page
title: User Guide
---

Course Management System (CMS) is a **desktop app for managing contacts, optimized for use via a Command Line Interface** (CLI) while still having the benefits of a Graphical User Interface (GUI). If you can type fast, CMS can get your contact management tasks done faster than traditional GUI apps.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure you have Java `17` or above installed in your Computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/CMS-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your CMS.

1. Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar CMS.jar` command to run the application.<br>
   A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type the command in the command box and press Enter to execute it. e.g. typing **`help`** and pressing Enter will open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe id/A0234567B role/tutor soc/johndoe gh/johndoe e/johndoe@u.nus.edu p/91234567 t/T01` : Adds a contact named `John Doe` to the Course Management System.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add n/NAME`, `NAME` is a parameter which can be used as `add n/John Doe`.

* Items in square brackets are optional.<br>
  e.g `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items with `…`​ after them can be used multiple times including zero times.<br>
  e.g. `[t/TAG]…​` can be used as ` ` (i.e. 0 times), `t/friend`, `t/friend t/family` etc.

* Parameters can be in any order.<br>
  e.g. if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that do not take in parameters (such as `help`, `list`, `exit` and `clear`) will be ignored.<br>
  e.g. if the command specifies `help 123`, it will be interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help : `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a student / tutor: `add`

Adds a student / tutor to the Course Management System.

Format: `add n/NAME id/NUS_ID role/ROLE soc/SOC_USERNAME 
        gh/GITHUB_USERNAME e/EMAIL p/PHONE t/TUTORIAL_GROUP [tag/TAG]…​`

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A student / tutor can have any number of tags (including 0)
</div>

Examples:
* `add n/David Tan id/A0211111C role/student soc/davidtan gh/davidtan99 e/david@u.nus.edu p/97654321 t/T05`
* `add n/John Doe id/A0234567B role/tutor soc/johndoe gh/johndoe e/johndoe@u.nus.edu p/91234567 t/T01
`

### Listing all student & tutor : `list`

Shows a list of all students & tutors in the Course Management System.

Format: `list`


### Deleting a student / tutor : `delete`

Deletes the specified student / tutor from the Course Management System.

Format: `delete INDEX`

* Deletes the student / tutor at the specified `INDEX`.
* The index refers to the index number shown in the displayed student / tutor list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd student / tutor in the Course Management System.
* `find Betsy` followed by `delete 1` deletes the 1st student / tutor in the results of the `find` command.

### Clearing all entries : `clear`

Clears all entries from the Course Management System.

Format: `clear`

### Exiting the program : `exit`

Exits the program.

Format: `exit`

### Saving the data

CMS data are saved in the hard disk automatically after any command that changes the data. There is no need to save manually.

### Editing the data file

CMS data are saved automatically as a JSON file `[JAR file location]/data/CMS.json`. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes to the data file makes its format invalid, CMS will discard all data and start with an empty data file at the next run. Hence, it is recommended to take a backup of the file before editing it.<br>
Furthermore, certain edits can cause the CMS to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous CMS home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME id/NUS_ID role/ROLE soc/SOC_USERNAME gh/GITHUB_USERNAME e/EMAIL p/PHONE t/TUTORIAL_GROUP [tag/TAG…​` <br> e.g., `add n/John Doe id/A0234567B role/tutor soc/johndoe gh/johndoe e/johndoe@u.nus.edu p/91234567 t/T01`
**Clear** | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**List** | `list`
**Help** | `help`
