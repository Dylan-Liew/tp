package cms.model.person;

import java.util.function.Predicate;

import cms.commons.util.ToStringBuilder;

/**
 * Composite predicate combining AllFields, Name, and NusMatric predicates with OR.
 */
public class CombinedFindPredicate implements Predicate<Person> {
    private final AllFieldsContainsKeywordsPredicate allPredicate;
    private final NameContainsKeywordsPredicate namePredicate;
    private final NusMatricContainsKeywordsPredicate idPredicate;

    /**
     * Constructs a {@code CombinedFindPredicate} that OR-combines three predicates.
     *
     * @param allPredicate predicate for the 'a/' (all fields) prefix
     * @param namePredicate predicate for the 'n/' (name) prefix
     * @param idPredicate predicate for the 'm/' (NUS Matric) prefix
     */
    public CombinedFindPredicate(AllFieldsContainsKeywordsPredicate allPredicate,
            NameContainsKeywordsPredicate namePredicate,
            NusMatricContainsKeywordsPredicate idPredicate) {
        this.allPredicate = allPredicate;
        this.namePredicate = namePredicate;
        this.idPredicate = idPredicate;
    }

    @Override
    public boolean test(Person person) {
        return allPredicate.test(person) || namePredicate.test(person) || idPredicate.test(person);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CombinedFindPredicate)) {
            return false;
        }
        CombinedFindPredicate o = (CombinedFindPredicate) other;
        return allPredicate.equals(o.allPredicate)
                && namePredicate.equals(o.namePredicate)
                && idPredicate.equals(o.idPredicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("allPredicate", allPredicate)
                .add("namePredicate", namePredicate)
                .add("idPredicate", idPredicate).toString();
    }
}
