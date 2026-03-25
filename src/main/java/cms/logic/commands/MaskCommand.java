package cms.logic.commands;

import static java.util.Objects.requireNonNull;

import cms.model.Model;

/**
 * Enables masking of sensitive fields in displays.
 */
public class MaskCommand extends Command {

    public static final String COMMAND_WORD = "mask";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Masks sensitive fields in displays.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Sensitive fields are now masked.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setMasked(true);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
