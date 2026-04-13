---
layout: page
title: User Guide
---

Managing large cohorts with point-and-click workflows is slow, repetitive, and prone to mistakes, especially when student and tutor records must stay consistent. Course Management System (CMS) is built for NUS course coordinators who need speed and accuracy: a command-driven workflow with built-in validation and uniqueness checks that helps you complete routine record tasks in seconds with confidence.

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
   A GUI similar to the image below should appear in a few seconds. Note how the app contains sample data.
   ![Ui](images/Ui.png)

1. Type the command in the command box and press `Enter` to execute it.
   Typing `help` and pressing `Enter` opens the help window.
   Refer to [Command summary](#command-summary) for a quick list of available commands and formats.

1. CMS stores data under the home folder in `data/cms.json`.

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
To transfer your data to another computer, install CMS there and overwrite the empty `data/cms.json` file it creates with your existing `data/cms.json`.
</div>

--------------------------------------------------------------------------------------------------------------------

## User interface overview

![UI overview](images/Ui.png)

CMS uses a single main window with four working areas:

1. **Command Box**: (Bottom) Enter commands such as `add`, `find`, and `edit`.
2. **Result Display**: (Top) Shows success, error, and guidance messages after each command.
3. **Person List Panel**: (Middle Left) Displays the current list (or filtered list) of students and tutors.
4. **Person Detail Panel**: (Middle Right) Shows details of the currently selected person.

The Help Window is a separate window opened by the `help` command (or `F1`), and displays command guidance plus a User Guide link.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format
--------|------------------
**List** | `list`
**Add** | `add n/NAME m/NUS_MATRIC [role/ROLE] soc/SOC_USERNAME gh/GITHUB_USERNAME e/EMAIL p/PHONE t/TUTORIAL_GROUP [tag/TAG]...`<br><br>e.g. `add n/John Doe m/A0234567X role/tutor soc/johndoe gh/johndoe e/johndoe@u.nus.edu p/91234567 t/01`
**Edit** | `edit INDEX [n/NAME] [m/NUS_MATRIC] [role/ROLE] [soc/SOC_USERNAME] [gh/GITHUB_USERNAME] [e/EMAIL] [p/PHONE] [t/TUTORIAL_GROUP] [tag/TAG]...`<br><br>e.g. `edit 2 p/98765432 e/johndoe@example.com`
**Delete** | `delete id/INDEX [MORE_INDEXES]...`<br>`delete m/NUS_MATRIC [MORE_NUS_MATRICS]...`<br><br>e.g. `delete id/1 3 5`
**Find** | `find a/KEYWORD [MORE_KEYWORDS]...`<br>`find n/KEYWORD [MORE_NAME_KEYWORDS]...`<br>`find m/NUS_MATRIC [MORE_NUS_MATRICS]...`<br><br>e.g. `find n/jane n/eunice m/A0123456J`
**Tag** | `tag add id/INDEX [MORE_INDEXES]... tag/TAG [MORE_TAGS]...`<br>`tag add m/NUS_MATRIC [MORE_NUS_MATRICS]... tag/TAG [MORE_TAGS]...`<br>`tag delete id/INDEX [MORE_INDEXES]... tag/TAG [MORE_TAGS]...`<br>`tag delete m/NUS_MATRIC [MORE_NUS_MATRICS]... tag/TAG [MORE_TAGS]...`<br><br>e.g. `tag add id/1 2 tag/friend tutor`
**Filter** | `filter [tag/TAG]... [t/TUTORIAL_GROUP_NUMBER]`<br><br>e.g. `filter tag/friends t/01`
**Sort** | `sort tg`<br>`sort name`<br><br>e.g. `sort tg`
**Import** | `import "FILE_PATH" [keep/current|keep/incoming]`<br><br>e.g. `import "data/addressbook.json" keep/current`
**Export** | `export "FILE_PATH"`<br><br>e.g. `export "data/backup.json"`
**Mask** | `mask`
**Unmask** | `unmask`
**Help** | `help [COMMAND]`
**Clear** | `clear`<br>`clear confirm/yes`
**Exit** | `exit`

--------------------------------------------------------------------------------------------------------------------

## Features
<div markdown="block" class="alert alert-info">

**:information_source: Notes about command format:**<br>

* A command has a command word plus fields.
* Command word: `add`, `edit`, `find`, ...
* Prefixes identify each field, e.g. `n/`, `m/`, `e/`.
* `/` is reserved for prefixes and cannot appear in any field value.
* Words in `UPPER_CASE` are values to provide.
* Items in square brackets are optional.
* `...` means the field can be repeated.
* Parameters can be in any order.
* For commands without parameters (`list`, `mask`, `unmask`, `exit`), extra text does not block execution and is reported as ignored.
* e.g. `add n/John Doe m/A0234567X role/tutor soc/johndoe gh/johndoe e/johndoe@u.nus.edu p/91234567 t/01 tag/mentor`
</div>

### Listing all student and tutor records : `list`

Shows all records currently stored in CMS.

**Format:** `list`

**Constraints:** None.

**Example:** `list`

**Expected result:**
* The Person List Panel shows all stored records.
* The Result Display confirms that all records are listed.

### Adding a student / tutor : `add`

Adds a student or tutor record to CMS.

**Format:** `add n/NAME m/NUS_MATRIC [role/ROLE] soc/SOC_USERNAME gh/GITHUB_USERNAME e/EMAIL p/PHONE t/TUTORIAL_GROUP [tag/TAG]...`

**Constraints:**
* `n/`, `m/`, `soc/`, `gh/`, `e/`, `p/`, and `t/` are mandatory.
* All field values must satisfy the rules in [Fields and accepted formats](#fields-and-accepted-formats).
* `m/`, `soc/`, `gh/`, and `e/` must be unique and cannot duplicate an existing person.
* If `role/` is omitted, the role defaults to `student`.

**Examples:**
* `add n/David Tan m/A0211111L role/student soc/david1 gh/davidtan99 e/david@u.nus.edu p/97654321 t/05`
* `add n/John Doe m/A0234567X role/tutor soc/johndoe gh/johndoe e/johndoe@u.nus.edu p/91234567 t/01 tag/python-experienced`
* `add n/Jane Lim m/A0123456J soc/janelim gh/jane-lim e/jane@u.nus.edu p/81234567 t/03`

**Expected result:**
* The new person appears in the Person List Panel.
* The Result Display confirms the added person.

<div markdown="span" class="alert alert-info">:information_source: **Note:**
Use the [Fields and accepted formats](#fields-and-accepted-formats) section as a checklist before submitting the command.
</div>

### Editing a student / tutor : `edit`

Edits an existing student or tutor record in CMS.

**Format:** `edit INDEX [n/NAME] [m/NUS_MATRIC] [role/ROLE] [soc/SOC_USERNAME] [gh/GITHUB_USERNAME] [e/EMAIL] [p/PHONE] [t/TUTORIAL_GROUP] [tag/TAG]...`

**Constraints:**
* `INDEX` must be a positive integer (1, 2, 3, ...).
* At least one field must be provided after the index.
* Each provided field replaces the existing value for that field.
* If `tag/` appears, it replaces the full tag set instead of adding to it.
* `tag/` with no value clears all tags.
* Edited field values must satisfy the same rules as `add`.

**Examples:**
* `edit 1 p/91234567 e/johndoe@example.com`
* `edit 2 n/Betsy Crower tag/`
* `edit 3 m/A0654321J role/student soc/betsy3 gh/betsycrowe t/07`

**Expected result:**
* The selected person's record is updated in CMS.
* The Person List Panel reflects the edited values.
* The Result Display confirms the edited person.

### Deleting a student / tutor : `delete`

Deletes one or more persons by displayed index, or by NUS Matric.

**Format:**
* `delete id/INDEX [MORE_INDEXES]...`
* `delete m/NUS_MATRIC [MORE_NUS_MATRICS]...`

**Constraints:**
* Use either displayed indexes or NUS Matrics, not both in the same command.
* `id/` is required for index-based deletion.
* Each index must refer to the current displayed list and be a positive integer.
* Each NUS Matric must be valid.

**Examples:**
* `delete id/2`
* `delete id/1 3 5`
* `delete m/A0234567X`
* `delete m/A0234567X A0345678L`

**Expected result:**
* Matching person(s) are removed from the Person List Panel.
* The Result Display confirms which person(s) were deleted.

### Finding students / tutors : `find`

Finds persons by name, NUS Matric, or any supported field.

**Format:**
* `find a/KEYWORD [MORE_KEYWORDS]...`
* `find n/KEYWORD [MORE_NAME_KEYWORDS]...`
* `find m/NUS_MATRIC [MORE_NUS_MATRICS]...`
* `find n/KEYWORD [MORE_NAME_KEYWORDS]... m/NUS_MATRIC [MORE_NUS_MATRICS]...`

**Constraints:**
* At least one of `a/`, `n/`, or `m/` must be present.
* Every used prefix must contain at least one non-blank keyword.
* `a/` matches across supported fields, including name, NUS Matric, phone, email, SoC username, GitHub username, role, tutorial group, and tags.
* When multiple prefixes are used, matches are combined with OR.
* `n/` matching is case-insensitive and matches full words.
* `m/` matching is case-insensitive.

**Examples:**
* `find a/jane`
* `find n/jane n/eunice`
* `find n/jane eunice`
* `find n/jane n/eunice m/A0123456J m/A1234567X`
* `find m/A0123456J A1234567X`
* `find m/A0123456J m/A1234567X`

**Expected result:**
* The Person List Panel updates to show matching persons.
* If there are no matches, the list is empty.

### Adding or removing tags : `tag`

Adds or removes one or more tags from one or more persons.

**Format:**
* `tag add id/INDEX [MORE_INDEXES]... tag/TAG [MORE_TAGS]...`
* `tag add m/NUS_MATRIC [MORE_NUS_MATRICS]... tag/TAG [MORE_TAGS]...`
* `tag delete id/INDEX [MORE_INDEXES]... tag/TAG [MORE_TAGS]...`
* `tag delete m/NUS_MATRIC [MORE_NUS_MATRICS]... tag/TAG [MORE_TAGS]...`

**Constraints:**
* The action must be either `add` or `delete`.
* Target persons must be specified by either displayed indexes (`id/`) or NUS Matrics (`m/`), not both.
* Each index must refer to the current displayed list and be a positive integer.
* At least one target person and one tag must be provided.
* Tag values must satisfy the rules for [`tag/TAG`](#field-tag).
* Repeated tags in the same command are ignored.

**Examples:**
* `tag add id/1 2 tag/friend tutor`
* `tag add m/A1234567X A2345678L tag/mentor`
* `tag delete id/3 tag/friend`
* `tag delete m/A1234567X tag/mentor`

**Expected result:**
* The selected persons' tags are updated.
* The Result Display confirms the tag operation.

### Filtering students / tutors : `filter`

Filters persons by tag, tutorial group, or both.

**Format:** `filter [tag/TAG]... [t/TUTORIAL_GROUP_NUMBER]`

**Constraints:**
* At least one of `tag/` or `t/` must be present.
* `tag/` may be repeated; `t/` may appear at most once.
* When multiple `tag/` values are given, all of them must match.
* If `t/` is given, it must match the person's tutorial group.
* When both `tag/` and `t/` are provided, both conditions must be satisfied.
* Tag matching is case-insensitive.
* Tutorial group input must be a number from `1` to `99`, with leading zeros allowed.

**Examples:**
* `filter tag/friend`
* `filter t/01`
* `filter tag/friend tag/mentor`
* `filter tag/friend t/01`

**Expected result:**
* The Person List Panel updates to show persons matching all provided filter criteria.
* If there are no matches, the list is empty.

### Sorting records : `sort`

Sorts all persons by name or tutorial group.

**Format:**
* `sort name`
* `sort tg`

**Constraints:** The sort key must be either `name` or `tg`.

**Examples:**
* `sort NAME`
* `sort Tg`

**Expected result:**
* Persons are reordered based on the selected sort key.

### File Path Requirements

For both `import` and `export` commands:
* File paths must be enclosed in double quotes.
* File paths may be relative or absolute.
* File names and folder names may contain spaces.
* Platform path separators such as `/` and `\` are accepted.
* The file name must end with `.json`.
* Do not include double quotes (`"`) in the path.
* Avoid characters that are invalid in file names on your operating system, such as `#`, `%`, `?`, `*`, `<`, `>`, or `|`.

**Valid examples:**
* `export "data/backup.json"`
* `export "data/My Backups/backup.json"`
* `export "C:/Users/Test/Documents/backup.json"`

**Invalid examples:**
* `export data/backup.json`<br>
   Missing the required double quotes.
* `export "data/backup.txt"`<br>
   The file name must end with `.json`.
* `export "data/backup?.json"`<br>
   `?` is not a valid file-name character.

### Importing records from a JSON file : `import`

Imports records from a `.json` file into CMS.

**Format:** `import "FILE_PATH" [keep/current|keep/incoming]`

**Constraints:**
* `FILE_PATH` must point to a `.json` file (see [File Path Requirements](#file-path-requirements) above).
* If CMS already contains data, a keep policy is required.
* The keep policy must be either `keep/current` or `keep/incoming`.

**Examples:**
* `import "data/addressbook.json"`
* `import "data/addressbook.json" keep/current`
* `import "data/addressbook.json" keep/incoming`

**Expected result:**
* Records from the file are imported into CMS.
* The Result Display confirms the import outcome.
* If CMS already contains data and no keep policy is provided, CMS rejects the command and asks you to rerun it with a keep policy.

### Exporting records to a JSON file : `export`

Exports current CMS data to a `.json` file.

**Format:** `export "FILE_PATH"`

**Constraints:**
* `FILE_PATH` must end with `.json` (see [File Path Requirements](#file-path-requirements) above).

**Examples:**
* `export "data/backup.json"`
* `export "data/My Backups/backup.json"`

**Expected result:**
* Current CMS data is written to the specified file.
* The Result Display confirms the export outcome.

### Masking sensitive fields : `mask`

Masks sensitive fields (NUS ID, SoC username, GitHub username, email, phone number) in the person list and detail panels.

**Format:** `mask`

**Constraints:** None.

**Example:** `mask`

**Expected result:**
* Sensitive fields are hidden until `unmask` is used.
* Extra text after `mask` is reported as ignored.

### Unmasking sensitive fields : `unmask`

Unmasks sensitive fields in the person list and detail panels.

**Format:** `unmask`

**Constraints:** None.

**Example:** `unmask`

**Expected result:**
* Sensitive fields are shown again.
* Extra text after `unmask` is reported as ignored.

### Viewing help : `help`

Opens the Help Window with command guidance and a User Guide link.

**Format:** `help [COMMAND]`

**Constraints:**
* `COMMAND` is optional.
* If provided, `COMMAND` must be a supported command word.
* Only one command word may be supplied.

**Examples:**
* `help`
* `help add`

**Expected result:**
* The Help Window opens or gains focus.
* CMS shows either the command summary or detailed help for the requested command.

### Purging all records : `clear`

Deletes **all** records from CMS.

**Format:**
* `clear`
* `clear confirm/yes`

**Constraints:**
* The command accepts only no argument or the exact confirmation token `confirm/yes`.
* Any other input after `clear` is treated as an invalid format.

**Examples:**
* `clear`
* `clear confirm/yes`

**Expected result:**
* All stored records are removed from CMS after confirmation.
* The Person List Panel becomes empty.
* The Result Display confirms that all records were cleared.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
This action cannot be undone from within CMS.
</div>

### Exiting the program : `exit`

Exits CMS.

**Format:** `exit`

**Constraints:** None.

**Example:** `exit`

**Expected result:**
* CMS closes.
* Extra text after `exit` is reported as ignored.

### Saving data

CMS saves data automatically after commands that modify data.

### Editing the data file

CMS data is stored in `[CMS home folder]/data/cms.json`.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
Invalid edits can cause CMS to reset your data file on next launch. Back up `cms.json` before editing manually.
</div>

--------------------------------------------------------------------------------------------------------------------

## Fields and accepted formats

Use this section as a quick checklist when adding or editing command examples and field requirements throughout the User Guide.

<a id="field-name"></a>
**`n/NAME`**
* 1 to 128 characters and must include at least one letter.
* Allowed characters: letters, spaces, hyphens (`-`), apostrophes (`'`), and periods (`.`).
* Cannot be blank.
* Consecutive spaces are collapsed. E.g. `n/John   Doe` is treated as `n/John Doe`.
* Case sensitivity: case-sensitive (stored as entered after space normalization).
* Valid: `n/John Doe`
* Invalid: `n/Ravi s/o Kumar`

<a id="field-nus-matric"></a>
**`m/NUS_MATRIC`**
* Must be `A` + 7 digits + uppercase letter (e.g. `A0234567X`) or `U` + 6 digits + uppercase letter (e.g. `U023456W`).
* Must satisfy checksum validation, not just format.
* Must be unique in CMS.
* Case sensitivity: case-insensitive input (stored in uppercase).
* Valid: `m/A0234567X`
* Invalid: `m/B0234567B`

<a id="field-role"></a>
**`role/ROLE`**
* Must be exactly `student` or `tutor`.
* Case sensitivity: case-sensitive (lowercase only).
* Valid: `role/student`
* Invalid: `role/Student`

<a id="field-soc-username"></a>
**`soc/SOC_USERNAME`**
* Either a SoC-style username or a valid checksum-validated NUS Matric.
* SoC-style username rules:
  * 5 to 8 characters.
  * Lowercase letters, digits, and hyphens only.
  * Cannot start or end with a hyphen.
  * No spaces.
* If `soc/SOC_USERNAME` itself is in valid NUS Matric format, it must match `m/NUS_MATRIC` for that same person.
* Must be unique in CMS.
* Case sensitivity: case-insensitive input (stored in lowercase).
* Valid: `soc/john1`
* Invalid: `soc/-john`

<a id="field-github-username"></a>
**`gh/GITHUB_USERNAME`**
* 1-39 characters.
* Letters, digits, and hyphens only.
* Cannot start/end with `-` and cannot contain consecutive hyphens (`--`).
* Must be unique in CMS.
* Case sensitivity: case-insensitive input (stored in lowercase).
* Valid: `gh/jane-lim123`
* Invalid: `gh/-jane`

<a id="field-email"></a>
**`e/EMAIL`**
* Must be a valid email format.
* Must be unique in CMS.
* Case sensitivity: case-insensitive input (stored in lowercase).
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
* Must be a number from `1` to `99`.
* Leading zeros are allowed in input.
* Valid: `t/01`
* Invalid: `t/100`

<a id="field-tag"></a>
**`tag/TAG`**
* Optional, repeatable.
* Alphanumeric characters, with optional single hyphens to replace spaces.
* Spaces are treated as separators, so `tag/needs help` becomes two tags: `needs` and `help`.
* Cannot start or end with a hyphen.
* Repeated tags for the same person are kept only once.
* Case sensitivity: case-insensitive input (stored in lowercase).
* Valid: `tag/python`
* Invalid: `tag/-help`

--------------------------------------------------------------------------------------------------------------------

## Glossary

**CLI**: Command Line Interface used to control CMS by typing commands.

**Command word**: The action keyword at the start of a command, e.g. `add`, `find`, `help`.

**Field**: A value supplied with a prefix in a command, e.g. `n/John Doe`.

**Prefix**: A marker that indicates what a field means, e.g. `n/`, `m/`, `e/`.

**INDEX**: A 1-based position of a person in the currently displayed list.

**NUS Matric**: Identifier given by NUS in checksum-validated format `A` + 7 digits + a letter (e.g., `A0234567X`) or `U` + 6 digits + a letter (e.g., `U023456W`).

**SoC username**: School of Computing account username stored in the `soc/` field.

**Tutorial group**: Class/tutorial group number in the `t/` field.

**Tag**: Optional label used to categorize a person, e.g. `tag/python`. Could be used for any purpose such as indicating needs, statuses, or grades.

**Help Window**: Separate window that displays help text and a User Guide hyperlink.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the app to a secondary screen and later switch to one screen, the GUI may open off-screen. Delete `preferences.json` before starting again.
2. **If the Help Window is minimized**, triggering help again may keep it minimized, and requires you to manually restore it.

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install CMS on the other computer, launch once, then replace the new `data/cms.json` with your old one.

**Q**: Where are my preferences saved?<br>
**A**: Preferences are saved in `preferences.json` in your CMS working directory.

**Q**: Can I undo `delete` or `clear`?<br>
**A**: No. There is currently no undo feature, so keep backups of `data/cms.json` if needed.

**Q**: Why is my `find` command not returning results?<br>
**A**: Check your prefixes and input format (`a/`, `n/`, `m/`), and verify that full-word matching rules are met for name searches.
