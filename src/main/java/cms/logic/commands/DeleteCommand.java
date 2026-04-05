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
import cms.model.person.NusMatric;
import cms.model.person.Person;

/**
 * Deletes one or more persons identified using their displayed indexes from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";
    public static final String MESSAGE_EMPTY_INDEX_LIST = "At least one person index must be provided.";
    public static final String MESSAGE_EMPTY_NUS_MATRIC_LIST = "At least one NUS Matric must be provided.";
    public static final String MESSAGE_INVALID_NUS_MATRIC = "One or more NUS Matrics do not match any person.";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes one or more persons by their displayed index or NUS Matric.\n"
            + "Parameters: INDEX [MORE_INDEXES]... or m/NUS_MATRIC [MORE_NUS_MATRICS]...\n"
            + "Examples: " + COMMAND_WORD + " 1, " + COMMAND_WORD + " 1 2 3, "
            + COMMAND_WORD + " m/A1234567X, " + COMMAND_WORD + " m/A1234567X A2345678L";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_DELETE_PERSONS_SUCCESS = "Deleted persons:\n%1$s";

    private final TargetType targetType;
    private final List<Index> targetIndexes;
    private final List<NusMatric> targetNusMatrics;

    private enum TargetType {
        INDEX,
        NUS_MATRIC
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

    private DeleteCommand(TargetType targetType, List<Index> targetIndexes, List<NusMatric> targetNusMatrics) {
        requireNonNull(targetType);
        requireNonNull(targetIndexes);
        requireNonNull(targetNusMatrics);
        if (targetType == TargetType.INDEX) {
            checkArgument(!targetIndexes.isEmpty(), MESSAGE_EMPTY_INDEX_LIST);
        } else {
            checkArgument(!targetNusMatrics.isEmpty(), MESSAGE_EMPTY_NUS_MATRIC_LIST);
        }
        this.targetType = targetType;
        this.targetIndexes = List.copyOf(targetIndexes);
        this.targetNusMatrics = List.copyOf(targetNusMatrics);
    }

    /**
     * Creates a {@code DeleteCommand} to delete a single person by NUS Matric.
     */
    public static DeleteCommand byNusMatric(NusMatric targetNusMatric) {
        return byNusMatrics(List.of(targetNusMatric));
    }

    /**
     * Creates a {@code DeleteCommand} to delete one or more persons by NUS Matric.
     */
    public static DeleteCommand byNusMatrics(List<NusMatric> targetNusMatrics) {
        return new DeleteCommand(TargetType.NUS_MATRIC, List.of(), targetNusMatrics);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> deletedPersons;
        if (targetType == TargetType.INDEX) {
            deletedPersons = deletePersonsByIndex(model);
        } else {
            deletedPersons = deletePersonsByNusMatric(model);
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

    private List<Person> deletePersonsByNusMatric(Model model) throws CommandException {
        List<Person> personsInAddressBook = model.getAddressBook().getPersonList();
        List<Person> personsToDelete = new ArrayList<>();
        List<NusMatric> seenNusMatrics = new ArrayList<>();

        for (NusMatric targetNusMatric : targetNusMatrics) {
            if (seenNusMatrics.contains(targetNusMatric)) {
                continue;
            }

            Optional<Person> matchingPerson = personsInAddressBook.stream()
                    .filter(person -> person.getNusMatric().equals(targetNusMatric))
                    .findFirst();
            if (matchingPerson.isEmpty()) {
                throw new CommandException(MESSAGE_INVALID_NUS_MATRIC);
            }

            personsToDelete.add(matchingPerson.get());
            seenNusMatrics.add(targetNusMatric);
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
                && targetNusMatrics.equals(otherDeleteCommand.targetNusMatrics);
    }

    @Override
    public String toString() {
        if (targetType == TargetType.NUS_MATRIC) {
            if (targetNusMatrics.size() == 1) {
                return new ToStringBuilder(this)
                        .add("targetNusMatric", targetNusMatrics.get(0))
                        .toString();
            }

            return new ToStringBuilder(this)
                    .add("targetNusMatrics", targetNusMatrics)
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
