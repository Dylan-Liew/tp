package cms.logic.parser;

import cms.logic.commands.ListCommand;

/**
 * Parses input arguments and creates a new ListCommand object.
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ListCommand
     * and returns a ListCommand object for execution.
     * Any non-empty arguments will be captured and reported as ignored.
     */
    @Override
    public ListCommand parse(String args) {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            return new ListCommand();
        }
        return new ListCommand(trimmedArgs);
    }
}
