package cms.model.person;

import static cms.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import cms.commons.util.ToStringBuilder;
import cms.model.tag.Tag;

/**
 * Represents a Person in the course management system.
 * Guarantees: details are present and not null, field values are validated,
 * immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;
    private final NusId nusId;
    private final SocUsername socUsername;
    private final GithubUsername githubUsername;

    // Data fields
    private final Role role;
    private final TutorialGroup tutorialGroup;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, NusId nusId, SocUsername socUsername,
            GithubUsername githubUsername, Role role,
            TutorialGroup tutorialGroup, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, nusId, socUsername, githubUsername, role, tutorialGroup, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.nusId = nusId;
        this.socUsername = socUsername;
        this.githubUsername = githubUsername;
        this.role = role;
        this.tutorialGroup = tutorialGroup;
        this.tags.addAll(tags);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public NusId getNusId() {
        return nusId;
    }

    public SocUsername getSocUsername() {
        return socUsername;
    }

    public GithubUsername getGithubUsername() {
        return githubUsername;
    }

    public Role getRole() {
        return role;
    }

    public TutorialGroup getTutorialGroup() {
        return tutorialGroup;
    }

    /**
     * Returns an immutable tag set, which throws
     * {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same NUS ID.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getNusId().equals(getNusId());
    }

    /**
     * Returns the first conflicting unique field shared with {@code otherPerson}, if any.
     */
    public FieldConflict findConflictingField(Person otherPerson) {
        if (otherPerson == null) {
            return null;
        }

        if (email.equals(otherPerson.email)) {
            return new FieldConflict("email", otherPerson.email.toString());
        }

        if (socUsername.equals(otherPerson.socUsername)) {
            return new FieldConflict("SOC username", otherPerson.socUsername.toString());
        }

        if (githubUsername.equals(otherPerson.githubUsername)) {
            return new FieldConflict("GitHub username", otherPerson.githubUsername.toString());
        }

        return null;
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && nusId.equals(otherPerson.nusId)
                && socUsername.equals(otherPerson.socUsername)
                && githubUsername.equals(otherPerson.githubUsername)
                && role.equals(otherPerson.role)
                && tutorialGroup.equals(otherPerson.tutorialGroup)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, nusId, socUsername, githubUsername, role, tutorialGroup, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("nusId", nusId)
                .add("socUsername", socUsername)
                .add("githubUsername", githubUsername)
                .add("role", role)
                .add("tutorialGroup", tutorialGroup)
                .add("tags", tags)
                .toString();
    }

}
