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
        NusMatricContainsKeywordsPredicate matric =
                new NusMatricContainsKeywordsPredicate(Collections.singletonList("A0000001X"));

        CombinedFindPredicate combined = new CombinedFindPredicate(all, name, matric);

        // Person matching 'all' predicate (name contains Alice)
        Person p1 = new PersonBuilder().withName("Alice Pauline").build();
        assertTrue(combined.test(p1));

        // Person matching 'name' predicate
        Person p2 = new PersonBuilder().withName("Bob").build();
        assertTrue(combined.test(p2));

        // Person matching 'matric' predicate
        Person p3 = new PersonBuilder().withNusMatric("A0000001X").build();
        assertTrue(combined.test(p3));

        // Person matching none
        Person p4 = new PersonBuilder().withName("Charlie").withNusMatric("A9999999W").build();
        assertFalse(combined.test(p4));
    }

    @Test
    public void equals_andToString_behaviour() {
        CombinedFindPredicate a = new CombinedFindPredicate(
                new AllFieldsContainsKeywordsPredicate(Arrays.asList("x")),
                new NameContainsKeywordsPredicate(Arrays.asList("y")),
                new NusMatricContainsKeywordsPredicate(Arrays.asList("Z")));

        // reflexive
        assertTrue(a.equals(a));

        // different type
        assertFalse(a.equals(new Object()));

        // equal content
        CombinedFindPredicate b = new CombinedFindPredicate(
                new AllFieldsContainsKeywordsPredicate(Arrays.asList("x")),
                new NameContainsKeywordsPredicate(Arrays.asList("y")),
                new NusMatricContainsKeywordsPredicate(Arrays.asList("Z")));
        assertTrue(a.equals(b));

        // different content
        CombinedFindPredicate c = new CombinedFindPredicate(
                new AllFieldsContainsKeywordsPredicate(Arrays.asList("other")),
                new NameContainsKeywordsPredicate(Arrays.asList("y")),
                new NusMatricContainsKeywordsPredicate(Arrays.asList("Z")));
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
                new NusMatricContainsKeywordsPredicate(Arrays.asList("Z")));
        CombinedFindPredicate p2 = new CombinedFindPredicate(
                new AllFieldsContainsKeywordsPredicate(Arrays.asList("x")),
                new NameContainsKeywordsPredicate(Arrays.asList("y2")),
                new NusMatricContainsKeywordsPredicate(Arrays.asList("Z")));
        assertFalse(p1.equals(p2));

        // same all and name, different matric -> should be false
        CombinedFindPredicate p3 = new CombinedFindPredicate(
                new AllFieldsContainsKeywordsPredicate(Arrays.asList("x")),
                new NameContainsKeywordsPredicate(Arrays.asList("y1")),
                new NusMatricContainsKeywordsPredicate(Arrays.asList("Z1")));
        CombinedFindPredicate p4 = new CombinedFindPredicate(
                new AllFieldsContainsKeywordsPredicate(Arrays.asList("x")),
                new NameContainsKeywordsPredicate(Arrays.asList("y1")),
                new NusMatricContainsKeywordsPredicate(Arrays.asList("Z2")));
        assertFalse(p3.equals(p4));
    }
}
