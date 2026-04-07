package cms.logic;

import static cms.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static cms.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static cms.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.GITHUBUSERNAME_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.NUSMATRIC_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.ROLE_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.SOCUSERNAME_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.TUTORIALGROUP_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_GITHUBUSERNAME_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_NUSMATRIC_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_SOCUSERNAME_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_TUTORIALGROUP_BOB;
import static cms.testutil.Assert.assertThrows;
import static cms.testutil.TypicalPersons.AMY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cms.commons.exceptions.DataLoadingException;
import cms.logic.commands.AddCommand;
import cms.logic.commands.CommandResult;
import cms.logic.commands.ExportCommand;
import cms.logic.commands.ImportCommand;
import cms.logic.commands.ListCommand;
import cms.logic.commands.SortCommand;
import cms.logic.commands.TagCommand;
import cms.logic.commands.exceptions.CommandException;
import cms.logic.parser.exceptions.ParseException;
import cms.model.AddressBook;
import cms.model.Model;
import cms.model.ModelManager;
import cms.model.ReadOnlyAddressBook;
import cms.model.UserPrefs;
import cms.model.person.Person;
import cms.storage.JsonAddressBookStorage;
import cms.storage.JsonUserPrefsStorage;
import cms.storage.StorageManager;
import cms.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private final Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_sortCommandByTutorialGroup_success() throws Exception {
        Person tutorialGroupTen = new PersonBuilder()
                .withName("Logic Sort Alpha")
                .withNusMatric("A1999991L")
                .withEmail("logic-sort-a@test.com")
                .withSocUsername("logic1")
                .withGithubUsername("logic-gh-1")
                .withTutorialGroup("10")
                .build();
        Person tutorialGroupTwo = new PersonBuilder()
                .withName("Logic Sort Beta")
                .withNusMatric("A1999992J")
                .withEmail("logic-sort-b@test.com")
                .withSocUsername("logic2")
                .withGithubUsername("logic-gh-2")
                .withTutorialGroup("02")
                .build();

        model.addPerson(tutorialGroupTen);
        model.addPerson(tutorialGroupTwo);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.sortPersonsByTutorialGroup();

        assertCommandSuccess(SortCommand.COMMAND_WORD + " " + SortCommand.SORT_BY_TUTORIAL_GROUP,
                SortCommand.MESSAGE_SUCCESS_TUTORIAL_GROUP, expectedModel);
    }

    @Test
    public void execute_sortCommandByName_success() throws Exception {
        Person zed = new PersonBuilder()
                .withName("Zed Logic")
                .withNusMatric("A1999993H")
                .withEmail("logic-sort-c@test.com")
                .withSocUsername("logic3")
                .withGithubUsername("logic-gh-3")
                .withTutorialGroup("03")
                .build();
        Person amy = new PersonBuilder()
                .withName("Amy Logic")
                .withNusMatric("A1999994E")
                .withEmail("logic-sort-d@test.com")
                .withSocUsername("logic4")
                .withGithubUsername("logic-gh-4")
                .withTutorialGroup("04")
                .build();

        model.addPerson(zed);
        model.addPerson(amy);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.sortPersonsByName();

        assertCommandSuccess(SortCommand.COMMAND_WORD + " " + SortCommand.SORT_BY_NAME,
                SortCommand.MESSAGE_SUCCESS_NAME, expectedModel);
    }

    @Test
    public void execute_tagCommandAdd_success() throws Exception {
        Person firstPerson = new PersonBuilder()
                .withName("Tag Logic Alpha")
                .withNusMatric("A1888881W")
                .withEmail("tag-logic-a@test.com")
                .withSocUsername("taglogi1")
                .withGithubUsername("tag-logic-1")
                .build();
        Person secondPerson = new PersonBuilder()
                .withName("Tag Logic Beta")
                .withNusMatric("A1888882U")
                .withEmail("tag-logic-b@test.com")
                .withSocUsername("taglogi2")
                .withGithubUsername("tag-logic-2")
                .build();

        model.addPerson(firstPerson);
        model.addPerson(secondPerson);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person updatedFirstPerson = new PersonBuilder(firstPerson).withTags("tag1", "tag2").build();
        Person updatedSecondPerson = new PersonBuilder(secondPerson).withTags("tag1", "tag2").build();
        expectedModel.setPerson(firstPerson, updatedFirstPerson);
        expectedModel.setPerson(secondPerson, updatedSecondPerson);

        String commandText = TagCommand.COMMAND_WORD + " add n/1 2 tag/tag1 tag2";
        String expectedMessage = "tag1, tag2 has been added to "
            + "1, Tag Logic Alpha, A1888881W; 2, Tag Logic Beta, A1888882U";

        assertCommandSuccess(commandText, expectedMessage, expectedModel);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_exportCommandStorageThrowsIoException_throwsCommandException() {
        Path prefPath = temporaryFolder.resolve("addressBook.json");
        IOException exportException = new IOException("dummy export IO exception");

        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath)
                    throws IOException {
                if (!filePath.equals(prefPath)) {
                    throw exportException;
                }
                super.saveAddressBook(addressBook, filePath);
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);

        Path exportPath = temporaryFolder.resolve("exports").resolve("willFail.json");
        String exportCommand = ExportCommand.COMMAND_WORD + " \"" + exportPath + "\"";
        String expectedMessage = String.format(
            ExportCommand.MESSAGE_EXPORT_ERROR_FORMAT, exportPath, exportException.getMessage());

        assertCommandFailure(exportCommand, CommandException.class, expectedMessage);
    }

    @Test
    public void execute_exportCommand_writesToSpecifiedPath() throws Exception {
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + NUSMATRIC_DESC_AMY + ROLE_DESC_AMY
                + SOCUSERNAME_DESC_AMY + GITHUBUSERNAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + TUTORIALGROUP_DESC_AMY;
        logic.execute(addCommand);

        Path exportPath = temporaryFolder.resolve("exports").resolve("exportedData.json");
        CommandResult result = logic.execute(ExportCommand.COMMAND_WORD + " \"" + exportPath + "\"");

        assertEquals(String.format(ExportCommand.MESSAGE_SUCCESS, exportPath), result.getFeedbackToUser());
        assertTrue(Files.exists(exportPath));
    }

    @Test
    public void execute_importCommand_readsFromSpecifiedPath() throws Exception {
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + NUSMATRIC_DESC_AMY + ROLE_DESC_AMY
                + SOCUSERNAME_DESC_AMY + GITHUBUSERNAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + TUTORIALGROUP_DESC_AMY;
        logic.execute(addCommand);

        Path exportPath = temporaryFolder.resolve("exports").resolve("importSource.json");
        logic.execute(ExportCommand.COMMAND_WORD + " \"" + exportPath + "\"");

        model.setAddressBook(new AddressBook());
        assertEquals(0, model.getFilteredPersonList().size());

        String importCommand = ImportCommand.COMMAND_WORD + " \"" + exportPath + "\"";
        CommandResult result = logic.execute(importCommand);

        assertEquals(String.format(ImportCommand.MESSAGE_SUCCESS, exportPath), result.getFeedbackToUser());
        assertEquals(1, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_importCommandEmptyFile_throwsCommandException() {
        Path prefPath = temporaryFolder.resolve("addressBook.json");

        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) {
                return Optional.empty();
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);

        Path importPath = temporaryFolder.resolve("imports").resolve("empty.json");
        String importCommand = ImportCommand.COMMAND_WORD + " \"" + importPath + "\"";

        assertCommandFailure(importCommand, CommandException.class,
            "Import file is empty or not a valid Course Management System data file.");
    }

    @Test
    public void execute_importCommandInvalidData_throwsCommandException() {
        Path prefPath = temporaryFolder.resolve("addressBook.json");

        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
                throw new DataLoadingException(new IOException("invalid data"));
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);

        Path importPath = temporaryFolder.resolve("imports").resolve("invalid.json");
        String importCommand = ImportCommand.COMMAND_WORD + " \"" + importPath + "\"";

        assertCommandFailure(importCommand, CommandException.class,
            "Import file contains invalid Course Management System data.");
    }

    @Test
    public void execute_importCommandWithCurrentDataAndNoKeep_throwsCommandException() throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + NUSMATRIC_DESC_AMY + ROLE_DESC_AMY
                + SOCUSERNAME_DESC_AMY + GITHUBUSERNAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + TUTORIALGROUP_DESC_AMY);
        Person expectedCurrentPerson = new PersonBuilder(AMY).withTags().build();

        Person conflictingIncomingPerson = new PersonBuilder(AMY)
            .withName("Amy Updated")
            .withPhone("99990000")
            .build();
        Path importPath = createImportFileWithSinglePerson(conflictingIncomingPerson);

        Path normalizedImportPath = importPath.toAbsolutePath().normalize();
        String importCommand = buildImportCommand(normalizedImportPath, null);

        CommandException thrownException = org.junit.jupiter.api.Assertions.assertThrows(
            CommandException.class, () -> logic.execute(importCommand));
        assertTrue(thrownException.getMessage().contains(ImportCommand.MESSAGE_KEEP_REQUIRED_NON_EMPTY));
        assertTrue(thrownException.getMessage().contains(conflictingIncomingPerson.getName().toString()));
        assertTrue(thrownException.getMessage().contains(expectedCurrentPerson.getName().toString()));

        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(expectedCurrentPerson, model.getFilteredPersonList().get(0));
    }

    @Test
    public void execute_importCommandKeepCurrent_keepsExistingData() throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + NUSMATRIC_DESC_AMY + ROLE_DESC_AMY
                + SOCUSERNAME_DESC_AMY + GITHUBUSERNAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + TUTORIALGROUP_DESC_AMY);
        Person expectedCurrentPerson = new PersonBuilder(AMY).withTags().build();

        Person conflictingIncomingPerson = new PersonBuilder(AMY)
                .withName("Amy Updated")
                .withPhone("99990000")
                .build();
        Person nonConflictingIncomingPerson = new PersonBuilder()
                .withName(VALID_NAME_BOB)
                .withNusMatric(VALID_NUSMATRIC_BOB)
                .withSocUsername(VALID_SOCUSERNAME_BOB)
                .withGithubUsername(VALID_GITHUBUSERNAME_BOB)
                .withEmail(VALID_EMAIL_BOB)
                .withPhone(VALID_PHONE_BOB)
                .withTutorialGroup(VALID_TUTORIALGROUP_BOB)
                .build();
        Path importPath = createImportFileWithPersons(conflictingIncomingPerson, nonConflictingIncomingPerson);
        Path normalizedImportPath = importPath.toAbsolutePath().normalize();

        String importCommand = buildImportCommand(normalizedImportPath, "keep/current");
        CommandResult result = logic.execute(importCommand);

        assertEquals(String.format(ImportCommand.MESSAGE_KEEP_CURRENT_SUCCESS, normalizedImportPath),
            result.getFeedbackToUser());
        assertEquals(2, model.getFilteredPersonList().size());
        assertTrue(model.getFilteredPersonList().contains(expectedCurrentPerson));
        assertTrue(model.getFilteredPersonList().contains(nonConflictingIncomingPerson));
    }

    @Test
    public void execute_importCommandKeepIncoming_replacesConflictsAndAddsNonConflicts() throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + NUSMATRIC_DESC_AMY + ROLE_DESC_AMY
                + SOCUSERNAME_DESC_AMY + GITHUBUSERNAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + TUTORIALGROUP_DESC_AMY);

        Person conflictingIncomingPerson = new PersonBuilder(AMY)
                .withName("Amy Updated")
                .withPhone("99990000")
                .build();
        Person nonConflictingIncomingPerson = new PersonBuilder()
                .withName(VALID_NAME_BOB)
                .withNusMatric(VALID_NUSMATRIC_BOB)
                .withSocUsername(VALID_SOCUSERNAME_BOB)
                .withGithubUsername(VALID_GITHUBUSERNAME_BOB)
                .withEmail(VALID_EMAIL_BOB)
                .withPhone(VALID_PHONE_BOB)
                .withTutorialGroup(VALID_TUTORIALGROUP_BOB)
                .build();
        Path importPath = createImportFileWithPersons(conflictingIncomingPerson, nonConflictingIncomingPerson);
        Path normalizedImportPath = importPath.toAbsolutePath().normalize();

        String importCommand = buildImportCommand(normalizedImportPath, "keep/incoming");
        CommandResult result = logic.execute(importCommand);

        assertEquals(String.format(ImportCommand.MESSAGE_KEEP_INCOMING_SUCCESS, normalizedImportPath),
                result.getFeedbackToUser());
        assertEquals(2, model.getFilteredPersonList().size());
        assertTrue(model.getFilteredPersonList().contains(conflictingIncomingPerson));
        assertTrue(model.getFilteredPersonList().contains(nonConflictingIncomingPerson));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    @Test
    public void isMasked_reflectsModelPreference() {
        assertEquals(false, logic.isMasked());
        model.setMasked(true);
        assertEquals(true, logic.isMasked());
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
                                      Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand) {
        assertCommandFailure(inputCommand, ParseException.class, MESSAGE_UNKNOWN_COMMAND);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand) {
        assertCommandFailure(inputCommand, CommandException.class, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
                                      String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     *
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
                                      String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e               the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with an AddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath)
                    throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + NUSMATRIC_DESC_AMY + ROLE_DESC_AMY
                + SOCUSERNAME_DESC_AMY + GITHUBUSERNAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + TUTORIALGROUP_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }

    private Path createImportFileWithSinglePerson(Person person) throws IOException {
        return createImportFileWithPersons(person);
    }

    private Path createImportFileWithPersons(Person... persons) throws IOException {
        AddressBook incomingAddressBook = new AddressBook();
        for (Person person : persons) {
            incomingAddressBook.addPerson(person);
        }

        String fileName = persons.length > 0 ? persons[0].getNusMatric().toString() : "import";
        Path importPath = temporaryFolder.resolve("imports").resolve(fileName + ".json");
        new JsonAddressBookStorage(importPath).saveAddressBook(incomingAddressBook, importPath);
        return importPath;
    }

    private String buildImportCommand(Path importPath, String keepOption) {
        String pathText = importPath.toAbsolutePath().normalize().toString();
        String pathArg = pathText.contains(" ") ? "\"" + pathText + "\"" : pathText;
        String command = ImportCommand.COMMAND_WORD + " " + pathArg;
        if (keepOption != null) {
            command += " " + keepOption;
        }
        return command;
    }
}
