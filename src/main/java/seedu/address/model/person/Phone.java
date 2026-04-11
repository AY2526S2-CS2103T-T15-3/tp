package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {


    public static final String MESSAGE_CONSTRAINTS =
            "Phone numbers must either: \n"
            + "1. Be 8 digits (will default to +65), OR \n"
            + "2. Start with '+' followed up by 10 digits.";
    private static final String DIGIT_REGEX = "\\d{8}";
    private static String INTERNATIONAL_REGEX = "\\+\\d+";
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        String localisedNumber = localise(phone);
        checkArgument(isValidPhone(localisedNumber), MESSAGE_CONSTRAINTS);
        value = localisedNumber;
    }

    private static String localise(String input) {
        if (input.matches(DIGIT_REGEX)) {
            return "+65" + input;
        }
        return input;
    }
    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        if (!test.matches(INTERNATIONAL_REGEX)) {
            return false;
        }

        String digits = test.substring(1);
        return digits.length() == 10;
    }

    /**
     * Returns true if the phone number contains the keyword as a word prefix, ignoring case.
     */
    public boolean isMatchingKeyword(String keyword) {
        return seedu.address.commons.util.StringUtil.containsWordPrefixIgnoreCase(this.value, keyword);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Phone)) {
            return false;
        }

        Phone otherPhone = (Phone) other;
        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
