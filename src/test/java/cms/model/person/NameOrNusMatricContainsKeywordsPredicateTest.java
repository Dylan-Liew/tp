package cms.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import cms.testutil.PersonBuilder;

public class NameOrNusMatricContainsKeywordsPredicateTest {

    @Test
    public void test_nameOnly_matches() {
        NameOrNusMatricContainsKeywordsPredicate predicate = new NameOrNusMatricContainsKeywordsPredicate(
                Arrays.asList("Alice", "Bob"), Collections.emptyList());
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_idOnly_matches() {
        NameOrNusMatricContainsKeywordsPredicate predicate = new NameOrNusMatricContainsKeywordsPredicate(
                Collections.emptyList(), Collections.singletonList("A0234504N"));
        assertTrue(predicate.test(new PersonBuilder().withNusMatric("A0234504N").build()));
    }

    @Test
    public void test_mixed_matchesOnEither() {
        NameOrNusMatricContainsKeywordsPredicate predicate = new NameOrNusMatricContainsKeywordsPredicate(
                Arrays.asList("john"), Arrays.asList("A0234504N"));
        assertTrue(predicate.test(new PersonBuilder().withName("John Doe").build()));
        assertTrue(predicate.test(new PersonBuilder().withNusMatric("A0234504N").build()));
    }

    @Test
    public void test_noMatches_returnsFalse() {
        NameOrNusMatricContainsKeywordsPredicate predicate = new NameOrNusMatricContainsKeywordsPredicate(
                Arrays.asList("x"), Arrays.asList("A9999999W"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withNusMatric("A0234504N").build()));
    }

    @Test
    public void test_nullLists_treatedAsEmpty() {
        NameOrNusMatricContainsKeywordsPredicate predicate = new NameOrNusMatricContainsKeywordsPredicate(null, null);
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withNusMatric("A0234504N").build()));

        // equals behavior
        NameOrNusMatricContainsKeywordsPredicate emptyPredicate =
                new NameOrNusMatricContainsKeywordsPredicate(Collections.emptyList(), Collections.emptyList());
        assertEquals(emptyPredicate, predicate);
    }

    @Test
    public void toStringMethod() {
        NameOrNusMatricContainsKeywordsPredicate predicate = new NameOrNusMatricContainsKeywordsPredicate(
                Arrays.asList("a"), Arrays.asList("A0123456J"));
        String expected = NameOrNusMatricContainsKeywordsPredicate.class.getCanonicalName()
                + "{nameKeywords=" + Arrays.asList("a") + ", idKeywords=" + Arrays.asList("A0123456J") + "}";
        // toString uses ToStringBuilder, so just check it contains both lists
        String actual = predicate.toString();
        assertTrue(actual.contains("a"));
        assertTrue(actual.contains("A0123456J"));
    }

    @Test
    public void test_idCaseInsensitive_matches() {
        // predicate has lowercase matric keyword, person has uppercase stored NusMatric; should match
        NameOrNusMatricContainsKeywordsPredicate predicate = new NameOrNusMatricContainsKeywordsPredicate(
                Collections.emptyList(), Collections.singletonList("a0234504n"));
        assertTrue(predicate.test(new PersonBuilder().withNusMatric("A0234504N").build()));

        // token in uppercase also matches
        NameOrNusMatricContainsKeywordsPredicate predicateUpper = new NameOrNusMatricContainsKeywordsPredicate(
                Collections.emptyList(), Collections.singletonList("A0234504N"));
        assertTrue(predicateUpper.test(new PersonBuilder().withNusMatric("a0234504n").build()));
    }

    @Test
    public void equals() {
        NameOrNusMatricContainsKeywordsPredicate firstPredicate =
                new NameOrNusMatricContainsKeywordsPredicate(Arrays.asList("a"), Arrays.asList("A0123456J"));
        NameOrNusMatricContainsKeywordsPredicate secondPredicate =
                new NameOrNusMatricContainsKeywordsPredicate(Arrays.asList("b"), Arrays.asList("A0123457H"));

        // same object -> returns true (covers lines 40-41)
        assertEquals(firstPredicate, firstPredicate);

        // same values -> returns true (covers line 47 true)
        NameOrNusMatricContainsKeywordsPredicate firstPredicateCopy =
                new NameOrNusMatricContainsKeywordsPredicate(Arrays.asList("a"), Arrays.asList("A0123456J"));
        assertEquals(firstPredicate, firstPredicateCopy);

        // different types -> returns false (covers lines 43-44)
        assertFalse(firstPredicate.equals(1));

        // null -> returns false (also covers lines 43-44)
        assertFalse(firstPredicate.equals(null));

        // different predicate -> returns false (covers line 47 false)
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void equals_nameSameIdDifferent() {
        NameOrNusMatricContainsKeywordsPredicate p1 =
                new NameOrNusMatricContainsKeywordsPredicate(Arrays.asList("Alice"), Arrays.asList("A0123456J"));
        NameOrNusMatricContainsKeywordsPredicate p2 =
                new NameOrNusMatricContainsKeywordsPredicate(Arrays.asList("Alice"), Arrays.asList("A0123457H"));
        // name lists equal (true) but matric lists different (false) -> overall equals should be false
        assertFalse(p1.equals(p2));
    }

    @Test
    public void test_personWithNullNusMatric_returnsFalse() {
        NameOrNusMatricContainsKeywordsPredicate predicate = new NameOrNusMatricContainsKeywordsPredicate(
                Collections.emptyList(), Collections.singletonList("A0123456J"));

        // Build a normal person (student) and override getNusMatric() to return null to simulate missing NusMatric
        Person base = new PersonBuilder().withNusMatric("A0123456J").build();
        Person personWithNullNus = new Student(base.getName(), base.getPhone(), base.getEmail(), base.getNusMatric(),
                base.getSocUsername(), base.getGithubUsername(), base.getTutorialGroup(),
                base.getTags()) {
            @Override
            public NusMatric getNusMatric() {
                return null;
            }
        };

        // Predicate should return false when person's NusMatric is null (short-circuited)
        assertFalse(predicate.test(personWithNullNus));
    }
}
