package cms.logic.parser;

import static cms.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import cms.logic.commands.UnmaskCommand;

public class UnmaskCommandParserTest {

    private final UnmaskCommandParser parser = new UnmaskCommandParser();

    @Test
    public void parse_emptyArgs_returnsUnmaskCommand() {
        assertParseSuccess(parser, "", new UnmaskCommand());
        assertParseSuccess(parser, "   ", new UnmaskCommand());
    }

    @Test
    public void parse_extraArgs_returnsUnmaskCommandWithIgnoredArgs() {
        assertParseSuccess(parser, "username", new UnmaskCommand("username"));
        assertParseSuccess(parser, "  all  ", new UnmaskCommand("all"));
    }
}
