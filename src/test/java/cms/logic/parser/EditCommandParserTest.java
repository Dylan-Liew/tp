package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static cms.logic.commands.CommandTestUtil.GITHUBUSERNAME_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static cms.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static cms.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static cms.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static cms.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.NUSMATRIC_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static cms.logic.commands.CommandTestUtil.ROLE_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.SOCUSERNAME_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static cms.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static cms.logic.commands.CommandTestUtil.TUTORIALGROUP_DESC_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_GITHUBUSERNAME_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_NUSMATRIC_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_ROLE_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_SOCUSERNAME_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static cms.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static cms.logic.commands.CommandTestUtil.VALID_TUTORIALGROUP_AMY;
import static cms.logic.parser.CliSyntax.PREFIX_EMAIL;
import static cms.logic.parser.CliSyntax.PREFIX_PHONE;
import static cms.logic.parser.CliSyntax.PREFIX_TAG;
import static cms.logic.parser.CommandParserTestUtil.assertParseFailure;
import static cms.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static cms.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static cms.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static cms.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import cms.commons.core.index.Index;
import cms.logic.Messages;
import cms.logic.commands.EditCommand;
import cms.logic.commands.EditCommand.EditPersonDescriptor;
import cms.model.person.Email;
import cms.model.person.Name;
import cms.model.person.Phone;
import cms.model.tag.Tag;
import cms.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "1" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "1" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // invalid phone followed by valid email
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC + EMAIL_DESC_AMY, Phone.MESSAGE_CONSTRAINTS);

        // invalid tag in a space-separated tag/ value
        assertParseFailure(parser, "1" + TAG_DESC_FRIEND + " hubby*", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_EMPTY + TAG_DESC_FRIEND + " hubby/", Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1" + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_PHONE_AMY,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_PERSON;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + TAG_DESC_HUSBAND
            + EMAIL_DESC_AMY + NAME_DESC_AMY + TAG_DESC_FRIEND;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
            .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_AMY)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);

        // empty tag prefixes are ignored; valid tags are still parsed
        String userInputWithTrailingEmptyTag = targetIndex.getOneBased()
            + TAG_DESC_FRIEND + TAG_DESC_HUSBAND + TAG_EMPTY;
        String userInputWithMiddleEmptyTag = targetIndex.getOneBased()
            + TAG_DESC_FRIEND + TAG_EMPTY + TAG_DESC_HUSBAND;
        String userInputWithLeadingEmptyTag = targetIndex.getOneBased()
            + TAG_EMPTY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND;

        EditPersonDescriptor tagsDescriptor = new EditPersonDescriptorBuilder()
            .withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND).build();
        EditCommand expectedTagsCommand = new EditCommand(targetIndex, tagsDescriptor);

        assertParseSuccess(parser, userInputWithTrailingEmptyTag, expectedTagsCommand);
        assertParseSuccess(parser, userInputWithMiddleEmptyTag, expectedTagsCommand);
        assertParseSuccess(parser, userInputWithLeadingEmptyTag, expectedTagsCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetIndex.getOneBased() + EMAIL_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // nusMatric
        userInput = targetIndex.getOneBased() + NUSMATRIC_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withNusMatric(VALID_NUSMATRIC_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // role
        userInput = targetIndex.getOneBased() + ROLE_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withRole(VALID_ROLE_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // socUsername
        userInput = targetIndex.getOneBased() + SOCUSERNAME_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withSocUsername(VALID_SOCUSERNAME_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // githubUsername
        userInput = targetIndex.getOneBased() + GITHUBUSERNAME_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withGithubUsername(VALID_GITHUBUSERNAME_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tutorialGroup
        userInput = targetIndex.getOneBased() + TUTORIALGROUP_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withTutorialGroup(VALID_TUTORIALGROUP_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + TAG_DESC_FRIEND;
        descriptor = new EditPersonDescriptorBuilder().withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // mulltiple valid fields repeated
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY + EMAIL_DESC_AMY
            + TAG_DESC_FRIEND + PHONE_DESC_AMY + EMAIL_DESC_AMY + TAG_DESC_FRIEND
            + PHONE_DESC_BOB + EMAIL_DESC_BOB + TAG_DESC_HUSBAND;

        assertParseFailure(parser, userInput,
            Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL));

        // multiple invalid values
        userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + INVALID_EMAIL_DESC
            + INVALID_PHONE_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
            Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL));
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parseTagsForEdit_nullTags_assertionError() throws Exception {
        Method parseTagsForEditMethod = EditCommandParser.class
                .getDeclaredMethod("parseTagsForEdit", java.util.Collection.class);
        parseTagsForEditMethod.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, ()
            -> parseTagsForEditMethod.invoke(parser, new Object[] {null}));

        assertTrue(exception.getCause() instanceof AssertionError);
        assertEquals("tags should not be null", exception.getCause().getMessage());
    }
}
