package cms.testutil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cms.logic.commands.EditCommand.EditPersonDescriptor;
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
 * A utility class to help with building EditPersonDescriptor objects.
 */
public class EditPersonDescriptorBuilder {

    private EditPersonDescriptor descriptor;

    public EditPersonDescriptorBuilder() {
        descriptor = new EditPersonDescriptor();
    }

    public EditPersonDescriptorBuilder(EditPersonDescriptor descriptor) {
        this.descriptor = new EditPersonDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditPersonDescriptor} with fields containing {@code person}'s details
     */
    public EditPersonDescriptorBuilder(Person person) {
        descriptor = new EditPersonDescriptor();
        descriptor.setName(person.getName());
        descriptor.setPhone(person.getPhone());
        descriptor.setEmail(person.getEmail());
        descriptor.setNusId(person.getNusId());
        descriptor.setRole(person.getRole());
        descriptor.setSocUsername(person.getSocUsername());
        descriptor.setGithubUsername(person.getGithubUsername());
        descriptor.setTutorialGroup(person.getTutorialGroup());
        descriptor.setTags(person.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withEmail(String email) {
        descriptor.setEmail(new Email(email));
        return this;
    }

    /**
     * Sets the {@code NusId} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withNusId(String nusId) {
        descriptor.setNusId(new NusId(nusId));
        return this;
    }

    /**
     * Sets the {@code Role} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withRole(String role) {
        descriptor.setRole(Role.valueOf(role.toUpperCase()));
        return this;
    }

    /**
     * Sets the {@code SocUsername} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withSocUsername(String socUsername) {
        descriptor.setSocUsername(new SocUsername(socUsername));
        return this;
    }

    /**
     * Sets the {@code GithubUsername} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withGithubUsername(String githubUsername) {
        descriptor.setGithubUsername(new GithubUsername(githubUsername));
        return this;
    }

    /**
     * Sets the {@code TutorialGroup} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withTutorialGroup(String tutorialGroup) {
        descriptor.setTutorialGroup(new TutorialGroup(tutorialGroup));
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code EditPersonDescriptor}
     * that we are building.
     */
    public EditPersonDescriptorBuilder withTags(String... tags) {
        Set<Tag> tagSet = Stream.of(tags).map(Tag::new).collect(Collectors.toSet());
        descriptor.setTags(tagSet);
        return this;
    }

    public EditPersonDescriptor build() {
        return descriptor;
    }
}
