package cms.model.person;

/**
 * Represents a field conflict between two persons.
 */
public class FieldConflict {
    private final String fieldName;
    private final String fieldValue;

    /**
     * Constructs a FieldConflict with the specified field name and value.
     * @param fieldName the name of the conflicting field (e.g. "email", "SOC username", "GitHub username")
     * @param fieldValue the value of the conflicting field that appears in both persons
     */
    public FieldConflict(String fieldName, String fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }
}
