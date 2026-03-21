package cms.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import cms.testutil.PersonBuilder;

/** Tests for {@link CombinedFindPredicate} to increase path coverage. */
public class CombinedFindPredicateTest {

    @Test
    public void test_orLogic_matchesAnyPredicate() {
        AllFieldsContainsKeywordsPredicate all =
                new AllFieldsContainsKeywordsPredicate(Collections.singletonList("Alice"));
        NameContainsKeywordsPredicate name =
                new NameContainsKeywordsPredicate(Collections.singletonList("Bob"));
        NusIdContainsKeywordsPredicate id =
                new NusIdContainsKeywordsPredicate(Collections.singletonList("A0000001B"));

        CombinedFindPredicate combined = new CombinedFindPredicate(all, name, id);

        // Person matching 'all' predicate (name contains Alice)
        Person p1 = new PersonBuilder().withName("Alice Pauline").build();
        assertTrue(combined.test(p1));

        // Person matching 'name' predicate
        Person p2 = new PersonBuilder().withName("Bob").build();
        assertTrue(combined.test(p2));

        // Person matching 'id' predicate
        Person p3 = new PersonBuilder().withNusId("A0000001B").build();
        assertTrue(combined.test(p3));

        // Person matching none
        Person p4 = new PersonBuilder().withName("Charlie").withNusId("A9999999Z").build();
        assertFalse(combined.test(p4));
    }

    @Test
    public void equals_andToString_behaviour() {
        CombinedFindPredicate a = new CombinedFindPredicate(
                new AllFieldsContainsKeywordsPredicate(Arrays.asList("x")),
                new NameContainsKeywordsPredicate(Arrays.asList("y")),
                new NusIdContainsKeywordsPredicate(Arrays.asList("Z")));

        // reflexive
        assertTrue(a.equals(a));

        // different type
        assertFalse(a.equals(new Object()));

        // equal content
        CombinedFindPredicate b = new CombinedFindPredicate(
                new AllFieldsContainsKeywordsPredicate(Arrays.asList("x")),
                new NameContainsKeywordsPredicate(Arrays.asList("y")),
                new NusIdContainsKeywordsPredicate(Arrays.asList("Z")));
        assertTrue(a.equals(b));

        // different content
        CombinedFindPredicate c = new CombinedFindPredicate(
                new AllFieldsContainsKeywordsPredicate(Arrays.asList("other")),
                new NameContainsKeywordsPredicate(Arrays.asList("y")),
                new NusIdContainsKeywordsPredicate(Arrays.asList("Z")));
        assertFalse(a.equals(c));

        // toString should mention predicate fields
        String s = a.toString();
        assertTrue(s.contains("allPredicate") && s.contains("namePredicate") && s.contains("idPredicate"));
    }

    @Test
    public void equals_partialDifferences_behaviour() {
        // same allPredicate, different namePredicate -> should be false
        CombinedFindPredicate p1 = new CombinedFindPredicate(
                new AllFieldsContainsKeywordsPredicate(Arrays.asList("x")),
                new NameContainsKeywordsPredicate(Arrays.asList("y1")),
                new NusIdContainsKeywordsPredicate(Arrays.asList("Z")));
        CombinedFindPredicate p2 = new CombinedFindPredicate(
                new AllFieldsContainsKeywordsPredicate(Arrays.asList("x")),
                new NameContainsKeywordsPredicate(Arrays.asList("y2")),
                new NusIdContainsKeywordsPredicate(Arrays.asList("Z")));
        assertFalse(p1.equals(p2));

        // same all and name, different id -> should be false
        CombinedFindPredicate p3 = new CombinedFindPredicate(
                new AllFieldsContainsKeywordsPredicate(Arrays.asList("x")),
                new NameContainsKeywordsPredicate(Arrays.asList("y1")),
                new NusIdContainsKeywordsPredicate(Arrays.asList("Z1")));
        CombinedFindPredicate p4 = new CombinedFindPredicate(
                new AllFieldsContainsKeywordsPredicate(Arrays.asList("x")),
                new NameContainsKeywordsPredicate(Arrays.asList("y1")),
                new NusIdContainsKeywordsPredicate(Arrays.asList("Z2")));
        assertFalse(p3.equals(p4));
    }
}

