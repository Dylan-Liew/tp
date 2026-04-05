package cms.logic.commands;

import static cms.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import cms.commons.core.index.Index;
import cms.commons.util.ToStringBuilder;
import cms.logic.Messages;
import cms.logic.commands.exceptions.CommandException;
import cms.model.Model;
import cms.model.person.NusId;
import cms.model.person.Person;

/**
 * Deletes one or more persons identified using their displayed indexes from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";
    public static final String MESSAGE_EMPTY_INDEX_LIST = "At least one person index must be provided.";
    public static final String MESSAGE_EMPTY_NUS_ID_LIST = "At least one NUS ID must be provided.";
    public static final String MESSAGE_INVALID_NUS_ID = "One or more NUS IDs do not match any person.";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes one or more persons by their displayed index or NUS ID.\n"
            + "Parameters: INDEX [MORE_INDEXES]... or id/NUS_ID [MORE_NUS_IDS]...\n"
            + "Examples: " + COMMAND_WORD + " 1, " + COMMAND_WORD + " 1 2 3, "
            + COMMAND_WORD + " id/A1234567X, " + COMMAND_WORD + " id/A1234567X A2345678L";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_DELETE_PERSONS_SUCCESS = "Deleted persons:\n%1$s";

    private final TargetType targetType;
    private final List<Index> targetIndexes;
    private final List<NusId> targetNusIds;

    private enum TargetType {
        INDEX,
        NUS_ID
    }

    /**
     * Creates a {@code DeleteCommand} to delete a single person.
     */
    public DeleteCommand(Index targetIndex) {
        this(List.of(targetIndex));
    }

    /**
     * Creates a {@code DeleteCommand} to delete one or more persons.
     */
    public DeleteCommand(List<Index> targetIndexes) {
        this(TargetType.INDEX, targetIndexes, List.of());
    }

    private DeleteCommand(TargetType targetType, List<Index> targetIndexes, List<NusId> targetNusIds) {
        requireNonNull(targetType);
        requireNonNull(targetIndexes);
        requireNonNull(targetNusIds);
        if (targetType == TargetType.INDEX) {
            checkArgument(!targetIndexes.isEmpty(), MESSAGE_EMPTY_INDEX_LIST);
        } else {
            checkArgument(!targetNusIds.isEmpty(), MESSAGE_EMPTY_NUS_ID_LIST);
        }
        this.targetType = targetType;
        this.targetIndexes = List.copyOf(targetIndexes);
        this.targetNusIds = List.copyOf(targetNusIds);
    }

    /**
     * Creates a {@code DeleteCommand} to delete a single person by NUS ID.
     */
    public static DeleteCommand byNusId(NusId targetNusId) {
        return byNusIds(List.of(targetNusId));
    }

    /**
     * Creates a {@code DeleteCommand} to delete one or more persons by NUS ID.
     */
    public static DeleteCommand byNusIds(List<NusId> targetNusIds) {
        return new DeleteCommand(TargetType.NUS_ID, List.of(), targetNusIds);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> deletedPersons;
        if (targetType == TargetType.INDEX) {
            deletedPersons = deletePersonsByIndex(model);
        } else {
            deletedPersons = deletePersonsByNusId(model);
        }

        if (deletedPersons.size() == 1) {
            return new CommandResult(
                    String.format(MESSAGE_DELETE_PERSON_SUCCESS,
                            Messages.format(deletedPersons.get(0), model.isMasked())));
        }

        return new CommandResult(String.format(MESSAGE_DELETE_PERSONS_SUCCESS,
                formatDeletedPersons(deletedPersons, model.isMasked())));
    }

    private String formatDeletedPersons(List<Person> deletedPersons, boolean isMasked) {
        return deletedPersons.stream()
                .map(person -> Messages.format(person, isMasked))
                .collect(Collectors.joining("\n"));
    }

    private List<Person> deletePersonsByIndex(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        for (Index targetIndex : targetIndexes) {
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
        }

        List<Index> indexesToDelete = new ArrayList<>(targetIndexes);
        indexesToDelete.sort((first, second) -> Integer.compare(second.getZeroBased(), first.getZeroBased()));

        List<Person> deletedPersons = new ArrayList<>();
        Index previousIndex = null;
        for (Index targetIndex : indexesToDelete) {
            if (targetIndex.equals(previousIndex)) {
                continue;
            }
            Person personToDelete = lastShownList.get(targetIndex.getZeroBased());
            deletedPersons.add(0, personToDelete);
            model.deletePerson(personToDelete);
            previousIndex = targetIndex;
        }

        return deletedPersons;
    }

    private List<Person> deletePersonsByNusId(Model model) throws CommandException {
        List<Person> personsInAddressBook = model.getAddressBook().getPersonList();
        List<Person> personsToDelete = new ArrayList<>();
        List<NusId> seenNusIds = new ArrayList<>();

        for (NusId targetNusId : targetNusIds) {
            if (seenNusIds.contains(targetNusId)) {
                continue;
            }

            Optional<Person> matchingPerson = personsInAddressBook.stream()
                    .filter(person -> person.getNusId().equals(targetNusId))
                    .findFirst();
            if (matchingPerson.isEmpty()) {
                throw new CommandException(MESSAGE_INVALID_NUS_ID);
            }

            personsToDelete.add(matchingPerson.get());
            seenNusIds.add(targetNusId);
        }

        List<Person> deletedPersons = new ArrayList<>();
        for (Person personToDelete : personsToDelete) {
            model.deletePerson(personToDelete);
            deletedPersons.add(personToDelete);
        }
        return deletedPersons;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand)) {
            return false;
        }

        DeleteCommand otherDeleteCommand = (DeleteCommand) other;
        return targetType == otherDeleteCommand.targetType
                && targetIndexes.equals(otherDeleteCommand.targetIndexes)
                && targetNusIds.equals(otherDeleteCommand.targetNusIds);
    }

    @Override
    public String toString() {
        if (targetType == TargetType.NUS_ID) {
            if (targetNusIds.size() == 1) {
                return new ToStringBuilder(this)
                        .add("targetNusId", targetNusIds.get(0))
                        .toString();
            }

            return new ToStringBuilder(this)
                    .add("targetNusIds", targetNusIds)
                    .toString();
        }

        if (targetIndexes.size() == 1) {
            return new ToStringBuilder(this)
                    .add("targetIndex", targetIndexes.get(0))
                    .toString();
        }

        return new ToStringBuilder(this)
                .add("targetIndexes", targetIndexes)
                .toString();
    }

}
