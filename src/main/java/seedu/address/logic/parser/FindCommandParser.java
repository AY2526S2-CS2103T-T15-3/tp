package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

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
import seedu.address.model.person.UniversalSearchPredicate;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.TagContainsKeywordsPredicate;


/**
 * Parses input arguments and creates a new FindCommand object.
 */
public class FindCommandParser implements Parser<FindCommand> {

    private static final String PREAMBLE_RESTRICTED_MESSAGE = "When using universal search (keywords before "
            + "prefixes), only the following prefixes may be used to further refine the search: n/, s/, r/, t/.\n"
            + "Note: any words appearing after a prefix are treated as that prefix's value (e.g. 'r/500 alice'"
            + " treats 'alice' as part of the r/ value).";

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = tokenizeAndValidate(args);

        validatePreamblePrefixCombination(argMultimap);

        Predicate<Person> combinedPredicate = person -> true;

        if (hasPreamble(argMultimap)) {
            combinedPredicate = combinedPredicate.and(parseUniversalSearchPredicate(argMultimap));
        }

        if (hasName(argMultimap)) {
            combinedPredicate = combinedPredicate.and(parseNamePredicate(argMultimap));
        }

        if (hasRate(argMultimap)) {
            combinedPredicate = combinedPredicate.and(parseRatePredicate(argMultimap));
        }

        if (hasSubject(argMultimap)) {
            combinedPredicate = combinedPredicate.and(parseSubjectPredicate(argMultimap));
        }

        if (hasTag(argMultimap)) {
            combinedPredicate = combinedPredicate.and(parseTagPredicate(argMultimap));
        }

        return new FindCommand(combinedPredicate);
    }

    private ArgumentMultimap tokenizeAndValidate(String args) throws ParseException {
        // Tokenize with all prefixes so we can detect presence of unsupported flags.
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_RATE,
                PREFIX_SUBJECT, PREFIX_TAG, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);

        // Allow multiple s/ and t/ prefixes, but reject duplicate n/ and r/
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_RATE);

        if (!isValidFindInput(argMultimap)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        return argMultimap;
    }

    private boolean isValidFindInput(ArgumentMultimap argMultimap) {
        return hasPreamble(argMultimap)
                || hasName(argMultimap)
                || hasRate(argMultimap)
                || hasSubject(argMultimap)
                || hasTag(argMultimap);
    }

    /**
     * Validates that when a universal search preamble is present, only the allowed prefixes
     * (n/, s/, r/) are used. Throws {@code ParseException} with a clear message otherwise.
     */
    private void validatePreamblePrefixCombination(ArgumentMultimap argMultimap) throws ParseException {
        if (!hasPreamble(argMultimap)) {
            return;
        }

        List<String> unsupported = new ArrayList<>();
        if (hasPhone(argMultimap)) {
            unsupported.add("p/");
        }
        if (hasEmail(argMultimap)) {
            unsupported.add("e/");
        }
        if (hasAddress(argMultimap)) {
            unsupported.add("a/");
        }

        if (!unsupported.isEmpty()) {
            String joined = String.join(", ", unsupported);
            String message = PREAMBLE_RESTRICTED_MESSAGE + "\n"
                    + "Unsupported flags present: " + joined + ".\n"
                    + "Remove these flags or place the keywords after the prefixes.\n\n"
                    + FindCommand.MESSAGE_USAGE;
            throw new ParseException(message);
        }
    }

    private Predicate<Person> parseUniversalSearchPredicate(ArgumentMultimap argMultimap) {
        String[] keywords = argMultimap.getPreamble().split("\\s+");
        return new UniversalSearchPredicate(Arrays.asList(keywords));
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
        List<String> normalizedSubjects = normalizeAndValidateSubjects(subjectArgs);

        return person -> person.getSubjects().stream()
                .map(subject -> subject.subject.toLowerCase())
                .anyMatch(personSubject ->
                        normalizedSubjects.stream().anyMatch(keyword -> personSubject.startsWith(keyword)));
    }

    private List<String> normalizeAndValidateSubjects(List<String> subjectArgs) throws ParseException {
        if (subjectArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        List<String> normalized = new ArrayList<>();
        for (String subjectArg : subjectArgs) {
            String trimmed = subjectArg.trim();

            if (trimmed.isEmpty() || !Subject.isValidSubject(trimmed)) {
                throw new ParseException(Subject.MESSAGE_CONSTRAINTS);
            }

            normalized.add(trimmed.toLowerCase());
        }

        return normalized;
    }

    private Predicate<Person> parseRatePredicate(ArgumentMultimap argMultimap) throws ParseException {
        String rateArgs = argMultimap.getValue(PREFIX_RATE).get().trim();

        if (rateArgs.isEmpty() || !Rate.isValidRate(rateArgs)) {
            throw new ParseException(Rate.MESSAGE_CONSTRAINTS);
        }

        return new RateEqualsPredicate(new Rate(rateArgs));
    }

    private Predicate<Person> parseTagPredicate(ArgumentMultimap argMultimap) throws ParseException {
        List<String> tagArgs = argMultimap.getAllValues(PREFIX_TAG);
        List<String> normalizedTags = normalizeAndValidateTags(tagArgs);

        // If a preamble (universal search) is present, use exclusive filtering (AND logic).
        // Otherwise (specific find), use inclusive filtering (OR logic).
        boolean isUniversalSearch = hasPreamble(argMultimap);
        return new TagContainsKeywordsPredicate(normalizedTags, isUniversalSearch);
    }

    private List<String> normalizeAndValidateTags(List<String> tagArgs) throws ParseException {
        if (tagArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        List<String> normalized = new ArrayList<>();
        for (String tagArg : tagArgs) {
            String trimmed = tagArg.trim();

            if (trimmed.isEmpty()) {
                throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
            }

            String[] keywords = trimmed.split("\\s+");
            for (String keyword : keywords) {
                if (!Tag.isValidTagName(keyword)) {
                    throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
                }
                normalized.add(keyword.toLowerCase());
            }
        }

        return normalized;
    }

    private boolean hasName(ArgumentMultimap argMultimap) {
        return argMultimap.getValue(PREFIX_NAME).isPresent();
    }

    private boolean hasPreamble(ArgumentMultimap argMultimap) {
        return !argMultimap.getPreamble().isEmpty();
    }

    private boolean hasRate(ArgumentMultimap argMultimap) {
        return argMultimap.getValue(PREFIX_RATE).isPresent();
    }

    private boolean hasSubject(ArgumentMultimap argMultimap) {
        return !argMultimap.getAllValues(PREFIX_SUBJECT).isEmpty();
    }

    private boolean hasTag(ArgumentMultimap argMultimap) {
        return !argMultimap.getAllValues(PREFIX_TAG).isEmpty();
    }

    private boolean hasPhone(ArgumentMultimap argMultimap) {
        return argMultimap.getValue(PREFIX_PHONE).isPresent();
    }

    private boolean hasEmail(ArgumentMultimap argMultimap) {
        return argMultimap.getValue(PREFIX_EMAIL).isPresent();
    }

    private boolean hasAddress(ArgumentMultimap argMultimap) {
        return argMultimap.getValue(PREFIX_ADDRESS).isPresent();
    }
}
