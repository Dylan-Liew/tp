package cms.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;

import cms.commons.util.MaskingUtil;
import cms.model.person.Person;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

/**
 * A side panel that shows the full details of the selected person.
 */
public class PersonDetailPanel extends UiPart<Region> {

    private static final String FXML = "PersonDetailPanel.fxml";
    private static final String EMPTY_LIST_MESSAGE = "No records to display yet.";
    private static final String NO_SELECTION_MESSAGE = "Select a person from the list to view their full profile.";
    private static final String ROLE_STUDENT_STYLE_CLASS = "role-student";
    private static final String ROLE_TUTOR_STYLE_CLASS = "role-tutor";

    @FXML
    private Label emptyStateLabel;
    @FXML
    private Region detailContent;
    @FXML
    private ScrollPane detailScrollPane;
    @FXML
    private HBox nameRow;
    @FXML
    private Label name;
    @FXML
    private Hyperlink nameToggle;
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

    private String fullName = "";
    private boolean isNameExpanded;

    /**
     * Creates an empty detail panel that updates when a person is selected.
     */
    public PersonDetailPanel() {
        super(FXML);
        name.setTextOverrun(OverrunStyle.ELLIPSIS);
        name.setWrapText(false);
        nameToggle.setOnAction(event -> toggleNameExpansion());
        nameRow.widthProperty().addListener((observable, oldValue, newValue) -> updateNamePresentation());
        showPerson(null, false, false);
    }

    /**
     * Updates the panel to show the selected person, optionally masking sensitive fields.
     */
    public void showPerson(Person person, boolean isMasked, boolean hasPersonsInList) {
        boolean hasPerson = person != null;
        emptyStateLabel.setVisible(!hasPerson);
        emptyStateLabel.setManaged(!hasPerson);
        detailScrollPane.setVisible(hasPerson);
        detailScrollPane.setManaged(hasPerson);
        detailContent.setVisible(hasPerson);
        detailContent.setManaged(hasPerson);

        if (!hasPerson) {
            emptyStateLabel.setText(hasPersonsInList ? NO_SELECTION_MESSAGE : EMPTY_LIST_MESSAGE);
            fullName = "";
            isNameExpanded = false;
            name.setText("");
            nameToggle.setVisible(false);
            nameToggle.setManaged(false);
            return;
        }

        fullName = person.getName().fullName;
        isNameExpanded = false;
        name.setText(fullName);
        role.setText(person.getRole().value.toUpperCase());
        tutorialGroup.setText(String.format("T%02d", person.getTutorialGroup().value));

        if (isMasked) {
            nusMatric.setText(MaskingUtil.maskNusMatric(person.getNusMatric()));
            socUsername.setText(MaskingUtil.maskSocUsername(person.getSocUsername()));
            githubUsername.setText(MaskingUtil.maskGithubUsername(person.getGithubUsername()));
            githubUsername.setOnAction(null);
            email.setText(MaskingUtil.maskEmail(person.getEmail()));
            email.setOnAction(null);
            phone.setText(MaskingUtil.maskPhone(person.getPhone()));
        } else {
            nusMatric.setText(person.getNusMatric().value);
            socUsername.setText(person.getSocUsername().value);
            String githubUrl = "https://github.com/" + person.getGithubUsername().value;
            githubUsername.setText(githubUrl);
            githubUsername.setOnAction(event -> openUri(githubUrl));
            String emailAddress = person.getEmail().value;
            email.setText(emailAddress);
            email.setOnAction(event -> openUri("mailto:" + emailAddress));
            phone.setText(person.getPhone().value);
        }

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

        Platform.runLater(this::updateNamePresentation);
    }

    private void toggleNameExpansion() {
        isNameExpanded = !isNameExpanded;
        updateNamePresentation();
    }

    private void updateNamePresentation() {
        if (fullName == null || fullName.isBlank() || nameRow == null) {
            return;
        }

        name.applyCss();
        nameRow.applyCss();
        nameToggle.applyCss();

        double rowWidth = nameRow.getWidth();
        if (rowWidth <= 0) {
            Platform.runLater(this::updateNamePresentation);
            return;
        }

        double fullNameWidth = measureNameWidth(fullName);
        double toggleWidth = nameToggle.prefWidth(-1);
        double reservedWidth = toggleWidth + nameRow.getSpacing();
        boolean shouldShowToggle = isNameExpanded || fullNameWidth > Math.max(0, rowWidth - reservedWidth);

        nameToggle.setVisible(shouldShowToggle);
        nameToggle.setManaged(shouldShowToggle);
        nameToggle.setText(isNameExpanded ? "Collapse" : "Expand");

        name.setWrapText(isNameExpanded);
        name.setTextOverrun(isNameExpanded ? OverrunStyle.CLIP : OverrunStyle.ELLIPSIS);
        name.setText(fullName);
    }

    private double measureNameWidth(String value) {
        Text helper = new Text(value);
        helper.setFont(name.getFont());
        return helper.getLayoutBounds().getWidth();
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
