package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.commands.CommandTestUtil.VALID_NUSMATRIC_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_NUSMATRIC_BOB;
import static cms.logic.parser.CommandParserTestUtil.assertParseFailure;
import static cms.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static cms.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static cms.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Test;

import cms.logic.commands.TagCommand;
import cms.logic.commands.TagCommand.Action;
import cms.model.person.NusMatric;
import cms.model.tag.Tag;

public class TagCommandParserTest {

    private final TagCommandParser parser = new TagCommandParser();

    @Test
    public void parse_validAddArgs_returnsTagCommand() {
        assertParseSuccess(parser, "add n/1 2 tag/friend tutor",
                new TagCommand(Action.ADD, List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON),
                        List.of(new Tag("friend"), new Tag("tutor"))));

        assertParseSuccess(parser, "add m/" + VALID_NUSMATRIC_AMY + " " + VALID_NUSMATRIC_BOB + " tag/friend",
                TagCommand.byNusMatrics(Action.ADD,
                        List.of(new NusMatric(VALID_NUSMATRIC_AMY), new NusMatric(VALID_NUSMATRIC_BOB)),
                        List.of(new Tag("friend"))));

        assertParseSuccess(parser, "add n/1 tag/friend friend",
                new TagCommand(Action.ADD, List.of(INDEX_FIRST_PERSON), List.of(new Tag("friend"))));
    }

    @Test
    public void parse_validDeleteArgs_returnsTagCommand() {
        assertParseSuccess(parser, "delete n/1 2 tag/friend tutor",
                new TagCommand(Action.DELETE, List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON),
                        List.of(new Tag("friend"), new Tag("tutor"))));

        assertParseSuccess(parser, "delete m/" + VALID_NUSMATRIC_AMY + " m/" + VALID_NUSMATRIC_BOB + " tag/friend",
                TagCommand.byNusMatrics(Action.DELETE,
                        List.of(new NusMatric(VALID_NUSMATRIC_AMY), new NusMatric(VALID_NUSMATRIC_BOB)),
                        List.of(new Tag("friend"))));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "add", expectedMessage);
        assertParseFailure(parser, "rename n/1 tag/friend", expectedMessage);
        assertParseFailure(parser, "add 1 tag/friend", expectedMessage);
        assertParseFailure(parser, "add n/1 m/" + VALID_NUSMATRIC_AMY + " tag/friend", expectedMessage);
        assertParseFailure(parser, "delete n/1", expectedMessage);
        assertParseFailure(parser, "add tag/friend", expectedMessage);
        assertParseFailure(parser, "add n/1 tag/   ", expectedMessage);
        assertParseFailure(parser, "delete n/1 x", expectedMessage);
    }

    @Test
    public void splitValues_skipsEmptyTokens() throws Exception {
        Method splitValues = TagCommandParser.class.getDeclaredMethod("splitValues", List.class);
        splitValues.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) splitValues.invoke(parser, List.of("   ", "friend tutor", " mentor "));

        assertEquals(List.of("friend", "tutor", "mentor"), result);
    }
}
