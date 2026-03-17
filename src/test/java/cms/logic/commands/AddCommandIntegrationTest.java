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
import cms.model.person.Person;
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
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicateSocUsername_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person duplicateSocUsernamePerson = new PersonBuilder()
                .withNusId("A1111111B")
                .withEmail("fresh@example.com")
                .withSocUsername(personInList.getSocUsername().value)
                .withGithubUsername("fresh-gh")
                .build();

        assertCommandFailure(new AddCommand(duplicateSocUsernamePerson), model,
                String.format("A person with SOC username [%s] already exists in the system.",
                        personInList.getSocUsername().value));
    }

}
