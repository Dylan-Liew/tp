package cms.testutil;

import static cms.logic.parser.CliSyntax.PREFIX_EMAIL;
import static cms.logic.parser.CliSyntax.PREFIX_GITHUBUSERNAME;
import static cms.logic.parser.CliSyntax.PREFIX_MATRIC;
import static cms.logic.parser.CliSyntax.PREFIX_NAME;
import static cms.logic.parser.CliSyntax.PREFIX_PHONE;
import static cms.logic.parser.CliSyntax.PREFIX_ROLE;
import static cms.logic.parser.CliSyntax.PREFIX_SOCUSERNAME;
import static cms.logic.parser.CliSyntax.PREFIX_TAG;
import static cms.logic.parser.CliSyntax.PREFIX_TUTORIALGROUP;

import java.util.Set;

import cms.logic.commands.AddCommand;
import cms.logic.commands.EditCommand.EditPersonDescriptor;
import cms.model.person.Person;
import cms.model.tag.Tag;

/**
 * A utility class for Person.
 */
public class PersonUtil {

    /**
     * Returns an add command string for adding the {@code person}.
     */
    public static String getAddCommand(Person person) {
        return AddCommand.COMMAND_WORD + " " + getPersonDetails(person);
    }

    /**
     * Returns the part of command string for the given {@code person}'s details.
     */
    public static String getPersonDetails(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + person.getName().fullName + " ");
        sb.append(PREFIX_MATRIC + person.getNusMatric().value + " ");
        sb.append(PREFIX_ROLE + person.getRole().value + " ");
        sb.append(PREFIX_SOCUSERNAME + person.getSocUsername().value + " ");
        sb.append(PREFIX_GITHUBUSERNAME + person.getGithubUsername().value + " ");
        sb.append(PREFIX_PHONE + person.getPhone().value + " ");
        sb.append(PREFIX_EMAIL + person.getEmail().value + " ");
        sb.append(PREFIX_TUTORIALGROUP).append(person.getTutorialGroup().value).append(" ");
        person.getTags().stream().forEach(
                s -> sb.append(PREFIX_TAG + s.tagName + " ")
        );
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditPersonDescriptor}'s details.
     */
    public static String getEditPersonDescriptorDetails(EditPersonDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name -> sb.append(PREFIX_NAME).append(name.fullName).append(" "));
        descriptor.getPhone().ifPresent(phone -> sb.append(PREFIX_PHONE).append(phone.value).append(" "));
        descriptor.getEmail().ifPresent(email -> sb.append(PREFIX_EMAIL).append(email.value).append(" "));
        descriptor.getNusMatric().ifPresent(nusMatric -> sb.append(PREFIX_MATRIC).append(nusMatric.value).append(" "));
        descriptor.getRole().ifPresent(role -> sb.append(PREFIX_ROLE).append(role.value).append(" "));
        descriptor.getSocUsername().ifPresent(
            socUsername -> sb.append(PREFIX_SOCUSERNAME).append(socUsername.value).append(" "));
        descriptor.getGithubUsername().ifPresent(
            githubUsername -> sb.append(PREFIX_GITHUBUSERNAME).append(githubUsername.value).append(" "));
        descriptor.getTutorialGroup().ifPresent(
            tutorialGroup -> sb.append(PREFIX_TUTORIALGROUP).append(tutorialGroup.value).append(" "));
        if (descriptor.getTags().isPresent()) {
            Set<Tag> tags = descriptor.getTags().get();
            if (tags.isEmpty()) {
                sb.append(PREFIX_TAG);
            } else {
                tags.forEach(s -> sb.append(PREFIX_TAG).append(s.tagName).append(" "));
            }
        }
        return sb.toString();
    }
}
