package cms.logic.parser;

import static cms.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import cms.logic.commands.ClearCommand;

public class ClearCommandParserTest {

    private final ClearCommandParser parser = new ClearCommandParser();

    @Test
    public void parse_emptyArgs_returnsClearCommand() {
        assertParseSuccess(parser, "", new ClearCommand());
        assertParseSuccess(parser, "   ", new ClearCommand());
    }

    @Test
    public void parse_extraArgs_returnsClearCommandWithIgnoredArgs() {
        assertParseSuccess(parser, "tag", new ClearCommand("tag"));
        assertParseSuccess(parser, "  all  ", new ClearCommand("all"));
    }
}
