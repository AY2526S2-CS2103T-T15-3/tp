package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import seedu.address.model.Model;
import seedu.address.model.person.PersonSortField;
import seedu.address.model.person.PersonSortOrder;

/**
 * Sorts the displayed person list by name or rate.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sorts the tutors in the displayed list by the given field.\n"
            + "Parameters: field (name | rate) order (asc | desc)\n"
            + "Example: " + COMMAND_WORD + " name asc";

    public static final String MESSAGE_SUCCESS = "Sorted tutors by %1$s in %2$s order!";

    private final PersonSortField sortField;
    private final PersonSortOrder sortOrder;

    /**
     * Creates a {@code SortCommand} to sort by the given field and order.
     */
    public SortCommand(PersonSortField sortField, PersonSortOrder sortOrder) {
        requireAllNonNull(sortField, sortOrder);
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateDisplayedPersonListSort(sortField, sortOrder);
        String fieldWord = sortField == PersonSortField.NAME ? "name" : "rate";
        String orderWord = sortOrder == PersonSortOrder.ASCENDING ? "ascending" : "descending";
        return new CommandResult(String.format(MESSAGE_SUCCESS, fieldWord, orderWord));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SortCommand)) {
            return false;
        }
        SortCommand otherSortCommand = (SortCommand) other;
        return sortField == otherSortCommand.sortField && sortOrder == otherSortCommand.sortOrder;
    }
}
