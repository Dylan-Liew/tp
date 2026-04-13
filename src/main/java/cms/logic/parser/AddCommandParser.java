package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.parser.CliSyntax.PREFIX_EMAIL;
import static cms.logic.parser.CliSyntax.PREFIX_GITHUBUSERNAME;
import static cms.logic.parser.CliSyntax.PREFIX_MATRIC;
import static cms.logic.parser.CliSyntax.PREFIX_NAME;
import static cms.logic.parser.CliSyntax.PREFIX_PHONE;
import static cms.logic.parser.CliSyntax.PREFIX_ROLE;
import static cms.logic.parser.CliSyntax.PREFIX_SOCUSERNAME;
import static cms.logic.parser.CliSyntax.PREFIX_TAG;
import static cms.logic.parser.CliSyntax.PREFIX_TUTORIALGROUP;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cms.logic.Messages;
import cms.logic.commands.AddCommand;
import cms.logic.parser.exceptions.ParseException;
import cms.model.person.Email;
import cms.model.person.GithubUsername;
import cms.model.person.Name;
import cms.model.person.NusMatric;
import cms.model.person.Person;
import cms.model.person.Phone;
import cms.model.person.Role;
import cms.model.person.SocUsername;
import cms.model.person.TutorialGroup;
import cms.model.person.exceptions.InvalidPersonException;
import cms.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    private static final List<Prefix> REQUIRED_PREFIXES = List.of(PREFIX_NAME, PREFIX_MATRIC,
            PREFIX_SOCUSERNAME, PREFIX_GITHUBUSERNAME, PREFIX_EMAIL, PREFIX_PHONE, PREFIX_TUTORIALGROUP);

    private static final List<String> REQUIRED_FIELD_NAMES = List.of("name", "nus matric", "soc username",
            "github username", "email", "phone", "tutorial group");

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_MATRIC, PREFIX_ROLE,
                        PREFIX_SOCUSERNAME, PREFIX_GITHUBUSERNAME, PREFIX_EMAIL,
                        PREFIX_PHONE, PREFIX_TUTORIALGROUP, PREFIX_TAG);

        String usageMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(usageMessage);
        }

        List<String> missingRequiredFields = getMissingRequiredFields(argMultimap);
        if (!missingRequiredFields.isEmpty()) {
            String missingFieldMessage = missingRequiredFields.size() == 1
                    ? Messages.getMissingRequiredAddFieldMessage(missingRequiredFields.get(0))
                    : "Missing required fields: " + String.join(", ", missingRequiredFields);
            throw new ParseException(missingFieldMessage + "\n" + usageMessage);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_MATRIC, PREFIX_ROLE,
                PREFIX_SOCUSERNAME, PREFIX_GITHUBUSERNAME, PREFIX_EMAIL,
                PREFIX_PHONE, PREFIX_TUTORIALGROUP);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        NusMatric nusMatric = ParserUtil.parseNusMatric(argMultimap.getValue(PREFIX_MATRIC).get());
        Role role = argMultimap.getValue(PREFIX_ROLE).isPresent()
            ? ParserUtil.parseRole(argMultimap.getValue(PREFIX_ROLE).get())
            : Role.STUDENT;
        SocUsername socUsername = ParserUtil.parseSocUsername(argMultimap.getValue(PREFIX_SOCUSERNAME).get());
        GithubUsername githubUsername = ParserUtil.parseGithubUsername(
                argMultimap.getValue(PREFIX_GITHUBUSERNAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        TutorialGroup tutorialGroup = ParserUtil.parseTutorialGroup(argMultimap.getValue(PREFIX_TUTORIALGROUP).get());
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        Person person;
        try {
            person = Person.create(name, phone, email, nusMatric, socUsername,
                githubUsername, role, tutorialGroup, tagList);
        } catch (InvalidPersonException e) {
            throw new ParseException(e.getMessage(), e);
        }
        return new AddCommand(person);
    }

    private List<String> getMissingRequiredFields(ArgumentMultimap argMultimap) {
        List<String> missingFields = new ArrayList<>();

        for (int i = 0; i < REQUIRED_PREFIXES.size(); i++) {
            if (argMultimap.getValue(REQUIRED_PREFIXES.get(i)).isEmpty()) {
                missingFields.add(REQUIRED_FIELD_NAMES.get(i));
            }
        }
        return missingFields;
    }

}
