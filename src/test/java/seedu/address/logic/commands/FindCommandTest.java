package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandResult.PersonIndexPair;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonSortField;
import seedu.address.model.person.PersonSortOrder;
import seedu.address.model.person.UniversalSearchPredicate;

/**
 * Contains integration tests (interaction with the Model) for
 * {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(
                Collections.singletonList("first"));
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(
                Collections.singletonList("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        CommandResult expectedCommandResult = new CommandResult(expectedMessage, Collections.emptyList(), "");

        assertCommandSuccess(command, model, expectedCommandResult, expectedModel);
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_singlePersonFound() {
        // Construct expected message
        List<Person> foundPersons = Arrays.asList(CARL);
        List<PersonIndexPair> expectedPairs = new ArrayList<>();

        // Indices must match the displayed list (filtered + sorted), same as
        // FindCommand. This is because of how sortedPersons is implemented - which
        // leads to an index mismatch if not updated.
        expectedModel.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        List<Person> displayedPersons = expectedModel.getFilteredPersonList();
        for (Person p : foundPersons) {
            int index = displayedPersons.indexOf(p) + 1;
            expectedPairs.add(new PersonIndexPair(p, index));
        }
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, foundPersons.size());

        CommandResult expectedCommandResult = new CommandResult(expectedMessage, expectedPairs, "");

        NameContainsKeywordsPredicate predicate = preparePredicate("Carl Kurz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        assertCommandSuccess(command, model, expectedCommandResult, expectedModel);
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_universalSearch_personFound() {
        // Search for "Physics" (Carl), "Bio" (Elle - prefix), "60" (Fiona - rate)
        List<Person> foundPersons = Arrays.asList(CARL, ELLE, FIONA);
        List<PersonIndexPair> expectedPairs = new ArrayList<>();

        // Indices must match the displayed list (filtered + sorted), same as
        // FindCommand. This is because of how sortedPersons is implemented - which
        // leads to an index mismatch if not updated.
        expectedModel.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        List<Person> displayedPersons = expectedModel.getFilteredPersonList();
        for (Person p : foundPersons) {
            int index = displayedPersons.indexOf(p) + 1;
            expectedPairs.add(new PersonIndexPair(p, index));
        }
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, foundPersons.size());

        CommandResult expectedCommandResult = new CommandResult(expectedMessage, expectedPairs, "");

        UniversalSearchPredicate predicate = new UniversalSearchPredicate(Arrays.asList("Physics", "Bio", "60"));
        FindCommand command = new FindCommand(predicate);
        // expectedModel is already updated to PREDICATE_SHOW_ALL_PERSONS above

        assertCommandSuccess(command, model, expectedCommandResult, expectedModel);
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_afterSort_resultsFollowDisplayedOrder() {
        model.updateDisplayedPersonListSort(PersonSortField.RATE, PersonSortOrder.DESCENDING);
        expectedModel.updateDisplayedPersonListSort(PersonSortField.RATE, PersonSortOrder.DESCENDING);

        List<Person> foundPersons = Arrays.asList(BENSON, DANIEL);
        List<PersonIndexPair> expectedPairs = new ArrayList<>();

        List<Person> displayedPersons = expectedModel.getFilteredPersonList();
        for (Person p : foundPersons) {
            int index = displayedPersons.indexOf(p) + 1;
            expectedPairs.add(new PersonIndexPair(p, index));
        }

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, foundPersons.size());
        CommandResult expectedCommandResult = new CommandResult(expectedMessage, expectedPairs, "");
        NameContainsKeywordsPredicate predicate = preparePredicate("Meier");
        FindCommand command = new FindCommand(predicate);

        assertCommandSuccess(command, model, expectedCommandResult, expectedModel);
        assertEquals(foundPersons, expectedCommandResult.getFoundPersons().get().stream()
                .map(pair -> pair.person)
                .collect(Collectors.toList()));
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Arrays.asList("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + ", findDescription=}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }
}
