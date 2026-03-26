package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cms.logic.commands.ImportCommand;
import cms.logic.commands.ImportCommand.KeepPolicy;
import cms.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ImportCommand object.
 */
public class ImportCommandParser implements Parser<ImportCommand> {

    private static final Pattern PATH_AND_OPTION_PATTERN = Pattern.compile(
            "^\\s*(\"(?:[^\"\\\\]|\\\\.)*\"|\\S+)(?:\\s+(\\S+))?\\s*$");

    public static final String MESSAGE_INVALID_FILE_PATH = "File path is invalid: %1$s\n"
        + "Format: " + ImportCommand.MESSAGE_USAGE;
    public static final String MESSAGE_FILE_EXTENSION_REQUIRED = "File path must end with .json\n"
        + "Format: " + ImportCommand.MESSAGE_USAGE;
    public static final String MESSAGE_INVALID_KEEP = "keep/ must be either 'current' or 'incoming'.\n"
            + "Format: " + ImportCommand.MESSAGE_USAGE;

    @Override
    public ImportCommand parse(String args) throws ParseException {
        Matcher matcher = PATH_AND_OPTION_PATTERN.matcher(args);
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE));
        }

        String rawPathToken = matcher.group(1);
        String rawKeepToken = matcher.group(2);

        String pathString = removeMatchingSurroundingQuotes(rawPathToken);
        if (pathString == null) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE));
        }

        final Path importFilePath;
        try {
            importFilePath = Path.of(pathString);
        } catch (InvalidPathException ipe) {
            throw new ParseException(String.format(MESSAGE_INVALID_FILE_PATH, ipe.getReason()));
        }

        if (!importFilePath.toString().toLowerCase().endsWith(".json")) {
            throw new ParseException(MESSAGE_FILE_EXTENSION_REQUIRED);
        }

        KeepPolicy keepPolicy = parseKeepPolicy(rawKeepToken);
        return new ImportCommand(importFilePath, keepPolicy);
    }

    private KeepPolicy parseKeepPolicy(String keepToken) throws ParseException {
        if (keepToken == null) {
            return null;
        }

        String normalizedKeepToken = keepToken.trim().toLowerCase();
        if (normalizedKeepToken.equals("keep/current")) {
            return KeepPolicy.CURRENT;
        }
        if (normalizedKeepToken.equals("keep/incoming")) {
            return KeepPolicy.INCOMING;
        }
        throw new ParseException(MESSAGE_INVALID_KEEP);
    }

    private String removeMatchingSurroundingQuotes(String input) {
        boolean startsWithQuote = input.startsWith("\"");
        boolean endsWithQuote = input.endsWith("\"");

        if (startsWithQuote && endsWithQuote && input.length() > 1) {
            return input.substring(1, input.length() - 1);
        }

        if (startsWithQuote != endsWithQuote) {
            return null;
        }

        return input;
    }
}

