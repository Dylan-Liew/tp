package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.parser.CommandParserTestUtil.assertParseFailure;
import static cms.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import cms.logic.commands.ExportCommand;
import cms.logic.parser.exceptions.ParseException;

public class ExportCommandParserTest {

    private final ExportCommandParser parser = new ExportCommandParser();

    @Test
    public void parse_validUnquotedPath_success() {
        String path = "data/export.json";
        assertParseSuccess(parser, path, new ExportCommand(Path.of(path)));
    }

    @Test
    public void parse_validQuotedPathWithWhitespace_success() {
        String path = "C:/Users/Josh/My Documents/export.json";
        assertParseSuccess(parser, "\"" + path + "\"", new ExportCommand(Path.of(path)));
    }

    @Test
    public void parse_missingPath_failure() {
        assertParseFailure(parser, "   ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidExtension_failure() {
        assertParseFailure(parser, "data/export.txt", ExportCommandParser.MESSAGE_FILE_EXTENSION_REQUIRED);
    }

    @Test
    public void parse_unbalancedQuotes_failure() {
        assertParseFailure(parser, "\"data/export.json",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_singleQuoteToken_failure() {
        assertParseFailure(parser, "\"",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidPath_failureIncludesReason() {
        String invalidPath = "bad\u0000name.json";

        InvalidPathException invalidPathException = new InvalidPathException(invalidPath, "");
        try {
            Path.of(invalidPath);
        } catch (InvalidPathException ipe) {
            invalidPathException = ipe;
        }

        try {
            parser.parse(invalidPath);
        } catch (ParseException pe) {
            assertTrue(pe.getMessage().contains("File path is invalid:"));
            assertTrue(pe.getMessage().contains(invalidPathException.getReason()));
            assertTrue(pe.getMessage().contains("Format: "));
            return;
        }

        throw new AssertionError("The expected ParseException was not thrown.");
    }
}
