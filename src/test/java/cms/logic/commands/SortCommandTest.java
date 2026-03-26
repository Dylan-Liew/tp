package cms.logic.commands;

import static cms.logic.commands.CommandTestUtil.assertCommandSuccess;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import cms.model.AddressBook;
import cms.model.Model;
import cms.model.ModelManager;
import cms.model.UserPrefs;
import cms.model.person.Person;
import cms.testutil.PersonBuilder;

/**
 * Contains integration tests for {@code SortCommand}.
 */
public class SortCommandTest {

    @Test
    public void execute_unsortedList_sortsByTutorialGroup() {
        Person tutorialGroupTen = new PersonBuilder()
                .withName("Sort Command Alpha")
                .withNusId("A1111111B")
                .withEmail("sort-command-a@test.com")
                .withSocUsername("sortcmd1")
                .withGithubUsername("sortcmd-gh-1")
                .withTutorialGroup("10")
                .build();
        Person tutorialGroupTwo = new PersonBuilder()
                .withName("Sort Command Beta")
                .withNusId("A1111112C")
                .withEmail("sort-command-b@test.com")
                .withSocUsername("sortcmd2")
                .withGithubUsername("sortcmd-gh-2")
                .withTutorialGroup("02")
                .build();

        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(tutorialGroupTen);
        addressBook.addPerson(tutorialGroupTwo);

        Model model = new ModelManager(addressBook, new UserPrefs());
        Model expectedModel = new ModelManager(new AddressBook(addressBook), new UserPrefs());
        expectedModel.sortPersonsByTutorialGroup();

        assertEquals(Arrays.asList(tutorialGroupTen, tutorialGroupTwo), model.getFilteredPersonList());
        assertCommandSuccess(new SortCommand(), model, SortCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(Arrays.asList(tutorialGroupTwo, tutorialGroupTen), model.getFilteredPersonList());
    }
}
