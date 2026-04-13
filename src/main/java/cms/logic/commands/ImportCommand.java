package cms.logic.commands;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cms.commons.exceptions.DataLoadingException;
import cms.commons.exceptions.IllegalValueException;
import cms.logic.commands.exceptions.CommandException;
import cms.model.AddressBook;
import cms.model.Model;
import cms.model.ReadOnlyAddressBook;
import cms.model.person.Person;
import cms.storage.Storage;

/**
 * Imports address book data from a user-specified JSON file path into the application.
 */
public class ImportCommand extends Command {

    public static final String COMMAND_WORD = "import";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Imports address book data from a JSON file.\n"
            + "Parameters: \"FILE_PATH\" (must be a .json file) [keep/POLICY]\n"
            + "Behavior: Imports new data and merges with existing data (if any).\n"
            + "When existing data would conflict with imported data:\n"
            + "  - If keep/POLICY is omitted and data exists: ERROR (user must choose strategy)\n"
            + "  - keep/current: keep existing, skip conflicting imports\n"
            + "  - keep/incoming: replace conflicts with imported data\n"
            + "Examples: " + COMMAND_WORD + " \"data/addressbook.json\"\n"
            + "          " + COMMAND_WORD + " \"data/addressbook.json\" keep/current\n"
            + "          " + COMMAND_WORD + " \"data/addressbook.json\" keep/incoming";

    public static final String MESSAGE_SUCCESS = "Address book imported from: %s";
    public static final String MESSAGE_KEEP_CURRENT_SUCCESS =
            "Address book merged from: %s (conflicts resolved with keep/current)";
    public static final String MESSAGE_KEEP_INCOMING_SUCCESS =
            "Address book merged from: %s (conflicts resolved with keep/incoming)";
    public static final String MESSAGE_EMPTY_OR_INVALID_FILE =
            "Import file is empty or not a valid Course Management System data file.";
    public static final String MESSAGE_INVALID_DATA =
            "Import file contains invalid Course Management System data.";
    public static final String MESSAGE_INVALID_DATA_DETAILS_FORMAT =
            "%s Details: %s";
    public static final String MESSAGE_STORAGE_CONTEXT_REQUIRED =
            "Import command requires storage context.";
    public static final String MESSAGE_KEEP_REQUIRED_NON_EMPTY = "Current data is non-empty. "
            + "Re-run the import command and add keep/current or keep/incoming "
            + "after the import command to choose how conflicts are resolved.";
    public static final String MESSAGE_NO_CONFLICT_PREVIEW =
            "\nNo direct conflicts were detected in the import preview.";
    public static final String MESSAGE_CONFLICT_COUNT_MORE_FORMAT =
            "\n- ... and %d more conflict(s)";
    public static final String MESSAGE_IDENTITY_CONFLICT_FORMAT =
            "incoming '%s' conflicts with current '%s' by NUS Matric (%s)";
    public static final String MESSAGE_FIELD_CONFLICT_FORMAT =
            "incoming '%s' conflicts with current '%s' by %s (%s)";
    public static final String MESSAGE_MULTIPLE_CONFLICTS_FORMAT =
            "Import aborted: incoming '%s' conflicts with multiple current persons (%d). "
            + "Please resolve conflicts manually before using keep/incoming.";
    private static final String MESSAGE_MULTIPLE_CONFLICTS_DETAILS_HEADER = "\nConflicting entries:";
    private static final String MESSAGE_CONFLICT_PREVIEW_HEADER = "\nConflicting entries detected:";
    private static final int CONFLICT_PREVIEW_LIMIT = 5;

    /** Resolution policy when importing into a non-empty address book. */
    public enum KeepPolicy {
        CURRENT,
        INCOMING
    }

    private final Path importFilePath;
    private final KeepPolicy keepPolicy;

    /**
     * Creates an ImportCommand to import address book data from the specified file path.
     *
     * @param importFilePath the path to the JSON file to import from
     */
    public ImportCommand(Path importFilePath) {
        this(importFilePath, null);
    }

    /**
     * Creates an ImportCommand with optional mode settings.
     */
    public ImportCommand(Path importFilePath, KeepPolicy keepPolicy) {
        requireNonNull(importFilePath);
        this.importFilePath = importFilePath;
        this.keepPolicy = keepPolicy;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException(MESSAGE_STORAGE_CONTEXT_REQUIRED);
    }

