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
import seedu.address.model.person.RateGreaterThanPredicate;
import seedu.address.model.person.RateLessThanPredicate;
import seedu.address.model.person.RateRangePredicate;
import seedu.address.model.person.Subject;
import seedu.address.model.person.SubjectContainsKeywordsPredicate;
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

        // Validate combinations (preamble + unsupported prefixes) early.
        validatePreamblePrefixCombination(argMultimap);

        // Build the combined predicate in a single helper to keep this method high-level.
        Predicate<Person> combinedPredicate = buildCombinedPredicate(argMultimap);
        String findDescription = buildFindDescription(argMultimap);

        return new FindCommand(combinedPredicate, findDescription);
    }

    /**
     * Assemble the combined predicate from the argument multimap. Each specific parser is
     * invoked only if its corresponding prefix/preamble is present. This keeps the high-level
     * parsing flow at a single abstraction level.
     */
    private Predicate<Person> buildCombinedPredicate(ArgumentMultimap argMultimap) throws ParseException {
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

        return combinedPredicate;
    }

    private String buildFindDescription(ArgumentMultimap argMultimap) {
        List<String> parts = new ArrayList<>();

        addUniversalPart(argMultimap, parts);
        addNamePart(argMultimap, parts);
        addSubjectPart(argMultimap, parts);
        addRatePart(argMultimap, parts);
        addTagPart(argMultimap, parts);

        return parts.isEmpty() ? "" : String.join(" • ", parts);
    }

    private void addUniversalPart(ArgumentMultimap argMultimap, List<String> parts) {
        String preamble = argMultimap.getPreamble().trim();
        if (!preamble.isEmpty()) {
            parts.add("All fields: \"" + preamble + "\"");
        }
    }

    private void addNamePart(ArgumentMultimap argMultimap, List<String> parts) {
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            String nameValue = argMultimap.getValue(PREFIX_NAME).get().trim();
            parts.add("Name: \"" + nameValue + "\"");
        }
    }

    private void addSubjectPart(ArgumentMultimap argMultimap, List<String> parts) {
        if (!argMultimap.getAllValues(PREFIX_SUBJECT).isEmpty()) {
            String subjectValue = String.join(", ",
                    argMultimap.getAllValues(PREFIX_SUBJECT).stream()
                            .map(String::trim)
                            .toList());
            parts.add("Subject: \"" + subjectValue + "\"");
        }
    }

    private void addRatePart(ArgumentMultimap argMultimap, List<String> parts) {
        if (argMultimap.getValue(PREFIX_RATE).isPresent()) {
            String rateValue = argMultimap.getValue(PREFIX_RATE).get().trim();
            parts.add("Rate: \"" + rateValue + "\"");
        }
    }

    private void addTagPart(ArgumentMultimap argMultimap, List<String> parts) {
        if (!argMultimap.getAllValues(PREFIX_TAG).isEmpty()) {
            String tagValue = String.join(", ",
                    argMultimap.getAllValues(PREFIX_TAG).stream()
                            .map(String::trim)
                            .toList());
            parts.add("Tag: \"" + tagValue + "\"");
        }
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
        List<String> unsupported = collectUnsupportedPrefixes(argMultimap);

        if (!unsupported.isEmpty()) {
            String joined = String.join(", ", unsupported);
            String message = PREAMBLE_RESTRICTED_MESSAGE + "\n"
                    + "Unsupported flags present: " + joined + ".\n"
                    + "Remove these flags or place the keywords after the prefixes.\n\n"
                    + FindCommand.MESSAGE_USAGE;
            throw new ParseException(message);
        }
    }

    private List<String> collectUnsupportedPrefixes(ArgumentMultimap argMultimap) {
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
        return unsupported;
    }

    private Predicate<Person> parseUniversalSearchPredicate(ArgumentMultimap argMultimap) {
        List<String> keywords = normalizePreambleKeywords(argMultimap.getPreamble());
        return new UniversalSearchPredicate(keywords);
    }

    private List<String> normalizePreambleKeywords(String preamble) {
        if (preamble == null || preamble.trim().isEmpty()) {
            return List.of();
        }
        String[] parts = preamble.trim().split("\\s+");
        return Arrays.asList(parts);
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

        return new SubjectContainsKeywordsPredicate(normalizedSubjects);
    }

    private List<String> normalizeAndValidateSubjects(List<String> subjectArgs) throws ParseException {
        ensureNonEmptyArgs(subjectArgs);

        List<String> normalized = new ArrayList<>();
        for (String subjectArg : subjectArgs) {
            String trimmed = trimOrEmpty(subjectArg);
            validateSubjectTrimmed(trimmed);
            normalized.add(trimmed.toLowerCase());
        }

        return normalized;
    }

    /**
     * Ensure the provided argument list is not empty. Throws a ParseException with
     * the standard usage message when empty.
     */
    private void ensureNonEmptyArgs(List<String> args) throws ParseException {
        if (args == null || args.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Trim the input string safely (returns empty string for null).
     */
    private String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    /**
     * Validate a trimmed subject string. Throws ParseException on invalid subject.
     */
    private void validateSubjectTrimmed(String trimmed) throws ParseException {
        if (trimmed.isEmpty() || !Subject.isValidSubject(trimmed)) {
            throw new ParseException(Subject.MESSAGE_CONSTRAINTS);
        }
    }

    private Predicate<Person> parseRatePredicate(ArgumentMultimap argMultimap) throws ParseException {
        String rateArgs = argMultimap.getValue(PREFIX_RATE).get().trim();

        // Reject empty input like r/
        if (rateArgs.isEmpty()) {
            throw new ParseException(Rate.MESSAGE_CONSTRAINTS);
        }

        // If it contains ANY non-digit separator but no '-'
        if (!rateArgs.contains("-") && !rateArgs.matches("[<>]?\\d+")) {
            throw new ParseException(Rate.MESSAGE_INVALID_RATE_RANGE_DELIMITER);
        }

        // Detect negative rate like r/-10 (but not range like 10-20)
        if (rateArgs.matches("-\\d+")) {
            throw new ParseException(Rate.MESSAGE_NEGATIVE_RATE_NOT_ALLOWED);
        }

        // Case 1: Less-than search, e.g. r/<10
        if (rateArgs.startsWith("<")) {
            String num = rateArgs.substring(1).trim();

            // Reject cases like r/< or r/<abc
            if (num.isEmpty() || !Rate.isValidRate(num)) {
                throw new ParseException(Rate.MESSAGE_CONSTRAINTS);
            }

            return new RateLessThanPredicate(Integer.parseInt(num));
        }

        // Case 2: Greater-than search, e.g. r/>10
        if (rateArgs.startsWith(">")) {
            String num = rateArgs.substring(1).trim();

            // Reject cases like r/> or r/>abc
            if (num.isEmpty() || !Rate.isValidRate(num)) {
                throw new ParseException(Rate.MESSAGE_CONSTRAINTS);
            }

            return new RateGreaterThanPredicate(Integer.parseInt(num));
        }

        // Case 3: Rate range search, e.g. r/10-20
        if (rateArgs.contains("-")) {
            // Reject malformed inputs like r/10-20-30, restrict to only 1 dash
            if (rateArgs.chars().filter(ch -> ch == '-').count() != 1) {
                throw new ParseException(Rate.MESSAGE_INVALID_RATE_FIND_FORMAT);
            }

            // Split into at most 2 parts only
            String[] parts = rateArgs.split("-", -1);

            String lower = parts[0].trim();
            String upper = parts[1].trim();

            // Reject missing bounds like r/10- or r/-20
            if (lower.isEmpty() || upper.isEmpty()) {
                throw new ParseException(Rate.MESSAGE_MISSING_RATE_BOUND);
            }

            // Reject non-numeric bounds like r/ten-20
            if (!Rate.isValidRate(lower) || !Rate.isValidRate(upper)) {
                throw new ParseException(Rate.MESSAGE_CONSTRAINTS);
            }

            int lowerBound = Integer.parseInt(lower);
            int upperBound = Integer.parseInt(upper);

            // Reject reversed range like r/20-10
            if (lowerBound > upperBound) {
                throw new ParseException(Rate.MESSAGE_INVALID_RATE_RANGE_ORDER);
            }

            // Inclusive range: lowerBound <= rate <= upperBound
            return new RateRangePredicate(lowerBound, upperBound);
        }

        // Case 4: Exact match, e.g. r/10
        if (!Rate.isValidRate(rateArgs)) {
            throw new ParseException(Rate.MESSAGE_CONSTRAINTS);
        }

        return new RateEqualsPredicate(new Rate(rateArgs));
    }

    private Predicate<Person> parseTagPredicate(ArgumentMultimap argMultimap) throws ParseException {
        List<String> tagArgs = argMultimap.getAllValues(PREFIX_TAG);
        List<String> normalizedTags = normalizeAndValidateTags(tagArgs);
        return new TagContainsKeywordsPredicate(normalizedTags);
    }

    private List<String> normalizeAndValidateTags(List<String> tagArgs) throws ParseException {
        ensureNonEmptyArgs(tagArgs);

        List<String> normalized = new ArrayList<>();
        for (String tagArg : tagArgs) {
            normalized.addAll(parseTagKeywords(tagArg));
        }

        return normalized;
    }

    private List<String> parseTagKeywords(String tagArg) throws ParseException {
        String trimmed = tagArg.trim();

        if (trimmed.isEmpty()) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }

        String[] keywords = trimmed.split("\\s+");
        List<String> parsedKeywords = new ArrayList<>();
        for (String keyword : keywords) {
            if (!Tag.isValidTagName(keyword)) {
                throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
            }
            parsedKeywords.add(keyword.toLowerCase());
        }
        return parsedKeywords;
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
