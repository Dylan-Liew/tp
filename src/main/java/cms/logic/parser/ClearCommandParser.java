package cms.logic.parser;

import cms.logic.commands.ClearCommand;

/**
 * Parses input arguments and creates a new ClearCommand object.
 */
public class ClearCommandParser implements Parser<ClearCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ClearCommand
     * and returns a ClearCommand object for execution.
     * Any non-empty arguments will be captured and reported as ignored.
     */
    @Override
    public ClearCommand parse(String args) {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            return new ClearCommand();
        }
        return new ClearCommand(trimmedArgs);
    }
}
