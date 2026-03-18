package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.parser.CliSyntax.PREFIX_NUSID;

import java.util.List;

import cms.commons.core.index.Index;
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
            String normalizedArgs = args.startsWith(" ") ? args : " " + args;
            ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_NUSID);
            List<String> nusIdValues = argMultimap.getAllValues(PREFIX_NUSID);

            if (!nusIdValues.isEmpty()) {
                if (!argMultimap.getPreamble().isEmpty()) {
                    throw new ParseException(
                            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
                }
                List<NusId> nusIds = ParserUtil.parseNusIds(nusIdValues);
                return DeleteCommand.byNusIds(nusIds);
            }

            List<Index> indexes = ParserUtil.parseIndexes(args);
            return new DeleteCommand(indexes);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE), pe);
        }
    }

}
