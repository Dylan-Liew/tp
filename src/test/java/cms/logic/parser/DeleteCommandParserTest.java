package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.commands.CommandTestUtil.VALID_NUSMATRIC_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_NUSMATRIC_BOB;
import static cms.logic.parser.CommandParserTestUtil.assertParseFailure;
import static cms.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static cms.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static cms.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import cms.logic.commands.DeleteCommand;
import cms.model.person.NusMatric;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "1 2", new DeleteCommand(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)));
        assertParseSuccess(parser, "m/" + VALID_NUSMATRIC_AMY,
                DeleteCommand.byNusMatric(new NusMatric(VALID_NUSMATRIC_AMY)));
        assertParseSuccess(parser, "m/" + VALID_NUSMATRIC_AMY + " " + VALID_NUSMATRIC_BOB,
                DeleteCommand.byNusMatrics(List.of(
                        new NusMatric(VALID_NUSMATRIC_AMY), new NusMatric(VALID_NUSMATRIC_BOB))));
        assertParseSuccess(parser, "m/" + VALID_NUSMATRIC_AMY + " m/" + VALID_NUSMATRIC_BOB,
                DeleteCommand.byNusMatrics(List.of(
                        new NusMatric(VALID_NUSMATRIC_AMY), new NusMatric(VALID_NUSMATRIC_BOB))));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "   ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "m/" + VALID_NUSMATRIC_AMY + " 1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "1 m/" + VALID_NUSMATRIC_AMY,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }
}
