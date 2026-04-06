package cms.logic.parser;

import static cms.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import cms.logic.commands.ListCommand;

public class ListCommandParserTest {

    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_emptyArgs_returnsListCommand() {
        assertParseSuccess(parser, "", new ListCommand());
        assertParseSuccess(parser, "   ", new ListCommand());
    }

    @Test
    public void parse_extraArgs_returnsListCommandWithIgnoredArgs() {
        assertParseSuccess(parser, "abc", new ListCommand("abc"));
        assertParseSuccess(parser, "  123  ", new ListCommand("123"));
        assertParseSuccess(parser, "n/Amy", new ListCommand("n/Amy"));
    }
}
