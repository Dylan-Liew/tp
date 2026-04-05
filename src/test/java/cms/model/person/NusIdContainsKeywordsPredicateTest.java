package cms.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import cms.testutil.PersonBuilder;

public class NusIdContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        NusIdContainsKeywordsPredicate firstPredicate =
                new NusIdContainsKeywordsPredicate(Collections.singletonList("A0123456J"));
        NusIdContainsKeywordsPredicate secondPredicate =
                new NusIdContainsKeywordsPredicate(Arrays.asList("A0123456J", "A0123457H"));

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NusIdContainsKeywordsPredicate firstPredicateCopy =
                new NusIdContainsKeywordsPredicate(Collections.singletonList("A0123456J"));
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different predicate -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nusIdContainsKeywords_returnsTrue() {
        // One keyword
        NusIdContainsKeywordsPredicate predicate =
                new NusIdContainsKeywordsPredicate(Collections.singletonList("A0123456J"));
        assertTrue(predicate.test(new PersonBuilder().withNusId("A0123456J").build()));

        // Multiple keywords (one match)
        predicate = new NusIdContainsKeywordsPredicate(Arrays.asList("A0123456J", "A0123457H"));
        assertTrue(predicate.test(new PersonBuilder().withNusId("A0123456J").build()));

        // Mixed-case keyword
        predicate = new NusIdContainsKeywordsPredicate(Collections.singletonList("a0123456j"));
        assertTrue(predicate.test(new PersonBuilder().withNusId("A0123456J").build()));

        // multiple keywords, match on second
        predicate = new NusIdContainsKeywordsPredicate(Arrays.asList("A9999999W", "A0123456J"));
        assertTrue(predicate.test(new PersonBuilder().withNusId("A0123456J").build()));
    }

    @Test
    public void test_nusIdDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        NusIdContainsKeywordsPredicate predicate = new NusIdContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withNusId("A0123456J").build()));

        // Non-matching keyword
        predicate = new NusIdContainsKeywordsPredicate(Collections.singletonList("A9999999W"));
        assertFalse(predicate.test(new PersonBuilder().withNusId("A0123456J").build()));

        // Keywords match other fields but not nusId
        predicate = new NusIdContainsKeywordsPredicate(Arrays.asList("12345", "alice@example.com", "Main", "Street"));
        assertFalse(predicate.test(new PersonBuilder().withNusId("A0123456J").withPhone("12345")
                .withEmail("alice@example.com").build()));
    }

    @Test
    public void toStringMethod() {
        java.util.List<String> keywords = java.util.List.of("A0123456J", "A0123457H");
        NusIdContainsKeywordsPredicate predicate = new NusIdContainsKeywordsPredicate(keywords);

        String expected = NusIdContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }

    @Test
    public void test_personWithNullNusId_returnsFalse() {
        NusIdContainsKeywordsPredicate predicate =
                new NusIdContainsKeywordsPredicate(Collections.singletonList("A0123456J"));

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

    @Test
    public void test_keywordsNull_returnsFalse() {
        NusIdContainsKeywordsPredicate predicate = new NusIdContainsKeywordsPredicate(null);
        assertFalse(predicate.test(new PersonBuilder().withNusId("A0123456J").build()));
    }

    @Test
    public void test_keywordsFieldNull_returnsFalse() throws Exception {
        // create a predicate with non-empty keywords
        NusIdContainsKeywordsPredicate predicate =
                new NusIdContainsKeywordsPredicate(Collections.singletonList("A0123456J"));

        // use reflection to set the private field 'keywords' to null to exercise the keywords == null branch
        java.lang.reflect.Field keywordsField = NusIdContainsKeywordsPredicate.class.getDeclaredField("keywords");
        keywordsField.setAccessible(true);
        keywordsField.set(predicate, null);

        // build a person with matching nus id; predicate should return false because keywords field is null
        assertFalse(predicate.test(new PersonBuilder().withNusId("A0123456J").build()));
    }
}
