---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project is adapted from [AddressBook-Level3](https://se-education.org/addressbook-level3/), created by the [SE-EDU initiative](https://se-education.org/). Some architecture, documentation structure, and starter code were inherited from that codebase.
* The GUI is built with [JavaFX](https://openjfx.io/).
* JSON serialization and deserialization use [Jackson](https://github.com/FasterXML/jackson).
* Automated tests use [JUnit 5](https://junit.org/junit5/).
* Documentation diagrams were created with [PlantUML](https://plantuml.com/).
* The project website and documentation site are built with [Jekyll](https://jekyllrb.com/) and the [Minima](https://github.com/jekyll/minima) theme.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in the [`docs/diagrams`](https://github.com/AY2526S2-CS2103T-F10-2/tp/tree/master/docs/diagrams) folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S2-CS2103T-F10-2/tp/tree/master/src/main/java/cms/Main.java) and [`MainApp`](https://github.com/AY2526S2-CS2103T-F10-2/tp/tree/master/src/main/java/cms/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete id/1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S2-CS2103T-F10-2/tp/tree/master/src/main/java/cms/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFX UI framework. The layout of these UI parts are defined in matching `.fxml` files in the [`src/main/resources/view`](https://github.com/AY2526S2-CS2103T-F10-2/tp/tree/master/src/main/resources/view) folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S2-CS2103T-F10-2/tp/tree/master/src/main/java/cms/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S2-CS2103T-F10-2/tp/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S2-CS2103T-F10-2/tp/tree/master/src/main/java/cms/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete id/1")` API call as an example.

![Interactions Inside the Logic Component for the `delete id/1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2526S2-CS2103T-F10-2/tp/tree/master/src/main/java/cms/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the CMS data, i.e. all `Person` objects, in a `UniquePersonList`.
* stores a _filtered_ list of `Person` objects (e.g. for `find` and `filter`) which is exposed to outsiders as an unmodifiable `ObservableList<Person>` so that the UI can update automatically when the displayed data changes.
* stores a `UserPrefs` object that represents the user's preferences. This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components, because the `Model` represents domain entities that should make sense on their own.

### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S2-CS2103T-F10-2/tp/tree/master/src/main/java/cms/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both CMS data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `cms.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes the current CMS implementation at a high level. The command flow, component responsibilities, and diagrams above reflect the existing architecture and supported command-based workflow.

### Masking sensitive fields

**Implementation**

The masking feature lets course coordinators hide sensitive fields in the UI without changing the underlying `Person` data.
Instead of rewriting stored records, CMS keeps a single masking preference in `UserPrefs` and routes it through the `Model` interface.

The `mask` command works as follows:

1. `LogicManager` receives the user input and forwards it to `AddressBookParser`.
2. `AddressBookParser` creates `MaskCommandParser`, which returns a `MaskCommand`.
3. `MaskCommand#execute(Model)` calls `Model#setMasked(true)`.
4. `ModelManager` updates the `isMasked` flag stored in `UserPrefs`.
5. After command execution succeeds, `MainWindow#refreshPersonListPanel(...)` rebuilds the list and detail panels using `logic.isMasked()`.
6. `PersonCard` and `PersonDetailPanel` use `MaskingUtil` to render masked values while the underlying `Person` objects remain unchanged.

The following sequence diagram shows the flow for `mask` and `unmask`:

![MaskUnmaskSequenceDiagram](images/MaskUnmaskSequenceDiagram.png)

`unmask` follows the same flow, except that `UnmaskCommand#execute(Model)` calls `Model#setMasked(false)`.

Because the masking state is stored in `UserPrefs`, it is restored on startup and saved when preferences are written on normal shutdown.

#### Design considerations

**Aspect: Where to store masking state**

* **Alternative 1 (current choice):** Store the masking flag in `UserPrefs` and expose it through `Model`.
  * Pros: Keeps a single source of truth for both commands and UI, and allows the preference to persist across sessions.
  * Cons: Since masking does not modify any `Person` objects, the UI must explicitly refresh after the command to repaint masked values.

* **Alternative 2:** Store the masking state only inside UI classes.
  * Pros: Keeps the feature local to presentation code.
  * Cons: The list panel and detail panel would need separate coordination, and the preference would not naturally persist across restarts.

### Import and export features

**Implementation**

The import and export features let course coordinators move CMS data between the in-memory model and external JSON files without bypassing the existing storage and validation pipeline.
Instead of introducing a separate file format or duplicate serialization path, CMS reuses the storage layer so that manual command input and imported data are validated consistently.

The `import` and `export` commands work as follows:

1. `LogicManager` receives the user input and forwards it to `AddressBookParser`.
2. `AddressBookParser` creates either `ImportCommandParser` or `ExportCommandParser`.
3. `ImportCommandParser` or `ExportCommandParser` validates that the file path is wrapped in double quotes and ends with `.json`.
4. For `import`, `ImportCommandParser` also extracts the optional keep policy token (`keep/current` or `keep/incoming`), and `ImportCommand#execute` rejects the command with usage guidance if current data is non-empty and no keep policy is given.
5. `ImportCommand#execute(Model, Storage)` asks the storage layer to read and deserialize the target JSON file into model-compatible records.
6. Imported persons are merged into the model according to the selected keep policy, with conflicts resolved in favor of either the current record or the incoming record.
7. `ExportCommand#execute(Model, Storage)` asks the storage layer to serialize the current model state and write it to the target JSON file.

Import reports invalid file paths, unsupported file extensions, invalid JSON content, and malformed records as command errors.
Export reports invalid file paths, unsupported file extensions, and write failures as command errors.
Existing files are overwritten during export.

### NUS matriculation number validation

**Implementation**

NUS matriculation number validation ensures that CMS accepts only canonical NUS-style identifiers with valid check digits, preventing malformed or mistyped identifiers from entering the model.
Instead of scattering validation rules across parsers and storage classes, CMS centralises the logic in `NusMatric` so that all entry points enforce the same format and checksum rules.

Validation through command parsing works as follows:

1. A command parser such as `AddCommandParser`, `EditCommandParser`, `DeleteCommandParser`, `FindCommandParser`, or `TagCommandParser` delegates matric parsing to `ParserUtil#parseNusMatric(...)` or `ParserUtil#parseNusMatrics(...)`.
2. `ParserUtil` trims the raw input and calls `NusMatric#getValidationErrorMessage(...)`.
3. `NusMatric` canonicalises the input by trimming surrounding whitespace and converting it to uppercase.
4. `NusMatric` validates the canonical form against the accepted patterns `A#######X` and `U######X`, where `#` is a digit and `X` is a letter.
5. If the format is valid, `NusMatric` computes the expected check digit using the numeric portion of the matric number, prefix-specific weights, modulo 13, and a fixed check-digit lookup table.
6. If the computed check digit matches the provided trailing letter, `ParserUtil` constructs a `NusMatric` object; otherwise, parsing fails with a checksum-specific error message.

Validation during JSON import follows a separate entry path.
`JsonAdaptedPerson#toModelType()` calls `NusMatric#getValidationErrorMessage(...)` before constructing the imported `NusMatric` object, so imported records and interactive command input follow the same format, canonicalisation, and checksum rules.

Because validation is performed before model objects are created, malformed or checksum-invalid matric numbers are rejected early and never enter the address book state.
The implementation also canonicalises accepted values into uppercase form, which ensures consistent storage, comparison, and duplicate detection.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

Given below are the requirements of Course Management System.

### Product scope

**Target user profile**:

* CS2103T course coordinator who manages 500+ students and a tutor team
* works mainly on a desktop workstation with a large monitor
* is extremely time-pressured and prefers high-efficiency workflows
* is a keyboard-first user who types very quickly
* prefers batch operations over repetitive manual tasks
* is comfortable using CLI-style tools, while appreciating a lightweight GUI for visibility

**Value proposition**: manage course logistics (students, tutors, groups) through fast, keyboard-driven commands


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a ... | I want to ... | So that ... |
| -------- | -------- | ------------- | ----------- |
| `* * *`  | new user | access an in-app command summary or help guide | I can recall command syntax without leaving the app |
| `* * *`  | course coordinator | add a new student or tutor record | I can populate the course roster quickly |
| `* * *`  | course coordinator | view all stored persons in one list | I can get an overview of the cohort and tutor team |
| `* * *`  | course coordinator | edit an existing person's details | I can keep contact, tutorial group, and tagging information accurate |
| `* * *`  | course coordinator | delete one or more persons by displayed index or NUS Matric | I can remove withdrawn or obsolete records efficiently |
| `* * *`  | course coordinator | search for persons by name, NUS Matric, or other supported fields | I can find specific records quickly in a large dataset |
| `* * *`  | course coordinator | receive clear validation errors for invalid inputs | I can correct mistakes immediately and preserve data quality |
| `* * *`  | course coordinator | prevent duplicate identifiers such as NUS Matric, SoC username, GitHub username, and email | I can keep each record unique and trustworthy |
| `* *`    | course coordinator | add tags to one or more persons | I can mark mentors, tutors, or other useful groupings quickly |
| `* *`    | course coordinator | remove tags from one or more persons | I can keep person categorisation up to date |
| `* *`    | course coordinator | filter persons by tutorial group and tags | I can focus on the subset of records relevant to the current task |
| `* *`    | course coordinator | sort the displayed records by name or tutorial group | I can review the roster in a useful order |
| `* *`    | course coordinator | import records from a JSON file | I can load an existing roster without retyping every entry |
| `* *`    | course coordinator | choose whether to keep current or incoming records during import conflicts | I can control how imported data merges with existing data |
| `* *`    | course coordinator | export the current roster to a JSON file | I can create a portable backup or share the data with other systems |
| `* *`    | course coordinator | mask sensitive fields in the UI | I can reduce unnecessary exposure of private information during demonstrations or shared viewing |
| `* *`    | course coordinator | clear all data when starting over | I can reset the system safely for a new dataset or test run |
| `* *`    | keyboard-first user | perform common record-management tasks entirely through commands | I can work faster than with a mouse-driven workflow |
| `*`      | course coordinator | preserve masked or unmasked display preferences across sessions | I do not have to reapply my preferred privacy view each time I reopen the app |
| `*`      | course coordinator | use displayed indexes for batch actions such as delete and tag | I can update multiple visible records with short commands |
| `*`      | course coordinator | import and export data using explicit file paths | I can manage backups and move data between environments when needed |

### Use cases


(For all use cases below, the **System** is the `Course Management System` and the **Actor** is the `course coordinator`, unless specified otherwise)

**Use case: Add a student or tutor**

**MSS**

1.  Course coordinator requests to add a person and provides the required fields
2.  Course Management System validates the input
3.  Course Management System adds the person and shows a confirmation message

    Use case ends.

**Extensions**

* 2a. The command is missing required fields or contains invalid values.

   * 2a1. Course Management System shows an error message.
   * 2a2. Course coordinator retries with corrected values.

      Use case resumes at step 1.

* 2b. A unique field duplicates an existing person.

   * 2b1. Course Management System shows an error message indicating the duplicate field.
   * 2b2. Course coordinator retries with a non-duplicate value.

      Use case resumes at step 1.

**Use case: Edit a person's details**

**MSS**

1.  Course coordinator requests to list or find persons
2.  Course Management System shows the matching persons
3.  Course coordinator requests to edit a person and provides one or more updated fields
4.  Course Management System validates the input
5.  Course Management System updates the person's details and shows a confirmation message

    Use case ends.

**Extensions**

* 2a. No persons match the current view or search.

   Use case ends.

* 4a. The specified displayed index is invalid.

   * 4a1. Course Management System shows an error message.
   * 4a2. Course coordinator retries with a valid index.

      Use case resumes at step 3.

* 4b. A provided field value is invalid or duplicates an existing unique field.

   * 4b1. Course Management System shows an error message.
   * 4b2. Course coordinator retries with corrected values.

      Use case resumes at step 3.

**Use case: Delete one or more persons**

**MSS**

1.  Course coordinator requests to list or find persons
2.  Course Management System shows the matching persons
3.  Course coordinator requests to delete one or more persons by displayed index or NUS Matric
4.  Course Management System validates the targets
5.  Course Management System deletes the matching person(s) and shows a confirmation message

    Use case ends.

**Extensions**

* 4a. The command mixes displayed indexes and NUS Matrics.

   * 4a1. Course Management System shows an error message.
   * 4a2. Course coordinator retries with only one target mode.

      Use case resumes at step 3.

* 4b. One or more targets are invalid or do not exist in the current displayed list.

   * 4b1. Course Management System shows an error message.
   * 4b2. Course coordinator retries with valid targets.

      Use case resumes at step 3.

**Use case: Find persons**

**MSS**

1.  Course coordinator requests to find persons using supported prefixes
2.  Course Management System validates the search input
3.  Course Management System shows the matching persons

    Use case ends.

**Extensions**

* 2a. The command omits required prefixes or uses an invalid format.

   * 2a1. Course Management System shows an error message.
   * 2a2. Course coordinator retries with a valid find command.

      Use case resumes at step 1.

* 3a. No persons match the search criteria.

   * 3a1. Course Management System shows an empty list and a result message indicating no matches.

      Use case ends.

**Use case: Add or remove tags from persons**

**MSS**

1.  Course coordinator requests to tag one or more persons by displayed index or NUS Matric
2.  Course Management System validates the target persons and tag values
3.  Course Management System updates the persons' tags and shows a confirmation message

    Use case ends.

**Extensions**

* 2a. The command mixes displayed indexes and NUS Matrics.

   * 2a1. Course Management System shows an error message.
   * 2a2. Course coordinator retries with only one target mode.

      Use case resumes at step 1.

* 2b. No target persons or no tags are provided, or one of the tags is invalid.

   * 2b1. Course Management System shows an error message.
   * 2b2. Course coordinator retries with valid targets and tags.

      Use case resumes at step 1.

**Use case: Filter persons**

**MSS**

1.  Course coordinator requests to filter persons by tag, tutorial group, or both
2.  Course Management System validates the filter criteria
3.  Course Management System shows the matching persons

    Use case ends.

**Extensions**

* 2a. No filter criteria are provided or a criterion is invalid.

   * 2a1. Course Management System shows an error message.
   * 2a2. Course coordinator retries with valid filter criteria.

      Use case resumes at step 1.

* 3a. No persons match the filter criteria.

   * 3a1. Course Management System shows an empty list.

      Use case ends.

**Use case: Sort persons**

**MSS**

1.  Course coordinator requests to sort the displayed persons by a supported key
2.  Course Management System validates the sort key
3.  Course Management System reorders the displayed persons

    Use case ends.

**Extensions**

* 2a. The sort key is not supported.

   * 2a1. Course Management System shows an error message.
   * 2a2. Course coordinator retries with a supported sort key.

      Use case resumes at step 1.

**Use case: Import records from JSON**

**MSS**

1.  Course coordinator requests to import data and provides a JSON file path
2.  Course Management System parses the file and validates the records
3.  Course Management System imports the records and shows a completion summary

    Use case ends.

**Extensions**

* 2a. The file cannot be read (missing, corrupted, invalid path, or unsupported extension).

   * 2a1. Course Management System shows an error message describing the issue.

      Use case ends.

* 2b. Existing data is non-empty and no keep policy is provided.

   * 2b1. Course Management System rejects the command and asks for `keep/current` or `keep/incoming`.
   * 2b2. Course coordinator retries the import with a keep policy.

      Use case resumes at step 1.

* 2c. Some records contain invalid data.

   * 2c1. Course Management System shows an error message.
   * 2c2. Course coordinator fixes the source file and retries the import.

      Use case resumes at step 1.

**Use case: Export records to JSON**

**MSS**

1.  Course coordinator requests to export data and provides a JSON file path
2.  Course Management System validates the file path
3.  Course Management System writes the current data to the file and shows a confirmation message

    Use case ends.

**Extensions**

* 2a. The file path is invalid or does not end with `.json`.

   * 2a1. Course Management System shows an error message.
   * 2a2. Course coordinator retries with a valid file path.

      Use case resumes at step 1.

* 3a. The file cannot be written.

   * 3a1. Course Management System shows an error message describing the failure.

      Use case ends.

**Use case: Mask or unmask sensitive fields**

**MSS**

1.  Course coordinator requests to mask or unmask sensitive fields
2.  Course Management System updates the masking state
3.  Course Management System refreshes the UI to reflect the new masking state

    Use case ends.

**Extensions**

* 1a. The command includes extra text after `mask` or `unmask`.

   * 1a1. Course Management System executes the command and reports that the extra text was ignored.

      Use case resumes at step 2.

**Use case: Clear all records**

**MSS**

1.  Course coordinator requests to clear all records
2.  Course Management System asks for explicit confirmation
3.  Course coordinator confirms the action
4.  Course Management System removes all records and shows a confirmation message

    Use case ends.

**Extensions**

* 3a. Course coordinator does not provide the required confirmation token.

   * 3a1. Course Management System leaves the data unchanged and shows a reminder message.

      Use case ends.

**Use case: View command help**

**MSS**

1.  Course coordinator requests help
2.  Course Management System opens the Help Window and shows command guidance

    Use case ends.

**Extensions**

* 1a. Course coordinator requests help for a specific command.

   * 1a1. Course Management System shows guidance for that command.

      Use case ends.

* 1b. Course coordinator specifies an unsupported command word.

   * 1b1. Course Management System shows an error message.

      Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to manage at least 500 student records (and associated tutors, groups, and courses) without a noticeable sluggishness in performance for typical usage.
3.  For common operations (e.g., add, search, filter, list, edit), the system should provide instant feedback (target: under 100ms) for a dataset size of 500 students.
4.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
5.  Should provide clear, actionable validation and error messages that help users recover quickly.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Tutorial group**: A tutor-led subgroup of students for administrative and teaching allocation purposes.
* **Sensitive student data**: Private fields such as phone numbers that may need masking.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

### Adding a student / tutor

1. Adding a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `add n/John Doe m/A0234567X role/student soc/johnd gh/john-gh p/91234567 e/john@example.com t/01`<br>
      Expected: New contact is added to the list. Details of the added contact shown in the status message.

   1. Test case: Repeat the same `add` command above a second time.<br>
      Expected: If a person with the same NUS Matric / SoC username / GitHub username / email already exists,
      the app shows an error message indicating a duplicate unique field. No person is added.

   1. Incorrect add command to try: `add`<br>
      Expected: Validation errors are shown describing the missing required fields or incorrect format.

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete id/1 3`<br>
      Expected: The first and third shown contacts are deleted in one command.

   1. Test case: `delete m/A0123456J`<br>
      Expected: The person with NUS Matric `A0123456J` is deleted.

   1. Incorrect delete command to try: `delete m/A9999999Z`<br>
      Expected: Error messages are shown describing the invalid command format or invalid target person(s).

### Finding persons

1. Finding persons by supported prefixes

   1. Prerequisites: List all persons using the `list` command. Use the sample data shown on first launch.

   1. Test case: `find n/Elle`<br>
      Expected: Only `Elle Meyer` remains in the shown list.

   1. Test case: `find m/A0234502U A0234505M`<br>
      Expected: Only the persons with NUS Matrics `A0234502U` and `A0234505M` remain in the shown list.

   1. Test case: `find a/friends`<br>
      Expected: Only persons whose fields contain `friends` remain in the shown list, including persons tagged `friends`.

   1. Test case: `find n/Elle m/A0234505M`<br>
      Expected: Matches are combined with OR, so both `Elle Meyer` and the person with NUS Matric `A0234505M` remain.

   1. Test case: `find n/NoSuchPerson`<br>
      Expected: The shown list becomes empty and the result display reports `0 persons listed!`

   1. Incorrect command to try: `find Elle`<br>
      Expected: Command is rejected because at least one supported prefix (`a/`, `n/`, or `m/`) is required.

### Tagging persons

1. Adding and removing tags

   1. Prerequisites: `list` has at least two persons.

   1. Test case: `tag add id/1 2 tag/mentor`<br>
      Expected: Tag `mentor` is added to both target persons.

   1. Test case: `tag delete id/1 tag/mentor`<br>
      Expected: Tag `mentor` is removed from person 1.

   1. Incorrect command to try: `tag add id/1 m/A0234501W tag/mentor`<br>
      Expected: Command is rejected because index and matric targeting cannot be mixed.

### Filtering persons

1. Filtering by tag/tutorial group

   1. Prerequisites: Add tags/tutorial groups so some people match and some do not.

   1. Test case: `filter tag/mentor t/01`<br>
      Expected: Only persons that have tag `mentor` and tutorial group `01` remain in the shown list.

   1. Incorrect command to try: `filter`<br>
      Expected: Command is rejected because at least one filter must be provided.

### Sorting persons

1. Sorting by supported keys

   1. Test case: `sort name`<br>
      Expected: Persons are sorted by name.

   1. Test case: `sort tg`<br>
      Expected: Persons are sorted by tutorial group.

   1. Test case: `sort NAME`<br>
      Expected: Persons are still sorted by name because the sort key is case-insensitive.

   1. Incorrect command to try: `sort tag`<br>
      Expected: Command is rejected with usage guidance.

### Masking and unmasking

1. Toggle display masking

   1. Test case: `mask`<br>
      Expected: Sensitive fields (NUS Matric, SoC username, GitHub username, email, phone) are masked in list/detail panels.

   1. Test case: `unmask`<br>
      Expected: Sensitive fields are shown again.

### Clearing all records

1. Clearing the address book with confirmation

   1. Prerequisites: CMS contains at least one person.

   1. Test case: `clear`<br>
      Expected: No records are deleted. CMS shows a reminder to run `clear confirm/yes`.

   1. Test case: `clear confirm/yes`<br>
      Expected: All records are deleted and the shown list becomes empty.

   1. Incorrect command to try: `clear foo`<br>
      Expected: Command is rejected with usage guidance.

### Exporting and importing JSON data

1. Export current data

   1. Test case: `export "data/manual-test-export.json"`<br>
      Expected: Command succeeds and creates/overwrites `data/manual-test-export.json`.

   1. Incorrect command to try: `export "data/manual-test-export.txt"`<br>
      Expected: Command is rejected because file path must end with `.json`.

1. Import data with and without keep policy

   1. Prerequisites: Use the exported file from the previous test.

   1. Test case: `import "data/manual-test-export.json"` (when current data is non-empty)<br>
      Expected: Command is rejected and asks for `keep/current` or `keep/incoming`.

   1. Test case: `import "data/manual-test-export.json" keep/current`<br>
      Expected: Command succeeds and keeps existing records where conflicts occur.

   1. Test case: `import "data/manual-test-export.json" keep/incoming`<br>
      Expected: Command succeeds and incoming records replace existing conflicting records.

   1. Incorrect command to try: `import "data/manual-test-export.txt"`<br>
      Expected: Command is rejected because file path must end with `.json`.

