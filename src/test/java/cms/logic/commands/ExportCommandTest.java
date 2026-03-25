package cms.logic.commands;

import static cms.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import cms.model.Model;
import cms.model.ModelManager;
import cms.model.UserPrefs;

public class ExportCommandTest {

    @Test
    public void execute_validPath_success() {
        Path path = Path.of("data/export.json");
        ExportCommand exportCommand = new ExportCommand(path);

        Model model = new ModelManager();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(exportCommand, model,
                String.format(ExportCommand.MESSAGE_SUCCESS, path), expectedModel);
    }
}
