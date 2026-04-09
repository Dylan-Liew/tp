package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.parser.CliSyntax.PREFIX_ALL;
import static cms.logic.parser.CliSyntax.PREFIX_MATRIC;
import static cms.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cms.logic.commands.FindCommand;
import cms.logic.parser.exceptions.ParseException;
import cms.model.person.AllFieldsContainsKeywordsPredicate;
import cms.model.person.NameContainsKeywordsPredicate;
import cms.model.person.NusMatricContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {
    public static final String MESSAGE_PREFIX_REQUIRED =
            "A supported prefix is required. Use at least one of a/, n/, or m/.";
    public static final String MESSAGE_EMPTY_KEYWORDS =
            "Find keywords cannot be blank. Provide at least one non-whitespace keyword after each prefix used.";

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(MESSAGE_PREFIX_REQUIRED);
        }

        // Tokenize for required prefixes
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ALL, PREFIX_NAME, PREFIX_MATRIC);

        boolean hasAll = argMultimap.getValue(PREFIX_ALL).isPresent();
        boolean hasName = argMultimap.getValue(PREFIX_NAME).isPresent();
        boolean hasNusMatric = argMultimap.getValue(PREFIX_MATRIC).isPresent();

        // require at least one prefix to be present and no preamble
        if (!(hasAll || hasName || hasNusMatric)) {
            throw new ParseException(MESSAGE_PREFIX_REQUIRED);
        }

        if (!argMultimap.getPreamble().isEmpty()) {
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
        if (hasNusMatric) {
            idKeywords = argMultimap.getAllValues(PREFIX_MATRIC).stream()
                    .flatMap(s -> Arrays.stream(s.trim().split("\\s+")))
                    .filter(s -> !s.isEmpty())
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());
        }

        if ((hasAll && allKeywords.isEmpty())
                || (hasName && nameKeywords.isEmpty())
                || (hasNusMatric && idKeywords.isEmpty())) {
            throw new ParseException(MESSAGE_EMPTY_KEYWORDS);
        }

        AllFieldsContainsKeywordsPredicate allPredicate = new AllFieldsContainsKeywordsPredicate(allKeywords);
        NameContainsKeywordsPredicate namePredicate = new NameContainsKeywordsPredicate(nameKeywords);
        NusMatricContainsKeywordsPredicate idPredicate = new NusMatricContainsKeywordsPredicate(idKeywords);

        // Combined predicate: matches if any prefix-predicate matches
        return new FindCommand(new cms.model.person.CombinedFindPredicate(allPredicate, namePredicate, idPredicate));
    }

}
