package cms.logic.parser;

import static cms.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import cms.logic.commands.MaskCommand;

public class MaskCommandParserTest {

    private final MaskCommandParser parser = new MaskCommandParser();

    @Test
    public void parse_emptyArgs_returnsMaskCommand() {
        assertParseSuccess(parser, "", new MaskCommand());
        assertParseSuccess(parser, "   ", new MaskCommand());
    }

    @Test
    public void parse_extraArgs_returnsMaskCommandWithIgnoredArgs() {
        assertParseSuccess(parser, "username", new MaskCommand("username"));
        assertParseSuccess(parser, "  all  ", new MaskCommand("all"));
    }
}
