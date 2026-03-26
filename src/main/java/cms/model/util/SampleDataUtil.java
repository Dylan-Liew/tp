package cms.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import cms.model.AddressBook;
import cms.model.ReadOnlyAddressBook;
import cms.model.person.Email;
import cms.model.person.GithubUsername;
import cms.model.person.Name;
import cms.model.person.NusId;
import cms.model.person.Person;
import cms.model.person.Phone;
import cms.model.person.Role;
import cms.model.person.SocUsername;
import cms.model.person.TutorialGroup;
import cms.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        return new Person[]{
            new Person(new Name("Alex Yeoh"),
                       new Phone("87438807"),
                       new Email("alexyeoh@example.com"),
                       new NusId("A0123456B"),
                       new SocUsername("alexyeoh"),
                       new GithubUsername("alexyeoh"),
                       Role.STUDENT,
                       new TutorialGroup("01"),
                       getTagSet("friends")),
            new Person(new Name("Bernice Yu"),
                       new Phone("99272758"),
                       new Email("berniceyu@example.com"),
                       new NusId("A0123457B"),
                       new SocUsername("bernice"),
                       new GithubUsername("berniceyu"),
                       Role.STUDENT,
                       new TutorialGroup("10"),
                       getTagSet("colleagues", "friends")),
            new Person(new Name("Charlotte Oliveiro"),
                       new Phone("93210283"),
                       new Email("charlotte@example.com"),
                       new NusId("A0123458B"),
                       new SocUsername("charlote"),
                       new GithubUsername("charlotte"),
                       Role.STUDENT,
                       new TutorialGroup("02"),
                       getTagSet("neighbours")),
            new Person(new Name("David Li"),
                       new Phone("91031282"),
                       new Email("lidavid@example.com"),
                       new NusId("A0123459B"),
                       new SocUsername("davidli"),
                       new GithubUsername("davidli"),
                       Role.STUDENT,
                       new TutorialGroup("01"),
                       getTagSet("family")),
            new Person(new Name("Irfan Ibrahim"),
                       new Phone("92492021"),
                       new Email("irfan@example.com"),
                       new NusId("A0123460B"),
                       new SocUsername("irfan"),
                       new GithubUsername("irfan"),
                       Role.STUDENT,
                       new TutorialGroup("01"),
                       getTagSet("classmates")),
            new Person(new Name("Roy Balakrishnan"),
                       new Phone("92624417"),
                       new Email("royb@example.com"),
                       new NusId("A0123461B"),
                       new SocUsername("roybal"),
                       new GithubUsername("royb"),
                       Role.STUDENT,
                       new TutorialGroup("01"),
                       getTagSet("colleagues"))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