    @Override
    public CommandResult execute(Model model, Storage storage) throws CommandException {
        requireNonNull(model);
        requireNonNull(storage);

        ReadOnlyAddressBook importedAddressBook;
        try {
            importedAddressBook = storage.readAddressBook(importFilePath)
                    .orElseThrow(() -> new CommandException(MESSAGE_EMPTY_OR_INVALID_FILE));
        } catch (DataLoadingException dle) {
            throw new CommandException(buildInvalidDataMessage(dle), dle);
        }

        boolean hasCurrentData = !model.getAddressBook().getPersonList().isEmpty();
        if (hasCurrentData && keepPolicy == null) {
            String conflictPreviewMessage = buildConflictPreviewMessage(model.getAddressBook(), importedAddressBook);
            throw new CommandException(MESSAGE_KEEP_REQUIRED_NON_EMPTY + conflictPreviewMessage);
        }

        if (!hasCurrentData) {
            model.setAddressBook(importedAddressBook);
            model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
            return new CommandResult(String.format(MESSAGE_SUCCESS, importFilePath));
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

            if (conflictingPersons.size() > 1) {
                throw new CommandException(buildMultipleConflictsMessage(incomingPerson, conflictingPersons));
            }

            for (Person conflictingPerson : conflictingPersons) {
                mergedAddressBook.removePerson(conflictingPerson);
            }
            mergedAddressBook.addPerson(incomingPerson);
        }

        model.setAddressBook(mergedAddressBook);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);

        if (keepPolicy == KeepPolicy.CURRENT) {
            return new CommandResult(String.format(MESSAGE_KEEP_CURRENT_SUCCESS, importFilePath));
        }
        return new CommandResult(String.format(MESSAGE_KEEP_INCOMING_SUCCESS, importFilePath));
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

    /**
     * Builds a concise preview of conflicts between incoming and current data for user decision-making.
     */
    private String buildConflictPreviewMessage(ReadOnlyAddressBook currentAddressBook,
                                               ReadOnlyAddressBook importedAddressBook) {
        Set<String> conflictLines = new LinkedHashSet<>();

        for (Person incomingPerson : importedAddressBook.getPersonList()) {
            for (Person existingPerson : currentAddressBook.getPersonList()) {
                String conflictLine = getConflictDescription(incomingPerson, existingPerson);
                if (conflictLine != null) {
                    conflictLines.add(conflictLine);
                }
            }
        }

        if (conflictLines.isEmpty()) {
            return MESSAGE_NO_CONFLICT_PREVIEW;
        }

        List<String> limitedConflictLines = new ArrayList<>(conflictLines);
        int displayedCount = Math.min(limitedConflictLines.size(), CONFLICT_PREVIEW_LIMIT);
        StringBuilder preview = new StringBuilder(MESSAGE_CONFLICT_PREVIEW_HEADER);
        for (int i = 0; i < displayedCount; i++) {
            preview.append("\n- ").append(limitedConflictLines.get(i));
        }

        int hiddenCount = limitedConflictLines.size() - displayedCount;
        if (hiddenCount > 0) {
            preview.append(String.format(MESSAGE_CONFLICT_COUNT_MORE_FORMAT, hiddenCount));
        }

        return preview.toString();
    }

    private String getConflictDescription(Person incomingPerson, Person existingPerson) {
        if (incomingPerson.isSamePerson(existingPerson)) {
            return String.format(MESSAGE_IDENTITY_CONFLICT_FORMAT,
                    incomingPerson.getName(), existingPerson.getName(), incomingPerson.getNusMatric());
        }

        cms.model.person.FieldConflict fieldConflict = incomingPerson.findConflictingField(existingPerson);
        if (fieldConflict == null) {
            return null;
        }

        return String.format(MESSAGE_FIELD_CONFLICT_FORMAT,
                incomingPerson.getName(), existingPerson.getName(),
                fieldConflict.getFieldName(), fieldConflict.getFieldValue());
    }

    private String buildMultipleConflictsMessage(Person incomingPerson, List<Person> conflictingPersons) {
        StringBuilder details = new StringBuilder(String.format(
                MESSAGE_MULTIPLE_CONFLICTS_FORMAT, incomingPerson.getName(), conflictingPersons.size()));
        details.append(MESSAGE_MULTIPLE_CONFLICTS_DETAILS_HEADER);
        for (Person conflictingPerson : conflictingPersons) {
            String conflictDescription = getConflictDescription(incomingPerson, conflictingPerson);
            if (conflictDescription != null) {
                details.append("\n- ").append(conflictDescription);
            }
        }
        return details.toString();
    }

    private String buildInvalidDataMessage(DataLoadingException dataLoadingException) {
        Throwable cause = dataLoadingException.getCause();
        if (!(cause instanceof IllegalValueException) || cause.getMessage() == null || cause.getMessage().isEmpty()) {
            return MESSAGE_INVALID_DATA;
        }
        return String.format(MESSAGE_INVALID_DATA_DETAILS_FORMAT, MESSAGE_INVALID_DATA, cause.getMessage());
    }

    public Path getImportFilePath() {
        return importFilePath;
    }

    public KeepPolicy getKeepPolicy() {
        return keepPolicy;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ImportCommand)) {
            return false;
        }

        ImportCommand otherImportCommand = (ImportCommand) other;
        return importFilePath.equals(otherImportCommand.importFilePath)
                && java.util.Objects.equals(keepPolicy, otherImportCommand.keepPolicy);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(importFilePath, keepPolicy);
    }
}
