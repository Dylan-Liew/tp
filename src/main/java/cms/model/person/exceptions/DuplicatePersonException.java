package cms.model.person.exceptions;

import static java.util.Objects.requireNonNull;

import cms.model.person.Person;

/**
 * Signals that the operation will result in duplicate Persons (Persons are
 * considered duplicates if they have the same identity).
 */
public class DuplicatePersonException extends RuntimeException {
    private static final String DETAILED_MESSAGE_TEMPLATE =
            "A person with NUS Matric [%s] already exists in the system. \nConflicting person: %s (%s).";

    public DuplicatePersonException() {
        super("Operation would result in duplicate persons");
    }

    /**
     * Constructs a DuplicatePersonException with details of the duplicate person.
     */
    public DuplicatePersonException(Person conflictingPerson) {
        super(buildMessage(conflictingPerson));
    }

    /**
     * Builds the duplicate person message for the specified conflicting person.
     */
    public static String buildMessage(Person conflictingPerson) {
        requireNonNull(conflictingPerson);
        return String.format(DETAILED_MESSAGE_TEMPLATE,
                conflictingPerson.getNusMatric(),
                conflictingPerson.getName(),
                conflictingPerson.getNusMatric());
    }
}
