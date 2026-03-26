package cms.model.person;

import static cms.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static cms.testutil.Assert.assertThrows;
import static cms.testutil.TypicalPersons.ALICE;
import static cms.testutil.TypicalPersons.BOB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import cms.model.person.exceptions.DuplicatePersonException;
import cms.model.person.exceptions.DuplicatePersonFieldException;
import cms.model.person.exceptions.PersonNotFoundException;
import cms.testutil.PersonBuilder;

public class UniquePersonListTest {

    private final UniquePersonList uniquePersonList = new UniquePersonList();

    @Test
    public void contains_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniquePersonList.contains(null));
    }

    @Test
    public void contains_personNotInList_returnsFalse() {
        assertFalse(uniquePersonList.contains(ALICE));
    }

    @Test
    public void contains_personInList_returnsTrue() {
        uniquePersonList.add(ALICE);
        assertTrue(uniquePersonList.contains(ALICE));
    }

    @Test
    public void contains_personWithSameIdentityFieldsInList_returnsTrue() {
        uniquePersonList.add(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(uniquePersonList.contains(editedAlice));
    }

    @Test
    public void containsFieldConflict_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniquePersonList.containsFieldConflict(null));
    }

    @Test
    public void containsFieldConflict_personNotInList_returnsFalse() {
        assertFalse(uniquePersonList.containsFieldConflict(ALICE));
    }

    @Test
    public void containsFieldConflict_personWithConflictingFieldInList_returnsTrue() {
        uniquePersonList.add(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withNusId("A1234567C").build();
        assertTrue(uniquePersonList.containsFieldConflict(editedAlice));
    }

    @Test
    public void add_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniquePersonList.add(null));
    }

    @Test
    public void add_duplicatePerson_throwsDuplicatePersonException() {
        uniquePersonList.add(ALICE);
        assertThrows(DuplicatePersonException.class, () -> uniquePersonList.add(ALICE));
    }

    @Test
    public void add_personsRemainInInsertionOrderUntilExplicitSort() {
        Person tutorialGroupTen = createSortTestPerson("Alice Sort", "A1234567B", "alice-sort@test.com",
                "asort1", "alice-sort-gh", "10");
        Person tutorialGroupTwo = createSortTestPerson("Bob Sort", "A1234568C", "bob-sort@test.com",
                "bsort1", "bob-sort-gh", "02");

        uniquePersonList.add(tutorialGroupTen);
        uniquePersonList.add(tutorialGroupTwo);

        assertEquals(Arrays.asList(tutorialGroupTen, tutorialGroupTwo),
                uniquePersonList.asUnmodifiableObservableList());
    }

    @Test
    public void add_personWithConflictingEmail_throwsDuplicatePersonFieldException() {
        uniquePersonList.add(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withNusId("A1234567C").build();
        FieldConflict conflict = new FieldConflict(FieldConflict.Type.EMAIL, ALICE);
        String expectedErrorMessage = DuplicatePersonFieldException.buildMessage(conflict);

        assertThrows(DuplicatePersonFieldException.class,
            expectedErrorMessage, () -> uniquePersonList.add(editedAlice));
    }

    @Test
    public void setPerson_nullTargetPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniquePersonList.setPerson(null, ALICE));
    }

    @Test
    public void setPerson_nullEditedPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniquePersonList.setPerson(ALICE, null));
    }

    @Test
    public void setPerson_targetPersonNotInList_throwsPersonNotFoundException() {
        assertThrows(PersonNotFoundException.class, () -> uniquePersonList.setPerson(ALICE, ALICE));
    }

    @Test
    public void setPerson_editedPersonIsSamePerson_success() {
        uniquePersonList.add(ALICE);
        uniquePersonList.setPerson(ALICE, ALICE);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        expectedUniquePersonList.add(ALICE);
        assertEquals(expectedUniquePersonList, uniquePersonList);
    }

    @Test
    public void setPerson_editedPersonHasSameIdentity_success() {
        uniquePersonList.add(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        uniquePersonList.setPerson(ALICE, editedAlice);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        expectedUniquePersonList.add(editedAlice);
        assertEquals(expectedUniquePersonList, uniquePersonList);
    }

    @Test
    public void setPerson_editedPersonHasDifferentIdentity_success() {
        uniquePersonList.add(ALICE);
        uniquePersonList.setPerson(ALICE, BOB);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        expectedUniquePersonList.add(BOB);
        assertEquals(expectedUniquePersonList, uniquePersonList);
    }

    @Test
    public void setPerson_editedPersonDoesNotTriggerAutomaticSort() {
        Person tutorialGroupTen = createSortTestPerson("Set Sort Alpha", "A1234571F", "set-sort-a@test.com",
                "ssort1", "set-sort-a-gh", "10");
        Person tutorialGroupTwo = createSortTestPerson("Set Sort Beta", "A1234572G", "set-sort-b@test.com",
                "ssort2", "set-sort-b-gh", "02");
        Person editedTutorialGroupTen = new PersonBuilder(tutorialGroupTen)
                .withName("Set Sort Alpha Edited")
                .build();

        uniquePersonList.add(tutorialGroupTen);
        uniquePersonList.add(tutorialGroupTwo);
        uniquePersonList.setPerson(tutorialGroupTen, editedTutorialGroupTen);

        assertEquals(Arrays.asList(editedTutorialGroupTen, tutorialGroupTwo),
                uniquePersonList.asUnmodifiableObservableList());
    }

    @Test
    public void setPerson_editedPersonHasNonUniqueIdentity_throwsDuplicatePersonException() {
        uniquePersonList.add(ALICE);
        uniquePersonList.add(BOB);
        assertThrows(DuplicatePersonException.class, () -> uniquePersonList.setPerson(ALICE, BOB));
    }

    @Test
    public void setPerson_editedPersonHasConflictingSocUsername_throwsDuplicatePersonFieldException() {
        uniquePersonList.add(ALICE);
        uniquePersonList.add(BOB);

        Person editedAlice = new PersonBuilder(ALICE)
            .withSocUsername(BOB.getSocUsername().toString())
            .build();
        FieldConflict conflict = new FieldConflict(FieldConflict.Type.SOC_USERNAME, BOB);
        String expectedErrorMessage = DuplicatePersonFieldException.buildMessage(conflict);

        assertThrows(DuplicatePersonFieldException.class,
            expectedErrorMessage, () -> uniquePersonList.setPerson(ALICE, editedAlice));
    }

    @Test
    public void setPerson_editedPersonHasConflictingGithubUsername_throwsDuplicatePersonFieldException() {
        uniquePersonList.add(ALICE);
        uniquePersonList.add(BOB);

        Person editedAlice = new PersonBuilder(ALICE)
            .withGithubUsername(BOB.getGithubUsername().toString())
            .build();
        FieldConflict conflict = new FieldConflict(FieldConflict.Type.GITHUB_USERNAME, BOB);
        String expectedErrorMessage = DuplicatePersonFieldException.buildMessage(conflict);

        assertThrows(DuplicatePersonFieldException.class,
            expectedErrorMessage, () -> uniquePersonList.setPerson(ALICE, editedAlice));
    }

    @Test
    public void remove_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniquePersonList.remove(null));
    }

    @Test
    public void remove_personDoesNotExist_throwsPersonNotFoundException() {
        assertThrows(PersonNotFoundException.class, () -> uniquePersonList.remove(ALICE));
    }

    @Test
    public void remove_existingPerson_removesPerson() {
        uniquePersonList.add(ALICE);
        uniquePersonList.remove(ALICE);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        assertEquals(expectedUniquePersonList, uniquePersonList);
    }

    @Test
    public void setPersons_nullUniquePersonList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniquePersonList.setPersons((UniquePersonList) null));
    }

    @Test
    public void setPersons_uniquePersonList_replacesOwnListWithProvidedUniquePersonList() {
        uniquePersonList.add(ALICE);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        expectedUniquePersonList.add(BOB);
        uniquePersonList.setPersons(expectedUniquePersonList);
        assertEquals(expectedUniquePersonList, uniquePersonList);
    }

    @Test
    public void setPersons_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniquePersonList.setPersons((List<Person>) null));
    }

    @Test
    public void setPersons_list_replacesOwnListWithProvidedList() {
        uniquePersonList.add(ALICE);
        List<Person> personList = Collections.singletonList(BOB);
        uniquePersonList.setPersons(personList);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        expectedUniquePersonList.add(BOB);
        assertEquals(expectedUniquePersonList, uniquePersonList);
    }

    @Test
    public void setPersons_listWithDuplicatePersons_throwsDuplicatePersonException() {
        List<Person> listWithDuplicatePersons = Arrays.asList(ALICE, ALICE);
        assertThrows(DuplicatePersonException.class, () -> uniquePersonList.setPersons(listWithDuplicatePersons));
    }

    @Test
    public void setPersons_listWithDuplicateFields_throwsDuplicatePersonFieldException() {
        Person editedAlice = new PersonBuilder(ALICE).withNusId("A1234567C").build();
        List<Person> listWithDuplicateFieldPersons = Arrays.asList(ALICE, editedAlice);
        FieldConflict conflict = new FieldConflict(FieldConflict.Type.EMAIL, editedAlice);
        String expectedErrorMessage = DuplicatePersonFieldException.buildMessage(conflict);

        assertThrows(DuplicatePersonFieldException.class,
            expectedErrorMessage, () -> uniquePersonList.setPersons(listWithDuplicateFieldPersons));
    }

    @Test
    public void setPersons_listPreservesProvidedOrderUntilExplicitSort() {
        Person tutorialGroupTen = createSortTestPerson("List Sort Alpha", "A1234573H", "list-sort-a@test.com",
                "lsort1", "list-sort-a-gh", "10");
        Person tutorialGroupTwo = createSortTestPerson("List Sort Beta", "A1234574I", "list-sort-b@test.com",
                "lsort2", "list-sort-b-gh", "02");

        uniquePersonList.setPersons(Arrays.asList(tutorialGroupTen, tutorialGroupTwo));

        assertEquals(Arrays.asList(tutorialGroupTen, tutorialGroupTwo),
                uniquePersonList.asUnmodifiableObservableList());
    }

    @Test
    public void sortByTutorialGroup_validTutorialGroups_sortsByGroupNumber() {
        Person tutorialGroupTen = createSortTestPerson("Sort Wrapper Alpha", "A1234575J", "sort-wrapper-a@test.com",
                "swrap1", "sort-wrapper-a-gh", "10");
        Person tutorialGroupTwo = createSortTestPerson("Sort Wrapper Beta", "A1234576K", "sort-wrapper-b@test.com",
                "swrap2", "sort-wrapper-b-gh", "02");

        uniquePersonList.add(tutorialGroupTen);
        uniquePersonList.add(tutorialGroupTwo);
        uniquePersonList.sortByTutorialGroup();

        assertEquals(Arrays.asList(tutorialGroupTwo, tutorialGroupTen),
                uniquePersonList.asUnmodifiableObservableList());
    }

    @Test
    public void sortByTutorialGroup_sameTutorialGroups_preservesRelativeOrder() {
        Person firstTutorialGroupTwo = createSortTestPerson("Sort Equal Alpha", "A1234577L", "sort-equal-a@test.com",
                "sequal1", "sort-equal-a-gh", "02");
        Person secondTutorialGroupTwo = createSortTestPerson("Sort Equal Beta", "A1234578M", "sort-equal-b@test.com",
                "sequal2", "sort-equal-b-gh", "2");

        uniquePersonList.add(firstTutorialGroupTwo);
        uniquePersonList.add(secondTutorialGroupTwo);
        uniquePersonList.sortByTutorialGroup();

        assertEquals(Arrays.asList(firstTutorialGroupTwo, secondTutorialGroupTwo),
                uniquePersonList.asUnmodifiableObservableList());
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, ()
                -> uniquePersonList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void toStringMethod() {
        assertEquals(uniquePersonList.asUnmodifiableObservableList().toString(), uniquePersonList.toString());
    }

    private static Person createSortTestPerson(String name, String nusId, String email,
                                               String socUsername, String githubUsername,
                                               String tutorialGroup) {
        return new PersonBuilder()
                .withName(name)
                .withNusId(nusId)
                .withEmail(email)
                .withSocUsername(socUsername)
                .withGithubUsername(githubUsername)
                .withTutorialGroup(tutorialGroup)
                .build();
    }
}
