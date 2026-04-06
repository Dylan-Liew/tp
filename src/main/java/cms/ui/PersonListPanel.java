package cms.ui;

import java.util.logging.Logger;

import cms.commons.core.LogsCenter;
import cms.model.person.Person;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Region;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    @FXML
    private ListView<Person> personListView;
    private final boolean isMasked;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList, boolean isMasked) {
        super(FXML);
        this.isMasked = isMasked;
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        personListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        if (!personList.isEmpty()) {
            personListView.getSelectionModel().selectFirst();
        }

        personList.addListener((ListChangeListener<Person>) change -> {
            if (personList.isEmpty()) {
                personListView.getSelectionModel().clearSelection();
            } else if (personListView.getSelectionModel().getSelectedItem() == null) {
                personListView.getSelectionModel().selectFirst();
            }
        });
    }

    public ReadOnlyObjectProperty<Person> selectedPersonProperty() {
        return personListView.getSelectionModel().selectedItemProperty();
    }

    /**
     * Selects the given person if it exists in the current list.
     *
     * @return true if the person was found and selected
     */
    public boolean selectPerson(Person person) {
        if (person == null) {
            return false;
        }

        for (Person candidate : personListView.getItems()) {
            if (candidate.isSamePerson(person)) {
                personListView.getSelectionModel().select(candidate);
                personListView.scrollTo(candidate);
                return true;
            }
        }

        return false;
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1, isMasked).getRoot());
            }
        }
    }

}
