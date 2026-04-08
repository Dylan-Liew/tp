package cms.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import cms.model.AddressBook;
import cms.model.Model;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";

    public static final String MESSAGE_USAGE = COMMAND_WORD
        + ": Clears all entries from the Course Management System after confirmation.\n"
        + "Format: " + COMMAND_WORD + " or " + COMMAND_WORD + " confirm/yes\n"
        + "Example: " + COMMAND_WORD + " confirm/yes";

    public static final String MESSAGE_SUCCESS = "Course Management System has been cleared!";
    public static final String MESSAGE_CONFIRMATION_REQUIRED = "This will delete all records from CMS. "
            + "Type clear confirm/yes to proceed.";

    private final boolean isConfirmed;

    /**
     * Creates a ClearCommand.
     */
    public ClearCommand(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        if (!isConfirmed) {
            return new CommandResult(MESSAGE_CONFIRMATION_REQUIRED);
        }

        model.setAddressBook(new AddressBook());
        return new CommandResult(MESSAGE_SUCCESS);
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
        return isConfirmed == otherCommand.isConfirmed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isConfirmed);
    }
}
