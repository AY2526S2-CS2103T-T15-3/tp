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

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds tutors by keywords.\n"
            + "Format: " + COMMAND_WORD + " [KEYWORD] [n/NAME] [s/SUBJECT]... [r/RATE]\n"
            + "Examples:\n"
            + "• " + COMMAND_WORD + " alice\n"
            + "• " + COMMAND_WORD + " n/Alex r/15-20 s/Math";

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

    private String buildResultCount(List<PersonIndexPair> pairs) {
        return String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, pairs.size());
    }

    private String buildResultDescription() {
        if (findDescription == null || findDescription.isEmpty()) {
            return "";
        }
        return findDescription;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        ObservableList<Person> displayedPersons = model.getFilteredPersonList();
        List<Person> foundPersons = getFoundPersons(displayedPersons);
        List<PersonIndexPair> foundPersonIndices = getPersonIndices(foundPersons, displayedPersons);

        String countMessage = buildResultCount(foundPersonIndices);
        String description = buildResultDescription();

        return new CommandResult(countMessage, foundPersonIndices, description);
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
