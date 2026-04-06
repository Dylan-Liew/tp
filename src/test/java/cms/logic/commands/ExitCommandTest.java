package cms.logic.commands;

import static cms.logic.commands.CommandTestUtil.assertCommandSuccess;
import static cms.logic.commands.ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import cms.logic.Messages;
import cms.model.Model;
import cms.model.ModelManager;

public class ExitCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_exit_success() {
        CommandResult expectedCommandResult = new CommandResult(MESSAGE_EXIT_ACKNOWLEDGEMENT, false, true);
        assertCommandSuccess(new ExitCommand(), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_exitWithIgnoredArgs_showsWarning() {
        String ignoredArgs = "now";
        String expectedMessage = MESSAGE_EXIT_ACKNOWLEDGEMENT + "\n"
                + String.format(Messages.MESSAGE_IGNORED_PARAMETERS, ignoredArgs);
        CommandResult expectedCommandResult = new CommandResult(expectedMessage, false, true);
        assertCommandSuccess(new ExitCommand(ignoredArgs), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_exitWithEmptyIgnoredArgs_noWarning() {
        // Empty string should not trigger warning
        CommandResult expectedCommandResult = new CommandResult(MESSAGE_EXIT_ACKNOWLEDGEMENT, false, true);
        assertCommandSuccess(new ExitCommand(""), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void equals() {
        ExitCommand exitCommand = new ExitCommand();
        ExitCommand exitCommandWithArgs = new ExitCommand("now");
        ExitCommand exitCommandWithSameArgs = new ExitCommand("now");
        ExitCommand exitCommandWithDifferentArgs = new ExitCommand("later");

        // same object -> returns true
        assertEquals(exitCommand, exitCommand);

        // same values -> returns true
        assertEquals(exitCommand, new ExitCommand());
        assertEquals(exitCommandWithArgs, exitCommandWithSameArgs);

        // different types -> returns false
        assertNotEquals(exitCommand, 1);

        // null -> returns false
        assertNotEquals(exitCommand, null);

        // different ignoredArgs -> returns false
        assertNotEquals(exitCommand, exitCommandWithArgs);
        assertNotEquals(exitCommandWithArgs, exitCommandWithDifferentArgs);
    }

    @Test
    public void hashCode_sameIgnoredArgs_sameHashCode() {
        ExitCommand exitCommand1 = new ExitCommand("now");
        ExitCommand exitCommand2 = new ExitCommand("now");
        assertEquals(exitCommand1.hashCode(), exitCommand2.hashCode());
    }

    @Test
    public void hashCode_differentIgnoredArgs_differentHashCode() {
        ExitCommand exitCommand1 = new ExitCommand("now");
        ExitCommand exitCommand2 = new ExitCommand("later");
        assertNotEquals(exitCommand1.hashCode(), exitCommand2.hashCode());
    }

    @Test
    public void hashCode_nullIgnoredArgs_consistent() {
        ExitCommand exitCommand1 = new ExitCommand();
        ExitCommand exitCommand2 = new ExitCommand();
        assertEquals(exitCommand1.hashCode(), exitCommand2.hashCode());
    }
}
