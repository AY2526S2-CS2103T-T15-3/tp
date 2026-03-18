package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TestUtil.pairOf;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;



public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");
        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));
    }

    @Test
    public void equals_sameValues() {
        CommandResult commandResult = new CommandResult("feedback");
        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));
    }

    @Test
    public void equals_nullAndDifferentType() {
        CommandResult commandResult = new CommandResult("feedback");
        // null -> returns false
        assertFalse(commandResult.equals(null));
        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));
    }

    @Test
    public void equals_differentFields() {
        CommandResult commandResult = new CommandResult("feedback");
        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));
        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));
        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");
        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());
    }

    @Test
    public void hashcode_differentFields() {
        CommandResult commandResult = new CommandResult("feedback");
        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());
        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());
        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String s = commandResult.toString();
        // high-level assertions: ensure primary fields are represented
        assertTrue(s.contains(commandResult.getFeedbackToUser()));
        assertTrue(s.contains("showHelp") || s.contains(String.valueOf(commandResult.isShowHelp())));
        assertTrue(s.contains("exit") || s.contains(String.valueOf(commandResult.isExit())));
    }

    @Test
    public void showHelpExit_defaultFalse() {
        CommandResult commandResult = new CommandResult("feedback");
        assertFalse(commandResult.isShowHelp());
        assertFalse(commandResult.isExit());
    }

    @Test
    public void showHelpExit_trueWhenSet() {
        CommandResult commandResult = new CommandResult("feedback", true, true);
        assertTrue(commandResult.isShowHelp());
        assertTrue(commandResult.isExit());
    }

    @Test
    public void getFoundPersons_optionalBehaviour() {
        // when no found persons provided -> empty optional
        CommandResult noFound = new CommandResult("no found");
        assertFalse(noFound.getFoundPersons().isPresent());

        // when found persons provided -> optional present and contains the list
        CommandResult.PersonIndexPair pair = pairOf("Bob", 3);
        List<CommandResult.PersonIndexPair> list = Arrays.asList(pair);
        CommandResult withFound = new CommandResult("found", list);
        assertTrue(withFound.getFoundPersons().isPresent());
        assertEquals(list, withFound.getFoundPersons().get());
    }

    @Test
    public void equals_withFoundPersonsDifferent() {
        CommandResult.PersonIndexPair pair1 = pairOf("Dana", 1);
        CommandResult.PersonIndexPair pair2 = pairOf("Dana", 2);

        CommandResult cr1 = new CommandResult("feedback", Collections.singletonList(pair1));
        CommandResult cr2 = new CommandResult("feedback", Collections.singletonList(pair2));

        // different found persons -> not equal and hashcodes differ
        assertFalse(cr1.equals(cr2));
        assertNotEquals(cr1.hashCode(), cr2.hashCode());
    }

    @Test
    public void toString_withFoundPersons() {
        CommandResult.PersonIndexPair pair = pairOf("Eve", 5);
        CommandResult cr = new CommandResult("feedback", Collections.singletonList(pair));

        String s = cr.toString();
        // should include class name, feedback, and found persons info
        assertTrue(s.contains(CommandResult.class.getCanonicalName()));
        assertTrue(s.contains(cr.getFeedbackToUser()));
        assertTrue(s.contains("Eve"));
    }
}
