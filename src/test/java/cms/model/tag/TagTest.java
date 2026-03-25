package cms.model.tag;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void canonicalisation_trimsAndLowercases() {
        // trims and lowercases
        Tag tag = new Tag("  Struggling  ");
        assertEquals("struggling", tag.tagName);
    }

    @Test
    public void isValidTagName_canonicalInput() {
        // null input
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));

        // invalid canonical forms
        assertFalse(Tag.isValidTagName("struggling struggling")); // spaces not allowed
        assertFalse(Tag.isValidTagName("-python")); // leading hyphen invalid
        assertFalse(Tag.isValidTagName("python-")); // trailing hyphen invalid
        assertFalse(Tag.isValidTagName("python--experienced")); // double hyphen invalid
        assertFalse(Tag.isValidTagName("struggling!")); // punctuation not allowed
        assertFalse(Tag.isValidTagName("")); // empty not allowed

        // valid canonical forms
        assertTrue(Tag.isValidTagName("struggling")); // plain alphanumeric
        assertTrue(Tag.isValidTagName("python-experienced")); // hyphenated alphanumeric segments
    }

    @Test
    public void constructor_acceptsValidInputs() {
        // valid tags
        assertDoesNotThrow(() -> new Tag("struggling")); // lowercase alphabetic
        assertDoesNotThrow(() -> new Tag("pythonexperienced")); // long alphabetic word
        assertDoesNotThrow(() -> new Tag("python-experienced")); // hyphenated words now allowed
        assertDoesNotThrow(() -> new Tag("needshelp")); // another plain alphanumeric tag
        assertDoesNotThrow(() -> new Tag("needs-help")); // mixed words with single hyphen
        assertDoesNotThrow(() -> new Tag("a123")); // mixed letters and digits
        // long tags are allowed by original validation
        assertDoesNotThrow(() -> new Tag("a123456789012345678901234567890")); // no explicit max length constraint
    }

    @Test
    public void constructor_rejectsInvalidInputs() {
        // null input
        assertThrows(NullPointerException.class, () -> new Tag(null));

        // invalid tags
        assertThrows(IllegalArgumentException.class, () -> new Tag("struggling struggling")); // spaces not allowed
        assertThrows(IllegalArgumentException.class, () -> new Tag("-python")); // leading hyphen not allowed
        assertThrows(IllegalArgumentException.class, () -> new Tag("python-")); // trailing hyphen not allowed
        assertThrows(IllegalArgumentException.class, () -> new Tag("python--experienced")); // double hyphen invalid
        assertThrows(IllegalArgumentException.class, () -> new Tag("struggling!")); // punctuation not allowed
        assertThrows(IllegalArgumentException.class, () -> new Tag("")); // empty tag not allowed
    }

    @Test
    public void canonicalise_null_returnsNull() {
        assertEquals(null, Tag.canonicalise(null));
    }

}
