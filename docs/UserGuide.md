---
layout: page
title: User Guide
---

Course Management System (CMS) is a desktop application designed for NUS course coordinators who need to efficiently manage large cohorts, tutor assignments, and student records. Optimized for speed and productivity, CMS leverages a Command Line Interface (CLI) to enable users to perform tasks quickly and accurately.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) is installed.
   * Check with: `java --version`

1. Download the latest `.jar` file from [here](https://github.com/AY2526S2-CS2103T-F10-2/tp/releases).

1. Create or choose a folder as your CMS home folder (e.g. `C:\Users\<you>\Documents\cms` on Windows).

1. Copy the downloaded jar into that folder.

1. Open a terminal, `cd` into that folder, and run `java -jar cms.jar`.
   A GUI similar to the below should appear in a few seconds. Note how the app contains sample data.
   ![Ui](images/Ui.png)

1. Type the command in the command box and press Enter to execute it.
   Typing `help` and pressing Enter opens the help window.
   Refer to [Command summary](#command-summary) for a quick list of available commands and formats.

1. CMS stores data under the home folder in `data/CMS.json`.

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
To transfer your data to another computer, install CMS there and overwrite the empty `data/CMS.json` file it creates with your existing `data/CMS.json`.
</div>

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format
--------|------------------
**Help** | `help`
**List** | `list`
**Add** | `add n/NAME m/NUS_MATRIC role/ROLE soc/SOC_USERNAME gh/GITHUB_USERNAME e/EMAIL p/PHONE t/TUTORIAL_GROUP [tag/TAG]...`<br><br>e.g. `add n/John Doe m/A0234567B role/tutor soc/johndoe gh/johndoe e/johndoe@u.nus.edu p/91234567 t/01`
**Edit** | `edit INDEX [n/NAME] [m/NUS_MATRIC] [role/ROLE] [soc/SOC_USERNAME] [gh/GITHUB_USERNAME] [e/EMAIL] [p/PHONE] [t/TUTORIAL_GROUP] [tag/TAG]...`<br><br>e.g. `edit 2 p/98765432 e/johndoe@example.com`
**Filter** | `filter [tag/TAG]... [t/TUTORIAL_GROUP_NUMBER]`<br><br>e.g. `filter tag/friend`<br>e.g. `filter tag/friend t/01`
**Find** | `find a/KEYWORD [MORE_KEYWORDS]...`<br>`find n/KEYWORD [MORE_NAME_KEYWORDS]...`<br>`find m/NUS_MATRIC [MORE_NUS_MATRIC]...`<br><br>e.g. `find n/jane n/eunice m/A0123456B`
**Tag** | `tag add n/INDEX [MORE_INDEXES]... tag/TAG [MORE_TAGS]...`<br>`tag add m/NUS_MATRIC[MORE_NUS_MATRICS]... tag/TAG [MORE_TAGS]...`<br>`tag delete n/INDEX [MORE_INDEXES]... tag/TAG [MORE_TAGS]...`<br>`tag delete m/NUS_MATRIC [MORE_NUS_MATRICS]... tag/TAG [MORE_TAGS]...`<br><br>e.g. `tag add n/1 2 tag/friend tutor`
**Delete** | `delete INDEX`<br>`delete INDEX [MORE_INDEXES]...`<br>`delete m/NUS_MATRIC`<br><br>e.g. `delete 1 3 5`
**Import** | `import FILE_PATH [keep/current|keep/incoming]`<br><br>e.g. `import data/addressbook.json keep/current`
**Export** | `export FILE_PATH`<br><br>e.g. `export "C:\\Users\\Josh\\Documents\\backup.json"`
**Clear** | `clear`
**Exit** | `exit`

--------------------------------------------------------------------------------------------------------------------

## Features
<div markdown="block" class="alert alert-info">

**:information_source: Notes about command format:**<br>

* A command has a command word plus fields.
* Command word: `add`, `edit`, `find`, ...
* Prefixes identify each field, e.g. `n/`, `m/`, `e/`.
* Words in `UPPER_CASE` are values to provide.
* Items in square brackets are optional.
* `...` means the field can be repeated.
* Parameters can be in any order.
* For commands without parameters (`help`, `list`, `exit`, `clear`), extra text is ignored.
* e.g. `add n/John Doe m/A0234567B role/tutor soc/johndoe gh/johndoe e/johndoe@u.nus.edu p/91234567 t/01 tag/mentor`
</div>

### Viewing help : `help`

Shows a message with a hyperlink to this User Guide.

Format: `help`

### Listing all student and tutor records : `list`

Shows all records currently stored in CMS.

Format: `list`

### Adding a student / tutor : `add`

Adds a student or tutor record to CMS.

All required fields must be valid (See [Fields and accepted formats](#fields-and-accepted-formats)).

Format: `add n/NAME m/NUS_MATRIC role/ROLE soc/SOC_USERNAME gh/GITHUB_USERNAME e/EMAIL p/PHONE t/TUTORIAL_GROUP [tag/TAG]...`

Examples:
* `add n/David Tan m/A0211111C role/student soc/david1 gh/davidtan99 e/david@u.nus.edu p/97654321 t/05`
* `add n/John Doe m/A0234567B role/tutor soc/johndoe gh/johndoe e/johndoe@u.nus.edu p/91234567 t/01 tag/python-experienced`

<div markdown="span" class="alert alert-info">:information_source: **Note:**
Add is rejected if unique fields conflict with an existing person (e.g. same NUS Matric / SoC username / GitHub username / email).
</div>

### Editing a student / tutor : `edit`

Edits an existing student or tutor record in CMS.

Format: `edit INDEX [n/NAME] [m/NUS_MATRIC] [role/ROLE] [soc/SOC_USERNAME] [gh/GITHUB_USERNAME] [e/EMAIL] [p/PHONE] [t/TUTORIAL_GROUP] [tag/TAG]...`

* Edits the person at the specified `INDEX`.
* `INDEX` must be a positive integer (1, 2, 3, ...).
* At least one optional field must be provided.
* Existing values are replaced by the input values.
* When `tag/` is used, existing tags are replaced (not cumulative).
* You can clear all tags by using `tag/` with no value.
* Edited values must satisfy the same field rules as `add` (see [Fields and accepted formats](#fields-and-accepted-formats)).

Examples:
* `edit 1 p/91234567 e/johndoe@example.com`
* `edit 2 n/Betsy Crower tag/`
* `edit 3 m/A0654321B role/student soc/betsy3 gh/betsycrowe t/07`

### Filtering students / tutors : `filter`

Filters persons by tag, tutorial group, or both.

Format: `filter [tag/TAG]... [t/TUTORIAL_GROUP_NUMBER]`

* At least one filter must be provided.
* `tag/` can be repeated to filter by multiple tags.
* `t/` can appear at most once.
* If multiple `tag/` values are given, a person must have all of them.
* If `t/` is given, a person must belong to that tutorial group.
* When both `tag/` and `t/` are provided, a person must satisfy both filters.
* Tag matching is case-insensitive.
* Tutorial group input accepts values from `1` to `99`, with leading zeros allowed (e.g. `t/1`, `t/01`, and `t/001` are all accepted).

Examples:
* `filter tag/friend`
* `filter t/01`
* `filter tag/friend tag/mentor`
* `filter tag/friend t/01`

### Finding students / tutors : `find`

Finds persons whose names or NUS Matrics contain any of the given keywords.

Format:
* `find a/KEYWORD [MORE_KEYWORDS]...`
* `find n/KEYWORD [MORE_NAME_KEYWORDS]...`
* `find m/NUS_MATRIC [MORE_NUS_MATRICS]...`
* `find n/KEYWORD [MORE_NAME_KEYWORDS]... m/NUS_MATRIC [MORE_NUS_MATRICS]...`

* Prefix is required (`a/`, `n/`, `m/`).
* Search is case-insensitive for names. e.g. `n/hans` will match `Hans`.
* Order of keywords does not matter for name search. e.g. `find n/Hans n/Bo` will match `find n/Bo n/Hans`.
* Full words are matched for names. e.g. `find n/Han` will not match `Hans`.
* `m/` matching is case-insensitive. e.g. `m/a0123456b` matches `A0123456B`.
* Mixed prefixes are allowed in one command, and results are returned by union (OR across prefixes).

Examples:
* `find a/jane`
* `find n/jane n/eunice`
* `find n/jane eunice`
* `find n/jane n/eunice m/A0123456B m/A1234567C`
* `find m/A0123456B A1234567C`
* `find m/A0123456B m/A1234567C`

### Adding or removing tags : `tag`

Adds or removes one or more tags from one or more persons.

Format:
* `tag add n/INDEX [MORE_INDEXES]... tag/TAG [MORE_TAGS]...`
* `tag add id/NUS_ID [MORE_NUS_IDS]... tag/TAG [MORE_TAGS]...`
* `tag delete n/INDEX [MORE_INDEXES]... tag/TAG [MORE_TAGS]...`
* `tag delete id/NUS_ID [MORE_NUS_IDS]... tag/TAG [MORE_TAGS]...`

* Use `add` to add tags and `delete` to remove tags.
* Target persons by either displayed index (`n/`) or NUS ID (`id/`), but not both in the same command.
* For index-based tagging, each index refers to the displayed list and must be a positive integer.
* At least one target person and one tag must be provided.
* Existing tags are not duplicated.
* Tag values must follow the same rules as [`tag/TAG`](#field-tag).

Examples:
* `tag add n/1 2 tag/friend tutor`
* `tag add id/A1234567B A2345678C tag/mentor`
* `tag delete n/3 tag/friend`
* `tag delete id/A1234567B tag/mentor`

### Deleting a student / tutor : `delete`

Deletes one or more persons by displayed index, or by NUS Matric.

Format:
* `delete INDEX`
* `delete INDEX [MORE_INDEXES]...`
* `delete m/NUS_MATRIC`

* For index-based delete, each index refers to the displayed list and must be a positive integer.

Examples:
* `delete 2`
* `delete 1 3 5`
* `delete m/A0234567B`

### Importing records from a JSON file : `import`

Imports records from a `.json` file into CMS.

Format: `import FILE_PATH [keep/current|keep/incoming]`

* `FILE_PATH` must point to a `.json` file.
* File paths with spaces are supported, e.g. `C:/Users/Test/My Data/import.json`.
* Quoted file paths are also supported, e.g. `"C:/Users/Test/My Data/import.json"`.
* If the file data has no conflicts with current data, import proceeds normally.
* Platform path separators are accepted (for example `/` and `\`).
* If conflicts exist and the app already has data:
   * No keep option: command is rejected and you must specify a keep policy.
   * `keep/current`: keeps existing records and skips conflicting incoming records.
   * `keep/incoming`: incoming records replace conflicting existing records.

Examples:
* `import data/addressbook.json`
* `import data/addressbook.json keep/current`
* `import data/addressbook.json keep/incoming`

### Exporting records to a JSON file : `export`

Exports current CMS data to a `.json` file.

Format: `export FILE_PATH`

* `FILE_PATH` must end with `.json`.
* File paths with spaces are supported, e.g. `C:/Users/Test/My Data/export.json`.
* Quoted file paths are also supported, e.g. `"C:/Users/Test/My Data/export.json"`.

Examples:
* `export data/backup.json`
* `export "C:/Users/Test/My Documents/backup.json"`

### Purging all records : `clear`

Deletes **all** records from CMS.

Format: `clear`

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
This command deletes all records and is irreversible from within the app.
</div>

### Exiting the program : `exit`

Exits CMS.

Format: `exit`

### Saving data

CMS saves data automatically after commands that modify data.

### Editing the data file

CMS data is stored in `[CMS home folder]/data/CMS.json`.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
Invalid edits can cause CMS to reset your data file on next launch. Back up `CMS.json` before editing manually.
</div>

--------------------------------------------------------------------------------------------------------------------

## Fields and accepted formats

Use this section as a quick checklist for `add` and `edit`.

<a id="field-name"></a>
**`n/NAME`**
* Letters, numbers, and spaces only.
* Cannot be blank.
* Case sensitivity: case-sensitive (stored as entered).
* Valid: `n/John Doe`
* Invalid: `n/John@Doe`

<a id="field-nus-matric"></a>
**`m/NUS_MATRIC`**
* Must be `A` + 7 digits + uppercase letter (e.g. `A0234567B`).
* Must be unique in CMS.
* Case sensitivity: case-sensitive (`A` and trailing letter must be uppercase).
* Valid: `m/A0234567B`
* Invalid: `m/a0234567b`

<a id="field-role"></a>
**`role/ROLE`**
* Must be exactly `student` or `tutor`.
* Case sensitivity: case-sensitive (lowercase only).
* Valid: `role/student`
* Invalid: `role/Student`

<a id="field-soc-username"></a>
**`soc/SOC_USERNAME`**
* Either a SoC-style username or a valid NUS Matric format.
* SoC-style username rules:
  * 5 to 8 characters.
  * Lowercase letters, digits, and hyphens only.
  * Cannot start or end with a hyphen.
  * No spaces.
* Must be unique in CMS.
* Case sensitivity: case-sensitive.
* Valid: `soc/john1`
* Invalid: `soc/-john`

<a id="field-github-username"></a>
**`gh/GITHUB_USERNAME`**
* 1-39 characters.
* Letters, digits, and hyphens only.
* Cannot start/end with `-`.
* Must be unique in CMS.
* Case sensitivity: case-sensitive.
* Valid: `gh/jane-lim123`
* Invalid: `gh/-jane`

<a id="field-email"></a>
**`e/EMAIL`**
* Must be a valid email format.
* Case sensitivity: case-sensitive.
* Valid: `e/johndoe@u.nus.edu`
* Invalid: `e/johndoe@u`

<a id="field-phone"></a>
**`p/PHONE`**
* Digits only.
* At least 3 digits.
* Case sensitivity: not applicable (numeric only).
* Valid: `p/91234567`
* Invalid: `p/+6591234567`

<a id="field-tutorial-group"></a>
**`t/TUTORIAL_GROUP`**
* Must be `01` to `99`.
* Case sensitivity: not applicable (numeric only).
* Valid: `t/01`
* Invalid: `t/100`

<a id="field-tag"></a>
**`tag/TAG`**
* Optional, repeatable.
* Alphanumeric characters only.
* No spaces inside a tag.
* Cannot start or end with a hyphen.
* Repeated tags for the same person are kept only once.
* Case sensitivity: case-sensitive.
* Valid: `tag/python`
* Invalid: `tag/needs help`

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the app to a secondary screen and later switch to one screen, the GUI may open off-screen. Delete `preferences.json` before starting again.
2. **If the Help Window is minimized**, triggering help again may keep it minimized, and requires you to manually restore it.

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install CMS on the other computer, launch once, then replace the new `data/CMS.json` with your old one.

**Q**: Where are my preferences saved?<br>
**A**: Preferences are saved in `preferences.json` in your CMS working directory.

**Q**: Can I undo `delete` or `clear`?<br>
**A**: No. There is currently no undo feature, so keep backups of `data/CMS.json` if needed.

**Q**: Why is my `find` command not returning results?<br>
**A**: Check your prefixes and input format (`a/`, `n/`, `m/`), and verify that full-word matching rules are met for name searches.
