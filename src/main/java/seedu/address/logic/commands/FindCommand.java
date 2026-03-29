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
 * Finds and lists all persons in address book whose name, phone, email, address, subject, rate, or tags
 * contains any of the argument keywords.
 * Keyword matching is case insensitive and prefix-based.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds tutors by name, subject, hourly rate, "
            + "or universally across all fields.\n"
            + "Parameters: [KEYWORD [MORE_KEYWORDS]] [n/NAME [MORE_NAME_KEYWORDS]] [s/SUBJECT]... [r/RATE]\n"
            + "Note: Unprefixed keywords (universal search) must appear before any prefixes.\n"
            + "If keywords are placed after a prefix, they are treated as that prefix's value.\n"
            + "Examples:\n"
            + COMMAND_WORD + " alice\n"
            + COMMAND_WORD + " 50\n"
            + COMMAND_WORD + " n/Jo\n"
            + COMMAND_WORD + " s/Math s/Science\n"
            + COMMAND_WORD + " r/16\n"
            + COMMAND_WORD + " r/<10\n"
            + COMMAND_WORD + " r/>10\n"
            + COMMAND_WORD + " r/10-20\n"
            + COMMAND_WORD + " n/Alex r/15 s/Chemistry";

    private final Predicate<Person> predicate;
    private final String findDescription;

    /**
     * Creates a FindCommand with the given predicate.
     *
     * @param predicate Predicate to filter persons
     */
    public FindCommand(Predicate<Person> predicate) {
        this(predicate, "");
    }

    /**
     * Creates a FindCommand with the given predicate and description.
     *
     * @param predicate Predicate to filter persons
     * @param findDescription Description of the find criteria
     */
    public FindCommand(Predicate<Person> predicate, String findDescription) {
        this.predicate = predicate;
        this.findDescription = findDescription == null ? "" : findDescription;
    }

    public Predicate<Person> getPredicate() {
        return predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        ObservableList<Person> displayedPersons = model.getFilteredPersonList();
        List<Person> foundPersons = getFoundPersons(displayedPersons);
        List<PersonIndexPair> foundPersonIndices = getPersonIndices(foundPersons, displayedPersons);
        String resultMessage = buildResultMessage(foundPersonIndices);

        return new CommandResult(resultMessage, foundPersonIndices);
    }

    private List<Person> getFoundPersons(ObservableList<Person> allPersons) {
        return allPersons.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    private List<PersonIndexPair> getPersonIndices(List<Person> foundPersons, ObservableList<Person> allPersons) {
        List<PersonIndexPair> pairs = new ArrayList<>();
        for (Person person : foundPersons) {
            int index = allPersons.indexOf(person) + 1;
            pairs.add(new PersonIndexPair(person, index));
        }
        return pairs;
    }

    private String buildResultMessage(List<PersonIndexPair> pairs) {
        String count = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, pairs.size());

        if (findDescription != null && !findDescription.isEmpty()) {
            return "Find results for:\n" + findDescription + "\n" + count;
        }
        return count;
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
                .add("findDescription", findDescription)
                .toString();
    }
}
