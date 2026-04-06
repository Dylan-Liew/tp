package cms.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import cms.logic.Messages;
import cms.model.Model;

/**
 * Disables masking of sensitive fields in displays.
 */
public class UnmaskCommand extends Command {

    public static final String COMMAND_WORD = "unmask";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Unmasks sensitive fields in displays.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Sensitive fields are now unmasked.";

    private final String ignoredArgs;

    /**
     * Creates an UnmaskCommand with no ignored arguments.
     */
    public UnmaskCommand() {
        this.ignoredArgs = null;
    }

    /**
     * Creates an UnmaskCommand that will report the given arguments as ignored.
     *
     * @param ignoredArgs The arguments that were provided but will be ignored.
     */
    public UnmaskCommand(String ignoredArgs) {
        this.ignoredArgs = ignoredArgs;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setMasked(false);

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

        if (!(other instanceof UnmaskCommand)) {
            return false;
        }

        UnmaskCommand otherCommand = (UnmaskCommand) other;
        return Objects.equals(ignoredArgs, otherCommand.ignoredArgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ignoredArgs);
    }
}
