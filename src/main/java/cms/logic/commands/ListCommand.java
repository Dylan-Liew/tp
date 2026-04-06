package cms.logic.commands;

import static cms.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static java.util.Objects.requireNonNull;

import java.util.Objects;

import cms.logic.Messages;
import cms.model.Model;

/**
 * Lists all persons in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all persons.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Listed all persons";

    private final String ignoredArgs;

    /**
     * Creates a ListCommand with no ignored arguments.
     */
    public ListCommand() {
        this.ignoredArgs = null;
    }

    /**
     * Creates a ListCommand that will report the given arguments as ignored.
     *
     * @param ignoredArgs The arguments that were provided but will be ignored.
     */
    public ListCommand(String ignoredArgs) {
        this.ignoredArgs = ignoredArgs;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        String feedback = MESSAGE_SUCCESS;
        if (ignoredArgs != null && !ignoredArgs.isEmpty()) {
            feedback += "\n" + String.format(Messages.MESSAGE_IGNORED_PARAMETERS, ignoredArgs);
        }
        return new CommandResult(feedback);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ListCommand)) {
            return false;
        }

        ListCommand otherCommand = (ListCommand) other;
        return Objects.equals(ignoredArgs, otherCommand.ignoredArgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ignoredArgs);
    }
}
