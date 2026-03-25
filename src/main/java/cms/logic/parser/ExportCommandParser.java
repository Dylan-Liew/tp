package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import cms.logic.commands.ExportCommand;
import cms.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code ExportCommand} object.
 */
public class ExportCommandParser implements Parser<ExportCommand> {

    public static final String MESSAGE_INVALID_FILE_PATH = "File path is invalid: %1$s\n"
        + "Format: " + ExportCommand.MESSAGE_USAGE;
    public static final String MESSAGE_FILE_EXTENSION_REQUIRED = "File path must end with .json\n"
        + "Format: " + ExportCommand.MESSAGE_USAGE;

    @Override
    public ExportCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
        }

        String pathString = removeMatchingSurroundingQuotes(trimmedArgs);
        if (pathString == null) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
        }

        final Path exportFilePath;
        try {
            exportFilePath = Path.of(pathString);
        } catch (InvalidPathException ipe) {
            throw new ParseException(String.format(MESSAGE_INVALID_FILE_PATH, ipe.getReason()));
        }

        if (!exportFilePath.toString().toLowerCase().endsWith(".json")) {
            throw new ParseException(MESSAGE_FILE_EXTENSION_REQUIRED);
        }

        return new ExportCommand(exportFilePath);
    }

    private String removeMatchingSurroundingQuotes(String input) {
        boolean startsWithQuote = input.startsWith("\"");
        boolean endsWithQuote = input.endsWith("\"");

        if (startsWithQuote != endsWithQuote) {
            return null;
        }

        if (startsWithQuote) {
            return input.substring(1, input.length() - 1);
        }

        return input;
    }
}