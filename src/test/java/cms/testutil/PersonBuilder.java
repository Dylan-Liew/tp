package cms.testutil;

import java.util.HashSet;
import java.util.Set;

import cms.model.person.Email;
import cms.model.person.GithubUsername;
import cms.model.person.Name;
import cms.model.person.NusMatric;
import cms.model.person.Person;
import cms.model.person.Phone;
import cms.model.person.Role;
import cms.model.person.SocUsername;
import cms.model.person.TutorialGroup;
import cms.model.tag.Tag;
import cms.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_NUSMATRIC = "A0000001X";
    public static final String DEFAULT_SOCUSERNAME = "amybee";
    public static final String DEFAULT_GITHUBUSERNAME = "amybee";
    public static final Role DEFAULT_ROLE = Role.STUDENT;
    public static final String DEFAULT_TUTORIALGROUP = "1";

    private Name name;
    private Phone phone;
    private Email email;
    private NusMatric nusMatric;
    private SocUsername socUsername;
    private GithubUsername githubUsername;
    private Role role;
    private TutorialGroup tutorialGroup;
    private Set<Tag> tags;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        nusMatric = new NusMatric(DEFAULT_NUSMATRIC);
        socUsername = new SocUsername(DEFAULT_SOCUSERNAME);
        githubUsername = new GithubUsername(DEFAULT_GITHUBUSERNAME);
        role = DEFAULT_ROLE;
        tutorialGroup = new TutorialGroup(DEFAULT_TUTORIALGROUP);
        tags = new HashSet<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        nusMatric = personToCopy.getNusMatric();
        socUsername = personToCopy.getSocUsername();
        githubUsername = personToCopy.getGithubUsername();
        role = personToCopy.getRole();
        tutorialGroup = personToCopy.getTutorialGroup();
        tags = new HashSet<>(personToCopy.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Sets the {@code NusMatric} of the {@code Person} that we are building.
     */
    public PersonBuilder withNusMatric(String nusMatric) {
        this.nusMatric = new NusMatric(nusMatric);
        return this;
    }

    /**
     * Sets the {@code SocUsername} of the {@code Person} that we are building.
     */
    public PersonBuilder withSocUsername(String socUsername) {
        this.socUsername = new SocUsername(socUsername);
        return this;
    }

    /**
     * Sets the {@code GithubUsername} of the {@code Person} that we are building.
     */
    public PersonBuilder withGithubUsername(String githubUsername) {
        this.githubUsername = new GithubUsername(githubUsername);
        return this;
    }

    /**
     * Sets the {@code Role} of the {@code Person} that we are building.
     */
    public PersonBuilder withRole(String role) {
        this.role = Role.valueOf(role.toUpperCase());
        return this;
    }

    /**
     * Sets the {@code TutorialGroup} of the {@code Person} that we are building.
     */
    public PersonBuilder withTutorialGroup(String tutorialGroup) {
        this.tutorialGroup = new TutorialGroup(tutorialGroup);
        return this;
    }

    public Person build() {
        return Person.create(name, phone, email, nusMatric, socUsername, githubUsername, role, tutorialGroup, tags);
    }

}

