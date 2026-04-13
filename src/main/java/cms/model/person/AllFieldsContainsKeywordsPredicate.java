package cms.model.person;

import java.util.List;
import java.util.Locale;
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
            String tutorialKeyword = keyword.replaceFirst("(?i)^T(?=0*[1-9][0-9]?$)", "");
            boolean matchesName = StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword);
            boolean matchesNusMatric = person.getNusMatric() != null
                    && person.getNusMatric().value.equalsIgnoreCase(keyword);
            boolean matchesPhone = person.getPhone() != null && containsIgnoreCase(person.getPhone().value, keyword);
            boolean matchesEmail = person.getEmail() != null && containsIgnoreCase(person.getEmail().value, keyword);
            boolean matchesSoc = person.getSocUsername() != null
                    && containsIgnoreCase(person.getSocUsername().value, keyword);
            boolean matchesGithub = person.getGithubUsername() != null
                    && containsIgnoreCase(person.getGithubUsername().value, keyword);
            boolean matchesRole = person.getRole() != null
                    && person.getRole().value.equalsIgnoreCase(keyword);
            boolean matchesTutorial = person.getTutorialGroup() != null
                    && TutorialGroup.isValidTutorialGroup(tutorialKeyword)
                    && person.getTutorialGroup().toString().equals(TutorialGroup.canonicalise(tutorialKeyword));
            boolean matchesTag = person.getTags().stream()
                    .anyMatch(tag -> tag.tagName.equalsIgnoreCase(keyword));

            if (matchesName
                    || matchesNusMatric
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

    private boolean containsIgnoreCase(String source, String keyword) {
        return source.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
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
