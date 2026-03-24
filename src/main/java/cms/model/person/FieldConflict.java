package cms.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a field conflict between two persons.
 */
public class FieldConflict {
    /**
     * The unique field types that can conflict between persons.
     */
    public enum Type {
        EMAIL("email"),
        SOC_USERNAME("SOC username"),
        GITHUB_USERNAME("GitHub username");

        private final String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final Type fieldType;
    private final Person conflictingPerson;

    /**
     * Constructs a FieldConflict with the specified field type and conflicting person.
     * @param fieldType the type of the conflicting unique field
     * @param conflictingPerson the person that already owns the conflicting field
     */
    public FieldConflict(Type fieldType, Person conflictingPerson) {
        this.fieldType = requireNonNull(fieldType);
        this.conflictingPerson = requireNonNull(conflictingPerson);
    }

    public String getFieldName() {
        return fieldType.getDisplayName();
    }

    public Type getFieldType() {
        return fieldType;
    }

    public Person getConflictingPerson() {
        return conflictingPerson;
    }

    /**
     * Returns the conflicting field value from the conflicting person.
     */
    public String getFieldValue() {
        return switch (fieldType) {
        case EMAIL -> conflictingPerson.getEmail().toString();
        case SOC_USERNAME -> conflictingPerson.getSocUsername().toString();
        case GITHUB_USERNAME -> conflictingPerson.getGithubUsername().toString();
        };
    }
}
