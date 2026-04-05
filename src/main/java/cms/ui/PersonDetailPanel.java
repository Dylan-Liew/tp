package cms.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;

import cms.model.person.Person;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

/**
 * A side panel that shows the full details of the selected person.
 */
public class PersonDetailPanel extends UiPart<Region> {

    private static final String FXML = "PersonDetailPanel.fxml";
    private static final String ROLE_STUDENT_STYLE_CLASS = "role-student";
    private static final String ROLE_TUTOR_STYLE_CLASS = "role-tutor";

    @FXML
    private Label emptyStateLabel;
    @FXML
    private Region detailContent;
    @FXML
    private Label name;
    @FXML
    private Label role;
    @FXML
    private Label tutorialGroup;
    @FXML
    private Label nusMatric;
    @FXML
    private Label socUsername;
    @FXML
    private Hyperlink githubUsername;
    @FXML
    private Hyperlink email;
    @FXML
    private Label phone;
    @FXML
    private FlowPane tags;

    /**
     * Creates an empty detail panel that updates when a person is selected.
     */
    public PersonDetailPanel() {
        super(FXML);
        showPerson(null);
    }

    /**
     * Updates the panel to show the selected person.
     */
    public void showPerson(Person person) {
        boolean hasPerson = person != null;
        emptyStateLabel.setVisible(!hasPerson);
        emptyStateLabel.setManaged(!hasPerson);
        detailContent.setVisible(hasPerson);
        detailContent.setManaged(hasPerson);

        if (!hasPerson) {
            return;
        }

        name.setText(person.getName().fullName);
        role.setText(person.getRole().value.toUpperCase());
        tutorialGroup.setText(String.valueOf(person.getTutorialGroup().value));
        nusMatric.setText(person.getNusMatric().value);
        socUsername.setText(person.getSocUsername().value);
        String githubUrl = "https://github.com/" + person.getGithubUsername().value;
        githubUsername.setText(githubUrl);
        githubUsername.setOnAction(event -> openUri(githubUrl));
        String emailAddress = person.getEmail().value;
        email.setText(emailAddress);
        email.setOnAction(event -> openUri("mailto:" + emailAddress));
        phone.setText(person.getPhone().value);

        role.getStyleClass().removeAll(ROLE_STUDENT_STYLE_CLASS, ROLE_TUTOR_STYLE_CLASS);
        role.getStyleClass().add(person.getRole().value.equals("student")
                ? ROLE_STUDENT_STYLE_CLASS : ROLE_TUTOR_STYLE_CLASS);

        tags.getChildren().clear();
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> {
                    Label tagLabel = new Label(tag.tagName);
                    tagLabel.getStyleClass().add("detail-tag");
                    tags.getChildren().add(tagLabel);
                });
    }

    /**
     * Opens the given URI with the local desktop integration when available.
     */
    private void openUri(String uriText) {
        if (!Desktop.isDesktopSupported()) {
            return;
        }

        try {
            Desktop.getDesktop().browse(new URI(uriText));
        } catch (IOException | URISyntaxException e) {
            // Silently ignore if the local machine cannot open links.
        }
    }
}
