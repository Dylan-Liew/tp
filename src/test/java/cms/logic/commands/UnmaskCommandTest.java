package cms.logic.commands;

import static cms.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import cms.model.Model;
import cms.model.ModelManager;
import cms.model.UserPrefs;
import cms.testutil.TypicalPersons;

public class UnmaskCommandTest {

    @Test
    public void execute_setsMaskingDisabled() {
        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
        model.setMasked(true);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setMasked(false);

        assertCommandSuccess(new UnmaskCommand(), model, UnmaskCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
