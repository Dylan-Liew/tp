package cms.logic.parser;

import cms.logic.commands.UnmaskCommand;

/**
 * Parses input arguments and creates a new UnmaskCommand object.
 */
public class UnmaskCommandParser implements Parser<UnmaskCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the UnmaskCommand
     * and returns a UnmaskCommand object for execution.
     * Any non-empty arguments will be captured and reported as ignored.
     */
    @Override
    public UnmaskCommand parse(String args) {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            return new UnmaskCommand();
        }
        return new UnmaskCommand(trimmedArgs);
    }
}
