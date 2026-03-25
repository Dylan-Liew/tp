package cms.model.tag;

import static cms.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Tag in the address book.
 * Guarantees: immutable; name is valid as declared in {@link #isValidTagName(String)}
 */
public class Tag {

    public static final String MESSAGE_CONSTRAINTS =
            "Tags must be alphanumeric or hyphenated, with no spaces, and cannot start or end with a hyphen.";
    public static final String VALIDATION_REGEX = "\\p{Alnum}+(?:-\\p{Alnum}+)*";

    public final String tagName;

    /**
     * Constructs a {@code Tag}.
     *
     * @param tagName A valid tag name.
     */
    public Tag(String tagName) {
        requireNonNull(tagName);
        String canonical = canonicalise(tagName);
        checkArgument(isValidTagName(canonical), MESSAGE_CONSTRAINTS);
        this.tagName = canonical;
    }

    /**
     * Canonicalises the tag: trims spaces and converts to lowercase.
     */
    public static String canonicalise(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().toLowerCase();
    }

    /**
     * Returns true if a given string is a valid tag name.
     */
    public static boolean isValidTagName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Tag)) {
            return false;
        }

        Tag otherTag = (Tag) other;
        return tagName.equals(otherTag.tagName);
    }

    @Override
    public int hashCode() {
        return tagName.hashCode();
    }

    /**
     * Format state as text for viewing.
     */
    public String toString() {
        return '[' + tagName + ']';
    }

}
