package cms.testutil;

import static cms.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_GITHUBUSERNAME_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_GITHUBUSERNAME_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_NUSID_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_NUSID_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_ROLE_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_ROLE_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_SOCUSERNAME_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_SOCUSERNAME_BOB;
import static cms.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static cms.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static cms.logic.commands.CommandTestUtil.VALID_TUTORIALGROUP_AMY;
import static cms.logic.commands.CommandTestUtil.VALID_TUTORIALGROUP_BOB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cms.model.AddressBook;
import cms.model.person.Person;

/**
 * A utility class containing a list of {@code Person} objects to be used in tests.
 */
public class TypicalPersons {

    public static final Person ALICE = new PersonBuilder().withName("Alice Pauline")
            .withEmail("alice@example.com")
            .withPhone("94351253").withNusId(PersonBuilder.DEFAULT_NUSID)
            .withSocUsername(PersonBuilder.DEFAULT_SOCUSERNAME).withGithubUsername(PersonBuilder.DEFAULT_GITHUBUSERNAME)
            .withTags("friends").build();
    public static final Person BENSON = new PersonBuilder().withName("Benson Meier")
            .withEmail("johnd@example.com").withPhone("98765432")
            .withNusId("A0234501W").withSocUsername("bensonm").withGithubUsername("benson-m")
            .withTags("owesMoney", "friends").build();
    public static final Person CARL = new PersonBuilder().withName("Carl Kurz").withPhone("95352563")
            .withEmail("heinz@example.com")
            .withNusId("A0234502U").withSocUsername("carlk").withGithubUsername("carl-kurz").build();
    public static final Person DANIEL = new PersonBuilder().withName("Daniel Meier").withPhone("87652533")
            .withEmail("cornelia@example.com")
            .withNusId("A0234503R").withSocUsername("danielm").withGithubUsername("daniel-m")
            .withTags("friends").build();
    public static final Person ELLE = new PersonBuilder().withName("Elle Meyer").withPhone("94822244")
            .withEmail("werner@example.com")
            .withNusId("A0234504N").withSocUsername("ellem").withGithubUsername("elle-m").build();
    public static final Person FIONA = new PersonBuilder().withName("Fiona Kunz").withPhone("94824277")
            .withEmail("lydia@example.com")
            .withNusId("A0234505M").withSocUsername("fionak").withGithubUsername("fiona-k").build();
    public static final Person GEORGE = new PersonBuilder().withName("George Best").withPhone("94824422")
            .withEmail("anna@example.com")
            .withNusId("A0234506L").withSocUsername("georgeb").withGithubUsername("george-b").build();

    // Manually added
    public static final Person HOON = new PersonBuilder().withName("Hoon Meier").withPhone("84824244")
            .withEmail("stefan@example.com")
            .withNusId("A0234507J").withSocUsername("hoonm").withGithubUsername("hoon-m").build();
    public static final Person IDA = new PersonBuilder().withName("Ida Mueller").withPhone("84821311")
            .withEmail("hans@example.com")
            .withNusId("A0234508H").withSocUsername("idaml").withGithubUsername("ida-m").build();

    // Manually added - Person's details found in {@code CommandTestUtil}
    public static final Person AMY = new PersonBuilder().withName(VALID_NAME_AMY).withPhone(VALID_PHONE_AMY)
            .withEmail(VALID_EMAIL_AMY)
            .withNusId(VALID_NUSID_AMY).withSocUsername(VALID_SOCUSERNAME_AMY)
            .withGithubUsername(VALID_GITHUBUSERNAME_AMY).withRole(VALID_ROLE_AMY)
            .withTutorialGroup(VALID_TUTORIALGROUP_AMY).withTags(VALID_TAG_FRIEND).build();
    public static final Person BOB = new PersonBuilder().withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
            .withEmail(VALID_EMAIL_BOB)
            .withNusId(VALID_NUSID_BOB).withSocUsername(VALID_SOCUSERNAME_BOB)
            .withGithubUsername(VALID_GITHUBUSERNAME_BOB).withRole(VALID_ROLE_BOB)
            .withTutorialGroup(VALID_TUTORIALGROUP_BOB).withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND)
            .build();

    public static final String KEYWORD_MATCHING_MEIER = "Meier"; // A keyword that matches MEIER

    private TypicalPersons() {
    } // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical persons.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Person person : getTypicalPersons()) {
            ab.addPerson(person);
        }
        return ab;
    }

    public static List<Person> getTypicalPersons() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
    }
}
