package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandResult.PersonIndexPair;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds tutors by name, subject, and/or hourly rate.\n"
            + "Parameters: [n/NAME [MORE_NAME_KEYWORDS]] [s/SUBJECT]... [r/RATE]\n"
            + "Examples:\n"
            + COMMAND_WORD + " n/Jo\n"
            + COMMAND_WORD + " s/Math s/Sci\n"
            + COMMAND_WORD + " r/16 s/Math\n"
            + COMMAND_WORD + " n/Alex r/20 s/Chem";

    private final Predicate<Person> predicate;

    public FindCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    public Predicate<Person> getPredicate() {
        return predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        ObservableList<Person> allPersons = model.getAddressBook().getPersonList();
        List<Person> foundPersons = allPersons.stream()
                .filter(predicate)
                .collect(Collectors.toList());

        List<PersonIndexPair> foundPersonIndices = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, foundPersons.size()));

        for (Person p : foundPersons) {
            int index = allPersons.indexOf(p) + 1;
            sb.append("\n").append(index).append(". ").append(Messages.format(p));
            foundPersonIndices.add(new PersonIndexPair(p, index));
        }

        return new CommandResult(sb.toString(), foundPersonIndices);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
