package cms.model.person;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SocUsernameTest {
    @Test
    public void canonicalisation_trimsAndLowercases() {
        // trims and lowercases
        SocUsername s = new SocUsername("  Tan8888  ");
        assertEquals("tan8888", s.value);
    }

    @Test
    public void isValidSocUsername_canonicalInput() {
        // null input
        assertThrows(NullPointerException.class, () -> SocUsername.isValidSocUsername(null));

        // invalid canonical forms
        assertFalse(SocUsername.isValidSocUsername("tak")); // too short
        assertFalse(SocUsername.isValidSocUsername("tanahkow88")); // too long
        assertFalse(SocUsername.isValidSocUsername("-tanmow")); // starts with hyphen
        assertFalse(SocUsername.isValidSocUsername("tanmow-")); // ends with hyphen
        assertFalse(SocUsername.isValidSocUsername("tan mow")); // contains space

        // valid canonical forms
        assertTrue(SocUsername.isValidSocUsername("tan8888"));
        assertTrue(SocUsername.isValidSocUsername("u1999999")); // accepted by current regex
    }

    @Test
    public void constructor_acceptsValidInputs() {
        // valid soc usernames
        assertDoesNotThrow(() -> new SocUsername("tan8888"));
        assertDoesNotThrow(() -> new SocUsername("Tan8888")); // canonicalised to lowercase
        assertDoesNotThrow(() -> new SocUsername("tanmow99"));
        assertDoesNotThrow(() -> new SocUsername("tanga"));
        assertDoesNotThrow(() -> new SocUsername("tang8888"));
        assertDoesNotThrow(() -> new SocUsername("u1999999")); // accepted by original regex
    }

    @Test
    public void constructor_rejectsInvalidInputs() {
        // invalid soc usernames
        assertThrows(IllegalArgumentException.class, () -> new SocUsername("tak")); // too short
        assertThrows(IllegalArgumentException.class, () -> new SocUsername("tanahkow88")); // too long
        // NUS form no longer matches after lowercase canonicalisation
        assertThrows(IllegalArgumentException.class, () -> new SocUsername("a0234567b")); // invalid NUS ID format
        assertThrows(IllegalArgumentException.class, () -> new SocUsername("A0234567B")); // invalid NUS ID format
        assertThrows(IllegalArgumentException.class, () -> new SocUsername("-tanmow")); // cannot start with hyphen
        assertThrows(IllegalArgumentException.class, () -> new SocUsername("tanmow-")); // cannot end with hyphen
        assertThrows(IllegalArgumentException.class, () -> new SocUsername("tan mow")); // cannot contain spaces
        assertThrows(IllegalArgumentException.class, () -> new SocUsername("tan@kow")); // special char invalid
    }
}
