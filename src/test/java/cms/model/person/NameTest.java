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
        assertFalse(Name.isValidName("---")); // no alphabetic character
        assertFalse(Name.isValidName("peter*")); // contains unsupported characters
        assertFalse(Name.isValidName("John2 Doe")); // numbers are not allowed
        assertFalse(Name.isValidName("Ravi s/o Kumar")); // slash is not allowed
        assertFalse(Name.isValidName("a".repeat(Name.MAX_LENGTH + 1))); // too long

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabetic and spaces
        assertTrue(Name.isValidName("O'Brien")); // apostrophe
        assertTrue(Name.isValidName("Jane-Lim")); // hyphen
        assertTrue(Name.isValidName("Dr. Tan")); // period
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("a".repeat(Name.MAX_LENGTH))); // length boundary
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
        assertDoesNotThrow(() -> new Name("John D."));
        assertDoesNotThrow(() -> new Name(" John   Doe ")); // should collapse spaces
    }

    @Test
    public void constructor_rejectsInvalidInputs() {
        // invalid names
        assertThrows(IllegalArgumentException.class, () -> new Name(""));
        assertThrows(IllegalArgumentException.class, () -> new Name("---"));
        assertThrows(IllegalArgumentException.class, () -> new Name("/"));
        assertThrows(IllegalArgumentException.class, () -> new Name("Ravi s/o Kumar"));
        assertThrows(IllegalArgumentException.class, () -> new Name("John2 Doe"));
        assertThrows(IllegalArgumentException.class, () -> new Name("John@Doe"));
        assertThrows(IllegalArgumentException.class, () -> new Name("John$Doe"));
        assertThrows(IllegalArgumentException.class, () -> new Name("a".repeat(Name.MAX_LENGTH + 1)));
        assertThrows(IllegalArgumentException.class, () -> new Name(" ")); // blank
    }

    @Test
    public void canonicalisation_trimsAndCollapsesInternalSpaces() {
        Name n = new Name("  John   Doe  ");
        assertEquals("John Doe", n.fullName);
    }

    @Test
    public void canonicalise_null_returnsNull() {
        assertEquals(null, Name.canonicalise(null));
    }
}
