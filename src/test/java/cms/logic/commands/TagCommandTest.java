package cms.logic.commands;

import static cms.logic.commands.CommandTestUtil.assertCommandFailure;
import static cms.logic.commands.CommandTestUtil.assertCommandSuccess;
import static cms.logic.commands.CommandTestUtil.showPersonAtIndex;
import static cms.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static cms.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static cms.testutil.TypicalPersons.ALICE;
import static cms.testutil.TypicalPersons.BENSON;
import static cms.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cms.commons.core.index.Index;
import cms.logic.commands.exceptions.CommandException;
import cms.model.Model;
import cms.model.ModelManager;
import cms.model.UserPrefs;
import cms.model.person.Email;
import cms.model.person.GithubUsername;
import cms.model.person.Name;
import cms.model.person.NusMatric;
import cms.model.person.Person;
import cms.model.person.Phone;
import cms.model.person.Role;
import cms.model.person.SocUsername;
import cms.model.person.TutorialGroup;
import cms.model.tag.Tag;
import cms.testutil.PersonBuilder;

public class TagCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addByIndexes_success() {
        TagCommand command = new TagCommand(TagCommand.Action.ADD,
                List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON),
                List.of(new Tag("tag1"), new Tag("tag2")));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person updatedAlice = new PersonBuilder(ALICE).withTags("friends", "tag1", "tag2").build();
        Person updatedBenson = new PersonBuilder(BENSON).withTags("owesMoney", "friends", "tag1", "tag2").build();
        expectedModel.setPerson(ALICE, updatedAlice);
        expectedModel.setPerson(BENSON, updatedBenson);

        String expectedMessage = "tag1, tag2 has been added to "
                + "1, Alice Pauline, A0000001X; 2, Benson Meier, A0234501W";
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_addByIndexFilteredList_usesFilteredIndexInSuccessMessage() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        TagCommand command = new TagCommand(TagCommand.Action.ADD,
                List.of(INDEX_FIRST_PERSON),
                List.of(new Tag("tag1")));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        Person updatedBenson = new PersonBuilder(BENSON).withTags("owesMoney", "friends", "tag1").build();
        expectedModel.setPerson(BENSON, updatedBenson);

        String expectedMessage = "tag1 has been added to 1, Benson Meier, A0234501W";
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_deleteByNusMatrics_success() {
        TagCommand command = TagCommand.byNusMatrics(TagCommand.Action.DELETE,
                List.of(ALICE.getNusMatric(), BENSON.getNusMatric()),
                List.of(new Tag("friends"), new Tag("owesMoney")));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person updatedAlice = new PersonBuilder(ALICE).withTags().build();
        Person updatedBenson = new PersonBuilder(BENSON).withTags().build();
        expectedModel.setPerson(ALICE, updatedAlice);
        expectedModel.setPerson(BENSON, updatedBenson);

        String expectedMessage = "friends has been removed from "
                + "1, Alice Pauline, A0000001X; 2, Benson Meier, A0234501W\n"
                + "owesmoney has been removed from 2, Benson Meier, A0234501W";
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_deleteByNusMatricPersonOutsideFilteredList_usesAddressBookIndexInSuccessMessage() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        TagCommand command = TagCommand.byNusMatrics(TagCommand.Action.DELETE,
                List.of(BENSON.getNusMatric()),
                List.of(new Tag("friends")));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);
        Person updatedBenson = new PersonBuilder(BENSON).withTags("owesMoney").build();
        expectedModel.setPerson(BENSON, updatedBenson);

        String expectedMessage = "friends has been removed from 2, Benson Meier, A0234501W";
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_deleteSkipsPersonsWithoutTag_success() {
        TagCommand command = new TagCommand(TagCommand.Action.DELETE,
                List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON),
                List.of(new Tag("owesMoney")));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person updatedBenson = new PersonBuilder(BENSON).withTags("friends").build();
        expectedModel.setPerson(BENSON, updatedBenson);

        String expectedMessage = "owesmoney has been removed from 2, Benson Meier, A0234501W";
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_addNoChanges_returnsNoChangesMessage() {
        TagCommand command = new TagCommand(TagCommand.Action.ADD,
                List.of(INDEX_FIRST_PERSON),
                List.of(new Tag("friends")));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandSuccess(command, model, TagCommand.MESSAGE_ADD_NO_CHANGES, expectedModel);
    }

    @Test
    public void execute_deleteNoChanges_returnsNoChangesMessage() {
        TagCommand command = new TagCommand(TagCommand.Action.DELETE,
                List.of(INDEX_FIRST_PERSON),
                List.of(new Tag("not-present")));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandSuccess(command, model, TagCommand.MESSAGE_DELETE_NO_CHANGES, expectedModel);
    }

    @Test
    public void execute_duplicateIndexes_updatesPersonOnce() {
        TagCommand command = new TagCommand(TagCommand.Action.ADD,
                List.of(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON),
                List.of(new Tag("tag1")));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person updatedAlice = new PersonBuilder(ALICE).withTags("friends", "tag1").build();
        expectedModel.setPerson(ALICE, updatedAlice);

        String expectedMessage = "tag1 has been added to 1, Alice Pauline, A0000001X";
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateNusMatrics_updatesPersonOnce() {
        TagCommand command = TagCommand.byNusMatrics(TagCommand.Action.DELETE,
                List.of(BENSON.getNusMatric(), BENSON.getNusMatric()),
                List.of(new Tag("friends")));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person updatedBenson = new PersonBuilder(BENSON).withTags("owesMoney").build();
        expectedModel.setPerson(BENSON, updatedBenson);

        String expectedMessage = "friends has been removed from 2, Benson Meier, A0234501W";
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        TagCommand command = new TagCommand(TagCommand.Action.ADD,
                List.of(Index.fromOneBased(model.getFilteredPersonList().size() + 1)),
                List.of(new Tag("friend")));

        assertCommandFailure(command, model, cms.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidNusMatric_throwsCommandException() {
        TagCommand command = TagCommand.byNusMatrics(TagCommand.Action.DELETE,
                List.of(new NusMatric("A9999999W")),
                List.of(new Tag("friend")));

        assertCommandFailure(command, model, TagCommand.MESSAGE_INVALID_NUS_MATRIC);
    }

    @Test
    public void constructor_emptyTargets_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new TagCommand(TagCommand.Action.ADD, List.of(), List.of(new Tag("friend"))));
        assertThrows(IllegalArgumentException.class, () ->
                TagCommand.byNusMatrics(TagCommand.Action.ADD, List.of(), List.of(new Tag("friend"))));
        assertThrows(IllegalArgumentException.class, () ->
                new TagCommand(TagCommand.Action.ADD, List.of(INDEX_FIRST_PERSON), List.of()));
        assertThrows(IllegalArgumentException.class, () ->
                TagCommand.byNusMatrics(TagCommand.Action.ADD, List.of(ALICE.getNusMatric()), List.of()));
    }

    @Test
    public void equals() {
        TagCommand addFirstCommand = new TagCommand(TagCommand.Action.ADD, List.of(INDEX_FIRST_PERSON),
                List.of(new Tag("friend")));
        TagCommand addFirstCommandCopy = new TagCommand(TagCommand.Action.ADD, List.of(INDEX_FIRST_PERSON),
                List.of(new Tag("friend")));
        TagCommand deleteSecondCommand = new TagCommand(TagCommand.Action.DELETE, List.of(INDEX_SECOND_PERSON),
                List.of(new Tag("friend")));
        TagCommand byNusMatricCommand = TagCommand.byNusMatrics(TagCommand.Action.ADD, List.of(ALICE.getNusMatric()),
                List.of(new Tag("friend")));
        TagCommand differentIndexesCommand = new TagCommand(TagCommand.Action.ADD, List.of(INDEX_SECOND_PERSON),
                List.of(new Tag("friend")));
        TagCommand differentNusMatricsCommand = TagCommand.byNusMatrics(TagCommand.Action.ADD,
                List.of(BENSON.getNusMatric()), List.of(new Tag("friend")));
        TagCommand differentTagsCommand = new TagCommand(TagCommand.Action.ADD, List.of(INDEX_FIRST_PERSON),
                List.of(new Tag("teammate")));

        assertTrue(addFirstCommand.equals(addFirstCommand));
        assertTrue(addFirstCommand.equals(addFirstCommandCopy));
        assertFalse(addFirstCommand.equals(deleteSecondCommand));
        assertFalse(addFirstCommand.equals(byNusMatricCommand));
        assertFalse(addFirstCommand.equals(differentIndexesCommand));
        assertFalse(byNusMatricCommand.equals(differentNusMatricsCommand));
        assertFalse(addFirstCommand.equals(differentTagsCommand));
        assertFalse(addFirstCommand.equals(null));
        assertFalse(addFirstCommand.equals(1));
    }

    @Test
    public void toStringMethod() {
        TagCommand byIndexCommand = new TagCommand(TagCommand.Action.ADD, List.of(INDEX_FIRST_PERSON),
                List.of(new Tag("friend")));
        String expectedByIndex = TagCommand.class.getCanonicalName()
                + "{action=ADD, targetType=INDEX, targetIndexes=["
                + Index.class.getCanonicalName() + "{zeroBasedIndex=0}], targetNusMatrics=[], targetTags=[[friend]]}";
        assertEquals(expectedByIndex, byIndexCommand.toString());

        TagCommand byNusMatricCommand = TagCommand.byNusMatrics(TagCommand.Action.DELETE, List.of(ALICE.getNusMatric()),
                List.of(new Tag("friend")));
        String expectedByNusMatric = TagCommand.class.getCanonicalName()
                + "{action=DELETE, targetType=NUS_MATRIC, targetIndexes=[], targetNusMatrics=[A0000001X], "
                + "targetTags=[[friend]]}";
        assertEquals(expectedByNusMatric, byNusMatricCommand.toString());
    }

    @Test
    public void createUpdatedPerson_invalidPerson_throwsCommandException() throws Exception {
        TagCommand command = new TagCommand(TagCommand.Action.ADD, List.of(INDEX_FIRST_PERSON),
                List.of(new Tag("friend")));
        Method createUpdatedPerson = TagCommand.class.getDeclaredMethod("createUpdatedPerson", Person.class, Set.class);
        createUpdatedPerson.setAccessible(true);

        Person invalidPerson = new InvalidSocUsernamePerson();

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, () ->
                createUpdatedPerson.invoke(command, invalidPerson, Set.of(new Tag("friend"))));
        assertTrue(thrown.getCause() instanceof CommandException);
        assertEquals(Person.MESSAGE_SOC_USERNAME_NUS_MATRIC_MISMATCH, thrown.getCause().getMessage());
    }

    @Test
    public void findOneBasedIndex_personNotInModel_returnsZero() throws Exception {
        TagCommand command = new TagCommand(TagCommand.Action.ADD, List.of(INDEX_FIRST_PERSON),
                List.of(new Tag("friend")));
        Method findOneBasedIndex = TagCommand.class.getDeclaredMethod("findOneBasedIndex", List.class, Person.class);
        findOneBasedIndex.setAccessible(true);

        Person absentPerson = new PersonBuilder().withNusMatric("A1234567X").withSocUsername("absent1")
                .withGithubUsername("absent-gh").withEmail("absent@example.com").build();

        int result = (int) findOneBasedIndex.invoke(command, model.getAddressBook().getPersonList(), absentPerson);
        assertEquals(0, result);
    }

    private static class InvalidSocUsernamePerson extends Person {
        private static final SocUsername INVALID_SOC_USERNAME = new SocUsername("A9999999W");

        InvalidSocUsernamePerson() {
            super(new Name("Invalid Person"), new Phone("81234567"), new Email("invalid@example.com"),
                    new NusMatric("A0000001X"), new SocUsername("amybee"), new GithubUsername("invalid-gh"),
                    new TutorialGroup("1"), Set.of());
        }

        @Override
        public SocUsername getSocUsername() {
            return INVALID_SOC_USERNAME;
        }

        @Override
        public Role getRole() {
            return Role.STUDENT;
        }
    }
}
