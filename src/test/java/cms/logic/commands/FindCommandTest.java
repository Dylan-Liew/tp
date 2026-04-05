package cms.logic.commands;

import static cms.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static cms.logic.commands.CommandTestUtil.assertCommandSuccess;
import static cms.testutil.TypicalPersons.CARL;
import static cms.testutil.TypicalPersons.ELLE;
import static cms.testutil.TypicalPersons.FIONA;
import static cms.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import cms.model.Model;
import cms.model.ModelManager;
import cms.model.UserPrefs;
import cms.model.person.AllFieldsContainsKeywordsPredicate;
import cms.model.person.CombinedFindPredicate;
import cms.model.person.NameContainsKeywordsPredicate;
import cms.model.person.NusMatricContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertEquals(findFirstCommand, findFirstCommandCopy);

        // different types -> returns false
        assertNotEquals(new Object(), findFirstCommand);

        // null -> returns false
        assertNotEquals(null, findFirstCommand);

        // different person -> returns false
        assertNotEquals(findSecondCommand, findFirstCommand);
    }

    @Test
    public void execute_allPrefix_singlePersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        AllFieldsContainsKeywordsPredicate predicate =
                new AllFieldsContainsKeywordsPredicate(Collections.singletonList("Elle"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.singletonList(ELLE), model.getFilteredPersonList());
    }

    @Test
    public void execute_namePrefix_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate =
                new NameContainsKeywordsPredicate(Arrays.asList("Kurz", "Elle", "Kunz"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_idPrefix_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2);
        NusMatricContainsKeywordsPredicate predicate =
                new NusMatricContainsKeywordsPredicate(
                        Arrays.asList("A0234502U", "A0234505M"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CARL, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_combinedPrefixes_returnsUnion() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        AllFieldsContainsKeywordsPredicate allPredicate =
                new AllFieldsContainsKeywordsPredicate(Arrays.asList("Kurz"));
        NameContainsKeywordsPredicate namePredicate =
                new NameContainsKeywordsPredicate(Arrays.asList("Elle"));
        NusMatricContainsKeywordsPredicate idPredicate =
                new NusMatricContainsKeywordsPredicate(Arrays.asList("A0234505M"));
        CombinedFindPredicate combinedPredicate =
                new CombinedFindPredicate(allPredicate, namePredicate, idPredicate);

        FindCommand command = new FindCommand(combinedPredicate);
        expectedModel.updateFilteredPersonList(combinedPredicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);

        // Expected union: CARL (Kurz), ELLE (name), FIONA (nusMatric)
        assertEquals(Arrays.asList(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName()
                + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }
}
