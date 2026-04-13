package seedu.address.model.person;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Builds comparators for sorting persons in the UI list.
 */
public final class PersonListSortComparator {

    private static final Comparator<Person> BY_NAME = Comparator.comparing(p -> p.getName().fullName,
            String.CASE_INSENSITIVE_ORDER);

    private PersonListSortComparator() {
    }

    /**
     * Returns a comparator for the given field and order.
     */
    public static Comparator<Person> forFieldAndOrder(PersonSortField field, PersonSortOrder order) {
        Comparator<Person> primary;
        switch (field) {
        case NAME:
            primary = BY_NAME;
            break;
        case RATE:
            primary = Comparator.comparing(p -> new BigDecimal(p.getRate().rate));
            break;
        default:
            throw new AssertionError(field);
        }
        if (order == PersonSortOrder.DESCENDING) {
            primary = primary.reversed();
        }
        if (field == PersonSortField.RATE) {
            return primary.thenComparing(BY_NAME);
        }
        return primary;
    }
}
