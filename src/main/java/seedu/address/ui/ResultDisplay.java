package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandResult.PersonIndexPair;

/**
 * A ui for the status bar that is displayed at the header of the application.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private TextArea resultDisplay;

    @FXML
    private ListView<PersonIndexPair> resultListView;

    @FXML
    private TextArea resultDescriptionDisplay;

    /**
     * Creates a {@code ResultDisplay} with the given {@code FXML} file.
     */
    public ResultDisplay() {
        super(FXML);
        resultListView.setCellFactory(listView -> new ResultListViewCell());
        if (resultDisplay != null) {
            resultDisplay.setWrapText(true);
        }
    }

    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);

        resultDisplay.setText(feedbackToUser);
        resultDisplay.setVisible(true);

        resultListView.setVisible(false);
        resultListView.getItems().clear();

        if (resultDescriptionDisplay != null) {
            resultDescriptionDisplay.setText("");
            resultDescriptionDisplay.setVisible(false);
        }
    }

    /**
     * Sets the list of persons to display in the result box.
     */
    public void setPersonList(List<PersonIndexPair> persons, String description) {
        requireNonNull(persons);

        resultListView.setItems(FXCollections.observableArrayList(persons));
        resultListView.setVisible(true);

        resultDisplay.setVisible(false);
        resultDisplay.setText("");

        if (resultDescriptionDisplay != null) {
            if (description != null && !description.isEmpty()) {
                resultDescriptionDisplay.setText(description);
                resultDescriptionDisplay.setVisible(true);
            } else {
                resultDescriptionDisplay.setText("");
                resultDescriptionDisplay.setVisible(false);
            }
        }
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class ResultListViewCell extends ListCell<PersonIndexPair> {
        @Override
        protected void updateItem(PersonIndexPair pair, boolean empty) {
            super.updateItem(pair, empty);

            if (empty || pair == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(pair.person, pair.index).getRoot());
            }
        }
    }

}
