package seedu.address.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.util.Duration;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    private Timeline resizeTimeline;

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
            if (event.getCode() == KeyCode.ENTER) {
                if (!event.isShiftDown()) {
                    event.consume();
                    handleCommandEntered();
                }
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

        Text text = new Text(commandTextField.getText());
        text.setFont(commandTextField.getFont());
        text.setWrappingWidth(width - 60); // Buffer for padding and scrollbar

        double height = text.getLayoutBounds().getHeight();

        double fontSize = commandTextField.getFont().getSize();
        double verticalBuffer = fontSize + 20;

        double newHeight = Math.ceil(height + verticalBuffer);

        if (newHeight < 50) {
            newHeight = 50;
        }

        double maxHeight = 200;
        double targetHeight = Math.min(newHeight, maxHeight);

        if (commandTextField.getPrefHeight() != targetHeight) {
            if (resizeTimeline != null) {
                resizeTimeline.stop();
            }

            resizeTimeline = new Timeline(new KeyFrame(Duration.millis(50),
                    new KeyValue(commandTextField.prefHeightProperty(), targetHeight)));
            resizeTimeline.play();
        }

        if (isPaste && newHeight > maxHeight) {
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
