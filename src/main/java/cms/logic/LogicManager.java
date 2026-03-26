package cms.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import cms.commons.core.GuiSettings;
import cms.commons.core.LogsCenter;
import cms.commons.exceptions.DataLoadingException;
import cms.logic.commands.Command;
import cms.logic.commands.CommandResult;
import cms.logic.commands.ExportCommand;
import cms.logic.commands.ImportCommand;
import cms.logic.commands.ImportCommand.KeepPolicy;
import cms.logic.commands.exceptions.CommandException;
import cms.logic.parser.AddressBookParser;
import cms.logic.parser.exceptions.ParseException;
import cms.model.AddressBook;
import cms.model.Model;
import cms.model.ReadOnlyAddressBook;
import cms.model.person.Person;
import cms.storage.Storage;
import javafx.collections.ObservableList;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";
    public static final String FILE_OPS_EXPORT_ERROR_FORMAT =
        "Could not export data to file %s due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    public static final String FILE_OPS_EXPORT_PERMISSION_ERROR_FORMAT =
        "Could not export data to file %s due to insufficient permissions to write to the file or the folder.";
    public static final String IMPORT_KEEP_REQUIRED_NON_EMPTY = "Current data is non-empty. "
            + "Re-run with keep/current to keep existing values on conflicts, "
            + "or keep/incoming to use imported values on conflicts.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        Command command = addressBookParser.parseCommand(commandText);

        // Special handling for file I/O operations
        if (command instanceof ImportCommand) {
            commandResult = executeImportCommand((ImportCommand) command);
        } else if (command instanceof ExportCommand) {
            commandResult = executeExportCommand((ExportCommand) command);
        } else {
            // Standard command execution
            commandResult = command.execute(model);
        }

        // Persist changes to storage
        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    private CommandResult executeImportCommand(ImportCommand importCommand) throws CommandException {
        String importFeedback = handleImportCommand(importCommand);
        return new CommandResult(importFeedback);
    }

    private CommandResult executeExportCommand(ExportCommand exportCommand) throws CommandException {
        Path exportFilePath = exportCommand.getExportFilePath();
        try {
            storage.saveAddressBook(model.getAddressBook(), exportFilePath);
            return new CommandResult(String.format(ExportCommand.MESSAGE_SUCCESS, exportFilePath));
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_EXPORT_PERMISSION_ERROR_FORMAT, exportFilePath), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_EXPORT_ERROR_FORMAT,
                    exportFilePath, ioe.getMessage()), ioe);
        }
    }

    private String handleImportCommand(ImportCommand importCommand) throws CommandException {
        Path importFilePath = importCommand.getImportFilePath();
        ReadOnlyAddressBook importedAddressBook;
        try {
            importedAddressBook = storage.readAddressBook(importFilePath)
                    .orElseThrow(() -> new CommandException(
                            "Import file is empty or not a valid Course Management System data file."));
        } catch (DataLoadingException dle) {
            throw new CommandException("Import file contains invalid Course Management System data.", dle);
        }

        boolean hasCurrentData = !model.getAddressBook().getPersonList().isEmpty();
        KeepPolicy keepPolicy = importCommand.getKeepPolicy();
        if (hasCurrentData && keepPolicy == null) {
            throw new CommandException(IMPORT_KEEP_REQUIRED_NON_EMPTY);
        }

        if (!hasCurrentData) {
            model.setAddressBook(importedAddressBook);
            model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
            return String.format(ImportCommand.MESSAGE_SUCCESS, importFilePath);
        }

        AddressBook mergedAddressBook = new AddressBook(model.getAddressBook());
        for (Person incomingPerson : importedAddressBook.getPersonList()) {
            List<Person> conflictingPersons = findConflictingPersons(mergedAddressBook, incomingPerson);
            if (conflictingPersons.isEmpty()) {
                mergedAddressBook.addPerson(incomingPerson);
                continue;
            }

            if (keepPolicy == KeepPolicy.CURRENT) {
                continue;
            }

            for (Person conflictingPerson : conflictingPersons) {
                mergedAddressBook.removePerson(conflictingPerson);
            }
            mergedAddressBook.addPerson(incomingPerson);
        }

        model.setAddressBook(mergedAddressBook);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        if (keepPolicy == KeepPolicy.CURRENT) {
            return String.format(ImportCommand.MESSAGE_KEEP_CURRENT_SUCCESS, importFilePath);
        }
        return String.format(ImportCommand.MESSAGE_KEEP_INCOMING_SUCCESS, importFilePath);
    }

    /**
     * Finds persons in {@code currentAddressBook} that conflict with {@code incomingPerson}
     * by identity or by unique-field collision.
     */
    private List<Person> findConflictingPersons(ReadOnlyAddressBook currentAddressBook, Person incomingPerson) {
        List<Person> conflicts = new ArrayList<>();
        for (Person existingPerson : currentAddressBook.getPersonList()) {
            if (incomingPerson.isSamePerson(existingPerson)
                    || incomingPerson.findConflictingField(existingPerson) != null) {
                conflicts.add(existingPerson);
            }
        }
        return conflicts;
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return model.getAddressBook();
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public Path getAddressBookFilePath() {
        return model.getAddressBookFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
