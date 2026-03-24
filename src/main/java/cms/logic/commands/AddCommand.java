package cms.logic.commands;

import static cms.logic.parser.CliSyntax.PREFIX_EMAIL;
import static cms.logic.parser.CliSyntax.PREFIX_GITHUBUSERNAME;
import static cms.logic.parser.CliSyntax.PREFIX_NAME;
import static cms.logic.parser.CliSyntax.PREFIX_NUSID;
import static cms.logic.parser.CliSyntax.PREFIX_PHONE;
import static cms.logic.parser.CliSyntax.PREFIX_ROLE;
import static cms.logic.parser.CliSyntax.PREFIX_SOCUSERNAME;
import static cms.logic.parser.CliSyntax.PREFIX_TAG;
import static cms.logic.parser.CliSyntax.PREFIX_TUTORIALGROUP;
import static java.util.Objects.requireNonNull;

import cms.commons.util.ToStringBuilder;
import cms.logic.Messages;
import cms.logic.commands.exceptions.CommandException;
import cms.model.Model;
import cms.model.person.Person;
import cms.model.person.exceptions.DuplicatePersonException;
import cms.model.person.exceptions.DuplicatePersonFieldException;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the system. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_NUSID + "NUSID "
            + PREFIX_ROLE + "ROLE "
            + PREFIX_SOCUSERNAME + "SOC_USERNAME "
            + PREFIX_GITHUBUSERNAME + "GITHUB_USERNAME "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_TUTORIALGROUP + "TUTORIAL_GROUP "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_NUSID + "A1234567E "
            + PREFIX_ROLE + "student "
            + PREFIX_SOCUSERNAME + "johndoe "
            + PREFIX_GITHUBUSERNAME + "johndoe "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_TUTORIALGROUP + "01 "
            + PREFIX_TAG + "struggling "
            + PREFIX_TAG + "python-experienced ";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the system";
    public static final String MESSAGE_DUPLICATE_FIELDS = "A person with the same fields already exists in the system";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        try {
            model.addPerson(toAdd);
        } catch (DuplicatePersonException | DuplicatePersonFieldException e) {
            throw new CommandException(e.getMessage());
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand otherAddCommand = (AddCommand) other;
        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
