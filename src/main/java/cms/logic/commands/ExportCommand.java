package cms.logic.commands;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;

import cms.model.Model;

/**
 * Exports the current in-memory address book data to a user-specified JSON file path.
 */
public class ExportCommand extends Command {

    public static final String COMMAND_WORD = "export";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Exports current data to the given JSON file path.\n"
            + "Parameters: FILE_PATH\n"
            + "Example: " + COMMAND_WORD + " \"C:\\Users\\Josh\\Documents\\backup.json\"";

    public static final String MESSAGE_SUCCESS = "Exported current data to: %1$s";

    private final Path exportFilePath;

    public ExportCommand(Path exportFilePath) {
        this.exportFilePath = requireNonNull(exportFilePath);
    }

    public Path getExportFilePath() {
        return exportFilePath;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return new CommandResult(String.format(MESSAGE_SUCCESS, exportFilePath));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ExportCommand)) {
            return false;
        }

        ExportCommand otherExportCommand = (ExportCommand) other;
        return exportFilePath.equals(otherExportCommand.exportFilePath);
    }

    @Override
    public int hashCode() {
        return exportFilePath.hashCode();
    }
}