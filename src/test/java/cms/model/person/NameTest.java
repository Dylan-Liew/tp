package cms.model.person;

import static cms.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void isValidName_canonicalInput() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains non-alphanumeric characters
        assertFalse(Name.isValidName("O'Brien")); // apostrophe not allowed
        assertFalse(Name.isValidName("Jane-Lim")); // hyphen not allowed
        assertFalse(Name.isValidName("Dr. Tan")); // period not allowed

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Junior")); // long names
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }

    @Test
    public void constructor_acceptsValidInputs() {
        // valid names
        assertDoesNotThrow(() -> new Name("John Doe"));
        assertDoesNotThrow(() -> new Name("John2 Doe"));
        assertDoesNotThrow(() -> new Name(" John   Doe ")); // should collapse spaces
    }

    @Test
    public void constructor_rejectsInvalidInputs() {
        // invalid names
        assertThrows(IllegalArgumentException.class, () -> new Name(""));
        assertThrows(IllegalArgumentException.class, () -> new Name("O'Brien"));
        assertThrows(IllegalArgumentException.class, () -> new Name("Jane-Lim"));
        assertThrows(IllegalArgumentException.class, () -> new Name("Dr. Tan"));
        assertThrows(IllegalArgumentException.class, () -> new Name("John@Doe"));
        assertThrows(IllegalArgumentException.class, () -> new Name("John$Doe"));
        assertThrows(IllegalArgumentException.class, () -> new Name(" ")); // blank
    }

    @Test
    public void canonicalisation_trimsAndCollapsesInternalSpaces() {
        Name n = new Name("  John   Doe  ");
        assertEquals("John Doe", n.fullName);
    }
}
