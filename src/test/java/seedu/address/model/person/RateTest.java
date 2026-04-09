package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RateTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Rate(null));
    }

    @Test
    public void constructor_rateWithLeadingZeroes_storesNormalisedRate() {
        Rate rate = new Rate("0000000007");
        assertTrue(rate.equals(new Rate("7")));
        assertTrue(rate.toString().equals("7"));
    }

    @Test
    public void constructor_invalidRate_throwsIllegalArgumentException() {
        //empty String
        assertThrows(IllegalArgumentException.class, () -> new Rate(""));

        //contains letters
        assertThrows(IllegalArgumentException.class, () -> new Rate("abc"));

        //contains spaces
        assertThrows(IllegalArgumentException.class, () -> new Rate("12 20"));

        //contains special characters
        assertThrows(IllegalArgumentException.class, () -> new Rate("12!"));

        //negative
        assertThrows(IllegalArgumentException.class, () -> new Rate("-67"));
    }

    @Test
    public void isValidRate() {
        // null rate
        assertThrows(NullPointerException.class, () -> Rate.isValidRate(null));

        // valid rate
        assertFalse(Rate.isValidRate("")); //Empty
        assertFalse(Rate.isValidRate(" ")); //Contains only space
        assertFalse(Rate.isValidRate("-")); //Contains only special character
        assertFalse(Rate.isValidRate("abc")); //letters
        assertFalse(Rate.isValidRate("10 20")); //spaces in between
        assertFalse(Rate.isValidRate("10!")); //Special character
        assertFalse(Rate.isValidRate("-10")); //negative

        // valid rates
        assertTrue(Rate.isValidRate("10"));
        assertTrue(Rate.isValidRate("0"));
        assertTrue(Rate.isValidRate("00000000007"));
    }

    @Test
    public void equals() {
        Rate rate = new Rate("25");

        // same values -> returns true
        assertTrue(rate.equals(new Rate("25")));

        // same object -> returns true
        assertTrue(rate.equals(rate));

        // null -> returns false
        assertFalse(rate.equals(null));

        // different types -> returns false
        assertFalse(rate.equals(5));

        // different values -> returns false
        assertFalse(rate.equals(new Rate("67")));
    }
}
