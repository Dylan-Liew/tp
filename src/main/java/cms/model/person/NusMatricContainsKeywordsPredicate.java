package cms.model.person;

import java.util.List;
import java.util.function.Predicate;

import cms.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code NusMatric} matches any of the keywords given.
 */
public class NusMatricContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public NusMatricContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = (keywords == null) ? List.of() : keywords;
    }

    @Override
    public boolean test(Person person) {
        if (keywords == null || keywords.isEmpty()) {
            return false;
        }
        return keywords.stream()
                .anyMatch(keyword -> person.getNusMatric() != null
                        && person.getNusMatric().value.equalsIgnoreCase(keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof NusMatricContainsKeywordsPredicate)) {
            return false;
        }
        NusMatricContainsKeywordsPredicate otherPredicate = (NusMatricContainsKeywordsPredicate) other;
        return keywords.equals(otherPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
