package cms.model.person;

import java.util.List;
import java.util.function.Predicate;

import cms.commons.util.StringUtil;
import cms.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} or {@code NusMatric} matches any of the keywords given.
 */
public class NameOrNusMatricContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> nameKeywords;
    private final List<String> idKeywords;

    /**
     * Constructs a {@code NameOrNusMatricContainsKeywordsPredicate}.
     *
     * @param nameKeywords list of name keywords (case-insensitive match)
     * @param idKeywords list of NUS Matric keywords (case-insensitive match)
     */
    public NameOrNusMatricContainsKeywordsPredicate(List<String> nameKeywords, List<String> idKeywords) {
        this.nameKeywords = (nameKeywords == null) ? List.of() : nameKeywords;
        this.idKeywords = (idKeywords == null) ? List.of() : idKeywords;
    }

    @Override
    public boolean test(Person person) {
        boolean nameMatches = nameKeywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword));

        boolean idMatches = idKeywords.stream()
                .anyMatch(keyword -> person.getNusMatric() != null
                        && person.getNusMatric().value.equalsIgnoreCase(keyword));

        return nameMatches || idMatches;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof NameOrNusMatricContainsKeywordsPredicate)) {
            return false;
        }
        NameOrNusMatricContainsKeywordsPredicate otherPredicate = (NameOrNusMatricContainsKeywordsPredicate) other;
        return nameKeywords.equals(otherPredicate.nameKeywords) && idKeywords.equals(otherPredicate.idKeywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameKeywords", nameKeywords)
                .add("idKeywords", idKeywords)
                .toString();
    }
}
