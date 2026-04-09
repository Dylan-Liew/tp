package cms.logic.commands;

import static cms.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import cms.commons.core.index.Index;
import cms.commons.util.ToStringBuilder;
import cms.logic.Messages;
import cms.logic.commands.exceptions.CommandException;
import cms.model.Model;
import cms.model.person.NusMatric;
import cms.model.person.Person;
import cms.model.tag.Tag;

/**
 * Adds or removes one or more tags from one or more persons identified by index or NUS Matric.
 */
public class TagCommand extends Command {

    public static final String COMMAND_WORD = "tag";

    public static final String ACTION_ADD = "add";
    public static final String ACTION_DELETE = "delete";

    public static final String MESSAGE_EMPTY_INDEX_LIST = "At least one person index must be provided.";
    public static final String MESSAGE_EMPTY_NUS_MATRIC_LIST = "At least one NUS Matric must be provided.";
    public static final String MESSAGE_EMPTY_TAG_LIST = "At least one tag must be provided.";
    public static final String MESSAGE_INVALID_NUS_MATRIC = "One or more NUS Matrics do not match any person.";
    public static final String MESSAGE_ADD_NO_CHANGES = "All specified tags already exist on the targeted persons.";
    public static final String MESSAGE_DELETE_NO_CHANGES = "No specified tags were removed from the targeted persons.";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds or removes tags from one or more persons.\n"
            + "Parameters: "
            + ACTION_ADD + " id/INDEX [MORE_INDEXES]... tag/TAG [MORE_TAGS]... or "
            + ACTION_ADD + " m/NUS_MATRIC [MORE_NUS_MATRICS]... tag/TAG [MORE_TAGS]...\n"
            + "            "
            + ACTION_DELETE + " id/INDEX [MORE_INDEXES]... tag/TAG [MORE_TAGS]... or "
            + ACTION_DELETE + " m/NUS_MATRIC [MORE_NUS_MATRICS]... tag/TAG [MORE_TAGS]...\n"
            + "Examples: " + COMMAND_WORD + " " + ACTION_ADD + " id/1 2 tag/friend tutor, "
            + COMMAND_WORD + " " + ACTION_DELETE + " m/A1234567X A2345678J tag/friend";

    private final Action action;
    private final TargetType targetType;
    private final List<Index> targetIndexes;
    private final List<NusMatric> targetNusMatrics;
    private final List<Tag> targetTags;

    /**
     * Represents the supported tag actions.
     */
    public enum Action {
        ADD,
        DELETE
    }

    private enum TargetType {
        INDEX,
        NUS_MATRIC
    }

    /**
     * Creates a {@code TagCommand} targeting one or more persons by index.
     */
    public TagCommand(Action action, List<Index> targetIndexes, List<Tag> targetTags) {
        this(action, TargetType.INDEX, targetIndexes, List.of(), targetTags);
    }

    private TagCommand(Action action, TargetType targetType, List<Index> targetIndexes,
            List<NusMatric> targetNusMatrics, List<Tag> targetTags) {
        requireNonNull(action);
        requireNonNull(targetType);
        requireNonNull(targetIndexes);
        requireNonNull(targetNusMatrics);
        requireNonNull(targetTags);

        if (targetType == TargetType.INDEX) {
            checkArgument(!targetIndexes.isEmpty(), MESSAGE_EMPTY_INDEX_LIST);
        } else {
            checkArgument(!targetNusMatrics.isEmpty(), MESSAGE_EMPTY_NUS_MATRIC_LIST);
        }
        checkArgument(!targetTags.isEmpty(), MESSAGE_EMPTY_TAG_LIST);

        this.action = action;
        this.targetType = targetType;
        this.targetIndexes = List.copyOf(targetIndexes);
        this.targetNusMatrics = List.copyOf(targetNusMatrics);
        this.targetTags = List.copyOf(targetTags);
    }

    /**
     * Creates a {@code TagCommand} targeting one or more persons by NUS ID.
     */
    public static TagCommand byNusMatrics(Action action, List<NusMatric> targetNusMatrics, List<Tag> targetTags) {
        return new TagCommand(action, TargetType.NUS_MATRIC, List.of(), targetNusMatrics, targetTags);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> targetedPersons = targetType == TargetType.INDEX
                ? getPersonsByIndex(model)
                : getPersonsByNusMatric(model);

        return action == Action.ADD
                ? executeAdd(model, targetedPersons)
                : executeDelete(model, targetedPersons);
    }

    private CommandResult executeAdd(Model model, List<Person> targetedPersons) throws CommandException {
        List<Person> updatedPersons = new ArrayList<>();

        for (Person person : targetedPersons) {
            Set<Tag> updatedTags = new LinkedHashSet<>(person.getTags());
            boolean isChanged = updatedTags.addAll(targetTags);
            if (!isChanged) {
                continue;
            }

            Person updatedPerson = createUpdatedPerson(person, updatedTags);
            model.setPerson(person, updatedPerson);
            updatedPersons.add(updatedPerson);
        }

        if (updatedPersons.isEmpty()) {
            return new CommandResult(MESSAGE_ADD_NO_CHANGES);
        }

        return new CommandResult(buildAddSuccessMessage(model, updatedPersons));
    }

