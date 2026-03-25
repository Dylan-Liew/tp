package cms.commons.util;

import cms.model.person.Email;
import cms.model.person.GithubUsername;
import cms.model.person.Phone;
import cms.model.person.SocUsername;

/**
 * Utility methods for masking sensitive values in displays.
 */
public class MaskingUtil {

    private static final String MASK = "*";
    private static final String EMAIL_MASK = "********";
    private static final String USERNAME_MASK = "********";

    /**
     * Masks a phone number by showing only the last 4 characters.
     */
    public static String maskPhone(Phone phone) {
        return maskPhoneValue(phone == null ? null : phone.value);
    }

    /**
     * Masks an email by replacing everything before '@' with 8 asterisks.
     */
    public static String maskEmail(Email email) {
        return maskEmailValue(email == null ? null : email.value);
    }

    /**
     * Masks a SOC username by keeping only first 3 characters.
     */
    public static String maskSocUsername(SocUsername socUsername) {
        if (socUsername == null) {
            return null;
        }
        return USERNAME_MASK;
    }

    /**
     * Masks a GitHub username by keeping only first 3 characters.
     */
    public static String maskGithubUsername(GithubUsername githubUsername) {
        if (githubUsername == null) {
            return null;
        }
        return USERNAME_MASK;
    }

    private static String maskPhoneValue(String phone) {
        if (phone == null || phone.isEmpty()) {
            return phone;
        }
        if (phone.length() <= 4) {
            return MASK.repeat(phone.length());
        }
        return MASK.repeat(phone.length() - 4) + phone.substring(phone.length() - 4);
    }

    private static String maskEmailValue(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) {
            return EMAIL_MASK;
        }
        String domain = email.substring(atIndex);
        return EMAIL_MASK + domain;
    }
}
