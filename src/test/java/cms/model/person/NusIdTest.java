package cms.model.person;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class NusIdTest {
    @Test
    public void canonicalisation_trimsAndUppercases() {
        // trims and uppercases
        NusId n = new NusId("  a0234567b  ");
        assertEquals("A0234567B", n.value);
    }

    @Test
    public void isValidNusId_canonicalInput() {
        // null input
        assertThrows(NullPointerException.class, () -> NusId.isValidNusId(null));

        // invalid canonical forms
        assertFalse(NusId.isValidNusId("A0234567")); // missing last letter
        assertFalse(NusId.isValidNusId("A0234567BB")); // extra trailing letter
        assertFalse(NusId.isValidNusId("B0234567B")); // invalid prefix
        assertFalse(NusId.isValidNusId("A0234567$")); // invalid trailing character

        // valid canonical forms
        assertTrue(NusId.isValidNusId("A0234567B")); // standard A-prefix form
        assertTrue(NusId.isValidNusId("U0234567B")); // legacy U-prefix form
    }

    @Test
    public void constructor_acceptsValidInputs() {
        // valid nus ids
        assertDoesNotThrow(() -> new NusId("A0234567B")); // exact canonical form
        assertDoesNotThrow(() -> new NusId("a0234567b")); // lowercase accepted via canonicalisation
        assertDoesNotThrow(() -> new NusId("a0234567B")); // mixed case accepted via canonicalisation
        assertDoesNotThrow(() -> new NusId("A0234567b")); // mixed case accepted via canonicalisation
        assertDoesNotThrow(() -> new NusId("U0234567B")); // legacy U-prefix form
        assertDoesNotThrow(() -> new NusId("u0234567b")); // mixed case U-prefix accepted
    }

    @Test
    public void constructor_rejectsInvalidInputs() {
        // invalid nus ids
        assertThrows(IllegalArgumentException.class, () -> new NusId("A0234567")); // missing last letter
        assertThrows(IllegalArgumentException.class, () -> new NusId("A0234567BB")); // extra trailing letter
        assertThrows(IllegalArgumentException.class, () -> new NusId("B0234567B")); // must start with A or U
        assertThrows(IllegalArgumentException.class, () -> new NusId("A0234567  B")); // internal spaces not allowed
        assertThrows(IllegalArgumentException.class, () -> new NusId("A0234567$")); // invalid trailing character
    }
}
