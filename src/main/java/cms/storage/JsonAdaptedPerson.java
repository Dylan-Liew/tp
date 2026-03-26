package cms.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import cms.commons.exceptions.IllegalValueException;
import cms.model.person.Email;
import cms.model.person.GithubUsername;
import cms.model.person.Name;
import cms.model.person.NusId;
import cms.model.person.Person;
import cms.model.person.Phone;
import cms.model.person.Role;
import cms.model.person.SocUsername;
import cms.model.person.TutorialGroup;
import cms.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String nusId;
    private final String socUsername;
    private final String githubUsername;
    private final String role;
    private final String tutorialGroup;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name,
                             @JsonProperty("phone") String phone,
                             @JsonProperty("email") String email,
                             @JsonProperty("nusId") String nusId,
                             @JsonProperty("socUsername") String socUsername,
                             @JsonProperty("githubUsername") String githubUsername,
                             @JsonProperty("role") String role,
                             @JsonProperty("tutorialGroup") String tutorialGroup,
                             @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.nusId = nusId;
        this.socUsername = socUsername;
        this.githubUsername = githubUsername;
        this.role = role;
        this.tutorialGroup = tutorialGroup;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        nusId = source.getNusId().value;
        socUsername = source.getSocUsername().value;
        githubUsername = source.getGithubUsername().value;
        role = source.getRole().value;
        tutorialGroup = String.valueOf(source.getTutorialGroup().value);
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        String canonicalName = Name.canonicalise(name);
        if (!Name.isValidName(canonicalName)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(canonicalName);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (nusId == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, NusId.class.getSimpleName()));
        }
        if (!NusId.isValidNusId(nusId)) {
            throw new IllegalValueException(NusId.MESSAGE_CONSTRAINTS);
        }
        final NusId modelNusId = new NusId(nusId);

        if (socUsername == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    SocUsername.class.getSimpleName()));
        }
        if (!SocUsername.isValidSocUsername(socUsername)) {
            throw new IllegalValueException(SocUsername.MESSAGE_CONSTRAINTS);
        }
        final SocUsername modelSocUsername = new SocUsername(socUsername);

        if (githubUsername == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    GithubUsername.class.getSimpleName()));
        }
        if (!GithubUsername.isValidGithubUsername(githubUsername)) {
            throw new IllegalValueException(GithubUsername.MESSAGE_CONSTRAINTS);
        }
        final GithubUsername modelGithubUsername = new GithubUsername(githubUsername);

        if (role == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Role.class.getSimpleName()));
        }
        if (!Role.isValidRole(role)) {
            throw new IllegalValueException(Role.MESSAGE_CONSTRAINTS);
        }
        final Role modelRole = Role.valueOf(role.toUpperCase());

        if (tutorialGroup == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    TutorialGroup.class.getSimpleName()));
        }
        if (!TutorialGroup.isValidTutorialGroup(tutorialGroup)) {
            throw new IllegalValueException(TutorialGroup.MESSAGE_CONSTRAINTS);
        }
        final TutorialGroup modelTutorialGroup = new TutorialGroup(tutorialGroup);

        final Set<Tag> modelTags = new HashSet<>(personTags);
        return new Person(modelName, modelPhone, modelEmail, modelNusId,
                modelSocUsername, modelGithubUsername,
                modelRole, modelTutorialGroup, modelTags);
    }

}
