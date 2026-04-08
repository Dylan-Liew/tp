package cms.ui;

import java.util.logging.Logger;

import cms.commons.core.GuiSettings;
import cms.commons.core.LogsCenter;
import cms.logic.Logic;
import cms.logic.commands.CommandResult;
import cms.logic.commands.HelpCommand;
import cms.logic.commands.exceptions.CommandException;
import cms.logic.parser.exceptions.ParseException;
import cms.model.person.Person;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private static final double MIN_LIST_PANEL_WIDTH = 180;
    private static final double MIN_DETAIL_PANEL_WIDTH = 280;
    private static final double MAX_INITIAL_LIST_RATIO = 0.5;

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private PersonDetailPanel personDetailPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private SplitPane mainContentSplitPane;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane personDetailPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     *
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        personDetailPanel = new PersonDetailPanel();
        refreshPersonListPanel();
        personDetailPlaceholder.getChildren().add(personDetailPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getFilteredPersonList());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        Platform.runLater(this::adjustPersonListPaneWidth);
    }

    /**
     * Sizes the left pane to the rendered card content so the app does not open with excess blank space.
     */
    private void adjustPersonListPaneWidth() {
        personListPanel.getRoot().applyCss();
        personListPanel.getRoot().layout();
        personListPanelPlaceholder.applyCss();
        personListPanelPlaceholder.layout();
        mainContentSplitPane.applyCss();
        mainContentSplitPane.layout();

        double widestCard = personListPanel.getRoot().lookupAll("#cardPane").stream()
                .filter(Region.class::isInstance)
                .map(Region.class::cast)
                .mapToDouble(card -> card.prefWidth(-1))
                .max()
                .orElse(personListPanel.getRoot().prefWidth(-1));

        double targetWidth = Math.max(MIN_LIST_PANEL_WIDTH, Math.ceil(widestCard) + 28);
        personListPanelPlaceholder.setMinWidth(MIN_LIST_PANEL_WIDTH);
        personListPanelPlaceholder.setPrefWidth(targetWidth);
        personListPanelPlaceholder.setMaxWidth(Region.USE_COMPUTED_SIZE);
        personDetailPlaceholder.setMinWidth(MIN_DETAIL_PANEL_WIDTH);

        double splitWidth = mainContentSplitPane.getWidth();
        if (splitWidth > 0) {
            double maxInitialListWidth = Math.max(0, splitWidth - MIN_DETAIL_PANEL_WIDTH);
            double boundedListWidth = Math.min(targetWidth, maxInitialListWidth);
            double dividerPosition = boundedListWidth <= 0 ? MAX_INITIAL_LIST_RATIO : boundedListWidth / splitWidth;
            mainContentSplitPane.setDividerPositions(Math.min(MAX_INITIAL_LIST_RATIO, dividerPosition));
        }
    }

    /**
     * Rebuilds the person list panel and wires its selection to the detail panel.
     */
    private void refreshPersonListPanel() {
        refreshPersonListPanel(personListPanel == null ? null : personListPanel.selectedPersonProperty().get());
    }

    /**
     * Rebuilds the person list panel and restores the previous selection when possible.
     */
    private void refreshPersonListPanel(Person previouslySelectedPerson) {
        boolean isMasked = logic.isMasked();
        ObservableList<Person> filteredPersons = logic.getFilteredPersonList();
        Person personToRestore = findMatchingPerson(filteredPersons, previouslySelectedPerson);

        personListPanel = new PersonListPanel(filteredPersons, isMasked);
        personListPanel.selectedPersonProperty().addListener((observable, oldValue, newValue) ->
                personDetailPanel.showPerson(newValue, isMasked));

        if (personToRestore != null) {
            personListPanel.selectPerson(personToRestore);
        }

        personDetailPanel.showPerson(personListPanel.selectedPersonProperty().get(), isMasked);

        personListPanelPlaceholder.getChildren().setAll(personListPanel.getRoot());
    }

    static Person findMatchingPerson(ObservableList<Person> persons, Person targetPerson) {
        if (targetPerson == null) {
            return null;
        }

        return persons.stream()
                .filter(candidate -> candidate.isSamePerson(targetPerson))
                .findFirst()
                .orElse(null);
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        helpWindow.setHelpMessage(HelpCommand.getOverviewHelpMessage());
        showOrFocusHelpWindow();
    }

    private void handleHelp(String helpContent) {
        helpWindow.setHelpMessage(helpContent);
        showOrFocusHelpWindow();
    }

    private void showOrFocusHelpWindow() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public PersonListPanel getPersonListPanel() {
        return personListPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see cms.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        Person previouslySelectedPerson = personListPanel == null
                ? null : personListPanel.selectedPersonProperty().get();

        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser(), false);

            if (commandResult.isShowHelp()) {
                handleHelp(commandResult.getHelpContent().orElse(HelpWindow.DEFAULT_HELP_MESSAGE));
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            refreshPersonListPanel(previouslySelectedPerson);

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage(), true);
            throw e;
        }
    }
}
