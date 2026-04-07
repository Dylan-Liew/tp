package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.parser.CommandParserTestUtil.assertParseFailure;
import static cms.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import cms.logic.commands.SortCommand;

public class SortCommandParserTest {

    private final SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_lowercaseArguments_success() {
        assertParseSuccess(parser, " tg ", new SortCommand(SortCommand.SORT_BY_TUTORIAL_GROUP));
        assertParseSuccess(parser, " name ", new SortCommand(SortCommand.SORT_BY_NAME));
    }

    @Test
    public void parse_mixedCaseArguments_success() {
        assertParseSuccess(parser, " Tg ", new SortCommand(SortCommand.SORT_BY_TUTORIAL_GROUP));
        assertParseSuccess(parser, " NaMe ", new SortCommand(SortCommand.SORT_BY_NAME));
    }

    @Test
    public void parse_invalidArgument_failure() {
        assertParseFailure(parser, " invalid ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }
}
