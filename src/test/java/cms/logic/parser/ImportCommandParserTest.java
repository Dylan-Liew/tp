package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.parser.CommandParserTestUtil.assertParseFailure;
import static cms.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import cms.logic.commands.ImportCommand;
import cms.logic.commands.ImportCommand.KeepPolicy;
import cms.logic.parser.exceptions.ParseException;

public class ImportCommandParserTest {

    private final ImportCommandParser parser = new ImportCommandParser();

    @Test
    public void parse_validUnquotedPath_success() {
        String path = "data/import.json";
        assertParseSuccess(parser, path, new ImportCommand(Path.of(path)));
    }

    @Test
    public void parse_validKeepIncoming_success() {
        String path = "data/import.json";
        assertParseSuccess(parser, path + " keep/incoming",
                new ImportCommand(Path.of(path), KeepPolicy.INCOMING));
    }

    @Test
    public void parse_validKeepCurrent_success() {
        String path = "data/import.json";
        assertParseSuccess(parser, path + " keep/current",
                new ImportCommand(Path.of(path), KeepPolicy.CURRENT));
    }

    @Test
    public void parse_pathContainingKeepSegmentWithKeepIncoming_success() {
        String path = "keep/data.json";
        assertParseSuccess(parser, path + " keep/incoming",
                new ImportCommand(Path.of(path), KeepPolicy.INCOMING));
    }

    @Test
    public void parse_quotedPathContainingKeepSegmentWithKeepCurrent_success() {
        String path = "C:/Users/Josh/keep/data.json";
        assertParseSuccess(parser, "\"" + path + "\" keep/current",
                new ImportCommand(Path.of(path), KeepPolicy.CURRENT));
    }

    @Test
    public void parse_validQuotedPathWithWhitespace_success() {
        String path = "C:/Users/Josh/My Documents/import.json";
        assertParseSuccess(parser, "\"" + path + "\"", new ImportCommand(Path.of(path)));
    }

    @Test
    public void parse_missingPath_failure() {
        assertParseFailure(parser, "   ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidExtension_failure() {
        assertParseFailure(parser, "data/import.txt", ImportCommandParser.MESSAGE_FILE_EXTENSION_REQUIRED);
    }

    @Test
    public void parse_invalidKeep_failure() {
        assertParseFailure(parser, "data/import.json keep/other", ImportCommandParser.MESSAGE_INVALID_KEEP);
    }

    @Test
    public void parse_tooManyArguments_failure() {
        assertParseFailure(parser, "data/import.json keep/current keep/incoming",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_unbalancedQuotes_failure() {
        assertParseFailure(parser, "\"data/import.json",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE));
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
