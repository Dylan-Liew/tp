package cms.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import cms.logic.Messages;
import cms.model.Model;

/**
 * Enables masking of sensitive fields in displays.
 */
public class MaskCommand extends Command {

    public static final String COMMAND_WORD = "mask";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Masks sensitive fields in displays.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Sensitive fields are now masked.";

    private final String ignoredArgs;

    /**
     * Creates a MaskCommand with no ignored arguments.
     */
    public MaskCommand() {
        this.ignoredArgs = null;
    }

    /**
     * Creates a MaskCommand that will report the given arguments as ignored.
     *
     * @param ignoredArgs The arguments that were provided but will be ignored.
     */
    public MaskCommand(String ignoredArgs) {
        this.ignoredArgs = ignoredArgs;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setMasked(true);

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

        if (!(other instanceof MaskCommand)) {
            return false;
        }

        MaskCommand otherCommand = (MaskCommand) other;
        return Objects.equals(ignoredArgs, otherCommand.ignoredArgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ignoredArgs);
    }
}
