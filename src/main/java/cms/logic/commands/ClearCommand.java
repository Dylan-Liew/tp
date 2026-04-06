package cms.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import cms.logic.Messages;
import cms.model.AddressBook;
import cms.model.Model;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";

    public static final String MESSAGE_USAGE = COMMAND_WORD
        + ": Clears all entries from the Course Management System.\n"
        + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Course Management System has been cleared!";

    private final String ignoredArgs;

    /**
     * Creates a ClearCommand with no ignored arguments.
     */
    public ClearCommand() {
        this.ignoredArgs = null;
    }

    /**
     * Creates a ClearCommand that will report the given arguments as ignored.
     *
     * @param ignoredArgs The arguments that were provided but will be ignored.
     */
    public ClearCommand(String ignoredArgs) {
        this.ignoredArgs = ignoredArgs;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setAddressBook(new AddressBook());

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

        if (!(other instanceof ClearCommand)) {
            return false;
        }

        ClearCommand otherCommand = (ClearCommand) other;
        return Objects.equals(ignoredArgs, otherCommand.ignoredArgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ignoredArgs);
    }
}
