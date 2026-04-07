package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static cms.logic.parser.CliSyntax.PREFIX_NAME;
import static cms.testutil.Assert.assertThrows;
import static cms.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import cms.logic.commands.AddCommand;
import cms.logic.commands.ClearCommand;
import cms.logic.commands.DeleteCommand;
import cms.logic.commands.EditCommand;
import cms.logic.commands.EditCommand.EditPersonDescriptor;
import cms.logic.commands.ExitCommand;
import cms.logic.commands.ExportCommand;
import cms.logic.commands.FilterCommand;
import cms.logic.commands.FindCommand;
import cms.logic.commands.HelpCommand;
import cms.logic.commands.ImportCommand;
import cms.logic.commands.ImportCommand.KeepPolicy;
import cms.logic.commands.ListCommand;
import cms.logic.commands.MaskCommand;
import cms.logic.commands.SortCommand;
import cms.logic.commands.TagCommand;
import cms.logic.commands.UnmaskCommand;
import cms.logic.parser.exceptions.ParseException;
import cms.model.person.AllFieldsContainsKeywordsPredicate;
import cms.model.person.CombinedFindPredicate;
import cms.model.person.NameContainsKeywordsPredicate;
import cms.model.person.NusMatricContainsKeywordsPredicate;
import cms.model.person.Person;
import cms.model.person.TagTutorialGroupMatchesPredicate;
import cms.model.person.TutorialGroup;
import cms.model.tag.Tag;
import cms.testutil.EditPersonDescriptorBuilder;
import cms.testutil.PersonBuilder;
import cms.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        // Extra arguments should create a ClearCommand with ignored args
        assertEquals(new ClearCommand("3"), parser.parseCommand(ClearCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        // Extra arguments should create an ExitCommand with ignored args
        assertEquals(new ExitCommand("3"), parser.parseCommand(ExitCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_export() throws Exception {
        String path = "data/export.json";
        ExportCommand command = (ExportCommand) parser.parseCommand(ExportCommand.COMMAND_WORD + " " + path);
        assertEquals(new ExportCommand(java.nio.file.Path.of(path)), command);

        String quotedPathWithWhitespace = "C:/Users/Josh/My Documents/export.json";
        ExportCommand commandQuoted = (ExportCommand) parser.parseCommand(
            ExportCommand.COMMAND_WORD + " \"" + quotedPathWithWhitespace + "\"");
        assertEquals(new ExportCommand(java.nio.file.Path.of(quotedPathWithWhitespace)), commandQuoted);
    }

    @Test
    public void parseCommand_import() throws Exception {
        String path = "data/import.json";
        ImportCommand command = (ImportCommand) parser.parseCommand(ImportCommand.COMMAND_WORD + " " + path);
        assertEquals(new ImportCommand(java.nio.file.Path.of(path)), command);

        ImportCommand keepIncomingCommand = (ImportCommand) parser.parseCommand(
                ImportCommand.COMMAND_WORD + " " + path + " keep/incoming");
        assertEquals(new ImportCommand(java.nio.file.Path.of(path), KeepPolicy.INCOMING), keepIncomingCommand);

        ImportCommand keepCurrentCommand = (ImportCommand) parser.parseCommand(
                ImportCommand.COMMAND_WORD + " " + path + " keep/current");
        assertEquals(new ImportCommand(java.nio.file.Path.of(path), KeepPolicy.CURRENT), keepCurrentCommand);

        String quotedPathWithWhitespace = "C:/Users/Josh/My Documents/import.json";
        ImportCommand commandQuoted = (ImportCommand) parser.parseCommand(
                ImportCommand.COMMAND_WORD + " \"" + quotedPathWithWhitespace + "\"");
        assertEquals(new ImportCommand(java.nio.file.Path.of(quotedPathWithWhitespace)), commandQuoted);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_NAME + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(
                new CombinedFindPredicate(
                        new AllFieldsContainsKeywordsPredicate(java.util.Collections.emptyList()),
                        new NameContainsKeywordsPredicate(keywords),
                        new NusMatricContainsKeywordsPredicate(java.util.Collections.emptyList())
                )), command);
    }

    @Test
    public void parseCommand_filter() throws Exception {
        FilterCommand command = (FilterCommand) parser.parseCommand(
                FilterCommand.COMMAND_WORD + " tag/friends t/01");
        assertEquals(new FilterCommand(
                new TagTutorialGroupMatchesPredicate(java.util.Set.of(new Tag("friends")),
                        java.util.Set.of(new TutorialGroup("01")))),
                command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertEquals(new HelpCommand(AddCommand.COMMAND_WORD),
                parser.parseCommand(HelpCommand.COMMAND_WORD + " " + AddCommand.COMMAND_WORD));
    }

    @Test
    public void parseCommand_helpInvalidArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE);
        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(HelpCommand.COMMAND_WORD
            + " add delete"));
        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(HelpCommand.COMMAND_WORD
            + " unknown"));
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        // Extra arguments should create a ListCommand with ignored args
        assertEquals(new ListCommand("3"), parser.parseCommand(ListCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_sortTutorialGroup() throws Exception {
        assertEquals(new SortCommand(SortCommand.SORT_BY_TUTORIAL_GROUP),
                parser.parseCommand(SortCommand.COMMAND_WORD + " " + SortCommand.SORT_BY_TUTORIAL_GROUP));
    }

    @Test
    public void parseCommand_sortName() throws Exception {
        assertEquals(new SortCommand(SortCommand.SORT_BY_NAME),
                parser.parseCommand(SortCommand.COMMAND_WORD + " " + SortCommand.SORT_BY_NAME));
    }

    @Test
    public void parseCommand_sortMixedCaseArguments() throws Exception {
        assertEquals(new SortCommand(SortCommand.SORT_BY_TUTORIAL_GROUP),
                parser.parseCommand(SortCommand.COMMAND_WORD + " Tg"));
        assertEquals(new SortCommand(SortCommand.SORT_BY_NAME),
                parser.parseCommand(SortCommand.COMMAND_WORD + " NaMe"));
    }

    @Test
    public void parseCommand_sortInvalidArgument_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(SortCommand.COMMAND_WORD
                + " invalid"));
    }

    @Test
    public void parseCommand_tag() throws Exception {
        assertEquals(new TagCommand(TagCommand.Action.ADD, List.of(INDEX_FIRST_PERSON), List.of(new Tag("friend"))),
                parser.parseCommand(TagCommand.COMMAND_WORD + " add n/" + INDEX_FIRST_PERSON.getOneBased()
                        + " tag/friend"));
    }

    @Test
    public void parseCommand_sortMissingArgument_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(SortCommand.COMMAND_WORD));
    }

    @Test
    public void parseCommand_mask() throws Exception {
        assertTrue(parser.parseCommand(MaskCommand.COMMAND_WORD) instanceof MaskCommand);
        // Extra arguments should create a MaskCommand with ignored args
        assertEquals(new MaskCommand("3"), parser.parseCommand(MaskCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_unmask() throws Exception {
        assertTrue(parser.parseCommand(UnmaskCommand.COMMAND_WORD) instanceof UnmaskCommand);
        // Extra arguments should create an UnmaskCommand with ignored args
        assertEquals(new UnmaskCommand("3"), parser.parseCommand(UnmaskCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
                -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
