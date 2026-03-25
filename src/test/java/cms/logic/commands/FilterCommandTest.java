package cms.logic.commands;

import static cms.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static cms.logic.commands.CommandTestUtil.assertCommandSuccess;
import static cms.testutil.TypicalPersons.ALICE;
import static cms.testutil.TypicalPersons.BENSON;
import static cms.testutil.TypicalPersons.DANIEL;
import static cms.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cms.model.Model;
import cms.model.ModelManager;
import cms.model.UserPrefs;
import cms.model.person.Person;
import cms.model.person.TagTutorialGroupMatchesPredicate;
import cms.model.person.TutorialGroup;
import cms.model.tag.Tag;
import cms.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code FilterCommand}.
 */
public class FilterCommandTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        TagTutorialGroupMatchesPredicate firstPredicate =
                new TagTutorialGroupMatchesPredicate(Set.of(new Tag("friends")), Set.of(new TutorialGroup("T01")));
        TagTutorialGroupMatchesPredicate secondPredicate =
                new TagTutorialGroupMatchesPredicate(Set.of(new Tag("owesMoney")), Set.of(new TutorialGroup("T02")));

        FilterCommand filterFirstCommand = new FilterCommand(firstPredicate);
        FilterCommand filterSecondCommand = new FilterCommand(secondPredicate);

        FilterCommand filterFirstCommandCopy = new FilterCommand(firstPredicate);
        assertEquals(filterFirstCommand, filterFirstCommandCopy);
        assertNotEquals(new Object(), filterFirstCommand);
        assertNotEquals(null, filterFirstCommand);
        assertNotEquals(filterSecondCommand, filterFirstCommand);
    }

    @Test
    public void execute_tagOnly_success() {
        TagTutorialGroupMatchesPredicate predicate =
                new TagTutorialGroupMatchesPredicate(Set.of(new Tag("friends")), Set.of());
        FilterCommand command = new FilterCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3), expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_tutorialGroupOnly_success() {
        TagTutorialGroupMatchesPredicate predicate =
                new TagTutorialGroupMatchesPredicate(Set.of(), Set.of(new TutorialGroup("T01")));
        FilterCommand command = new FilterCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 7), expectedModel);
        assertEquals(getTypicalAddressBook().getPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_tagAndTutorialGroup_success() {
        TagTutorialGroupMatchesPredicate predicate =
                new TagTutorialGroupMatchesPredicate(Set.of(new Tag("friends")), Set.of(new TutorialGroup("T01")));
        FilterCommand command = new FilterCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3), expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_caseInsensitiveMatching_success() {
        TagTutorialGroupMatchesPredicate predicate =
                new TagTutorialGroupMatchesPredicate(Set.of(new Tag("FRIENDS")), Set.of(new TutorialGroup("T01")));
        FilterCommand command = new FilterCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3), expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_repeatedTags_requiresAllTags_success() {
        Model localModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model localExpectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person dualTaggedPerson = new PersonBuilder().withName("Hannah Dualtag")
                .withNusId("A1234567B").withSocUsername("hannah1").withGithubUsername("hannah-dual")
                .withEmail("hannah@example.com").withPhone("81234567")
                .withTutorialGroup("T01").withTags("friends", "owesMoney").build();
        localModel.addPerson(dualTaggedPerson);
        localExpectedModel.addPerson(dualTaggedPerson);

        TagTutorialGroupMatchesPredicate predicate =
                new TagTutorialGroupMatchesPredicate(Set.of(new Tag("friends"), new Tag("owesMoney")),
                        Set.of());
        FilterCommand command = new FilterCommand(predicate);

        localExpectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, localModel, String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2), localExpectedModel);
        assertEquals(Arrays.asList(BENSON, dualTaggedPerson), localModel.getFilteredPersonList());
    }

    @Test
    public void execute_repeatedTutorialGroups_matchesAnyTutorialGroup_success() {
        TagTutorialGroupMatchesPredicate predicate =
                new TagTutorialGroupMatchesPredicate(Set.of(),
                        Set.of(new TutorialGroup("T01"), new TutorialGroup("T02")));
        FilterCommand command = new FilterCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 7), expectedModel);
        assertEquals(getTypicalAddressBook().getPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_noMatches_success() {
        TagTutorialGroupMatchesPredicate predicate =
                new TagTutorialGroupMatchesPredicate(Collections.singleton(new Tag("mentor")),
                        Collections.singleton(new TutorialGroup("T99")));
        FilterCommand command = new FilterCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0), expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        TagTutorialGroupMatchesPredicate predicate =
                new TagTutorialGroupMatchesPredicate(Set.of(new Tag("friends")), Set.of(new TutorialGroup("T01")));
        FilterCommand filterCommand = new FilterCommand(predicate);
        String expected = FilterCommand.class.getCanonicalName()
                + "{predicate=" + predicate + "}";
        assertEquals(expected, filterCommand.toString());
    }
}
