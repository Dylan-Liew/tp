package cms.model.person.exceptions;

/**
 * Signals that the operation will result in duplicate Persons (Persons are
 * considered duplicates if they have the same identity).
 */
public class DuplicatePersonException extends RuntimeException {
    public DuplicatePersonException() {
        super("Operation would result in duplicate persons");
    }

    public DuplicatePersonException(String name) {
        super("Operation would result in duplicate persons: " + name);
    }
}
