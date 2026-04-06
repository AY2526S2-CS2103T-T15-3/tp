package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command! Type 'help' to open the help window "
            + "and see the list of available commands.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The tutor index provided is invalid! "
            + "Please provide an index between 1 and the list size.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d tutors found!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /** @see seedu.address.logic.parser.SortCommandParser */
    public static final String MESSAGE_INVALID_SORT_FIELD =
            "Invalid sort field '%1$s'. Only name and rate are allowed.";
    /** @see seedu.address.logic.parser.SortCommandParser */
    public static final String MESSAGE_INVALID_SORT_ORDER =
            "Invalid sort order '%1$s'. Only asc and desc are allowed.";
    /** @see seedu.address.logic.parser.SortCommandParser */
    public static final String MESSAGE_SORT_WRONG_ARGUMENT_COUNT =
            "Sort expects exactly two parameters: field (name or rate) and order (asc or desc).\n%1$s";
    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Subject: ")
                .append(person.getSubjects())
                .append("; Rate: ")
                .append(person.getRate())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

}
