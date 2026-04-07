package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;

import cms.logic.commands.SortCommand;
import cms.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new SortCommand object.
 */
public class SortCommandParser implements Parser<SortCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the SortCommand
     * and returns a SortCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public SortCommand parse(String args) throws ParseException {
        String normalizedArgs = args.trim().toLowerCase(Locale.ROOT);
        if (SortCommand.SORT_BY_TUTORIAL_GROUP.equals(normalizedArgs)
                || SortCommand.SORT_BY_NAME.equals(normalizedArgs)) {
            return new SortCommand(normalizedArgs);
        }

        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }
}
