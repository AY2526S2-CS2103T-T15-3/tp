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
        assertFalse(Rate.isValidRate(null));

        // invalid rates
        assertFalse(Rate.isValidRate("")); //Empty
        assertFalse(Rate.isValidRate(" ")); //Contains only space
        assertFalse(Rate.isValidRate("-")); //Contains only special character
        assertFalse(Rate.isValidRate("abc")); //letters
        assertFalse(Rate.isValidRate("10 20")); //spaces in between
        assertFalse(Rate.isValidRate("10!")); //Special character
        assertFalse(Rate.isValidRate("-10")); //negative integer
        assertFalse(Rate.isValidRate("-1.5")); //negative decimal
        assertFalse(Rate.isValidRate("1.2.3")); //multiple decimal points

        // valid rates
        assertTrue(Rate.isValidRate("10"));
        assertTrue(Rate.isValidRate("0"));
        assertTrue(Rate.isValidRate("00000000007"));
        assertTrue(Rate.isValidRate("49.50")); //decimal
        assertTrue(Rate.isValidRate("0.5")); //decimal less than 1
        assertTrue(Rate.isValidRate(String.valueOf(Integer.MAX_VALUE)));
        assertTrue(Rate.isValidRate("2147483648"));
        assertTrue(Rate.isValidRate("999999999999999999999999"));
    }

    @Test
    public void constructor_decimalRate_storesNormalisedRate() {
        Rate rate = new Rate("49.50");
        assertTrue(rate.toString().equals("49.5"));
        assertTrue(rate.equals(new Rate("49.5")));
        // trailing zeros after decimal dropped; whole numbers stay clean
        assertTrue(new Rate("50.00").toString().equals("50"));
    }

    @Test
    public void constructor_veryLargeDigitString_normalizesWithBigDecimal() {
        Rate rate = new Rate("2147483648");
        assertTrue(rate.toString().equals("2147483648"));
        assertTrue(rate.equals(new Rate("000002147483648")));
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
