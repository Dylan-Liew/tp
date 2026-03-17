package cms.model.person.exceptions;

import cms.model.person.FieldConflict;

/**
 * Signals that the operation will result in two persons sharing a field that
 * must be globally unique.
 */
public class DuplicatePersonFieldException extends RuntimeException {

    public DuplicatePersonFieldException(FieldConflict conflict) {
        super(String.format("A person with %s [%s] already exists in the system.", conflict.getFieldName(),
                conflict.getFieldValue()));
    }
}
