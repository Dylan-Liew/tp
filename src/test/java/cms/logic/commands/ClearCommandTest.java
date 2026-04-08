package cms.logic.commands;

import static cms.logic.commands.CommandTestUtil.assertCommandSuccess;
import static cms.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import cms.model.AddressBook;
import cms.model.Model;
import cms.model.ModelManager;
import cms.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_withoutConfirmation_showsReminder() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(false), model,
                ClearCommand.MESSAGE_CONFIRMATION_REQUIRED, expectedModel);
    }

    @Test
    public void execute_withConfirmationOnNonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ClearCommand(true), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_withConfirmationOnEmptyAddressBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(true), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() {
        ClearCommand clearCommand = new ClearCommand(false);
        ClearCommand confirmedClearCommand = new ClearCommand(true);
        ClearCommand anotherConfirmedClearCommand = new ClearCommand(true);

        // same object -> returns true
        assertEquals(clearCommand, clearCommand);

        // same values -> returns true
        assertEquals(clearCommand, new ClearCommand(false));
        assertEquals(confirmedClearCommand, anotherConfirmedClearCommand);

        // different types -> returns false
        assertNotEquals(clearCommand, 1);

        // null -> returns false
        assertNotEquals(clearCommand, null);

        // different confirmation state -> returns false
        assertNotEquals(clearCommand, confirmedClearCommand);
    }

    @Test
    public void hashCode_sameConfirmationState_sameHashCode() {
        ClearCommand clearCommand1 = new ClearCommand(true);
        ClearCommand clearCommand2 = new ClearCommand(true);
        assertEquals(clearCommand1.hashCode(), clearCommand2.hashCode());
    }

    @Test
    public void hashCode_differentConfirmationState_differentHashCode() {
        ClearCommand clearCommand1 = new ClearCommand(true);
        ClearCommand clearCommand2 = new ClearCommand(false);
        assertNotEquals(clearCommand1.hashCode(), clearCommand2.hashCode());
    }

    @Test
    public void hashCode_unconfirmedClear_consistent() {
        ClearCommand clearCommand1 = new ClearCommand(false);
        ClearCommand clearCommand2 = new ClearCommand(false);
        assertEquals(clearCommand1.hashCode(), clearCommand2.hashCode());
    }
}
