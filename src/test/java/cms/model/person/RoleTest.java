package cms.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RoleTest {
    @Test
    public void validRoles() {
        // valid roles
        assertTrue(Role.isValidRole("student"));
        assertTrue(Role.isValidRole("tutor"));
    }

    @Test
    public void invalidRoles() {
        // invalid roles
        assertFalse(Role.isValidRole("admin"));
        assertFalse(Role.isValidRole("STUDENT")); // case-sensitive
        assertFalse(Role.isValidRole("TUTOR"));
        assertFalse(Role.isValidRole("student ")); // trailing space
        assertFalse(Role.isValidRole(" stu dent")); // internal space
        assertFalse(Role.isValidRole("")); // empty
        assertFalse(Role.isValidRole("student1")); // extra char
    }
}
