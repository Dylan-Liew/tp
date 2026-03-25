package cms.logic.commands;

import static java.util.Objects.requireNonNull;

import cms.model.Model;

/**
 * Disables masking of sensitive fields in displays.
 */
public class UnmaskCommand extends Command {

    public static final String COMMAND_WORD = "unmask";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Unmasks sensitive fields in displays.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Sensitive fields are now unmasked.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setMasked(false);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
