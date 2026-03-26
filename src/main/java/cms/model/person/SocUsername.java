package cms.model.person;

import static cms.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's SOC username in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidSocUsername(String)}
 */
public class SocUsername {

    public static final String MESSAGE_CONSTRAINTS =
            "SOC username must be 5-8 characters (or a valid NUS-ID form), using only lowercase letters, "
                + "digits, and hyphens, and cannot start or end with a hyphen.";
    public static final String VALIDATION_REGEX = "^(?=.{5,8}$)(?!-)[a-z0-9-]+(?<!-)$|"
        + NusId.VALIDATION_REGEX; // Either 5-8 chars or valid NUS ID
    public final String value;

    /**
     * Constructs a {@code SocUsername}.
     *
     * @param socUsername A valid SOC username.
     */
    public SocUsername(String socUsername) {
        requireNonNull(socUsername);
        String canonical = canonicalise(socUsername);
        checkArgument(isValidSocUsername(canonical), MESSAGE_CONSTRAINTS);
        value = canonical;
    }

    /**
     * Canonicalises the soc username: trims spaces and converts to lowercase.
     */
    public static String canonicalise(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().toLowerCase();
    }

    /**
     * Returns true if a given string is a valid SOC username.
     */
    public static boolean isValidSocUsername(String test) {
        return test.matches(VALIDATION_REGEX) || NusId.isValidNusId(test);
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
        if (!(other instanceof SocUsername)) {
            return false;
        }

        SocUsername otherSocUsername = (SocUsername) other;
        return value.equals(otherSocUsername.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
