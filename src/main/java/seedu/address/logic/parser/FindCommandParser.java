package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.Rate;
import seedu.address.model.person.RateEqualsPredicate;
import seedu.address.model.person.Subject;

/**
 * Parses input arguments and creates a new FindCommand object.
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = tokenizeAndValidate(args);

        Predicate<Person> combinedPredicate = person -> true;

        if (hasName(argMultimap)) {
            combinedPredicate = combinedPredicate.and(parseNamePredicate(argMultimap));
        }

        if (hasRate(argMultimap)) {
            combinedPredicate = combinedPredicate.and(parseRatePredicate(argMultimap));
        }

        if (hasSubject(argMultimap)) {
            combinedPredicate = combinedPredicate.and(parseSubjectPredicate(argMultimap));
        }

        return new FindCommand(combinedPredicate);
    }

    private ArgumentMultimap tokenizeAndValidate(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_RATE, PREFIX_SUBJECT);

        // Allow multiple s/ prefixes, but reject duplicate n/ and r/
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_RATE);

        if (!isValidFindInput(argMultimap)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        return argMultimap;
    }

    private boolean isValidFindInput(ArgumentMultimap argMultimap) {
        if (!argMultimap.getPreamble().isEmpty()) {
            return false;
        }

        return hasName(argMultimap) || hasRate(argMultimap) || hasSubject(argMultimap);
    }

    private Predicate<Person> parseNamePredicate(ArgumentMultimap argMultimap) throws ParseException {
        String nameArgs = argMultimap.getValue(PREFIX_NAME).get().trim();

        if (nameArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        String[] nameKeywords = nameArgs.split("\\s+");
        return new NameContainsKeywordsPredicate(Arrays.asList(nameKeywords));
    }

    /**
     * Parses all subject arguments from the given {@code ArgumentMultimap} and returns a predicate.
     *
     * Multiple s/ prefixes are allowed and combined using OR logic.
     * Subject matching uses prefix search, case-insensitively.
     *
     * Example:
     * {@code find s/Math s/Sci} matches tutors teaching Math or Science.
     */
    private Predicate<Person> parseSubjectPredicate(ArgumentMultimap argMultimap) throws ParseException {
        List<String> subjectArgs = argMultimap.getAllValues(PREFIX_SUBJECT);

        if (subjectArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        List<String> trimmedSubjects = new ArrayList<>();
        for (String subjectArg : subjectArgs) {
            String trimmed = subjectArg.trim();

            if (trimmed.isEmpty() || !Subject.isValidSubject(trimmed)) {
                throw new ParseException(Subject.MESSAGE_CONSTRAINTS);
            }

            trimmedSubjects.add(trimmed.toLowerCase());
        }

        return person -> person.getSubjects().stream()
                .map(subject -> subject.subject.toLowerCase())
                .anyMatch(personSubject ->
                        trimmedSubjects.stream().anyMatch(keyword -> personSubject.startsWith(keyword)));
    }

    private Predicate<Person> parseRatePredicate(ArgumentMultimap argMultimap) throws ParseException {
        String rateArgs = argMultimap.getValue(PREFIX_RATE).get().trim();

        if (rateArgs.isEmpty() || !Rate.isValidRate(rateArgs)) {
            throw new ParseException(Rate.MESSAGE_CONSTRAINTS);
        }

        return new RateEqualsPredicate(new Rate(rateArgs));
    }

    private boolean hasName(ArgumentMultimap argMultimap) {
        return argMultimap.getValue(PREFIX_NAME).isPresent();
    }

    private boolean hasRate(ArgumentMultimap argMultimap) {
        return argMultimap.getValue(PREFIX_RATE).isPresent();
    }

    private boolean hasSubject(ArgumentMultimap argMultimap) {
        return !argMultimap.getAllValues(PREFIX_SUBJECT).isEmpty();
    }
}
