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
                Collections.emptyList(), Collections.singletonList("A0234504F"));
        assertTrue(predicate.test(new PersonBuilder().withNusId("A0234504F").build()));
    }

    @Test
    public void test_mixed_matchesOnEither() {
        NameOrNusIdContainsKeywordsPredicate predicate = new NameOrNusIdContainsKeywordsPredicate(
                Arrays.asList("john"), Arrays.asList("A0234504F"));
        assertTrue(predicate.test(new PersonBuilder().withName("John Doe").build()));
        assertTrue(predicate.test(new PersonBuilder().withNusId("A0234504F").build()));
    }

    @Test
    public void test_noMatches_returnsFalse() {
        NameOrNusIdContainsKeywordsPredicate predicate = new NameOrNusIdContainsKeywordsPredicate(
                Arrays.asList("x"), Arrays.asList("A9999999Z"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withNusId("A0234504F").build()));
    }

    @Test
    public void test_nullLists_treatedAsEmpty() {
        NameOrNusIdContainsKeywordsPredicate predicate = new NameOrNusIdContainsKeywordsPredicate(null, null);
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withNusId("A0234504F").build()));

        // equals behavior
        NameOrNusIdContainsKeywordsPredicate emptyPredicate =
                new NameOrNusIdContainsKeywordsPredicate(Collections.emptyList(), Collections.emptyList());
        assertEquals(emptyPredicate, predicate);
    }

    @Test
    public void toStringMethod() {
        NameOrNusIdContainsKeywordsPredicate predicate = new NameOrNusIdContainsKeywordsPredicate(
                Arrays.asList("a"), Arrays.asList("A0123456B"));
        String expected = NameOrNusIdContainsKeywordsPredicate.class.getCanonicalName()
                + "{nameKeywords=" + Arrays.asList("a") + ", idKeywords=" + Arrays.asList("A0123456B") + "}";
        // toString uses ToStringBuilder, so just check it contains both lists
        String actual = predicate.toString();
        assertTrue(actual.contains("a"));
        assertTrue(actual.contains("A0123456B"));
    }

    @Test
    public void test_idCaseInsensitive_matches() {
        // predicate has lowercase id keyword, person has uppercase stored NusId; should match
        NameOrNusIdContainsKeywordsPredicate predicate = new NameOrNusIdContainsKeywordsPredicate(
                Collections.emptyList(), Collections.singletonList("a0234504f"));
        assertTrue(predicate.test(new PersonBuilder().withNusId("A0234504F").build()));

        // token in uppercase also matches
        NameOrNusIdContainsKeywordsPredicate predicateUpper = new NameOrNusIdContainsKeywordsPredicate(
                Collections.emptyList(), Collections.singletonList("A0234504F"));
        assertTrue(predicateUpper.test(new PersonBuilder().withNusId("a0234504f").build()));
    }

    @Test
    public void equals() {
        NameOrNusIdContainsKeywordsPredicate firstPredicate =
                new NameOrNusIdContainsKeywordsPredicate(Arrays.asList("a"), Arrays.asList("A0123456B"));
        NameOrNusIdContainsKeywordsPredicate secondPredicate =
                new NameOrNusIdContainsKeywordsPredicate(Arrays.asList("b"), Arrays.asList("A0123457B"));

        // same object -> returns true (covers lines 40-41)
        assertEquals(firstPredicate, firstPredicate);

        // same values -> returns true (covers line 47 true)
        NameOrNusIdContainsKeywordsPredicate firstPredicateCopy =
                new NameOrNusIdContainsKeywordsPredicate(Arrays.asList("a"), Arrays.asList("A0123456B"));
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
                new NameOrNusIdContainsKeywordsPredicate(Arrays.asList("Alice"), Arrays.asList("A0123456B"));
        NameOrNusIdContainsKeywordsPredicate p2 =
                new NameOrNusIdContainsKeywordsPredicate(Arrays.asList("Alice"), Arrays.asList("A0123457B"));
        // name lists equal (true) but id lists different (false) -> overall equals should be false
        assertFalse(p1.equals(p2));
    }

    @Test
    public void test_personWithNullNusId_returnsFalse() {
        NameOrNusIdContainsKeywordsPredicate predicate = new NameOrNusIdContainsKeywordsPredicate(
                Collections.emptyList(), Collections.singletonList("A0123456B"));

        // Build a normal person and override getNusId() to return null to simulate missing NusId
        Person base = new PersonBuilder().withNusId("A0123456B").build();
        Person personWithNullNus = new Person(base.getName(), base.getPhone(), base.getEmail(), base.getNusId(),
                base.getSocUsername(), base.getGithubUsername(), base.getRole(), base.getTutorialGroup(),
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
