package cms.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import cms.testutil.PersonBuilder;

public class NameOrNusIdContainsKeywordsPredicateTest {

    @Test
    public void test_nameOnly_matches() {
        NameOrNusIdContainsKeywordsPredicate predicate = new NameOrNusIdContainsKeywordsPredicate(
                Arrays.asList("Alice", "Bob"), Collections.emptyList());
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_idOnly_matches() {
        NameOrNusIdContainsKeywordsPredicate predicate = new NameOrNusIdContainsKeywordsPredicate(
                Collections.emptyList(), Collections.singletonList("A0234504N"));
        assertTrue(predicate.test(new PersonBuilder().withNusId("A0234504N").build()));
    }

    @Test
    public void test_mixed_matchesOnEither() {
        NameOrNusIdContainsKeywordsPredicate predicate = new NameOrNusIdContainsKeywordsPredicate(
                Arrays.asList("john"), Arrays.asList("A0234504N"));
        assertTrue(predicate.test(new PersonBuilder().withName("John Doe").build()));
        assertTrue(predicate.test(new PersonBuilder().withNusId("A0234504N").build()));
    }

    @Test
    public void test_noMatches_returnsFalse() {
        NameOrNusIdContainsKeywordsPredicate predicate = new NameOrNusIdContainsKeywordsPredicate(
                Arrays.asList("x"), Arrays.asList("A9999999W"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withNusId("A0234504N").build()));
    }

    @Test
    public void test_nullLists_treatedAsEmpty() {
        NameOrNusIdContainsKeywordsPredicate predicate = new NameOrNusIdContainsKeywordsPredicate(null, null);
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withNusId("A0234504N").build()));

        // equals behavior
        NameOrNusIdContainsKeywordsPredicate emptyPredicate =
                new NameOrNusIdContainsKeywordsPredicate(Collections.emptyList(), Collections.emptyList());
        assertEquals(emptyPredicate, predicate);
    }

    @Test
    public void toStringMethod() {
        NameOrNusIdContainsKeywordsPredicate predicate = new NameOrNusIdContainsKeywordsPredicate(
                Arrays.asList("a"), Arrays.asList("A0123456J"));
        String expected = NameOrNusIdContainsKeywordsPredicate.class.getCanonicalName()
                + "{nameKeywords=" + Arrays.asList("a") + ", idKeywords=" + Arrays.asList("A0123456J") + "}";
        // toString uses ToStringBuilder, so just check it contains both lists
        String actual = predicate.toString();
        assertTrue(actual.contains("a"));
        assertTrue(actual.contains("A0123456J"));
    }

    @Test
    public void test_idCaseInsensitive_matches() {
        // predicate has lowercase id keyword, person has uppercase stored NusId; should match
        NameOrNusIdContainsKeywordsPredicate predicate = new NameOrNusIdContainsKeywordsPredicate(
                Collections.emptyList(), Collections.singletonList("a0234504n"));
        assertTrue(predicate.test(new PersonBuilder().withNusId("A0234504N").build()));

        // token in uppercase also matches
        NameOrNusIdContainsKeywordsPredicate predicateUpper = new NameOrNusIdContainsKeywordsPredicate(
                Collections.emptyList(), Collections.singletonList("A0234504N"));
        assertTrue(predicateUpper.test(new PersonBuilder().withNusId("a0234504n").build()));
    }

    @Test
    public void equals() {
        NameOrNusIdContainsKeywordsPredicate firstPredicate =
                new NameOrNusIdContainsKeywordsPredicate(Arrays.asList("a"), Arrays.asList("A0123456J"));
        NameOrNusIdContainsKeywordsPredicate secondPredicate =
                new NameOrNusIdContainsKeywordsPredicate(Arrays.asList("b"), Arrays.asList("A0123457H"));

        // same object -> returns true (covers lines 40-41)
        assertEquals(firstPredicate, firstPredicate);

        // same values -> returns true (covers line 47 true)
        NameOrNusIdContainsKeywordsPredicate firstPredicateCopy =
                new NameOrNusIdContainsKeywordsPredicate(Arrays.asList("a"), Arrays.asList("A0123456J"));
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
        NameOrNusIdContainsKeywordsPredicate p1 =
                new NameOrNusIdContainsKeywordsPredicate(Arrays.asList("Alice"), Arrays.asList("A0123456J"));
        NameOrNusIdContainsKeywordsPredicate p2 =
                new NameOrNusIdContainsKeywordsPredicate(Arrays.asList("Alice"), Arrays.asList("A0123457H"));
        // name lists equal (true) but id lists different (false) -> overall equals should be false
        assertFalse(p1.equals(p2));
    }

    @Test
    public void test_personWithNullNusId_returnsFalse() {
        NameOrNusIdContainsKeywordsPredicate predicate = new NameOrNusIdContainsKeywordsPredicate(
                Collections.emptyList(), Collections.singletonList("A0123456J"));

        // Build a normal person (student) and override getNusId() to return null to simulate missing NusId
        Person base = new PersonBuilder().withNusId("A0123456J").build();
        Person personWithNullNus = new Student(base.getName(), base.getPhone(), base.getEmail(), base.getNusId(),
                base.getSocUsername(), base.getGithubUsername(), base.getTutorialGroup(),
                base.getTags()) {
            @Override
            public NusId getNusId() {
                return null;
            }
        };

        // Predicate should return false when person's NusId is null (short-circuited)
        assertFalse(predicate.test(personWithNullNus));
    }
}
