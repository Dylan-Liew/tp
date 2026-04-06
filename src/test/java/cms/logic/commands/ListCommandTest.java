package cms.logic.commands;

import static cms.logic.commands.CommandTestUtil.assertCommandSuccess;
import static cms.logic.commands.CommandTestUtil.showPersonAtIndex;
import static cms.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static cms.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cms.logic.Messages;
import cms.model.Model;
import cms.model.ModelManager;
import cms.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listWithIgnoredArgs_showsWarning() {
        String ignoredArgs = "abc 123";
        String expectedMessage = ListCommand.MESSAGE_SUCCESS + "\n"
                + String.format(Messages.MESSAGE_IGNORED_PARAMETERS, ignoredArgs);
        assertCommandSuccess(new ListCommand(ignoredArgs), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_listWithEmptyIgnoredArgs_noWarning() {
        // Empty string should not trigger warning
        assertCommandSuccess(new ListCommand(""), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() {
        ListCommand listCommand = new ListCommand();
        ListCommand listCommandWithArgs = new ListCommand("abc");
        ListCommand listCommandWithSameArgs = new ListCommand("abc");
        ListCommand listCommandWithDifferentArgs = new ListCommand("xyz");

        // same object -> returns true
        assertEquals(listCommand, listCommand);

        // same values -> returns true
        assertEquals(listCommand, new ListCommand());
        assertEquals(listCommandWithArgs, listCommandWithSameArgs);

        // different types -> returns false
        assertNotEquals(listCommand, 1);

        // null -> returns false
        assertNotEquals(listCommand, null);

        // different ignoredArgs -> returns false
        assertNotEquals(listCommand, listCommandWithArgs);
        assertNotEquals(listCommandWithArgs, listCommandWithDifferentArgs);
    }

    @Test
    public void hashCode_sameIgnoredArgs_sameHashCode() {
        ListCommand listCommand1 = new ListCommand("abc");
        ListCommand listCommand2 = new ListCommand("abc");
        assertEquals(listCommand1.hashCode(), listCommand2.hashCode());
    }

    @Test
    public void hashCode_differentIgnoredArgs_differentHashCode() {
        ListCommand listCommand1 = new ListCommand("abc");
        ListCommand listCommand2 = new ListCommand("xyz");
        assertNotEquals(listCommand1.hashCode(), listCommand2.hashCode());
    }

    @Test
    public void hashCode_nullIgnoredArgs_consistent() {
        ListCommand listCommand1 = new ListCommand();
        ListCommand listCommand2 = new ListCommand();
        assertEquals(listCommand1.hashCode(), listCommand2.hashCode());
    }
}
