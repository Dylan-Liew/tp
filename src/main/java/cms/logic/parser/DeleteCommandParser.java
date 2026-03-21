package cms.logic.parser;

import java.util.List;
import java.util.stream.Collectors;

import cms.commons.core.index.Index;
import cms.logic.Messages;
import cms.logic.commands.DeleteCommand;
import cms.logic.parser.exceptions.ParseException;
import cms.model.person.NusId;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        try {
            String trimmedArgs = args.trim();
            if (trimmedArgs.startsWith("id/")) {
                List<NusId> nusIds = ParserUtil.parseNusIds(parseNusIdTokens(trimmedArgs));
                return DeleteCommand.byNusIds(nusIds);
            }

            List<Index> indexes = ParserUtil.parseIndexes(trimmedArgs);
            return new DeleteCommand(indexes);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE), pe);
        }
    }

    private List<String> parseNusIdTokens(String trimmedArgs) throws ParseException {
        String[] tokens = trimmedArgs.split("\\s+");
        if (tokens.length == 0) {
            throw new ParseException(NusId.MESSAGE_CONSTRAINTS);
        }

        return List.of(tokens).stream()
                .map(this::stripNusIdPrefix)
                .collect(Collectors.toList());
    }

    private String stripNusIdPrefix(String token) {
        return token.startsWith("id/") ? token.substring(3) : token;
    }

}
