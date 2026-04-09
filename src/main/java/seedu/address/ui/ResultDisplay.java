package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.logic.commands.CommandResult.PersonIndexPair;

/**
 * A ui for the status bar that is displayed at the header of the application.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";
    private static final String STYLE_CLASS_ERROR = "error";
    private static final String STYLE_CLASS_SUCCESS = "success";
    private static final String ICON_ERROR = "❌  ";
    private static final String ICON_SUCCESS = "✨  ";
    private static final String EMPTY_LIST_PLACEHOLDER_TEXT = "No tutors found.";
    private static final String EMPTY_LIST_PLACEHOLDER_STYLE = "-fx-text-fill: grey; "
            + "-fx-alignment: center; -fx-padding: 20;";

    @FXML
    private TextArea resultDisplay;

    @FXML
    private ListView<PersonIndexPair> resultListView;

    @FXML
    private Label resultDescriptionDisplay;

    @FXML
    private VBox resultListContainer;

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

    /**
     * Sets the feedback text to be displayed to the user.
     *
     * @param feedbackToUser The string feedback to display.
     */
    public void setFeedbackToUser(String feedbackToUser) {
        setFeedbackToUser(feedbackToUser, false);
    }

    /**
     * Sets the feedback text to be displayed to the user, with an optional error style.
     *
     * @param feedbackToUser The string feedback to display.
     * @param isError True if the feedback represents an error, false otherwise.
     */
    public void setFeedbackToUser(String feedbackToUser, boolean isError) {
        requireNonNull(feedbackToUser);
        showFeedbackTextArea(feedbackToUser, isError);
        hideResultListContainer();
        clearResultList();
    }

    private void showFeedbackTextArea(String feedbackToUser, boolean isError) {
        if (isError) {
            resultDisplay.setText(ICON_ERROR + feedbackToUser);
            setStyleToIndicateError();
        } else {
            resultDisplay.setText(ICON_SUCCESS + feedbackToUser);
            setStyleToIndicateSuccess();
        }
        resultDisplay.setVisible(true);
    }

    private void setStyleToIndicateError() {
        resultDisplay.getStyleClass().remove(STYLE_CLASS_SUCCESS);
        if (!resultDisplay.getStyleClass().contains(STYLE_CLASS_ERROR)) {
            resultDisplay.getStyleClass().add(STYLE_CLASS_ERROR);
        }
    }

    private void setStyleToIndicateSuccess() {
        resultDisplay.getStyleClass().remove(STYLE_CLASS_ERROR);
        if (!resultDisplay.getStyleClass().contains(STYLE_CLASS_SUCCESS)) {
            resultDisplay.getStyleClass().add(STYLE_CLASS_SUCCESS);
        }
    }

    private void hideResultListContainer() {
        resultListContainer.setVisible(false);
    }

    private void clearResultList() {
        resultListView.getItems().clear();
    }

    /**
     * Sets the list of persons to display in the result box.
     */
    public void setPersonList(List<PersonIndexPair> persons, String description) {
        requireNonNull(persons);

        hideFeedbackTextArea();
        showResultListContainer();

        updateDescriptionDisplay(description);
        updatePersonList(persons);
    }

    private void hideFeedbackTextArea() {
        resultDisplay.setVisible(false);
    }

    private void showResultListContainer() {
        resultListContainer.setVisible(true);
    }

    private void updatePersonList(List<PersonIndexPair> persons) {
        resultListView.setItems(FXCollections.observableArrayList(persons));
        updatePlaceholder(persons.isEmpty());
    }

    private void updatePlaceholder(boolean isEmpty) {
        if (isEmpty) {
            Label placeholder = new Label(EMPTY_LIST_PLACEHOLDER_TEXT);
            placeholder.setStyle(EMPTY_LIST_PLACEHOLDER_STYLE);
            resultListView.setPlaceholder(placeholder);
        } else {
            resultListView.setPlaceholder(null);
        }
    }

    private void updateDescriptionDisplay(String description) {
        if (resultDescriptionDisplay == null) {
            return;
        }

        boolean hasDescription = description != null && !description.isEmpty();
        if (hasDescription) {
            resultDescriptionDisplay.setText(description);
            resultDescriptionDisplay.setVisible(true);
            resultDescriptionDisplay.setManaged(true);
            resultDescriptionDisplay.setStyle(
                    "-fx-font-size: 20px; "
                            + "-fx-font-weight: 700;"
            );
        } else {
            resultDescriptionDisplay.setText("");
            resultDescriptionDisplay.setVisible(false);
            resultDescriptionDisplay.setManaged(false);
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
