package cms.logic.commands;

import static cms.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import cms.commons.core.index.Index;
import cms.commons.util.ToStringBuilder;
import cms.logic.Messages;
import cms.logic.commands.exceptions.CommandException;
import cms.model.Model;
import cms.model.person.Person;
import cms.model.tag.Tag;

/**
 * Deletes one or more persons identified using their displayed indexes from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";
    public static final String MESSAGE_EMPTY_INDEX_LIST = "At least one person index must be provided.";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes one or more persons by their displayed index.\n"
            + "Parameters: INDEX [MORE_INDEXES]... \n"
            + "Examples: " + COMMAND_WORD + " 1 or " + COMMAND_WORD + " 1 2 3";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_DELETE_PERSONS_SUCCESS = "Deleted persons:\n%1$s";

    private final List<Index> targetIndexes;

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
        requireNonNull(targetIndexes);
        checkArgument(!targetIndexes.isEmpty(), MESSAGE_EMPTY_INDEX_LIST);
        this.targetIndexes = List.copyOf(targetIndexes);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        for (Index targetIndex : targetIndexes) {
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
        }

        Person singlePersonToDelete = null;
        if (targetIndexes.size() == 1) {
            singlePersonToDelete = lastShownList.get(targetIndexes.get(0).getZeroBased());
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

        if (targetIndexes.size() == 1) {
            return new CommandResult(
                    String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(singlePersonToDelete)));
        }

        String deletedPersonsMessage = deletedPersons.stream()
                .map(DeleteCommand::formatDeletedPersonDetails)
                .collect(Collectors.joining("\n"));

        return new CommandResult(String.format(MESSAGE_DELETE_PERSONS_SUCCESS, deletedPersonsMessage));
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
        return targetIndexes.equals(otherDeleteCommand.targetIndexes);
    }

    @Override
    public String toString() {
        if (targetIndexes.size() == 1) {
            return new ToStringBuilder(this)
                    .add("targetIndex", targetIndexes.get(0))
                    .toString();
        }

        return new ToStringBuilder(this)
                .add("targetIndexes", targetIndexes)
                .toString();
    }

    /**
     * Formats a deleted person's details for the bulk delete success message.
     */
    private static String formatDeletedPersonDetails(Person person) {
        String tags = person.getTags().stream()
                .map(DeleteCommand::formatTag)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining(" "));

        String personDetails = person.getName()
                + " id/" + person.getNusId()
                + " role/" + person.getRole()
                + " soc/" + person.getSocUsername()
                + " gh/" + person.getGithubUsername()
                + " p/" + person.getPhone()
                + " e/" + person.getEmail()
                + " t/" + person.getTutorialGroup();

        if (!tags.isEmpty()) {
            personDetails += " " + tags;
        }

        return personDetails;
    }

    private static String formatTag(Tag tag) {
        return "tag/" + tag.tagName;
    }
}
