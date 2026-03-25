package cms.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import cms.model.person.Email;
import cms.model.person.GithubUsername;
import cms.model.person.Phone;
import cms.model.person.SocUsername;

public class MaskingUtilTest {

    @Test
    public void constructor() {
        new MaskingUtil();
    }

    @Test
    public void maskPhone() {
        assertNull(MaskingUtil.maskPhone(null));
        assertEquals("***", MaskingUtil.maskPhone(new Phone("123")));
        assertEquals("****", MaskingUtil.maskPhone(new Phone("1234")));
        assertEquals("****5678", MaskingUtil.maskPhone(new Phone("12345678")));
    }

    @Test
    public void maskEmail() {
        assertNull(MaskingUtil.maskEmail(null));
        assertEquals("********@u.nus.edu", MaskingUtil.maskEmail(new Email("abc@u.nus.edu")));
        assertEquals("********@u.nus.edu", MaskingUtil.maskEmail(new Email("abcdefg@u.nus.edu")));
        assertEquals("********@u.nus.edu", MaskingUtil.maskEmail(new Email("ab@u.nus.edu")));
    }

    @Test
    public void maskSocUsername() {
        assertNull(MaskingUtil.maskSocUsername(null));
        assertEquals("********", MaskingUtil.maskSocUsername(new SocUsername("abcde")));
        assertEquals("********", MaskingUtil.maskSocUsername(new SocUsername("abcdefgh")));
    }

    @Test
    public void maskGithubUsername() {
        assertNull(MaskingUtil.maskGithubUsername(null));
        assertEquals("********", MaskingUtil.maskGithubUsername(new GithubUsername("abc")));
        assertEquals("********", MaskingUtil.maskGithubUsername(new GithubUsername("abcdefg")));
        assertEquals("********", MaskingUtil.maskGithubUsername(new GithubUsername("ab")));
    }

    @Test
    public void privateGuards_phoneEmptyAndInvalidEmail() throws Exception {
        Method maskPhoneValue = MaskingUtil.class.getDeclaredMethod("maskPhoneValue", String.class);
        maskPhoneValue.setAccessible(true);
        assertEquals("", (String) maskPhoneValue.invoke(null, ""));

        Method maskEmailValue = MaskingUtil.class.getDeclaredMethod("maskEmailValue", String.class);
        maskEmailValue.setAccessible(true);
        assertEquals("", (String) maskEmailValue.invoke(null, ""));
        assertEquals("********", (String) maskEmailValue.invoke(null, "abc"));
        assertEquals("********", (String) maskEmailValue.invoke(null, "abc@"));
    }
}
