package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a tutor to Tuto.\n"
            + "Format: " + COMMAND_WORD + " "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_SUBJECT + "SUBJECT "
            + PREFIX_RATE + "RATE "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_SUBJECT + "Biology "
            + PREFIX_RATE + "25 "
            + PREFIX_ADDRESS + "311 Clementi Ave 2 "
            + PREFIX_TAG + "F2F "
            + PREFIX_TAG + "cafe";

    public static final String MESSAGE_SUCCESS = "New tutor added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This tutor already exists in Tuto!";
    public static final String MESSAGE_DUPLICATE_PHONE = "Another tutor already uses this phone number!";
    public static final String MESSAGE_DUPLICATE_EMAIL = "Another tutor already uses this email address!";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasDuplicatePhone(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PHONE);
        }

        if (model.hasDuplicateEmail(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_EMAIL);
        }

        model.addPerson(toAdd);
        int displayedIndex = model.getFilteredPersonList().indexOf(toAdd) + 1;
        List<CommandResult.PersonIndexPair> personsToDisplay =
                List.of(new CommandResult.PersonIndexPair(toAdd, displayedIndex));

        return new CommandResult(MESSAGE_SUCCESS, personsToDisplay);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand otherAddCommand = (AddCommand) other;
        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
