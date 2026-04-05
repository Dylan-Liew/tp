package cms.model.person;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class NusMatricTest {
    @Test
    public void canonicalisation_trimsAndUppercases() {
        // trims and uppercases
        NusMatric n = new NusMatric("  a0234567x  ");
        assertEquals("A0234567X", n.value);
    }

    @Test
    public void isValidNusMatric_canonicalInput() {
        // null input
        assertFalse(NusMatric.isValidNusMatric(null));

        // invalid canonical forms
        assertFalse(NusMatric.isValidNusMatric("A0234567")); // missing last letter
        assertFalse(NusMatric.isValidNusMatric("A0234567BB")); // extra trailing letter
        assertFalse(NusMatric.isValidNusMatric("B0234567B")); // invalid prefix
        assertFalse(NusMatric.isValidNusMatric("A0234567$")); // invalid trailing character
        assertFalse(NusMatric.isValidNusMatric("A0234567B")); // wrong checksum
        assertFalse(NusMatric.isValidNusMatric("U0234567B")); // legacy U format should have 6 digits only
        assertFalse(NusMatric.isValidNusMatric("U023456X")); // wrong checksum
        assertFalse(NusMatric.isValidNusMatric("U012345A")); // wrong checksum for canonical U-prefix form

        // valid canonical forms
        assertTrue(NusMatric.isValidNusMatric("A0234567X")); // standard A-prefix form with valid checksum
        assertTrue(NusMatric.isValidNusMatric("U023456W")); // legacy U-prefix form with valid checksum
        assertTrue(NusMatric.isValidNusMatric("U0906931")); // old U-prefix NUSNET form accepted via canonicalisation
    }

    @Test
    public void isValidNusMatric_nonCanonicalInput() {
        assertTrue(NusMatric.isValidNusMatric("  a0234567x  "));
    }

    @Test
    public void constructor_acceptsValidInputs() {
        // valid nus ids
        assertDoesNotThrow(() -> new NusMatric("A0234567X")); // exact canonical form
        assertDoesNotThrow(() -> new NusMatric("a0234567x")); // lowercase accepted via canonicalisation
        assertDoesNotThrow(() -> new NusMatric("a0234567X")); // mixed case accepted via canonicalisation
        assertDoesNotThrow(() -> new NusMatric("A0234567x")); // mixed case accepted via canonicalisation
        assertDoesNotThrow(() -> new NusMatric("U023456W")); // legacy U-prefix form
        assertDoesNotThrow(() -> new NusMatric("u023456w")); // mixed case U-prefix accepted
        assertDoesNotThrow(() -> new NusMatric("U0906931")); // old U-prefix NUSNET form
    }

    @Test
    public void constructor_legacyUNusnetInput_canonicalisesToMatricForm() {
        NusMatric n = new NusMatric("U0906931");
        assertEquals("U096931E", n.value);
    }

    @Test
    public void constructor_rejectsInvalidInputs() {
        // invalid nus ids
        assertThrows(IllegalArgumentException.class, () -> new NusMatric("A0234567")); // missing last letter
        assertThrows(IllegalArgumentException.class, () -> new NusMatric("A0234567BB")); // extra trailing letter
        assertThrows(IllegalArgumentException.class, () -> new NusMatric("B0234567B")); // must start with A or U
        assertThrows(IllegalArgumentException.class, () -> new NusMatric("A0234567  B")); // internal spaces not allowed
        assertThrows(IllegalArgumentException.class, () -> new NusMatric("A0234567$")); // invalid trailing character
        assertThrows(IllegalArgumentException.class, () -> new NusMatric("A0234567B")); // wrong checksum
        // legacy U format should have 6 digits
        assertThrows(IllegalArgumentException.class, () -> new NusMatric("U0234567B"));
    }
}
