package cms.model.person;

import static cms.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final int MAX_LENGTH = 128;

    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain alphabetic characters, spaces, hyphens, apostrophes or periods,"
                    + " and must not be blank";

    public static final String VALIDATION_REGEX =
            "(?=.{1," + MAX_LENGTH + "}$)(?=.*[A-Za-z])[-A-Za-z .']+";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        String canonical = canonicalise(name);
        checkArgument(isValidName(canonical), MESSAGE_CONSTRAINTS);
        fullName = canonical;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Canonicalises the name: strips leading/trailing spaces, collapses multiple spaces, preserves case.
     */
    public static String canonicalise(String input) {
        if (input == null) {
            return null;
        }
        String collapsed = input.trim().replaceAll(" +", " ");
        return collapsed;
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name)) {
            return false;
        }

        Name otherName = (Name) other;
        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
