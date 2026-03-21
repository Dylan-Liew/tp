package cms.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import cms.commons.util.ToStringBuilder;
import cms.logic.Messages;
import cms.model.Model;
import cms.model.person.Person;

/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds all persons by name, NUS ID, or any field (using the a/ prefix).\n"
            + "Parameters: a/KEYWORD [MORE_KEYWORDS]... | n/NAME [MORE_NAMES]... | id/NUS_ID [MORE_IDS]... "
            + "(multiple prefixes allowed; no prefix treats input as name search)\n"
            + "Examples: " + COMMAND_WORD + " a/Alice Bob, " + COMMAND_WORD + " n/Alice Bob, "
            + COMMAND_WORD + " id/A0234567B, " + COMMAND_WORD + " a/Alice n/Bob id/A0123456B";

    private final Predicate<Person> predicate;

    public FindCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
