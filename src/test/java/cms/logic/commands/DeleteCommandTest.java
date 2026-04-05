package cms.logic.commands;

import static cms.logic.commands.CommandTestUtil.assertCommandFailure;
import static cms.logic.commands.CommandTestUtil.assertCommandSuccess;
import static cms.logic.commands.CommandTestUtil.showPersonAtIndex;
import static cms.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static cms.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static cms.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static cms.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import cms.commons.core.index.Index;
import cms.logic.Messages;
import cms.model.Model;
import cms.model.ModelManager;
import cms.model.UserPrefs;
import cms.model.person.NusMatric;
import cms.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexesUnfilteredList_success() {
        DeleteCommand deleteCommand = new DeleteCommand(List.of(INDEX_FIRST_PERSON, INDEX_THIRD_PERSON));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person thirdPersonToDelete = expectedModel.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        Person firstPersonToDelete = expectedModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        expectedModel.deletePerson(thirdPersonToDelete);
        expectedModel.deletePerson(firstPersonToDelete);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS,
                String.join("\n",
                        Messages.format(firstPersonToDelete),
                        Messages.format(thirdPersonToDelete)));

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateIndexesUnfilteredList_deletesPersonOnce() {
        DeleteCommand deleteCommand = new DeleteCommand(List.of(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON));

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validNusMatricUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = DeleteCommand.byNusMatric(personToDelete.getNusMatric());

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validNusMatricsUnfilteredList_success() {
        Person firstPersonToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person thirdPersonToDelete = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        DeleteCommand deleteCommand = DeleteCommand.byNusMatrics(
                List.of(firstPersonToDelete.getNusMatric(), thirdPersonToDelete.getNusMatric()));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(firstPersonToDelete);
        expectedModel.deletePerson(thirdPersonToDelete);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS,
                String.join("\n",
                        Messages.format(firstPersonToDelete),
                        Messages.format(thirdPersonToDelete)));

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidNusMatricUnfilteredList_throwsCommandException() {
        DeleteCommand deleteCommand = DeleteCommand.byNusMatric(new NusMatric("A9999999W"));

        assertCommandFailure(deleteCommand, model, DeleteCommand.MESSAGE_INVALID_NUS_MATRIC);
    }

    @Test
    public void execute_duplicateNusMatricsUnfilteredList_deletesPersonOnce() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = DeleteCommand.byNusMatrics(
                List.of(personToDelete.getNusMatric(), personToDelete.getNusMatric()));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void constructor_emptyIndexes_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new DeleteCommand(List.of()));
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);
        DeleteCommand deleteMultipleIndexesCommand =
                new DeleteCommand(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // same nus matric values -> returns true
        DeleteCommand deleteFirstNusMatricCommand = DeleteCommand.byNusMatric(new NusMatric("A0000001X"));
        DeleteCommand deleteFirstNusMatricCommandCopy = DeleteCommand.byNusMatric(new NusMatric("A0000001X"));
        assertTrue(deleteFirstNusMatricCommand.equals(deleteFirstNusMatricCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));

        // different number of indexes -> returns false
        assertFalse(deleteFirstCommand.equals(deleteMultipleIndexesCommand));

        // different target type -> returns false
        assertFalse(deleteFirstCommand.equals(deleteFirstNusMatricCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, deleteCommand.toString());

        List<Index> targetIndexes = List.of(Index.fromOneBased(1), Index.fromOneBased(2));
        DeleteCommand multiDeleteCommand = new DeleteCommand(targetIndexes);
        String multiExpected = DeleteCommand.class.getCanonicalName() + "{targetIndexes=" + targetIndexes + "}";
        assertEquals(multiExpected, multiDeleteCommand.toString());

        NusMatric targetNusMatric = new NusMatric("A0000001X");
        DeleteCommand deleteByNusMatricCommand = DeleteCommand.byNusMatric(targetNusMatric);
        String nusMatricExpected = DeleteCommand.class.getCanonicalName() + "{targetNusMatric=" + targetNusMatric + "}";
        assertEquals(nusMatricExpected, deleteByNusMatricCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
