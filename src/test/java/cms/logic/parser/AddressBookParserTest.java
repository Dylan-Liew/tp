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
import cms.logic.commands.FindCommand;
import cms.logic.commands.HelpCommand;
import cms.logic.commands.ImportCommand;
import cms.logic.commands.ListCommand;
import cms.logic.parser.exceptions.ParseException;
import cms.model.person.AllFieldsContainsKeywordsPredicate;
import cms.model.person.CombinedFindPredicate;
import cms.model.person.NameContainsKeywordsPredicate;
import cms.model.person.NusIdContainsKeywordsPredicate;
import cms.model.person.Person;
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
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
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
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
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
                        new NusIdContainsKeywordsPredicate(java.util.Collections.emptyList())
                )), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
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
