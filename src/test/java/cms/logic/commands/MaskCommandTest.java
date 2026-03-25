package cms.logic.commands;

import static cms.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

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
}
