package cms.model.person;

import java.util.Set;

import cms.model.tag.Tag;

/**
 * Represents a student in the course management system.
 */
public class Student extends Person {

    public Student(Name name, Phone phone, Email email, NusMatric nusMatric, SocUsername socUsername,
            GithubUsername githubUsername, TutorialGroup tutorialGroup, Set<Tag> tags) {
        super(name, phone, email, nusMatric, socUsername, githubUsername, tutorialGroup, tags);
    }

    @Override
    public Role getRole() {
        return Role.STUDENT;
    }
}
