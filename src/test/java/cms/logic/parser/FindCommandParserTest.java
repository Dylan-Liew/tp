package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.parser.CommandParserTestUtil.assertParseFailure;
import static cms.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import cms.logic.commands.FindCommand;
import cms.model.person.AllFieldsContainsKeywordsPredicate;
import cms.model.person.CombinedFindPredicate;
import cms.model.person.NameContainsKeywordsPredicate;
import cms.model.person.NusMatricContainsKeywordsPredicate;

public class FindCommandParserTest {

    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_noPrefix_throwsParseException() {
        assertParseFailure(parser, "john doe", FindCommandParser.MESSAGE_PREFIX_REQUIRED);
    }

    @Test
    public void parse_emptyInput_throwsPrefixRequiredParseException() {
        assertParseFailure(parser, "   ", FindCommandParser.MESSAGE_PREFIX_REQUIRED);
    }

    @Test
    public void parse_allPrefix_returnsFindCommand() {
        FindCommand expectedSingle = new FindCommand(
                new CombinedFindPredicate(
                        new AllFieldsContainsKeywordsPredicate(
                                Collections.singletonList("john")),
                        new NameContainsKeywordsPredicate(Collections.emptyList()),
                        new NusMatricContainsKeywordsPredicate(Collections.emptyList())));
        assertParseSuccess(parser, " a/john", expectedSingle);

        FindCommand expectedMulti = new FindCommand(
                new CombinedFindPredicate(
                        new AllFieldsContainsKeywordsPredicate(
                                Arrays.asList("john", "A0234504N")),
                        new NameContainsKeywordsPredicate(Collections.emptyList()),
                        new NusMatricContainsKeywordsPredicate(Collections.emptyList())));
        assertParseSuccess(parser, " a/john A0234504N", expectedMulti);
    }

    @Test
    public void parse_namePrefix_returnsFindCommand() {
        FindCommand expected = new FindCommand(
                new CombinedFindPredicate(
                        new AllFieldsContainsKeywordsPredicate(Collections.emptyList()),
                        new NameContainsKeywordsPredicate(
                                Arrays.asList("John", "David")),
                        new NusMatricContainsKeywordsPredicate(Collections.emptyList())));
        assertParseSuccess(parser, " n/John David", expected);
    }

    @Test
    public void parse_idPrefix_returnsFindCommand() {
        FindCommand expected = new FindCommand(
                new CombinedFindPredicate(
                        new AllFieldsContainsKeywordsPredicate(Collections.emptyList()),
                        new NameContainsKeywordsPredicate(Collections.emptyList()),
                        new NusMatricContainsKeywordsPredicate(
                                Arrays.asList("A0234502U", "A0234505M"))));
        assertParseSuccess(parser, " m/A0234502U A0234505M", expected);
    }

    @Test
    public void parse_multiplePrefixes_returnsFindCommand() {
        FindCommand expected = new FindCommand(
                new CombinedFindPredicate(
                        new AllFieldsContainsKeywordsPredicate(
                                Arrays.asList("john", "david")),
                        new NameContainsKeywordsPredicate(
                                Arrays.asList("tan", "lim")),
                        new NusMatricContainsKeywordsPredicate(
                                Arrays.asList("A1234567X", "A0211111L"))));
        assertParseSuccess(parser,
                " a/john david n/tan lim m/a1234567x a0211111l",
                expected);
    }

    @Test
    public void parse_withPreamble_throwsParseException() {
        // any preamble text should cause failure
        assertParseFailure(parser, "preamble a/john",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_allPrefix_multipleWhitespaceSplits() {
        // ensure multiple whitespace is handled when splitting keywords
        FindCommand expected = new FindCommand(
                new CombinedFindPredicate(
                        new AllFieldsContainsKeywordsPredicate(
                                Arrays.asList("john", "david")),
                        new NameContainsKeywordsPredicate(Collections.emptyList()),
                        new NusMatricContainsKeywordsPredicate(Collections.emptyList())));
        assertParseSuccess(parser, " a/  john   david  ", expected);
    }

    @Test
    public void parse_namePrefix_multipleWhitespaceSplits() {
        // ensure name splitting filters out empty tokens
        FindCommand expected = new FindCommand(
                new CombinedFindPredicate(
                        new AllFieldsContainsKeywordsPredicate(Collections.emptyList()),
                        new NameContainsKeywordsPredicate(Arrays.asList("john", "david")),
                        new NusMatricContainsKeywordsPredicate(Collections.emptyList())));
        assertParseSuccess(parser, " n/  john   david  ", expected);
    }

    @Test
    public void parse_idPrefix_uppercasesIds() {
        // ensure ids are uppercased by parser
        FindCommand expected = new FindCommand(
                new CombinedFindPredicate(
                        new AllFieldsContainsKeywordsPredicate(Collections.emptyList()),
                        new NameContainsKeywordsPredicate(Collections.emptyList()),
                        new NusMatricContainsKeywordsPredicate(Arrays.asList("A0234502U", "A0234505M"))));
        assertParseSuccess(parser, " m/a0234502u a0234505m", expected);
    }

    @Test
    public void parse_allPrefix_blankValue() {
        assertParseFailure(parser, " a/  ", FindCommandParser.MESSAGE_EMPTY_KEYWORDS);
    }

    @Test
    public void parse_namePrefix_blankValue() {
        assertParseFailure(parser, " n/   ", FindCommandParser.MESSAGE_EMPTY_KEYWORDS);
    }

    @Test
    public void parse_idPrefix_blankValue() {
        assertParseFailure(parser, " m/   ", FindCommandParser.MESSAGE_EMPTY_KEYWORDS);
    }

    @Test
    public void parse_mixedPrefixesBlankValue_throwsParseException() {
        assertParseFailure(parser, " a/john n/   ", FindCommandParser.MESSAGE_EMPTY_KEYWORDS);
    }
}


