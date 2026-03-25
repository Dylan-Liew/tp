package cms.model.person;

import static cms.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's tutorial group in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidTutorialGroup(String)}
 */
public class TutorialGroup {

    public static final String MESSAGE_CONSTRAINTS =
        "Tutorial group should be a number between 1 and 99 (leading zeros are allowed).";
    public static final String VALIDATION_REGEX = "0*[1-9][0-9]?";
    public final int value;

    /**
     * Constructs a {@code TutorialGroup}.
     *
     * @param tutorialGroup A valid tutorial group.
     */
    public TutorialGroup(String tutorialGroup) {
        requireNonNull(tutorialGroup);
        String canonical = canonicalise(tutorialGroup);
        checkArgument(isValidTutorialGroup(canonical), MESSAGE_CONSTRAINTS);
        value = Integer.parseInt(canonical);
    }

    /**
     * Canonicalises the tutorial group by trimming spaces and removing leading zeros.
     */
    public static String canonicalise(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        return trimmed.replaceFirst("^0+(?!$)", "");
    }

    /**
     * Returns true if a given string is a valid tutorial group.
     */
    public static boolean isValidTutorialGroup(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TutorialGroup)) {
            return false;
        }

        TutorialGroup otherTutorialGroup = (TutorialGroup) other;
        return value == otherTutorialGroup.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
