package cms.logic.commands;

import static cms.logic.commands.CommandTestUtil.assertCommandSuccess;
import static cms.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import cms.logic.Messages;
import cms.model.AddressBook;
import cms.model.Model;
import cms.model.ModelManager;
import cms.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_clearWithIgnoredArgs_showsWarning() {
        String ignoredArgs = "tag";
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();
        String expectedMessage = ClearCommand.MESSAGE_SUCCESS + "\n"
                + String.format(Messages.MESSAGE_IGNORED_PARAMETERS, ignoredArgs);

        assertCommandSuccess(new ClearCommand(ignoredArgs), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clearWithEmptyIgnoredArgs_noWarning() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();
        // Empty string should not trigger warning
        assertCommandSuccess(new ClearCommand(""), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() {
        ClearCommand clearCommand = new ClearCommand();
        ClearCommand clearCommandWithArgs = new ClearCommand("tag");
        ClearCommand clearCommandWithSameArgs = new ClearCommand("tag");
        ClearCommand clearCommandWithDifferentArgs = new ClearCommand("all");

        // same object -> returns true
        assertEquals(clearCommand, clearCommand);

        // same values -> returns true
        assertEquals(clearCommand, new ClearCommand());
        assertEquals(clearCommandWithArgs, clearCommandWithSameArgs);

        // different types -> returns false
        assertNotEquals(clearCommand, 1);

        // null -> returns false
        assertNotEquals(clearCommand, null);

        // different ignoredArgs -> returns false
        assertNotEquals(clearCommand, clearCommandWithArgs);
        assertNotEquals(clearCommandWithArgs, clearCommandWithDifferentArgs);
    }

    @Test
    public void hashCode_sameIgnoredArgs_sameHashCode() {
        ClearCommand clearCommand1 = new ClearCommand("tag");
        ClearCommand clearCommand2 = new ClearCommand("tag");
        assertEquals(clearCommand1.hashCode(), clearCommand2.hashCode());
    }

    @Test
    public void hashCode_differentIgnoredArgs_differentHashCode() {
        ClearCommand clearCommand1 = new ClearCommand("tag");
        ClearCommand clearCommand2 = new ClearCommand("all");
        assertNotEquals(clearCommand1.hashCode(), clearCommand2.hashCode());
    }

    @Test
    public void hashCode_nullIgnoredArgs_consistent() {
        ClearCommand clearCommand1 = new ClearCommand();
        ClearCommand clearCommand2 = new ClearCommand();
        assertEquals(clearCommand1.hashCode(), clearCommand2.hashCode());
    }
}
