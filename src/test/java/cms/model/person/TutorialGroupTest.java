package cms.model.person;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TutorialGroupTest {
    @Test
    public void canonicalisation_trimsAndUppercases() {
        // trims and uppercases
        TutorialGroup t = new TutorialGroup("  t01  ");
        assertEquals("T01", t.value);
    }

    @Test
    public void isValidTutorialGroup_canonicalInput() {
        // null input
        assertThrows(NullPointerException.class, () -> TutorialGroup.isValidTutorialGroup(null));

        // invalid canonical forms
        assertFalse(TutorialGroup.isValidTutorialGroup("T1")); // too short
        assertFalse(TutorialGroup.isValidTutorialGroup("T123")); // too long
        assertFalse(TutorialGroup.isValidTutorialGroup("TAA")); // not digits
        assertFalse(TutorialGroup.isValidTutorialGroup("01T")); // wrong order

        // valid canonical forms
        assertTrue(TutorialGroup.isValidTutorialGroup("T01"));
        assertTrue(TutorialGroup.isValidTutorialGroup("T12"));
    }

    @Test
    public void constructor_acceptsValidInputs() {
        // valid tutorial groups
        assertDoesNotThrow(() -> new TutorialGroup("T01"));
        assertDoesNotThrow(() -> new TutorialGroup("t12")); // auto-uppercase
        assertDoesNotThrow(() -> new TutorialGroup("t01 ")); // trims trailing spaces
    }

    @Test
    public void constructor_rejectsInvalidInputs() {
        // invalid tutorial groups
        assertThrows(IllegalArgumentException.class, () -> new TutorialGroup("T1"));
        assertThrows(IllegalArgumentException.class, () -> new TutorialGroup("T123"));
        assertThrows(IllegalArgumentException.class, () -> new TutorialGroup("TAA"));
        assertThrows(IllegalArgumentException.class, () -> new TutorialGroup("T 1"));
        assertThrows(IllegalArgumentException.class, () -> new TutorialGroup("01T"));
    }
}
