package cms.logic.commands;

import static cms.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_GITHUBUSERNAME_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_GITHUBUSERNAME_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_NUSMATRIC_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_NUSMATRIC_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_SOCUSERNAME_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_SOCUSERNAME_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_TUTORIALGROUP_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_TUTORIALGROUP_BOB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cms.logic.commands.ImportCommand.KeepPolicy;
import cms.model.Model;
import cms.model.ModelManager;
import cms.model.person.Person;
import cms.storage.JsonAddressBookStorage;
import cms.storage.JsonUserPrefsStorage;
import cms.storage.StorageManager;
import cms.testutil.PersonBuilder;

public class ImportCommandTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void execute_validPath_success() throws Exception {
        Path path = temporaryFolder.resolve("data").resolve("import.json");
        ImportCommand importCommand = new ImportCommand(path);

        Model sourceModel = new ModelManager();
        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")));
        storage.saveAddressBook(sourceModel.getAddressBook(), path);

        Model model = new ModelManager();
        CommandResult result = importCommand.execute(model, storage);

        assertEquals(String.format(ImportCommand.MESSAGE_SUCCESS, path), result.getFeedbackToUser());
        assertEquals(sourceModel.getAddressBook(), model.getAddressBook());
    }

    @Test
    public void execute_withoutStorageContext_throwsCommandException() {
        Path path = temporaryFolder.resolve("data").resolve("import.json");
        ImportCommand importCommand = new ImportCommand(path);

        Model model = new ModelManager();
        assertThrows(cms.logic.commands.exceptions.CommandException.class, () -> importCommand.execute(model));
    }

    @Test
    public void execute_keepIncoming_conflictByUniqueField() throws Exception {
        Path path = temporaryFolder.resolve("data").resolve("importConflict.json");
        ImportCommand importCommand = new ImportCommand(path, KeepPolicy.INCOMING);

        Person existingPerson = new PersonBuilder()
                .withName(VALID_NAME_AMY)
                .withNusMatric(VALID_NUSMATRIC_AMY)
                .withSocUsername(VALID_SOCUSERNAME_AMY)
                .withGithubUsername(VALID_GITHUBUSERNAME_AMY)
                .withEmail(VALID_EMAIL_AMY)
                .withPhone(VALID_PHONE_AMY)
                .withTutorialGroup(VALID_TUTORIALGROUP_AMY)
                .build();
        Person incomingPerson = new PersonBuilder()
                .withName(VALID_NAME_BOB)
                .withNusMatric(VALID_NUSMATRIC_BOB)
                .withSocUsername(VALID_SOCUSERNAME_BOB)
                .withGithubUsername(VALID_GITHUBUSERNAME_BOB)
                .withEmail(VALID_EMAIL_AMY)
                .withPhone(VALID_PHONE_BOB)
                .withTutorialGroup(VALID_TUTORIALGROUP_BOB)
                .build();

        Model model = new ModelManager();
        model.addPerson(existingPerson);

        Model sourceModel = new ModelManager();
        sourceModel.addPerson(incomingPerson);

        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")));
        storage.saveAddressBook(sourceModel.getAddressBook(), path);

        CommandResult result = importCommand.execute(model, storage);

        assertEquals(String.format(ImportCommand.MESSAGE_KEEP_INCOMING_SUCCESS
                        + " (%d added, %d replaced, %d processed)", path, 0, 1, 1),
                result.getFeedbackToUser());
        assertEquals(1, model.getFilteredPersonList().size());
        assertTrue(model.getFilteredPersonList().contains(incomingPerson));
        assertFalse(model.getFilteredPersonList().contains(existingPerson));
    }

    @Test
    public void equals() {
        ImportCommand importFirstCommand = new ImportCommand(Path.of("data/first.json"),
                KeepPolicy.CURRENT);
        ImportCommand importFirstCommandCopy = new ImportCommand(Path.of("data/first.json"),
                KeepPolicy.CURRENT);
        ImportCommand importSecondCommand = new ImportCommand(Path.of("data/second.json"),
                KeepPolicy.CURRENT);
        ImportCommand importDifferentMode = new ImportCommand(Path.of("data/first.json"),
                KeepPolicy.INCOMING);

        assertTrue(importFirstCommand.equals(importFirstCommand));
        assertTrue(importFirstCommand.equals(importFirstCommandCopy));
        assertFalse(importFirstCommand.equals(importSecondCommand));
        assertFalse(importFirstCommand.equals(importDifferentMode));
        assertFalse(importFirstCommand.equals(1));
        assertFalse(importFirstCommand.equals(null));
    }
}
