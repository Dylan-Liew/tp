package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.parser.CommandParserTestUtil.assertParseFailure;
import static cms.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Set;

import org.junit.jupiter.api.Test;

import cms.logic.commands.FilterCommand;
import cms.model.person.TagTutorialGroupMatchesPredicate;
import cms.model.person.TutorialGroup;
import cms.model.tag.Tag;

public class FilterCommandParserTest {

    private final FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_noSupportedPrefix_throwsParseException() {
        assertParseFailure(parser, " n/Amy",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_tagOnly_success() {
        FilterCommand expectedCommand =
                new FilterCommand(new TagTutorialGroupMatchesPredicate(Set.of(new Tag("friend")), Set.of()));
        assertParseSuccess(parser, " tag/friend", expectedCommand);
    }

    @Test
    public void parse_tutorialGroupOnly_success() {
        FilterCommand expectedCommand =
                new FilterCommand(new TagTutorialGroupMatchesPredicate(Set.of(), Set.of(new TutorialGroup("01"))));
        assertParseSuccess(parser, " t/01", expectedCommand);
    }

    @Test
    public void parse_tutorialGroupWithoutLeadingZero_success() {
        FilterCommand expectedCommand =
                new FilterCommand(new TagTutorialGroupMatchesPredicate(Set.of(), Set.of(new TutorialGroup("01"))));
        assertParseSuccess(parser, " t/1", expectedCommand);
    }

    @Test
    public void parse_tagThenTutorialGroup_success() {
        FilterCommand expectedCommand =
                new FilterCommand(new TagTutorialGroupMatchesPredicate(Set.of(new Tag("friend")),
                        Set.of(new TutorialGroup("01"))));
        assertParseSuccess(parser, " tag/friend t/01", expectedCommand);
    }

    @Test
    public void parse_tutorialGroupThenTag_success() {
        FilterCommand expectedCommand =
                new FilterCommand(new TagTutorialGroupMatchesPredicate(Set.of(new Tag("friend")),
                        Set.of(new TutorialGroup("01"))));
        assertParseSuccess(parser, " t/01 tag/friend", expectedCommand);
    }

    @Test
    public void parse_repeatedPrefixes_success() {
        FilterCommand expectedCommand =
                new FilterCommand(new TagTutorialGroupMatchesPredicate(Set.of(new Tag("friend"), new Tag("husband")),
                        Set.of(new TutorialGroup("01"))));
        assertParseSuccess(parser, " tag/friend tag/husband t/01", expectedCommand);
    }

    @Test
    public void parse_duplicateTutorialGroup_throwsParseException() {
        assertParseFailure(parser, " t/01 t/02",
                cms.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_TUTORIALGROUP));
    }

    @Test
    public void parse_upperBoundTutorialGroup_success() {
        FilterCommand expectedCommand =
                new FilterCommand(new TagTutorialGroupMatchesPredicate(Set.of(), Set.of(new TutorialGroup("99"))));
        assertParseSuccess(parser, " t/99", expectedCommand);
    }

    @Test
    public void parse_invalidTag_throwsParseException() {
        assertParseFailure(parser, " tag/needs help",
                cms.model.tag.Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidTutorialGroup_throwsParseException() {
        assertParseFailure(parser, " t/T01",
                FilterCommandParser.MESSAGE_FILTER_TUTORIAL_GROUP_CONSTRAINTS);
    }

    @Test
    public void parse_zeroTutorialGroup_throwsParseException() {
        assertParseFailure(parser, " t/00",
                FilterCommandParser.MESSAGE_FILTER_TUTORIAL_GROUP_CONSTRAINTS);
    }

    @Test
    public void parse_tutorialGroupWithMultipleLeadingZeros_success() {
        FilterCommand expectedCommand =
                new FilterCommand(new TagTutorialGroupMatchesPredicate(Set.of(), Set.of(new TutorialGroup("01"))));
        assertParseSuccess(parser, " t/001", expectedCommand);
    }

    @Test
    public void parse_blankTag_throwsParseException() {
        assertParseFailure(parser, " tag/",
                cms.model.tag.Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_blankTutorialGroup_throwsParseException() {
        assertParseFailure(parser, " t/",
                FilterCommandParser.MESSAGE_FILTER_TUTORIAL_GROUP_CONSTRAINTS);
    }

    @Test
    public void parse_withPreamble_throwsParseException() {
        assertParseFailure(parser, " preamble tag/friend",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }
}
