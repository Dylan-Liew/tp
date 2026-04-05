package cms.model.person;

import static cms.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's NUS Matric in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidNusMatric(String)}
 */
public class NusMatric {

    public static final String MESSAGE_CONSTRAINTS =
            "NUS Matric must be in the format A#######X or U######X with a valid checksum, where # is a digit "
                + "and X is a letter (e.g., A0234567X or U023456W). Legacy U####### input is also accepted "
                + "and canonicalised to U######X (e.g., U0906931 is accepted and canonicalised to U096931E).";
    public static final String VALIDATION_REGEX = "(A\\d{7}|U\\d{6})[A-Z]";
    private static final String LEGACY_U_NUSNET_REGEX = "U\\d{7}";

    private static final String CHECK_DIGIT_TABLE = "YXWURNMLJHEAB";
    private static final int[] A_WEIGHTS = {1, 1, 1, 1, 1, 1};
    private static final int[] U_WEIGHTS = {0, 1, 3, 1, 2, 7};

    public final String value;

    /**
     * Constructs a {@code NusMatric}.
     *
     * @param nusMatric A valid NUS Matric.
     */
    public NusMatric(String nusMatric) {
        requireNonNull(nusMatric);
        String canonical = canonicalise(nusMatric);
        checkArgument(isValidNusMatric(canonical), MESSAGE_CONSTRAINTS);
        value = canonical;
    }

    /**
     * Canonicalises the NUS Matric: trims spaces and converts to uppercase.
     */
    public static String canonicalise(String input) {
        if (input == null) {
            return null;
        }
        String canonical = input.trim().toUpperCase();

        if (canonical.matches(LEGACY_U_NUSNET_REGEX)) {
            return canonicaliseLegacyUNusnet(canonical);
        }

        return canonical;
    }

    /**
     * Canonicalises the legacy U-prefix NUSNET form to the standard U-prefix form.
     */
    private static String canonicaliseLegacyUNusnet(String legacyUNusnet) {
        // Legacy U-prefix NUSNET form has 7 digits; drop the 3rd digit to get U + 6 digits.
        String matricWithoutCheckDigit = legacyUNusnet.substring(0, 3) + legacyUNusnet.substring(4);
        char checkDigit = computeCheckDigit('U', matricWithoutCheckDigit.substring(1));
        return matricWithoutCheckDigit + checkDigit;
    }

    /**
     * Returns true if a given string is a valid NUS Matric.
     */
    public static boolean isValidNusMatric(String test) {
        if (test == null) {
            return false;
        }
        String canonical = canonicalise(test);
        if (!canonical.matches(VALIDATION_REGEX)) {
            return false;
        }

        return hasValidChecksum(canonical);
    }

    private static boolean hasValidChecksum(String canonical) {
        char prefix = canonical.charAt(0);
        char expectedCheckDigit = computeCheckDigit(prefix, canonical.substring(1, canonical.length() - 1));
        char actualCheckDigit = canonical.charAt(canonical.length() - 1);
        return expectedCheckDigit == actualCheckDigit;
    }

    private static char computeCheckDigit(char prefix, String numericPart) {
        int[] weights = prefix == 'A' ? A_WEIGHTS : U_WEIGHTS;
        String digits = numericPart.substring(numericPart.length() - 6);

        int sum = 0;
        for (int i = 0; i < 6; i++) {
            int digit = digits.charAt(i) - '0';
            sum += weights[i] * digit;
        }

        return CHECK_DIGIT_TABLE.charAt(sum % CHECK_DIGIT_TABLE.length());
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
        if (!(other instanceof NusMatric)) {
            return false;
        }

        NusMatric otherNusMatric = (NusMatric) other;
        return value.equals(otherNusMatric.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
