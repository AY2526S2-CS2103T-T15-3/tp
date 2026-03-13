package seedu.address.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
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
    private boolean isScrollable = false;

    private final CommandExecutor commandExecutor;

    @FXML
    private TextArea commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            setStyleToDefault();
            int oldLength = (oldValue == null) ? 0 : oldValue.length();
            int newLength = (newValue == null) ? 0 : newValue.length();
            boolean isLargeChange = Math.abs(newLength - oldLength) > 1;
            handleHeightChange(!isLargeChange);
        });

        // Prevent scrolling when content fits
        commandTextField.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (!isScrollable) {
                event.consume();
            }
        });

        // Add listener for Enter key
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!event.isShiftDown()) {
                    event.consume(); // Prevent new line being added
                    handleCommandEntered();
                }
                // else allow new line (default behavior)
            }
        });

        // Also update height when width changes (e.g. window resize)
        commandTextField.widthProperty().addListener((observable, oldValue, newValue) -> handleHeightChange(false));
    }

    /**
     * Adjusts the height of the command box based on its content.
     * @param shouldAnimate Whether to animate the height change.
     */
    private void handleHeightChange(boolean shouldAnimate) {
        Text text = new Text(commandTextField.getText());
        text.setFont(commandTextField.getFont());

        // Approximate width: subtract horizontal padding/margins
        double width = commandTextField.getWidth();
        if (width <= 0) {
            return;
        }

        // Assuming specific padding and borders from CSS (12px vertical, 16px horizontal, 1.5px border)
        // We subtract a significantly larger buffer to ensure the helper Text wraps BEFORE the TextArea does
        text.setWrappingWidth(width - 60);

        double height = text.getLayoutBounds().getHeight();

        // Add vertical padding (top + bottom) + borders + buffer
        double newHeight = height + 40;

        // Enforce constraints (min 50, max 400)
        if (newHeight < 50) {
            newHeight = 50;
        }

        double maxHeight = 200; // aligned with FXML
        boolean wasScrollable = isScrollable;
        isScrollable = newHeight > maxHeight;
        boolean transitionedToScrollable = !wasScrollable && isScrollable;
        double targetHeight = Math.min(newHeight, maxHeight);

        // Manage scrollbar visibility: show only if content exceeds max height
        ScrollPane scrollPane = (ScrollPane) commandTextField.lookup(".scroll-pane");
        if (scrollPane != null) {
            if (newHeight > maxHeight) {
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            } else {
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            }
        }

        if (commandTextField.getPrefHeight() != targetHeight || transitionedToScrollable) {
            if (resizeTimeline != null) {
                resizeTimeline.stop();
            }

            if (shouldAnimate) {
                resizeTimeline = new Timeline(new KeyFrame(Duration.millis(150),
                        new KeyValue(commandTextField.prefHeightProperty(), targetHeight)));

                // If we are fully expanded (fitting all text), reset scroll to top to ensure first row is visible
                if (newHeight <= maxHeight || transitionedToScrollable) {
                    resizeTimeline.setOnFinished(event -> commandTextField.setScrollTop(0));
                }

                resizeTimeline.play();
            } else {
                commandTextField.setPrefHeight(targetHeight);
                if (newHeight <= maxHeight) {
                    commandTextField.setScrollTop(0);
                } else if (transitionedToScrollable) {
                    Platform.runLater(() -> commandTextField.setScrollTop(0));
                }
            }
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