    private CommandResult executeDelete(Model model, List<Person> targetedPersons) throws CommandException {
        Map<Tag, List<Person>> removedPersonsByTag = new LinkedHashMap<>();
        Map<Person, Set<Tag>> updatedTagsByPerson = new LinkedHashMap<>();

        for (Tag tag : targetTags) {
            removedPersonsByTag.put(tag, new ArrayList<>());
        }

        for (Person person : targetedPersons) {
            Set<Tag> updatedTags = new LinkedHashSet<>(person.getTags());
            boolean isChanged = false;

            for (Tag tag : targetTags) {
                if (updatedTags.remove(tag)) {
                    removedPersonsByTag.get(tag).add(person);
                    isChanged = true;
                }
            }

            if (isChanged) {
                updatedTagsByPerson.put(person, updatedTags);
            }
        }

        for (Map.Entry<Person, Set<Tag>> entry : updatedTagsByPerson.entrySet()) {
            Person updatedPerson = createUpdatedPerson(entry.getKey(), entry.getValue());
            model.setPerson(entry.getKey(), updatedPerson);
        }

        String successMessage = buildDeleteSuccessMessage(model, removedPersonsByTag);
        if (successMessage.isEmpty()) {
            return new CommandResult(MESSAGE_DELETE_NO_CHANGES);
        }

        return new CommandResult(successMessage);
    }

    private List<Person> getPersonsByIndex(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();
        List<Person> persons = new ArrayList<>();
        Set<Index> seenIndexes = new LinkedHashSet<>();

        for (Index targetIndex : targetIndexes) {
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            if (!seenIndexes.add(targetIndex)) {
                continue;
            }

            persons.add(lastShownList.get(targetIndex.getZeroBased()));
        }

        return persons;
    }

    private List<Person> getPersonsByNusMatric(Model model) throws CommandException {
        List<Person> personsInAddressBook = model.getAddressBook().getPersonList();
        List<Person> persons = new ArrayList<>();
        Set<NusMatric> seenNusMatrics = new LinkedHashSet<>();

        for (NusMatric targetNusMatric : targetNusMatrics) {
            if (!seenNusMatrics.add(targetNusMatric)) {
                continue;
            }

            Optional<Person> matchingPerson = personsInAddressBook.stream()
                    .filter(person -> person.getNusMatric().equals(targetNusMatric))
                    .findFirst();
            if (matchingPerson.isEmpty()) {
                throw new CommandException(MESSAGE_INVALID_NUS_MATRIC);
            }

            persons.add(matchingPerson.get());
        }

        return persons;
    }

    private Person createUpdatedPerson(Person personToEdit, Set<Tag> updatedTags) throws CommandException {
        try {
            return Person.create(personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                    personToEdit.getNusMatric(), personToEdit.getSocUsername(), personToEdit.getGithubUsername(),
                    personToEdit.getRole(), personToEdit.getTutorialGroup(), updatedTags);
        } catch (IllegalArgumentException e) {
            throw new CommandException(e.getMessage(), e);
        }
    }

    private String buildAddSuccessMessage(Model model, List<Person> updatedPersons) {
        return formatTags(targetTags) + " has been added to "
                + updatedPersons.stream()
                .map(person -> formatPersonSummary(model, person))
                .collect(Collectors.joining("; "));
    }

    private String buildDeleteSuccessMessage(Model model, Map<Tag, List<Person>> removedPersonsByTag) {
        return targetTags.stream()
                .filter(tag -> !removedPersonsByTag.get(tag).isEmpty())
                .map(tag -> tag.tagName + " has been removed from "
                        + removedPersonsByTag.get(tag).stream()
                        .map(person -> formatPersonSummary(model, person))
                        .collect(Collectors.joining("; ")))
                .collect(Collectors.joining("\n"));
    }

    private String formatTags(List<Tag> tags) {
        return tags.stream()
                .map(tag -> tag.tagName)
                .collect(Collectors.joining(", "));
    }

    private String formatPersonSummary(Model model, Person person) {
        int oneBasedIndex = findOneBasedIndex(model.getFilteredPersonList(), person);
        if (oneBasedIndex == 0) {
            oneBasedIndex = findOneBasedIndex(model.getAddressBook().getPersonList(), person);
        }
        return oneBasedIndex + ", " + person.getName() + ", " + person.getNusMatric();
    }

    private int findOneBasedIndex(List<Person> persons, Person person) {
        for (int i = 0; i < persons.size(); i++) {
            if (persons.get(i).isSamePerson(person)) {
                return i + 1;
            }
        }

        return 0;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TagCommand)) {
            return false;
        }

        TagCommand otherTagCommand = (TagCommand) other;
        return action == otherTagCommand.action
                && targetType == otherTagCommand.targetType
                && targetIndexes.equals(otherTagCommand.targetIndexes)
                && targetNusMatrics.equals(otherTagCommand.targetNusMatrics)
                && targetTags.equals(otherTagCommand.targetTags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("action", action)
                .add("targetType", targetType)
                .add("targetIndexes", targetIndexes)
                .add("targetNusMatrics", targetNusMatrics)
                .add("targetTags", targetTags)
                .toString();
    }
}
