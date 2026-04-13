package seedu.address.model.person;

import java.math.BigDecimal;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Rate} is greater than the given value.
 */
public class RateGreaterThanPredicate implements Predicate<Person> {
    private final BigDecimal lowerBound;

    /**
     * Creates a predicate that checks whether a person's rate is
     * greater than {@code lowerBound}.
     */
    public RateGreaterThanPredicate(BigDecimal lowerBound) {
        this.lowerBound = lowerBound;
    }

    @Override
    public boolean test(Person person) {
        return person.getRate().compareNumericValueTo(lowerBound) > 0;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof RateGreaterThanPredicate)) {
            return false;
        }

        RateGreaterThanPredicate otherPredicate = (RateGreaterThanPredicate) other;
        return lowerBound.equals(otherPredicate.lowerBound);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("lowerBound", lowerBound)
                .toString();
    }
}
