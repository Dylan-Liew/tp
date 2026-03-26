package cms.model.person;

import java.util.List;
import java.util.function.Predicate;

import cms.commons.util.StringUtil;
import cms.commons.util.ToStringBuilder;

/**
 * Tests that any field of {@code Person} contains any of the given keywords.
 */
public class AllFieldsContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public AllFieldsContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = (keywords == null) ? List.of() : keywords;
    }

    @Override
    public boolean test(Person person) {
        if (keywords == null || keywords.isEmpty()) {
            return false;
        }
        for (String keyword : keywords) {
            boolean matchesName = StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword);
            boolean matchesNusId = person.getNusId() != null
                    && person.getNusId().value.equalsIgnoreCase(keyword);
            boolean matchesPhone = person.getPhone() != null && person.getPhone().value.contains(keyword);
            boolean matchesEmail = person.getEmail() != null && person.getEmail().value.contains(keyword);
            boolean matchesSoc = person.getSocUsername() != null && person.getSocUsername().value.contains(keyword);
            boolean matchesGithub = person.getGithubUsername() != null
                    && person.getGithubUsername().value.contains(keyword);
            boolean matchesRole = person.getRole() != null
                    && person.getRole().value.equalsIgnoreCase(keyword);
            boolean matchesTutorial = person.getTutorialGroup() != null
                    && person.getTutorialGroup().toString().contains(keyword);
            boolean matchesTag = person.getTags().stream()
                    .anyMatch(tag -> tag.tagName.equalsIgnoreCase(keyword));

            if (matchesName
                    || matchesNusId
                    || matchesPhone
                    || matchesEmail
                    || matchesSoc
                    || matchesGithub
                    || matchesRole
                    || matchesTutorial
                    || matchesTag) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AllFieldsContainsKeywordsPredicate)) {
            return false;
        }
        AllFieldsContainsKeywordsPredicate otherPredicate = (AllFieldsContainsKeywordsPredicate) other;
        return keywords.equals(otherPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
