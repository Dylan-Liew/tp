package cms.logic;

import static cms.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static cms.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static cms.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.GITHUBUSERNAME_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.NUSID_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.ROLE_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.SOCUSERNAME_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.TUTORIALGROUP_DESC_AMY;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static cms.testutil.Assert.assertThrows;
import static cms.testutil.TypicalPersons.AMY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cms.logic.commands.AddCommand;
import cms.logic.commands.CommandResult;
import cms.logic.commands.ExportCommand;
import cms.logic.commands.ListCommand;
import cms.logic.commands.exceptions.CommandException;
import cms.logic.parser.exceptions.ParseException;
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

    private Model model = new ModelManager();
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
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
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
                LogicManager.FILE_OPS_EXPORT_ERROR_FORMAT, exportPath, exportException.getMessage());

        assertCommandFailure(exportCommand, CommandException.class, expectedMessage);
    }

    @Test
    public void execute_exportCommand_writesToSpecifiedPath() throws Exception {
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + NUSID_DESC_AMY + ROLE_DESC_AMY
                + SOCUSERNAME_DESC_AMY + GITHUBUSERNAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + TUTORIALGROUP_DESC_AMY;
        logic.execute(addCommand);

        Path exportPath = temporaryFolder.resolve("exports").resolve("exportedData.json");
        CommandResult result = logic.execute(ExportCommand.COMMAND_WORD + " \"" + exportPath + "\"");

        assertEquals(String.format(ExportCommand.MESSAGE_SUCCESS, exportPath), result.getFeedbackToUser());
        assertTrue(Files.exists(exportPath));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
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
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
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
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + NUSID_DESC_AMY + ROLE_DESC_AMY
                + SOCUSERNAME_DESC_AMY + GITHUBUSERNAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + TUTORIALGROUP_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
