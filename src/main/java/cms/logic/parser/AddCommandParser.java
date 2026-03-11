package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cms.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static cms.logic.parser.CliSyntax.PREFIX_EMAIL;
import static cms.logic.parser.CliSyntax.PREFIX_NAME;
import static cms.logic.parser.CliSyntax.PREFIX_PHONE;
import static cms.logic.parser.CliSyntax.PREFIX_TAG;
import static cms.logic.parser.CliSyntax.PREFIX_NUSID;
import static cms.logic.parser.CliSyntax.PREFIX_SOCUSERNAME;
import static cms.logic.parser.CliSyntax.PREFIX_GITHUBUSERNAME;
import static cms.logic.parser.CliSyntax.PREFIX_ROLE;
import static cms.logic.parser.CliSyntax.PREFIX_TUTORIALGROUP;

import java.util.Set;
import java.util.stream.Stream;

import cms.logic.commands.AddCommand;
import cms.logic.parser.exceptions.ParseException;
import cms.model.person.Address;
import cms.model.person.Email;
import cms.model.person.Name;
import cms.model.person.Person;
import cms.model.person.Phone;
import cms.model.tag.Tag;
import cms.model.role.Role;
import cms.model.person.NusId;
import cms.model.person.SocUsername;
import cms.model.person.GithubUsername;
import cms.model.person.TutorialGroup;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_NUSID, PREFIX_ROLE, PREFIX_SOCUSERNAME, PREFIX_GITHUBUSERNAME, PREFIX_EMAIL, PREFIX_PHONE, PREFIX_ADDRESS, PREFIX_TUTORIALGROUP, PREFIX_TAG);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_NUSID, PREFIX_ROLE, PREFIX_SOCUSERNAME, PREFIX_GITHUBUSERNAME, PREFIX_EMAIL, PREFIX_PHONE, PREFIX_ADDRESS, PREFIX_TUTORIALGROUP)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_NUSID, PREFIX_ROLE, PREFIX_SOCUSERNAME, PREFIX_GITHUBUSERNAME, PREFIX_EMAIL, PREFIX_PHONE, PREFIX_ADDRESS, PREFIX_TUTORIALGROUP);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        NusId nusId = ParserUtil.parseNusId(argMultimap.getValue(PREFIX_NUSID).get());
        Role role = ParserUtil.parseRole(argMultimap.getValue(PREFIX_ROLE).get());
        SocUsername socUsername = ParserUtil.parseSocUsername(argMultimap.getValue(PREFIX_SOCUSERNAME).get());
        GithubUsername githubUsername = ParserUtil.parseGithubUsername(argMultimap.getValue(PREFIX_GITHUBUSERNAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
        TutorialGroup tutorialGroup = ParserUtil.parseTutorialGroup(argMultimap.getValue(PREFIX_TUTORIALGROUP).get());
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        Person person = new Person(name, nusId, role, socUsername, githubUsername, phone, email, address, tutorialGroup, tagList);

        return new AddCommand(person);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
