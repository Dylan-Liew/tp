package cms.logic.commands;

import static cms.logic.commands.CommandTestUtil.assertCommandSuccess;
import static cms.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void execute_nullModel_throwsNullPointerException() {
        SortCommand sortCommand = new SortCommand(SortCommand.SORT_BY_NAME);
        assertThrows(NullPointerException.class, () -> sortCommand.execute(null));
    }

    @Test
    public void execute_sortByTutorialGroup_sortsByTutorialGroup() {
        Person tutorialGroupTen = new PersonBuilder()
                .withName("Sort Command Alpha")
                .withNusId("A1111111M")
                .withEmail("sort-command-a@test.com")
                .withSocUsername("sortcmd1")
                .withGithubUsername("sortcmd-gh-1")
                .withTutorialGroup("10")
                .build();
        Person tutorialGroupTwo = new PersonBuilder()
                .withName("Sort Command Beta")
                .withNusId("A1111112L")
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
        assertCommandSuccess(new SortCommand(SortCommand.SORT_BY_TUTORIAL_GROUP), model,
                SortCommand.MESSAGE_SUCCESS_TUTORIAL_GROUP, expectedModel);
        assertEquals(Arrays.asList(tutorialGroupTwo, tutorialGroupTen), model.getFilteredPersonList());
    }

    @Test
    public void execute_sortByName_sortsByName() {
        Person zed = new PersonBuilder()
                .withName("Zed Sort")
                .withNusId("A1111113J")
                .withEmail("sort-command-c@test.com")
                .withSocUsername("sortcmd3")
                .withGithubUsername("sortcmd-gh-3")
                .withTutorialGroup("03")
                .build();
        Person amy = new PersonBuilder()
                .withName("Amy Sort")
                .withNusId("A1111114H")
                .withEmail("sort-command-d@test.com")
                .withSocUsername("sortcmd4")
                .withGithubUsername("sortcmd-gh-4")
                .withTutorialGroup("04")
                .build();

        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(zed);
        addressBook.addPerson(amy);

        Model model = new ModelManager(addressBook, new UserPrefs());
        Model expectedModel = new ModelManager(new AddressBook(addressBook), new UserPrefs());
        expectedModel.sortPersonsByName();

        assertEquals(Arrays.asList(zed, amy), model.getFilteredPersonList());
        assertCommandSuccess(new SortCommand(SortCommand.SORT_BY_NAME), model,
                SortCommand.MESSAGE_SUCCESS_NAME, expectedModel);
        assertEquals(Arrays.asList(amy, zed), model.getFilteredPersonList());
    }

    @Test
    public void equals() {
        SortCommand sortByTutorialGroup = new SortCommand(SortCommand.SORT_BY_TUTORIAL_GROUP);
        SortCommand sortByTutorialGroupCopy = new SortCommand(SortCommand.SORT_BY_TUTORIAL_GROUP);
        SortCommand sortByName = new SortCommand(SortCommand.SORT_BY_NAME);

        assertTrue(sortByTutorialGroup.equals(sortByTutorialGroup));
        assertTrue(sortByTutorialGroup.equals(sortByTutorialGroupCopy));
        assertFalse(sortByTutorialGroup.equals(sortByName));
        assertFalse(sortByTutorialGroup.equals(1));
        assertFalse(sortByTutorialGroup.equals(null));
    }
}
