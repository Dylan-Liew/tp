package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.parser.CommandParserTestUtil.assertParseFailure;
import static cms.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import cms.logic.commands.FindCommand;
import cms.model.person.CombinedFindPredicate;
import cms.model.person.AllFieldsContainsKeywordsPredicate;
import cms.model.person.NameContainsKeywordsPredicate;
import cms.model.person.NusIdContainsKeywordsPredicate;

public class FindCommandParserTest {

    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_noPrefix_throwsParseException() {
        assertParseFailure(parser, "john doe", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_allPrefix_singleAndMultipleValues_returnsFindCommand() {
        FindCommand expectedSingle = new FindCommand(
                new CombinedFindPredicate(new AllFieldsContainsKeywordsPredicate(Collections.singletonList("john")),
                        new NameContainsKeywordsPredicate(Collections.emptyList()),
                        new NusIdContainsKeywordsPredicate(Collections.emptyList())));
        assertParseSuccess(parser, " a/john", expectedSingle);

        FindCommand expectedMulti = new FindCommand(
                new CombinedFindPredicate(new AllFieldsContainsKeywordsPredicate(Arrays.asList("john", "A0234504F")),
                        new NameContainsKeywordsPredicate(Collections.emptyList()),
                        new NusIdContainsKeywordsPredicate(Collections.emptyList())));
        assertParseSuccess(parser, " a/john A0234504F", expectedMulti);
    }

    @Test
    public void parse_namePrefix_returnsFindCommand() {
        FindCommand expected = new FindCommand(
                new CombinedFindPredicate(new AllFieldsContainsKeywordsPredicate(Collections.emptyList()),
                        new NameContainsKeywordsPredicate(Arrays.asList("John", "David")),
                        new NusIdContainsKeywordsPredicate(Collections.emptyList())));
        assertParseSuccess(parser, " n/John David", expected);
    }

    @Test
    public void parse_idPrefix_multipleIds_returnsFindCommand() {
        FindCommand expected = new FindCommand(
                new CombinedFindPredicate(new AllFieldsContainsKeywordsPredicate(Collections.emptyList()),
                        new NameContainsKeywordsPredicate(Collections.emptyList()),
                        new NusIdContainsKeywordsPredicate(Arrays.asList("A0234502D", "A0234505G"))));
        assertParseSuccess(parser, " id/A0234502D A0234505G", expected);
    }

    @Test
    public void parse_multiplePrefixes_combined_returnsFindCommand() {
        // a/john david n/tan lim id/a12345678b a0211111c
        FindCommand expected = new FindCommand(
                new cms.model.person.CombinedFindPredicate(
                        new AllFieldsContainsKeywordsPredicate(Arrays.asList("john", "david")),
                        new NameContainsKeywordsPredicate(Arrays.asList("tan", "lim")),
                        new NusIdContainsKeywordsPredicate(Arrays.asList("A12345678B", "A0211111C"))));
        assertParseSuccess(parser, " a/john david n/tan lim id/a12345678b a0211111c", expected);
    }

}
