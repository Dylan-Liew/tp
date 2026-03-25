package cms.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Set;
import java.util.function.Predicate;

import cms.commons.util.ToStringBuilder;
import cms.model.tag.Tag;

/**
 * Tests that a {@code Person} matches the requested tags and tutorial groups.
 * Matching is case-insensitive. All requested tags must be present on the person.
 * If tutorial groups are provided, the person must belong to any one of them.
 */
public class TagTutorialGroupMatchesPredicate implements Predicate<Person> {

    private final Set<Tag> tags;
    private final Set<TutorialGroup> tutorialGroups;

    /**
     * Creates a predicate that matches persons by tags and tutorial groups.
     *
     * @param tags Tags to match against a person's tags.
     * @param tutorialGroups Tutorial groups to match against a person's tutorial group.
     */
    public TagTutorialGroupMatchesPredicate(Set<Tag> tags, Set<TutorialGroup> tutorialGroups) {
        requireNonNull(tags);
        requireNonNull(tutorialGroups);
        this.tags = Set.copyOf(tags);
        this.tutorialGroups = Set.copyOf(tutorialGroups);
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);

        boolean matchesTags = tags.isEmpty() || tags.stream()
                .allMatch(filterTag -> person.getTags().stream()
                        .anyMatch(personTag -> personTag.tagName.equalsIgnoreCase(filterTag.tagName)));

        boolean matchesTutorialGroups = tutorialGroups.isEmpty()
                || tutorialGroups.stream()
                .anyMatch(filterGroup -> filterGroup.equals(person.getTutorialGroup()));

        return matchesTags && matchesTutorialGroups;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TagTutorialGroupMatchesPredicate)) {
            return false;
        }

        TagTutorialGroupMatchesPredicate otherPredicate = (TagTutorialGroupMatchesPredicate) other;
        return tags.equals(otherPredicate.tags)
                && tutorialGroups.equals(otherPredicate.tutorialGroups);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(tags, tutorialGroups);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("tags", tags)
                .add("tutorialGroups", tutorialGroups)
                .toString();
    }
}
