package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.Arrays;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Rate;
import seedu.address.model.person.RateEqualsPredicate;
import seedu.address.model.person.Subject;
import seedu.address.model.person.SubjectEqualsPredicate;

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
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_RATE, PREFIX_SUBJECT);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_RATE, PREFIX_SUBJECT);

        boolean hasName = argMultimap.getValue(PREFIX_NAME).isPresent();
        boolean hasRate = argMultimap.getValue(PREFIX_RATE).isPresent();
        boolean hasSubject = argMultimap.getValue(PREFIX_SUBJECT).isPresent();

        int prefixCount = 0;
        if (hasName) {
            prefixCount++;
        }
        if (hasRate) {
            prefixCount++;
        }
        if (hasSubject) {
            prefixCount++;
        }

        if (!argMultimap.getPreamble().isEmpty() || prefixCount != 1) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        if (hasName) {
            String nameArgs = argMultimap.getValue(PREFIX_NAME).get().trim();

            if (nameArgs.isEmpty()) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
            }

            String[] nameKeywords = nameArgs.split("\\s+");
            return new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
        }

        if (hasSubject) {
            String subjectArgs = argMultimap.getValue(PREFIX_SUBJECT).get().trim();

            if (subjectArgs.isEmpty() || !Subject.isValidSubject(subjectArgs)) {
                throw new ParseException(Subject.MESSAGE_CONSTRAINTS);
            }

            return new FindCommand(new SubjectEqualsPredicate(new Subject(subjectArgs)));
        }

        String rateArgs = argMultimap.getValue(PREFIX_RATE).get().trim();

        if (rateArgs.isEmpty() || !Rate.isValidRate(rateArgs)) {
            throw new ParseException(Rate.MESSAGE_CONSTRAINTS);
        }

        return new FindCommand(new RateEqualsPredicate(new Rate(rateArgs)));
    }
}
