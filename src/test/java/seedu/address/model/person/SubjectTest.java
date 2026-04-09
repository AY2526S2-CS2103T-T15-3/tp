package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class SubjectTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Subject(null));
    }

    @Test
    public void isValidSubject() {
        // null subject
        assertThrows(NullPointerException.class, () -> Subject.isValidSubject(null));

        // invalid subjects
        assertFalse(Subject.isValidSubject("")); //Empty
        assertFalse(Subject.isValidSubject(" ")); //Contains only space
        assertFalse(Subject.isValidSubject("-")); //Contains only special character
        assertFalse(Subject.isValidSubject(" Math")); //starts with space
        assertFalse(Subject.isValidSubject("Math!")); //Contains special character

        // valid subjects
        assertTrue(Subject.isValidSubject("Math"));
        assertTrue(Subject.isValidSubject("MA1521"));

    }

    @Test
    public void equals() {
        Subject subject = new Subject("Math");

        // same values -> returns true
        assertTrue(subject.equals(new Subject("Math")));

        // same object -> returns true
        assertTrue(subject.equals(subject));

        // null -> returns false
        assertFalse(subject.equals(null));

        // different types -> returns false
        assertFalse(subject.equals(5));

        // different values -> returns false
        assertFalse(subject.equals(new Subject("Biology")));
    }
}
