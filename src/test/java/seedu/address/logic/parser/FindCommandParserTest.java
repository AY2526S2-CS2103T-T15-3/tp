package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Rate;
import seedu.address.model.person.RateEqualsPredicate;
import seedu.address.model.person.Subject;
import seedu.address.model.person.SubjectEqualsPredicate;



public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")));
        assertParseSuccess(parser, " n/Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " n/Alice \n \t Bob \t", expectedFindCommand);
    }

    @Test
    public void parse_validRateArg_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(new RateEqualsPredicate(new Rate("17")));
        assertParseSuccess(parser, " r/17", expectedFindCommand);
    }

    @Test
    public void parse_validRateWithLeadingZeroes_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(new RateEqualsPredicate(new Rate("007")));
        assertParseSuccess(parser, " r/007", expectedFindCommand);
    }

    @Test
    public void parse_invalidPreamble_throwsParseException() {
        assertParseFailure(parser, " garbage n/Alice",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateNamePrefix_throwsParseException() {
        assertParseFailure(parser, " n/Alice n/Bob",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    @Test
    public void parse_duplicateRatePrefix_throwsParseException() {
        assertParseFailure(parser, " r/10 r/20",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_RATE));
    }

    @Test
    public void parse_validSubjectArg_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(
                new SubjectEqualsPredicate(Collections.singleton(new Subject("Biology"))));
        assertParseSuccess(parser, " s/Biology", expectedFindCommand);
    }

    @Test
    public void parse_subjectWithWhitespace_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(
                new SubjectEqualsPredicate(Collections.singleton(new Subject("Biology"))));
        assertParseSuccess(parser, " s/  Biology  ", expectedFindCommand);
    }

    @Test
    public void parse_invalidSubject_throwsParseException() {
        // empty after trimming
        assertParseFailure(parser, " s/ ", Subject.MESSAGE_CONSTRAINTS);
        // starts with non-alphanumeric
        assertParseFailure(parser, " s/!bio", Subject.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateSubjectPrefix_throwsParseException() {
        assertParseFailure(parser, " s/Bio s/Math",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_SUBJECT));
    }

    @Test
    public void createSubjectPredicate_null_throwsParseException() throws Exception {
        Method m = FindCommandParser.class.getDeclaredMethod("createSubjectPredicate", String.class);
        m.setAccessible(true);

        try {
            m.invoke(parser, new Object[] { null });
            fail("Expected InvocationTargetException");
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            assertTrue(cause instanceof ParseException);
            assertEquals(Subject.MESSAGE_CONSTRAINTS, cause.getMessage());
        }
    }
}
