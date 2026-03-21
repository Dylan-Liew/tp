package cms.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cms.commons.core.index.Index;
import cms.commons.util.StringUtil;
import cms.logic.parser.exceptions.ParseException;
import cms.model.person.Email;
import cms.model.person.GithubUsername;
import cms.model.person.Name;
import cms.model.person.NusId;
import cms.model.person.Phone;
import cms.model.person.Role;
import cms.model.person.SocUsername;
import cms.model.person.TutorialGroup;
import cms.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     *
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses whitespace-separated one-based indexes into a list of {@code Index} objects.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if any specified index is invalid or if no indexes are provided.
     */
    public static List<Index> parseIndexes(String oneBasedIndexes) throws ParseException {
        String trimmedIndexes = oneBasedIndexes.trim();
        if (trimmedIndexes.isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }

        String[] tokens = trimmedIndexes.split("\\s+");
        List<Index> indexes = new ArrayList<>();
        for (String token : tokens) {
            indexes.add(parseIndex(token));
        }
        return indexes;
    }

    /**
     * Parses {@code Collection<String> nusIds} into a list of {@code NusId}s.
     *
     * @throws ParseException if no NUS IDs are provided or if any provided NUS ID is invalid.
     */
    public static List<NusId> parseNusIds(Collection<String> nusIds) throws ParseException {
        requireNonNull(nusIds);
        if (nusIds.isEmpty()) {
            throw new ParseException(NusId.MESSAGE_CONSTRAINTS);
        }

        List<NusId> parsedNusIds = new ArrayList<>();
        for (String nusId : nusIds) {
            parsedNusIds.add(parseNusId(nusId));
        }
        return parsedNusIds;
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a {@code String nusId} into a {@code NusId}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code nusId} is invalid.
     */
    public static NusId parseNusId(String nusId) throws ParseException {
        requireNonNull(nusId);
        String trimmedNusId = nusId.trim();
        if (!NusId.isValidNusId(trimmedNusId)) {
            throw new ParseException(NusId.MESSAGE_CONSTRAINTS);
        }
        return new NusId(trimmedNusId);
    }

    /**
     * Parses a {@code String role} into a {@code Role}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code role} is invalid.
     */
    public static Role parseRole(String role) throws ParseException {
        requireNonNull(role);
        String trimmedRole = role.trim();
        if (!Role.isValidRole(trimmedRole)) {
            throw new ParseException(Role.MESSAGE_CONSTRAINTS);
        }
        return Role.valueOf(trimmedRole.toUpperCase());
    }

    /**
     * Parses a {@code String socUsername} into a {@code SocUsername}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code socUsername} is invalid.
     */
    public static SocUsername parseSocUsername(String socUsername) throws ParseException {
        requireNonNull(socUsername);
        String trimmedSocUsername = socUsername.trim();
        if (!SocUsername.isValidSocUsername(trimmedSocUsername)) {
            throw new ParseException(SocUsername.MESSAGE_CONSTRAINTS);
        }
        return new SocUsername(trimmedSocUsername);
    }

    /**
     * Parses a {@code String githubUsername} into a {@code GithubUsername}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code githubUsername} is invalid.
     */
    public static GithubUsername parseGithubUsername(String githubUsername) throws ParseException {
        requireNonNull(githubUsername);
        String trimmedGithubUsername = githubUsername.trim();
        if (!GithubUsername.isValidGithubUsername(trimmedGithubUsername)) {
            throw new ParseException(GithubUsername.MESSAGE_CONSTRAINTS);
        }
        return new GithubUsername(trimmedGithubUsername);
    }

    /**
     * Parses a {@code String tutorialGroup} into a {@code TutorialGroup}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tutorialGroup} is invalid.
     */
    public static TutorialGroup parseTutorialGroup(String tutorialGroup) throws ParseException {
        requireNonNull(tutorialGroup);
        String trimmedTutorialGroup = tutorialGroup.trim();
        if (!TutorialGroup.isValidTutorialGroup(trimmedTutorialGroup)) {
            throw new ParseException(TutorialGroup.MESSAGE_CONSTRAINTS);
        }
        return new TutorialGroup(trimmedTutorialGroup);
    }
}
