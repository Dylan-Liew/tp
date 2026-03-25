package cms.logic.commands;

import static cms.logic.commands.CommandTestUtil.assertCommandFailure;
import static cms.logic.commands.CommandTestUtil.assertCommandSuccess;
import static cms.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cms.logic.Messages;
import cms.model.Model;
import cms.model.ModelManager;
import cms.model.UserPrefs;
import cms.model.person.FieldConflict;
import cms.model.person.Person;
import cms.model.person.exceptions.DuplicatePersonException;
import cms.model.person.exceptions.DuplicatePersonFieldException;
import cms.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().withNusId("A7654321Z")
                .withEmail("newperson@example.com")
                .withSocUsername("newsoc1")
                .withGithubUsername("newpersongh")
                .build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        String expectedMessage = DuplicatePersonException.buildMessage(personInList);
        assertCommandFailure(new AddCommand(personInList), model,
            expectedMessage);
    }

    @Test
    public void execute_duplicateFields_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person editedPerson = new PersonBuilder(personInList)
                .withNusId("A7654321Z")
                .build();

        String expectedMessage = DuplicatePersonFieldException
            .buildMessage(new FieldConflict(FieldConflict.Type.EMAIL, personInList));

        assertCommandFailure(new AddCommand(editedPerson), model,
            expectedMessage);
    }

}
