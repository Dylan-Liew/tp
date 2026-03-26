package cms.model;

import static cms.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class UserPrefsTest {

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        UserPrefs userPref = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPref.setGuiSettings(null));
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        UserPrefs userPrefs = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPrefs.setAddressBookFilePath(null));
    }

    @Test
    public void setMasked_updatesMaskPreference() {
        UserPrefs userPrefs = new UserPrefs();
        assertFalse(userPrefs.isMasked());

        userPrefs.setMasked(true);
        assertTrue(userPrefs.isMasked());
    }

    @Test
    public void equalsAndHashCode_withDifferentMaskValue_notEqual() {
        UserPrefs unmasked = new UserPrefs();
        UserPrefs masked = new UserPrefs();
        masked.setMasked(true);

        assertFalse(unmasked.equals(masked));
        assertFalse(masked.equals(unmasked));
        assertFalse(unmasked.hashCode() == masked.hashCode());
    }

    @Test
    public void toString_withMaskedTrue_containsMaskingEnabled() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setMasked(true);
        assertTrue(userPrefs.toString().contains("Masking enabled : true"));
    }

}
