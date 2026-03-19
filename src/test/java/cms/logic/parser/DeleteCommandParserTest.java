package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.commands.CommandTestUtil.VALID_NUSID_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_NUSID_BOB;
import static cms.logic.parser.CommandParserTestUtil.assertParseFailure;
import static cms.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static cms.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static cms.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import cms.logic.commands.DeleteCommand;
import cms.model.person.NusId;

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
        assertParseSuccess(parser, "id/" + VALID_NUSID_AMY,
                DeleteCommand.byNusId(new NusId(VALID_NUSID_AMY)));
        assertParseSuccess(parser, "id/" + VALID_NUSID_AMY + " " + VALID_NUSID_BOB,
                DeleteCommand.byNusIds(List.of(new NusId(VALID_NUSID_AMY), new NusId(VALID_NUSID_BOB))));
        assertParseSuccess(parser, "id/" + VALID_NUSID_AMY + " id/" + VALID_NUSID_BOB,
                DeleteCommand.byNusIds(List.of(new NusId(VALID_NUSID_AMY), new NusId(VALID_NUSID_BOB))));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "   ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "id/" + VALID_NUSID_AMY + " 1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "1 id/" + VALID_NUSID_AMY,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }
}
