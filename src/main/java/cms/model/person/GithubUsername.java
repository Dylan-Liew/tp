package cms.model.person;

import static cms.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's Github username in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidGithubUsername(String)}
 */
public class GithubUsername {

    public static final String MESSAGE_CONSTRAINTS =
            "Github username must be 1-39 characters, using only alphanumeric characters or hyphens, "
                + "cannot start or end with a hyphen, and cannot contain consecutive hyphens.";
    public static final String VALIDATION_REGEX = "^(?=.{1,39}$)(?!-)(?!.*--)[a-zA-Z0-9-]+(?<!-)$";
    public final String value;

    /**
     * Constructs a {@code GithubUsername}.
     *
     * @param githubUsername A valid Github username.
     */
    public GithubUsername(String githubUsername) {
        requireNonNull(githubUsername);
        checkArgument(isValidGithubUsername(githubUsername), MESSAGE_CONSTRAINTS);
        value = githubUsername;
    }

    /**
     * Returns true if a given string is a valid Github username.
     */
    public static boolean isValidGithubUsername(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof GithubUsername)) {
            return false;
        }

        GithubUsername otherGithubUsername = (GithubUsername) other;
        return value.equals(otherGithubUsername.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
