package cms.model.person;

import static cms.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import cms.commons.util.ToStringBuilder;
import cms.model.person.exceptions.InvalidPersonException;
import cms.model.tag.Tag;

/**
 * Represents a Person in the course management system.
 * Guarantees: details are present and not null, field values are validated,
 * immutable.
 */
public abstract class Person {
    public static final String MESSAGE_SOC_USERNAME_NUS_MATRIC_MISMATCH =
            "SOC usernames that are in NUS Matric format must match the person's NUS Matric.";
    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;
    private final NusMatric nusMatric;
    private final SocUsername socUsername;
    private final GithubUsername githubUsername;

    // Data fields
    private final TutorialGroup tutorialGroup;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Every field must be present and not null.
         *
         * @throws InvalidPersonException if any model-level person invariant is violated
     */
    public Person(Name name, Phone phone, Email email, NusMatric nusMatric, SocUsername socUsername,
            GithubUsername githubUsername, TutorialGroup tutorialGroup, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, nusMatric, socUsername, githubUsername, tutorialGroup, tags);
        validateSocUsernameNusMatricConsistency(nusMatric, socUsername);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.nusMatric = nusMatric;
        this.socUsername = socUsername;
        this.githubUsername = githubUsername;
        this.tutorialGroup = tutorialGroup;
        this.tags.addAll(tags);
    }

    /**
     * Creates a role-specific person instance.
     */
    public static Person create(Name name, Phone phone, Email email, NusMatric nusMatric, SocUsername socUsername,
            GithubUsername githubUsername, Role role, TutorialGroup tutorialGroup, Set<Tag> tags) {
        Objects.requireNonNull(role);

        if (role == Role.STUDENT) {
            return new Student(name, phone, email, nusMatric, socUsername, githubUsername, tutorialGroup, tags);
        }
        return new Tutor(name, phone, email, nusMatric, socUsername, githubUsername, tutorialGroup, tags);
    }

    /**
     * Ensures that if SOC username uses NUS Matric format, it matches this person's NUS Matric.
     */
    private static void validateSocUsernameNusMatricConsistency(NusMatric nusMatric, SocUsername socUsername) {
        if (NusMatric.isValidNusMatric(socUsername.value)
                && !NusMatric.canonicalise(socUsername.value).equals(nusMatric.value)) {
            throw new InvalidPersonException(MESSAGE_SOC_USERNAME_NUS_MATRIC_MISMATCH);
        }
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

    public NusMatric getNusMatric() {
        return nusMatric;
    }

    public SocUsername getSocUsername() {
        return socUsername;
    }

    public GithubUsername getGithubUsername() {
        return githubUsername;
    }

    public abstract Role getRole();

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
     * Returns true if both persons have the same NUS Matric.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getNusMatric().equals(getNusMatric());
    }

    /**
     * Returns the first conflicting unique field shared with {@code otherPerson}, if any.
     */
    public FieldConflict findConflictingField(Person otherPerson) {
        if (otherPerson == null) {
            return null;
        }

        if (email.equals(otherPerson.email)) {
            return new FieldConflict(FieldConflict.Type.EMAIL, otherPerson);
        }

        if (socUsername.equals(otherPerson.socUsername)) {
            return new FieldConflict(FieldConflict.Type.SOC_USERNAME, otherPerson);
        }

        if (githubUsername.equals(otherPerson.githubUsername)) {
            return new FieldConflict(FieldConflict.Type.GITHUB_USERNAME, otherPerson);
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
                && nusMatric.equals(otherPerson.nusMatric)
                && socUsername.equals(otherPerson.socUsername)
                && githubUsername.equals(otherPerson.githubUsername)
                && getRole().equals(otherPerson.getRole())
                && tutorialGroup.equals(otherPerson.tutorialGroup)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, nusMatric, socUsername, githubUsername, getRole(), tutorialGroup, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("nusMatric", nusMatric)
                .add("socUsername", socUsername)
                .add("githubUsername", githubUsername)
                .add("role", getRole())
                .add("tutorialGroup", tutorialGroup)
                .add("tags", tags)
                .toString();
    }

}
