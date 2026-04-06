package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.parser.CliSyntax.PREFIX_TAG;
import static cms.logic.parser.CliSyntax.PREFIX_TUTORIALGROUP;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cms.logic.commands.FilterCommand;
import cms.logic.parser.exceptions.ParseException;
import cms.model.person.TagTutorialGroupMatchesPredicate;
import cms.model.person.TutorialGroup;
import cms.model.tag.Tag;

/**
 * Parses input arguments and creates a new {@code FilterCommand} object.
 */
public class FilterCommandParser implements Parser<FilterCommand> {
    static final String MESSAGE_FILTER_TUTORIAL_GROUP_CONSTRAINTS =
            "Tutorial group filter should be a number between 1 and 99 (leading zeros are allowed).";

    @Override
    public FilterCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TAG, PREFIX_TUTORIALGROUP);
        boolean hasTag = argMultimap.getValue(PREFIX_TAG).isPresent();
        boolean hasTutorialGroup = argMultimap.getValue(PREFIX_TUTORIALGROUP).isPresent();

        if (!(hasTag || hasTutorialGroup) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TUTORIALGROUP);

        Set<Tag> tagKeywords = parseTags(argMultimap.getAllValues(PREFIX_TAG));
        Set<TutorialGroup> tutorialGroupKeywords = parseTutorialGroups(argMultimap.getAllValues(PREFIX_TUTORIALGROUP));

        return new FilterCommand(new TagTutorialGroupMatchesPredicate(tagKeywords, tutorialGroupKeywords));
    }

    private Set<Tag> parseTags(List<String> rawTags) throws ParseException {
        Set<Tag> parsedTags = new LinkedHashSet<>();
        for (String rawTag : rawTags) {
            parsedTags.add(ParserUtil.parseTag(rawTag));
        }
        return parsedTags;
    }

    private Set<TutorialGroup> parseTutorialGroups(List<String> rawTutorialGroups) throws ParseException {
        Set<TutorialGroup> parsedTutorialGroups = new LinkedHashSet<>();
        for (String rawTutorialGroup : rawTutorialGroups) {
            parsedTutorialGroups.add(parseFilterTutorialGroup(rawTutorialGroup));
        }
        return parsedTutorialGroups;
    }

    /**
     * Parses tutorial group input for the filter command.
     * Filter users enter groups as 1-99, with leading zeros allowed.
     * Canonicalisation is handled by {@code TutorialGroup}.
     */
    private TutorialGroup parseFilterTutorialGroup(String rawTutorialGroup) throws ParseException {
        try {
            return ParserUtil.parseTutorialGroup(rawTutorialGroup);
        } catch (ParseException e) {
            throw new ParseException(MESSAGE_FILTER_TUTORIAL_GROUP_CONSTRAINTS);
        }
    }
}
