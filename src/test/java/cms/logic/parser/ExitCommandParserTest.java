package cms.logic.parser;

import static cms.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import cms.logic.commands.ExitCommand;

public class ExitCommandParserTest {

    private final ExitCommandParser parser = new ExitCommandParser();

    @Test
    public void parse_emptyArgs_returnsExitCommand() {
        assertParseSuccess(parser, "", new ExitCommand());
        assertParseSuccess(parser, "   ", new ExitCommand());
    }

    @Test
    public void parse_extraArgs_returnsExitCommandWithIgnoredArgs() {
        assertParseSuccess(parser, "now", new ExitCommand("now"));
        assertParseSuccess(parser, "  p/+10  ", new ExitCommand("p/+10"));
    }
}
