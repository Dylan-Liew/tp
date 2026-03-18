package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.parser.CliSyntax.PREFIX_ALL;
import static cms.logic.parser.CliSyntax.PREFIX_NAME;
import static cms.logic.parser.CliSyntax.PREFIX_NUSID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cms.logic.commands.FindCommand;
import cms.logic.parser.exceptions.ParseException;
import cms.model.person.AllFieldsContainsKeywordsPredicate;
import cms.model.person.NameContainsKeywordsPredicate;
import cms.model.person.NusIdContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        // Tokenize for required prefixes
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ALL, PREFIX_NAME, PREFIX_NUSID);

        boolean hasAll = argMultimap.getValue(PREFIX_ALL).isPresent();
        boolean hasName = argMultimap.getValue(PREFIX_NAME).isPresent();
        boolean hasNusId = argMultimap.getValue(PREFIX_NUSID).isPresent();

        // require at least one prefix to be present and no preamble
        if (!(hasAll || hasName || hasNusId) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        // Build predicates for each prefix present and combine with OR
        List<String> allKeywords = new ArrayList<>();
        if (hasAll) {
            // getAllValues returns list of values for the prefix; split each by whitespace
            allKeywords = argMultimap.getAllValues(PREFIX_ALL).stream()
                    .flatMap(s -> Arrays.stream(s.trim().split("\\s+")))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        List<String> nameKeywords = new ArrayList<>();
        if (hasName) {
            nameKeywords = argMultimap.getAllValues(PREFIX_NAME).stream()
                    .flatMap(s -> Arrays.stream(s.trim().split("\\s+")))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        List<String> idKeywords = new ArrayList<>();
        if (hasNusId) {
            idKeywords = argMultimap.getAllValues(PREFIX_NUSID).stream()
                    .flatMap(s -> Arrays.stream(s.trim().split("\\s+")))
                    .filter(s -> !s.isEmpty())
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());
        }

        AllFieldsContainsKeywordsPredicate allPredicate = new AllFieldsContainsKeywordsPredicate(allKeywords);
        NameContainsKeywordsPredicate namePredicate = new NameContainsKeywordsPredicate(nameKeywords);
        NusIdContainsKeywordsPredicate idPredicate = new NusIdContainsKeywordsPredicate(idKeywords);

        // Combined predicate: matches if any prefix-predicate matches
        return new FindCommand(new cms.model.person.CombinedFindPredicate(allPredicate, namePredicate, idPredicate));
    }

}
