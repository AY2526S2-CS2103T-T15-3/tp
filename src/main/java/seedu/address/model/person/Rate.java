package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.math.BigInteger;

/**
 * Represents a Person's rate in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidRate(String)}
 */
public class Rate {

    public static final String MESSAGE_CONSTRAINTS = "Rate should only contain numbers, and it should not be blank";

    public static final String MESSAGE_INVALID_RATE_FIND_FORMAT =
            "Invalid rate format for find command.\n"
                    + "Use r/NUMBER for exact rate, r/<NUMBER for rates less than NUMBER, "
                    + "r/>NUMBER for rates greater than NUMBER, or r/LOWER-UPPER for an inclusive range.";

    public static final String MESSAGE_MISSING_RATE_BOUND =
            "Invalid rate range: both lower and upper bounds must be provided.\n"
                    + "Use format r/LOWER-UPPER (e.g. r/10-20).";

    public static final String MESSAGE_INVALID_RATE_RANGE_ORDER =
            "Invalid rate range: lower bound cannot be greater than upper bound.\n"
                    + "Ensure LOWER ≤ UPPER (e.g. r/10-20).";

    public static final String MESSAGE_INVALID_RATE_RANGE_DELIMITER =
            "Invalid rate range format. Use r/LOWER-UPPER with '-' between the bounds, "
                    + "e.g. r/10-20.";

    public static final String MESSAGE_NEGATIVE_RATE_NOT_ALLOWED =
            "Rate cannot be negative. Please enter a non-negative value.";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "\\d+";

    public final String rate;

    /**
     * Constructs a {@code rate}.
     *
     * @param rate A valid hourly rate.
     */
    public Rate(String rate) {
        requireNonNull(rate);
        checkArgument(isValidRate(rate), MESSAGE_CONSTRAINTS);
        this.rate = new BigInteger(rate).toString();
    }

    /**
     * Returns true if a given string is a valid rate (non-empty digit sequence).
     */
    public static boolean isValidRate(String test) {
        return test != null && test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if the rate contains the keyword as a word prefix, ignoring case.
     */
    public boolean isMatchingKeyword(String keyword) {
        return seedu.address.commons.util.StringUtil.containsWordPrefixIgnoreCase(this.rate, keyword);
    }

    /**
     * Compares this rate's numeric value with {@code other}.
     *
     * @return a negative integer, zero, or a positive integer as this value is numerically less than, equal to,
     *         or greater than {@code other}
     */
    public int compareNumericValueTo(BigInteger other) {
        return new BigInteger(rate).compareTo(other);
    }

    @Override
    public String toString() {
        return rate;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Rate)) {
            return false;
        }

        Rate otherRate = (Rate) other;
        return rate.equals(otherRate.rate);
    }

    @Override
    public int hashCode() {
        return rate.hashCode();
    }

}
