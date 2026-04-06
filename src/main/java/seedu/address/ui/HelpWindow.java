package seedu.address.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2526s2-cs2103t-t15-3.github.io/tp/UserGuide.html";
    public static final String HELP_MESSAGE = "For more detailed instructions, refer to the user guide: "
            + USERGUIDE_URL + "\n\n"
            + "Available Commands:\n"
            + "• add    : Adds a tutor (e.g. add n/John p/91234567 e/j@o.com s/Math r/50 [a/ADDR] [t/TAG])\n"
            + "• list   : Shows all tutors\n"
            + "• find   : Finds tutors by keywords (e.g. find n/John r/50)\n"
            + "• edit   : Edits an existing tutor (e.g. edit 1 p/98765432)\n"
            + "• delete : Deletes a tutor (e.g. delete 1)\n"
            + "• sort   : Sorts tutors by name or rate (e.g. sort rate asc)\n"
            + "• clear  : Clears all tutors\n"
            + "• help   : Shows this window\n"
            + "• exit   : Closes Tuto\n\n"
            + "Some Important Constraints:\n"
            + "• Names should only contain alphanumeric characters and spaces.\n"
            + "• Phone numbers should only contain numbers, and it should be at least 3 digits long.\n"
            + "• Emails should be of the format local-part@domain and adhere to standard constraints.\n"
            + "• Addresses can take any values.\n"
            + "• Rates should only contain numbers, and it should not be blank.\n"
            + "• Subjects should only contain alphanumeric characters and spaces, and it should not be blank.\n"
            + "• Tags names should be alphanumeric.";

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private Button copyButton;

    @FXML
    private Label helpMessage;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        helpMessage.setText(HELP_MESSAGE);
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }
}
