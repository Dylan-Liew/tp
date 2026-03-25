package cms.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import cms.model.person.Person;
import cms.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void format_unmasked_returnsRawValues() {
        Person person = new PersonBuilder()
                .withPhone("12345678")
                .withEmail("abcdefg@u.nus.edu")
                .withSocUsername("socuser")
                .withGithubUsername("gituser")
                .build();

        String formatted = Messages.format(person, false);

        assertTrue(formatted.contains("Phone: 12345678"));
        assertTrue(formatted.contains("Email: abcdefg@u.nus.edu"));
        assertTrue(formatted.contains("SoC Username: socuser"));
        assertTrue(formatted.contains("GitHub Username: gituser"));
    }

    @Test
    public void format_masked_masksSensitiveFields() {
        Person person = new PersonBuilder()
                .withPhone("12345678")
                .withEmail("abcdefg@u.nus.edu")
                .withSocUsername("socuser")
                .withGithubUsername("gituser")
                .build();

        String formatted = Messages.format(person, true);

        assertTrue(formatted.contains("Phone: ****5678"));
        assertTrue(formatted.contains("Email: ********@u.nus.edu"));
        assertTrue(formatted.contains("SoC Username: ********"));
        assertTrue(formatted.contains("GitHub Username: ********"));
    }

    @Test
    public void format_defaultOverload_isUnmasked() {
        Person person = new PersonBuilder().build();
        assertEquals(Messages.format(person, false), Messages.format(person));
    }

    @Test
    public void format_maskedHidesShortValues() {
        Person person = new PersonBuilder()
                .withEmail("ab@u.nus.edu")
                .withGithubUsername("ab")
                .build();

        String formatted = Messages.format(person, true);
        assertTrue(formatted.contains("Email: ********@u.nus.edu"));
        assertTrue(formatted.contains("GitHub Username: ********"));
    }
}
