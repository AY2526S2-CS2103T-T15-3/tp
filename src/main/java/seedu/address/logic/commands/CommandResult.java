package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    private final List<PersonIndexPair> foundPersons;
    private final String description;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.foundPersons = null;
        this.description = null;
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser}
     * and list of found persons.
     */
    public CommandResult(String feedbackToUser, List<PersonIndexPair> foundPersons) {
        this(feedbackToUser, foundPersons, null);
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * list of found persons, and find description.
     */
    public CommandResult(String feedbackToUser, List<PersonIndexPair> foundPersons, String description) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = false;
        this.exit = false;
        this.foundPersons = foundPersons;
        this.description = description;
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public Optional<List<PersonIndexPair>> getFoundPersons() {
        return Optional.ofNullable(foundPersons);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult)) {
            return false;
        }

        CommandResult otherCommandResult = (CommandResult) other;
        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit
                && Objects.equals(foundPersons, otherCommandResult.foundPersons)
                && Objects.equals(description, otherCommandResult.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, foundPersons, description);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit);
        if (foundPersons != null) {
            builder.add("foundPersons", foundPersons);
        }
        if (description != null) {
            builder.add("description", description);
        }
        return builder.toString();
    }

    /**
     * Represents a pair of a person and their index in the displayed list.
     */
    public static class PersonIndexPair {
        public final Person person;
        public final int index;

        /**
         * Constructs a {@code PersonIndexPair} with the specified person and index.
         */
        public PersonIndexPair(Person person, int index) {
            this.person = person;
            this.index = index;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof PersonIndexPair)) {
                return false;
            }
            PersonIndexPair otherPair = (PersonIndexPair) other;
            return person.equals(otherPair.person) && index == otherPair.index;
        }

        @Override
        public int hashCode() {
            return Objects.hash(person, index);
        }

        @Override
        public String toString() {
            return index + ". " + person.getName();
        }
    }

}
