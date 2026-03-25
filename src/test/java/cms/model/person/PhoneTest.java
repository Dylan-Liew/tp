package cms.model.person;

import static cms.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void canonicalisation_trimsSurroundingWhitespace() {
        Phone phone = new Phone(" 91234567 ");
        assertEquals("91234567", phone.value);
    }

    @Test
    public void isValidPhone_canonicalInput() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid phone numbers
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone("-")); // hyphen
        assertFalse(Phone.isValidPhone("91")); // less than 3 numbers
        assertFalse(Phone.isValidPhone("phone")); // non-numeric
        assertFalse(Phone.isValidPhone("9011p041")); // alphabets within digits
        assertFalse(Phone.isValidPhone("9312 1534")); // spaces within digits

        // valid phone numbers
        assertTrue(Phone.isValidPhone("911")); // exactly 3 numbers
        assertTrue(Phone.isValidPhone("93121534"));
        assertTrue(Phone.isValidPhone("124293842033123")); // long phone numbers
    }

    @Test
    public void equals() {
        Phone phone = new Phone("999");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("999")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("995")));
    }

    @Test
    public void constructor_acceptsValidInputs() {
        // valid phones
        assertDoesNotThrow(() -> new Phone("911"));
        assertDoesNotThrow(() -> new Phone("91234567"));
        assertDoesNotThrow(() -> new Phone("123456"));
        assertDoesNotThrow(() -> new Phone(" 91234567 ")); // trims surrounding spaces
    }

    @Test
    public void constructor_rejectsInvalidInputs() {
        // invalid phones
        assertThrows(IllegalArgumentException.class, () -> new Phone("12")); // too short
        assertThrows(IllegalArgumentException.class, () -> new Phone("9123 4567")); // space
        assertThrows(IllegalArgumentException.class, () -> new Phone("9123-4567")); // dash
        assertThrows(IllegalArgumentException.class, () -> new Phone("+6591234567")); // country code
        assertThrows(IllegalArgumentException.class, () -> new Phone("9123456a")); // letter
        assertThrows(IllegalArgumentException.class, () -> new Phone("")); // empty
    }

    @Test
    public void canonicalise_null_returnsNull() {
        assertEquals(null, Phone.canonicalise(null));
    }
}
