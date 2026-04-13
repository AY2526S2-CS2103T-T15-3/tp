package seedu.address.model.person;

import java.math.BigDecimal;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Rate} is less than the given value.
 */
public class RateLessThanPredicate implements Predicate<Person> {
    private final BigDecimal upperBound;

    public RateLessThanPredicate(BigDecimal upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    public boolean test(Person person) {
        return person.getRate().compareNumericValueTo(upperBound) < 0;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof RateLessThanPredicate)) {
            return false;
        }

        RateLessThanPredicate otherPredicate = (RateLessThanPredicate) other;
        return upperBound.equals(otherPredicate.upperBound);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("upperBound", upperBound)
                .toString();
    }
}
