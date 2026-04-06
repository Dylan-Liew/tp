package cms.logic.commands;

import static cms.logic.commands.CommandTestUtil.assertCommandSuccess;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import cms.logic.Messages;
import cms.model.Model;
import cms.model.ModelManager;
import cms.model.UserPrefs;
import cms.testutil.TypicalPersons;

public class MaskCommandTest {

    @Test
    public void execute_setsMaskingEnabled() {
        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setMasked(true);

        assertCommandSuccess(new MaskCommand(), model, MaskCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_maskWithIgnoredArgs_showsWarning() {
        String ignoredArgs = "username";
        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setMasked(true);
        String expectedMessage = MaskCommand.MESSAGE_SUCCESS + "\n"
                + String.format(Messages.MESSAGE_IGNORED_PARAMETERS, ignoredArgs);

        assertCommandSuccess(new MaskCommand(ignoredArgs), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_maskWithEmptyIgnoredArgs_noWarning() {
        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setMasked(true);
        // Empty string should not trigger warning
        assertCommandSuccess(new MaskCommand(""), model, MaskCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() {
        MaskCommand maskCommand = new MaskCommand();
        MaskCommand maskCommandWithArgs = new MaskCommand("username");
        MaskCommand maskCommandWithSameArgs = new MaskCommand("username");
        MaskCommand maskCommandWithDifferentArgs = new MaskCommand("all");

        // same object -> returns true
        assertEquals(maskCommand, maskCommand);

        // same values -> returns true
        assertEquals(maskCommand, new MaskCommand());
        assertEquals(maskCommandWithArgs, maskCommandWithSameArgs);

        // different types -> returns false
        assertNotEquals(maskCommand, 1);

        // null -> returns false
        assertNotEquals(maskCommand, null);

        // different ignoredArgs -> returns false
        assertNotEquals(maskCommand, maskCommandWithArgs);
        assertNotEquals(maskCommandWithArgs, maskCommandWithDifferentArgs);
    }

    @Test
    public void hashCode_sameIgnoredArgs_sameHashCode() {
        MaskCommand maskCommand1 = new MaskCommand("username");
        MaskCommand maskCommand2 = new MaskCommand("username");
        assertEquals(maskCommand1.hashCode(), maskCommand2.hashCode());
    }

    @Test
    public void hashCode_differentIgnoredArgs_differentHashCode() {
        MaskCommand maskCommand1 = new MaskCommand("username");
        MaskCommand maskCommand2 = new MaskCommand("all");
        assertNotEquals(maskCommand1.hashCode(), maskCommand2.hashCode());
    }

    @Test
    public void hashCode_nullIgnoredArgs_consistent() {
        MaskCommand maskCommand1 = new MaskCommand();
        MaskCommand maskCommand2 = new MaskCommand();
        assertEquals(maskCommand1.hashCode(), maskCommand2.hashCode());
    }
}
