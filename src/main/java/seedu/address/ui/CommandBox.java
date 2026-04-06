package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private static final double MIN_HEIGHT = 50.0;
    private static final double MAX_HEIGHT = 350.0;
    private static final double HEIGHT_PADDING_BUFFER = 50.0;
    private static final double WIDTH_PADDING_BUFFER = 60.0;

    private final CommandExecutor commandExecutor;

    @FXML
    private TextArea commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        commandTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            setStyleToDefault();

            int oldLength;
            if (oldValue == null) {
                oldLength = 0;
            } else {
                oldLength = oldValue.length();
            }

            int newLength;
            if (newValue == null) {
                newLength = 0;
            } else {
                newLength = newValue.length();
            }

            boolean isPaste = (newLength - oldLength) > 1;
            handleHeightChange(isPaste);
        });

        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            boolean isEnterKey = event.getCode() == KeyCode.ENTER;
            boolean isShiftDown = event.isShiftDown();

            if (isEnterKey && !isShiftDown) {
                event.consume();
                handleCommandEntered();
            }
        });

        commandTextField.widthProperty().addListener((observable, oldValue, newValue) -> handleHeightChange(false));
    }

    /**
     * Adjusts the height of the command box based on its content.
     * @param isPaste Whether the change is caused by pasting text.
     */
    private void handleHeightChange(boolean isPaste) {
        double width = commandTextField.getWidth();
        if (width <= 0) {
            return;
        }

        double newHeight = computeContentHeight(width);
        double targetHeight = Math.min(newHeight, MAX_HEIGHT);

        updateCommandBoxHeight(targetHeight);
        adjustScrollPosition(newHeight, isPaste);
    }

    /**
     * Computes the height required for the content in the command box.
     */
    private double computeContentHeight(double width) {
        String content = getFormattedTextContent();

        Text text = new Text(content);
        text.setFont(commandTextField.getFont());
        text.setWrappingWidth(width - WIDTH_PADDING_BUFFER);

        double height = text.getLayoutBounds().getHeight();
        double computedHeight = Math.ceil(height + HEIGHT_PADDING_BUFFER);

        return Math.max(computedHeight, MIN_HEIGHT);
    }

    /**
     * Formats the text content to ensure accurate height calculation.
     */
    private String getFormattedTextContent() {
        String content = commandTextField.getText();
        if (content == null || content.isEmpty()) {
            return " "; // use a placeholder to calculate minimum height
        } else if (content.endsWith("\n")) {
            return content + " "; // so that trailing newlines are accounted for by Text bounds
        }
        return content;
    }

    /**
     * Updates the command box preferred height if it differs from the target.
     */
    private void updateCommandBoxHeight(double targetHeight) {
        if (commandTextField.getPrefHeight() != targetHeight) {
            commandTextField.setPrefHeight(targetHeight);
        }
    }

    /**
     * Adjusts the scroll position of the command box.
     */
    private void adjustScrollPosition(double newHeight, boolean isPaste) {
        if (newHeight <= MAX_HEIGHT || isPaste) {
            commandTextField.setScrollTop(0);
        }
    }

    /**
     * Handles the Enter button pressed event.
     */
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.isEmpty()) {
            return;
        }

        try {
            commandExecutor.execute(commandText);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

}
