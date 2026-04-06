package cms.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import cms.model.person.Person;
import cms.testutil.PersonBuilder;
import cms.testutil.TypicalPersons;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainWindowTest {

    @Test
    public void findMatchingPerson_nullTargetPerson_returnsNull() {
        ObservableList<Person> persons = FXCollections.observableArrayList(new PersonBuilder().build());

        assertNull(MainWindow.findMatchingPerson(persons, null));
    }

    @Test
    public void findMatchingPerson_matchingIdentity_returnsPersonFromList() {
        Person originalPerson = new PersonBuilder()
                .withName("Alice Tan")
                .withNusMatric("A0234567X")
                .withSocUsername("alicetan")
                .withGithubUsername("alice-tan")
                .withEmail("alice@u.nus.edu")
                .withPhone("91234567")
                .withTutorialGroup("1")
                .build();
        Person editedVersion = new PersonBuilder(originalPerson)
                .withPhone("98765432")
                .withTags("leader")
                .build();
        ObservableList<Person> persons = FXCollections.observableArrayList(editedVersion);

        assertEquals(editedVersion, MainWindow.findMatchingPerson(persons, originalPerson));
    }

    @Test
    public void findMatchingPerson_missingIdentity_returnsNull() {
        ObservableList<Person> persons = FXCollections.observableArrayList(TypicalPersons.BENSON);
        Person previouslySelectedPerson = new PersonBuilder()
                .withName("Alice Tan")
                .withNusMatric("A0234567X")
                .withSocUsername("alicetan")
                .withGithubUsername("alice-tan")
                .withEmail("alice@u.nus.edu")
                .withPhone("91234567")
                .withTutorialGroup("1")
                .build();

        assertNull(MainWindow.findMatchingPerson(persons, previouslySelectedPerson));
    }
}
