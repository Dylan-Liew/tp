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
                new NusIdContainsKeywordsPredicate(Collections.singletonList("A0123456B"));
        NusIdContainsKeywordsPredicate secondPredicate =
                new NusIdContainsKeywordsPredicate(Arrays.asList("A0123456B", "A0123457B"));

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NusIdContainsKeywordsPredicate firstPredicateCopy =
                new NusIdContainsKeywordsPredicate(Collections.singletonList("A0123456B"));
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
                new NusIdContainsKeywordsPredicate(Collections.singletonList("A0123456B"));
        assertTrue(predicate.test(new PersonBuilder().withNusId("A0123456B").build()));

        // Multiple keywords (one match)
        predicate = new NusIdContainsKeywordsPredicate(Arrays.asList("A0123456B", "A0123457B"));
        assertTrue(predicate.test(new PersonBuilder().withNusId("A0123456B").build()));

        // Mixed-case keyword
        predicate = new NusIdContainsKeywordsPredicate(Collections.singletonList("a0123456b"));
        assertTrue(predicate.test(new PersonBuilder().withNusId("A0123456B").build()));
    }

    @Test
    public void test_nusIdDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        NusIdContainsKeywordsPredicate predicate = new NusIdContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withNusId("A0123456B").build()));

        // Non-matching keyword
        predicate = new NusIdContainsKeywordsPredicate(Collections.singletonList("A9999999Z"));
        assertFalse(predicate.test(new PersonBuilder().withNusId("A0123456B").build()));

        // Keywords match other fields but not nusId
        predicate = new NusIdContainsKeywordsPredicate(Arrays.asList("12345", "alice@example.com", "Main", "Street"));
        assertFalse(predicate.test(new PersonBuilder().withNusId("A0123456B").withPhone("12345")
                .withEmail("alice@example.com").build()));
    }

    @Test
    public void toStringMethod() {
        java.util.List<String> keywords = java.util.List.of("A0123456B", "A0123457B");
        NusIdContainsKeywordsPredicate predicate = new NusIdContainsKeywordsPredicate(keywords);

        String expected = NusIdContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
