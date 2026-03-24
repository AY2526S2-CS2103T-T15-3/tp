package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;

public class UserPrefsTest {

    @Test
    public void constructor() {
        UserPrefs userPrefs = new UserPrefs();
        assertEquals(new GuiSettings(), userPrefs.getGuiSettings());
        assertEquals(Paths.get("data", "addressbook.json"), userPrefs.getAddressBookFilePath());
    }

    @Test
    public void copyConstructor() {
        UserPrefs original = new UserPrefs();
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        Path path = Paths.get("my", "file", "path.json");

        original.setGuiSettings(guiSettings);
        original.setAddressBookFilePath(path);

        UserPrefs copy = new UserPrefs(original);

        assertEquals(original, copy);
    }

    @Test
    public void resetData() {
        UserPrefs userPrefs = new UserPrefs();

        UserPrefs newPrefs = new UserPrefs();
        GuiSettings newGui = new GuiSettings(5, 6, 7, 8);
        Path newPath = Paths.get("new", "path", "addressbook.json");

        newPrefs.setGuiSettings(newGui);
        newPrefs.setAddressBookFilePath(newPath);

        userPrefs.resetData(newPrefs);

        assertEquals(newGui, userPrefs.getGuiSettings());
        assertEquals(newPath, userPrefs.getAddressBookFilePath());
    }

    @Test
    public void resetData_null_throws() {
        UserPrefs userPrefs = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPrefs.resetData(null));
    }

    @Test
    public void setGuiSettings() {
        UserPrefs userPrefs = new UserPrefs();
        GuiSettings gui = new GuiSettings(1, 2, 3, 4);

        userPrefs.setGuiSettings(gui);
        assertEquals(gui, userPrefs.getGuiSettings());
    }

    @Test
    public void setGuiSettings_null_throws() {
        UserPrefs userPrefs = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPrefs.setGuiSettings(null));
    }

    @Test
    public void setAddressBookFilePath() {
        UserPrefs userPrefs = new UserPrefs();
        Path path = Paths.get("some", "other", "file.json");

        userPrefs.setAddressBookFilePath(path);
        assertEquals(path, userPrefs.getAddressBookFilePath());
    }

    @Test
    public void setAddressBookFilePath_null_throws() {
        UserPrefs userPrefs = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPrefs.setAddressBookFilePath(null));
    }

    @Test
    public void equals() {
        UserPrefs userPrefs = new UserPrefs();

        // same object
        assertEquals(userPrefs, userPrefs);

        // null
        assertNotEquals(null, userPrefs);

        // different type
        assertNotEquals(5, userPrefs);

        // same values
        UserPrefs same = new UserPrefs(userPrefs);
        assertEquals(userPrefs, same);

        // different gui settings
        UserPrefs diffGui = new UserPrefs();
        diffGui.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertNotEquals(userPrefs, diffGui);

        // different file path
        UserPrefs diffPath = new UserPrefs();
        diffPath.setAddressBookFilePath(Paths.get("diff.json"));
        assertNotEquals(userPrefs, diffPath);

        // both different (extra branch coverage)
        UserPrefs diffBoth = new UserPrefs();
        diffBoth.setGuiSettings(new GuiSettings(9, 9, 9, 9));
        diffBoth.setAddressBookFilePath(Paths.get("another.json"));
        assertNotEquals(userPrefs, diffBoth);
    }

    @Test
    public void hashCode_sameValues_sameHash() {
        UserPrefs u1 = new UserPrefs();
        UserPrefs u2 = new UserPrefs();

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    public void toStringMethod() {
        UserPrefs userPrefs = new UserPrefs();
        String expected = "Gui Settings : " + userPrefs.getGuiSettings()
                + "\nLocal data file location : " + userPrefs.getAddressBookFilePath();

        assertEquals(expected, userPrefs.toString());
    }
}
