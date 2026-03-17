package cms.model.person;

/**
 * Represents a field conflict between two persons.
 */
public class FieldConflict {
    private final String fieldName;
    private final String fieldValue;

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