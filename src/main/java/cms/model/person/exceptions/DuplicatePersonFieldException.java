package cms.model.person.exceptions;

import static java.util.Objects.requireNonNull;

import cms.model.person.FieldConflict;

/**
 * Signals that the operation will result in two persons sharing a field that
 * must be globally unique.
 */
public class DuplicatePersonFieldException extends RuntimeException {

    private static final String DETAILED_MESSAGE_TEMPLATE =
            "A person with %s [%s] already exists in the system. \nConflicting person: %s (%s).";

    /**
     * Constructs a DuplicatePersonFieldException for the given field conflict.
     * @param conflict the field conflict causing the exception
     */
    public DuplicatePersonFieldException(FieldConflict conflict) {
        super(buildMessage(conflict));
    }

    /**
     * Builds the duplicate field message for the specified field conflict.
     */
    public static String buildMessage(FieldConflict conflict) {
        requireNonNull(conflict);
        return String.format(DETAILED_MESSAGE_TEMPLATE,
                conflict.getFieldName(), conflict.getFieldValue(),
                conflict.getConflictingPerson().getName(), conflict.getConflictingPerson().getNusId());
    }
}
