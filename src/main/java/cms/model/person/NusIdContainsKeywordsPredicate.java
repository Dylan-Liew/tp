package cms.model.person;

import java.util.List;
import java.util.function.Predicate;

import cms.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code NusId} matches any of the keywords given.
 */
public class NusIdContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public NusIdContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = (keywords == null) ? List.of() : keywords;
    }

    @Override
    public boolean test(Person person) {
        if (keywords == null || keywords.isEmpty()) {
            return false;
        }
        return keywords.stream()
                .anyMatch(keyword -> person.getNusId() != null
                        && person.getNusId().value.equalsIgnoreCase(keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof NusIdContainsKeywordsPredicate)) {
            return false;
        }
        NusIdContainsKeywordsPredicate otherPredicate = (NusIdContainsKeywordsPredicate) other;
        return keywords.equals(otherPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
