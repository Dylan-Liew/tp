package cms.logic.commands;

import java.util.Objects;

import cms.logic.Messages;
import cms.model.Model;

/**
 * Terminates the program.
 */
public class ExitCommand extends Command {

    public static final String COMMAND_WORD = "exit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Exits the program.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_EXIT_ACKNOWLEDGEMENT = "Exiting Course Management System as requested ...";

    private final String ignoredArgs;

    /**
     * Creates an ExitCommand with no ignored arguments.
     */
    public ExitCommand() {
        this.ignoredArgs = null;
    }

    /**
     * Creates an ExitCommand that will report the given arguments as ignored.
     *
     * @param ignoredArgs The arguments that were provided but will be ignored.
     */
    public ExitCommand(String ignoredArgs) {
        this.ignoredArgs = ignoredArgs;
    }

    @Override
    public CommandResult execute(Model model) {
        String feedback = MESSAGE_EXIT_ACKNOWLEDGEMENT;
        if (ignoredArgs != null && !ignoredArgs.isEmpty()) {
            feedback += "\n" + String.format(Messages.MESSAGE_IGNORED_PARAMETERS, ignoredArgs);
        }
        return new CommandResult(feedback, false, true);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ExitCommand)) {
            return false;
        }

        ExitCommand otherCommand = (ExitCommand) other;
        return Objects.equals(ignoredArgs, otherCommand.ignoredArgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ignoredArgs);
    }
}
