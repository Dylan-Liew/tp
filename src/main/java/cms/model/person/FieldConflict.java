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
        EMAIL("email") {
            @Override
            public String getValue(Person person) {
                return person.getEmail().toString();
            }
        },
        SOC_USERNAME("SOC username") {
            @Override
            public String getValue(Person person) {
                return person.getSocUsername().toString();
            }
        },
        GITHUB_USERNAME("GitHub username") {
            @Override
            public String getValue(Person person) {
                return person.getGithubUsername().toString();
            }
        };

        private final String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        /**
         * Returns the value of this conflict type from the given person.
         */
        public abstract String getValue(Person person);
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
        return fieldType.getValue(conflictingPerson);
    }
}
