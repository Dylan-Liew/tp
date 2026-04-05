package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.parser.CliSyntax.PREFIX_MATRIC;
import static cms.logic.parser.CliSyntax.PREFIX_NAME;
import static cms.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cms.commons.core.index.Index;
import cms.logic.commands.TagCommand;
import cms.logic.commands.TagCommand.Action;
import cms.logic.parser.exceptions.ParseException;
import cms.model.person.NusMatric;
import cms.model.tag.Tag;

/**
 * Parses input arguments and creates a new TagCommand object.
 */
public class TagCommandParser implements Parser<TagCommand> {

    @Override
    public TagCommand parse(String args) throws ParseException {
        try {
            String trimmedArgs = args.trim();
            String[] splitArgs = trimmedArgs.split("\\s+", 2);
            if (splitArgs.length < 2) {
                throw new ParseException(TagCommand.MESSAGE_USAGE);
            }

            Action action = parseAction(splitArgs[0]);
            String remainingArgs = splitArgs[1];

            ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                    " " + remainingArgs, PREFIX_NAME, PREFIX_MATRIC, PREFIX_TAG);

            if (!argMultimap.getPreamble().isEmpty()) {
                throw new ParseException(TagCommand.MESSAGE_USAGE);
            }

            boolean hasIndexTargets = !argMultimap.getAllValues(PREFIX_NAME).isEmpty();
            boolean hasNusMatricTargets = !argMultimap.getAllValues(PREFIX_MATRIC).isEmpty();
            if (hasIndexTargets == hasNusMatricTargets) {
                throw new ParseException(TagCommand.MESSAGE_USAGE);
            }

            List<Tag> tags = parseTags(argMultimap.getAllValues(PREFIX_TAG));
            if (tags.isEmpty()) {
                throw new ParseException(TagCommand.MESSAGE_USAGE);
            }

            if (hasIndexTargets) {
                List<Index> indexes = ParserUtil.parseIndexes(String.join(" ", argMultimap.getAllValues(PREFIX_NAME)));
                return new TagCommand(action, indexes, tags);
            }

            List<NusMatric> nusMatrics = ParserUtil.parseNusMatrics(
                    splitValues(argMultimap.getAllValues(PREFIX_MATRIC)));
            return TagCommand.byNusMatrics(action, nusMatrics, tags);
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE), pe);
        }
    }

    private Action parseAction(String actionText) throws ParseException {
        if (TagCommand.ACTION_ADD.equals(actionText)) {
            return Action.ADD;
        }

        if (TagCommand.ACTION_DELETE.equals(actionText)) {
            return Action.DELETE;
        }

        throw new ParseException(TagCommand.MESSAGE_USAGE);
    }

    private List<Tag> parseTags(List<String> tagValues) throws ParseException {
        Set<Tag> uniqueTags = new LinkedHashSet<>(ParserUtil.parseTags(splitValues(tagValues)));
        return new ArrayList<>(uniqueTags);
    }

    private List<String> splitValues(List<String> rawValues) {
        List<String> values = new ArrayList<>();
        for (String rawValue : rawValues) {
            String trimmedValue = rawValue.trim();
            if (trimmedValue.isEmpty()) {
                continue;
            }

            for (String token : trimmedValue.split("\\s+")) {
                values.add(token);
            }
        }
        return values;
    }
}
