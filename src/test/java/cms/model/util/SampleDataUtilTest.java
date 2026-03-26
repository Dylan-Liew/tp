package cms.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import cms.model.ReadOnlyAddressBook;
import cms.model.person.Person;
import cms.model.person.Role;

public class SampleDataUtilTest {

    @Test
    public void getSamplePersons_returnsAllStudents() {
        Person[] samplePersons = SampleDataUtil.getSamplePersons();

        assertEquals(6, samplePersons.length);
        for (Person samplePerson : samplePersons) {
            assertEquals(Role.STUDENT, samplePerson.getRole());
        }
    }

    @Test
    public void getSampleAddressBook_containsSamplePersons() {
        Person[] samplePersons = SampleDataUtil.getSamplePersons();
        ReadOnlyAddressBook sampleAddressBook = SampleDataUtil.getSampleAddressBook();

        assertEquals(samplePersons.length, sampleAddressBook.getPersonList().size());
        for (Person samplePerson : samplePersons) {
            assertTrue(sampleAddressBook.getPersonList().contains(samplePerson));
        }
    }
}
