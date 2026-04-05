package cms.model.person;

import static cms.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_NUSID_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static cms.testutil.Assert.assertThrows;
import static cms.testutil.TypicalPersons.ALICE;
import static cms.testutil.TypicalPersons.BOB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import cms.testutil.PersonBuilder;

public class PersonTest {

    private static final String NUSID_FOR_SOC_USERNAME_MATCH_TEST = "A1234567X";
    private static final String SOCUSERNAME_MATCHING_NUSID_FOR_TEST = "A1234567x";
    private static final String SOCUSERNAME_MISMATCHING_NUSID_FOR_TEST = "a7654321j";

    @Test
    public void create_returnsCorrectSubtypeForRole() {
        Person student = new PersonBuilder().withRole("student").build();
        Person tutor = new PersonBuilder().withRole("tutor").build();

        assertTrue(student instanceof Student);
        assertTrue(tutor instanceof Tutor);
    }

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void constructor_socUsernameInNusIdFormatMismatch_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Person.MESSAGE_SOC_USERNAME_NUS_ID_MISMATCH, () ->
            new PersonBuilder()
                .withNusId(NUSID_FOR_SOC_USERNAME_MATCH_TEST)
                .withSocUsername(SOCUSERNAME_MISMATCHING_NUSID_FOR_TEST)
                .build());
    }

    @Test
    public void constructor_socUsernameInNusIdFormatMatch_success() {
        Person person = new PersonBuilder()
                .withNusId(NUSID_FOR_SOC_USERNAME_MATCH_TEST)
                .withSocUsername(SOCUSERNAME_MATCHING_NUSID_FOR_TEST)
                .build();

        assertEquals(NUSID_FOR_SOC_USERNAME_MATCH_TEST, person.getNusId().value);
        assertEquals("a1234567x", person.getSocUsername().value); // canonicalised to lowercase
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same NUS ID, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withTags(VALID_TAG_HUSBAND).withName(VALID_NAME_BOB).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different NUS ID, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withNusId(VALID_NUSID_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, NUS ID unchanged -> returns true
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertTrue(BOB.isSamePerson(editedBob));

        // NUS ID has different value, all other attributes same -> returns false
        editedBob = new PersonBuilder(BOB).withNusId("A1111111M").build();
        assertFalse(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different nusId -> returns false
        editedAlice = new PersonBuilder(ALICE).withNusId("A1234567X").build();
        assertFalse(ALICE.equals(editedAlice));

        // different soc username -> returns false
        editedAlice = new PersonBuilder(ALICE).withSocUsername("alice1").build();
        assertFalse(ALICE.equals(editedAlice));

        // different github username -> returns false
        editedAlice = new PersonBuilder(ALICE).withGithubUsername("alice-gh").build();
        assertFalse(ALICE.equals(editedAlice));

        // different role -> returns false
        editedAlice = new PersonBuilder(ALICE).withRole("tutor").build();
        assertFalse(ALICE.equals(editedAlice));

        // different tutorial group -> returns false
        editedAlice = new PersonBuilder(ALICE).withTutorialGroup("2").build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void findConflictingField() {
        // null -> returns null
        assertNull(ALICE.findConflictingField(null));

        // same email -> returns email conflict
        Person emailConflict = new PersonBuilder(BOB).withEmail(ALICE.getEmail().toString()).build();
        FieldConflict emailFieldConflict = ALICE.findConflictingField(emailConflict);
        assertEquals("email", emailFieldConflict.getFieldName());
        assertEquals(ALICE.getEmail().toString(), emailFieldConflict.getFieldValue());
        assertEquals(emailConflict, emailFieldConflict.getConflictingPerson());

        // same SOC username -> returns SOC username conflict
        Person socUsernameConflict = new PersonBuilder(BOB).withSocUsername(ALICE.getSocUsername().toString()).build();
        FieldConflict socFieldConflict = ALICE.findConflictingField(socUsernameConflict);
        assertEquals("SOC username", socFieldConflict.getFieldName());
        assertEquals(ALICE.getSocUsername().toString(), socFieldConflict.getFieldValue());
        assertEquals(socUsernameConflict, socFieldConflict.getConflictingPerson());

        // same GitHub username -> returns GitHub username conflict
        Person githubUsernameConflict = new PersonBuilder(BOB)
                .withGithubUsername(ALICE.getGithubUsername().toString())
                .build();
        FieldConflict githubFieldConflict = ALICE.findConflictingField(githubUsernameConflict);
        assertEquals("GitHub username", githubFieldConflict.getFieldName());
        assertEquals(ALICE.getGithubUsername().toString(), githubFieldConflict.getFieldValue());
        assertEquals(githubUsernameConflict, githubFieldConflict.getConflictingPerson());

        // no shared unique fields -> returns null
        assertNull(ALICE.findConflictingField(BOB));
    }

    @Test
    public void hashCodeMethod() {
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertEquals(ALICE.hashCode(), aliceCopy.hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = ALICE.getClass().getCanonicalName() + "{name=" + ALICE.getName()
            + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", nusId=" + ALICE.getNusId()
                + ", socUsername=" + ALICE.getSocUsername()
                + ", githubUsername=" + ALICE.getGithubUsername()
                + ", role=" + ALICE.getRole() + ", tutorialGroup=" + ALICE.getTutorialGroup()
                + ", tags=" + ALICE.getTags() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
