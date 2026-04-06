package cms.logic.parser;

import cms.logic.commands.ExitCommand;

/**
 * Parses input arguments and creates a new ExitCommand object.
 */
public class ExitCommandParser implements Parser<ExitCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ExitCommand
     * and returns an ExitCommand object for execution.
     * Any non-empty arguments will be captured and reported as ignored.
     */
    @Override
    public ExitCommand parse(String args) {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            return new ExitCommand();
        }
        return new ExitCommand(trimmedArgs);
    }
}
