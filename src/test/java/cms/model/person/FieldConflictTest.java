package cms.model.person;

import static cms.testutil.Assert.assertThrows;
import static cms.testutil.TypicalPersons.ALICE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cms.testutil.PersonBuilder;

public class FieldConflictTest {

    @Test
    public void constructor_nullFieldType_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FieldConflict(null, ALICE));
    }

    @Test
    public void constructor_nullConflictingPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FieldConflict(FieldConflict.Type.EMAIL, null));
    }

    @Test
    public void emailConflict_accessors_returnExpectedValues() {
        Person conflictingPerson = new PersonBuilder(ALICE).withEmail("other@example.com").build();
        FieldConflict conflict = new FieldConflict(FieldConflict.Type.EMAIL, conflictingPerson);

        assertEquals("email", conflict.getFieldName());
        assertEquals(FieldConflict.Type.EMAIL, conflict.getFieldType());
        assertEquals(conflictingPerson, conflict.getConflictingPerson());
        assertEquals(conflictingPerson.getEmail().toString(), conflict.getFieldValue());
    }

    @Test
    public void socUsernameConflict_accessors_returnExpectedValues() {
        Person conflictingPerson = new PersonBuilder(ALICE).withSocUsername("alice1").build();
        FieldConflict conflict = new FieldConflict(FieldConflict.Type.SOC_USERNAME, conflictingPerson);

        assertEquals("SOC username", conflict.getFieldName());
        assertEquals(FieldConflict.Type.SOC_USERNAME, conflict.getFieldType());
        assertEquals(conflictingPerson, conflict.getConflictingPerson());
        assertEquals(conflictingPerson.getSocUsername().toString(), conflict.getFieldValue());
    }

    @Test
    public void githubUsernameConflict_accessors_returnExpectedValues() {
        Person conflictingPerson = new PersonBuilder(ALICE).withGithubUsername("alice-gh").build();
        FieldConflict conflict = new FieldConflict(FieldConflict.Type.GITHUB_USERNAME, conflictingPerson);

        assertEquals("GitHub username", conflict.getFieldName());
        assertEquals(FieldConflict.Type.GITHUB_USERNAME, conflict.getFieldType());
        assertEquals(conflictingPerson, conflict.getConflictingPerson());
        assertEquals(conflictingPerson.getGithubUsername().toString(), conflict.getFieldValue());
    }

}
