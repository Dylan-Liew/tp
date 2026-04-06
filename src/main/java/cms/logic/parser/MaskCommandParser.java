package cms.logic.parser;

import cms.logic.commands.MaskCommand;

/**
 * Parses input arguments and creates a new MaskCommand object.
 */
public class MaskCommandParser implements Parser<MaskCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the MaskCommand
     * and returns a MaskCommand object for execution.
     * Any non-empty arguments will be captured and reported as ignored.
     */
    @Override
    public MaskCommand parse(String args) {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            return new MaskCommand();
        }
        return new MaskCommand(trimmedArgs);
    }
}
