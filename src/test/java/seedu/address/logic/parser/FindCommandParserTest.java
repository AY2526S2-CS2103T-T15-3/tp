package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RATE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.Person;
import seedu.address.model.person.Subject;
import seedu.address.testutil.PersonBuilder;

public class FindCommandParserTest {

    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidPreamble_throwsParseException() {
        assertParseFailure(parser, " garbage n/Alice",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateNamePrefix_throwsParseException() {
        assertParseFailure(parser, " n/Alice n/Bob",
                getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    @Test
    public void parse_duplicateRatePrefix_throwsParseException() {
        assertParseFailure(parser, " r/10 r/20",
                getErrorMessageForDuplicatePrefixes(PREFIX_RATE));
    }

    @Test
    public void parse_duplicateSubjectPrefix_allowed() throws Exception {
        FindCommand command = parser.parse(" s/Bio s/Math");

        Person biologyTutor = new PersonBuilder().withSubject("Biology").build();
        Person mathTutor = new PersonBuilder().withSubject("Math").build();
        Person physicsTutor = new PersonBuilder().withSubject("Physics").build();

        assertTrue(command.getPredicate().test(biologyTutor));
        assertTrue(command.getPredicate().test(mathTutor));
        assertFalse(command.getPredicate().test(physicsTutor));
    }

    @Test
    public void parse_invalidSubject_throwsParseException() {
        assertParseFailure(parser, " s/ ", Subject.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " s/!bio", Subject.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_validNameArg_returnsFindCommand() throws Exception {
        FindCommand command = parser.parse(" n/Ali Bob");

        Person alice = new PersonBuilder().withName("Alice Pauline").build();
        Person bob = new PersonBuilder().withName("Bob Tan").build();
        Person charlie = new PersonBuilder().withName("Charlie Lim").build();

        assertTrue(command.getPredicate().test(alice));
        assertTrue(command.getPredicate().test(bob));
        assertFalse(command.getPredicate().test(charlie));
    }

    @Test
    public void parse_validRateArg_returnsFindCommand() throws Exception {
        FindCommand command = parser.parse(" r/17");

        Person rate17 = new PersonBuilder().withRate("17").build();
        Person rate18 = new PersonBuilder().withRate("18").build();

        assertTrue(command.getPredicate().test(rate17));
        assertFalse(command.getPredicate().test(rate18));
    }

    @Test
    public void parse_validRateWithLeadingZeroes_returnsFindCommand() throws Exception {
        FindCommand command = parser.parse(" r/007");

        Person rate007 = new PersonBuilder().withRate("007").build();
        Person rate7 = new PersonBuilder().withRate("7").build();

        assertTrue(command.getPredicate().test(rate007));
        assertFalse(command.getPredicate().test(rate7));
    }

    @Test
    public void parse_validSubjectArg_returnsFindCommand() throws Exception {
        FindCommand command = parser.parse(" s/Bio");

        Person biologyTutor = new PersonBuilder().withSubject("Biology").build();
        Person mathTutor = new PersonBuilder().withSubject("Math").build();

        assertTrue(command.getPredicate().test(biologyTutor));
        assertFalse(command.getPredicate().test(mathTutor));
    }

    @Test
    public void parse_subjectWithWhitespace_returnsFindCommand() throws Exception {
        FindCommand command = parser.parse(" s/  Bio  ");

        Person biologyTutor = new PersonBuilder().withSubject("Biology").build();
        Person chemistryTutor = new PersonBuilder().withSubject("Chemistry").build();

        assertTrue(command.getPredicate().test(biologyTutor));
        assertFalse(command.getPredicate().test(chemistryTutor));
    }

    @Test
    public void parse_multiplePrefixes_returnsFindCommand() throws Exception {
        FindCommand command = parser.parse(" n/Ali r/17 s/Bio");

        Person matchingPerson = new PersonBuilder()
                .withName("Alice Pauline")
                .withRate("17")
                .withSubject("Biology")
                .build();

        Person wrongName = new PersonBuilder()
                .withName("Brenda Pauline")
                .withRate("17")
                .withSubject("Biology")
                .build();

        Person wrongRate = new PersonBuilder()
                .withName("Alice Pauline")
                .withRate("18")
                .withSubject("Biology")
                .build();

        Person wrongSubject = new PersonBuilder()
                .withName("Alice Pauline")
                .withRate("17")
                .withSubject("Math")
                .build();

        assertTrue(command.getPredicate().test(matchingPerson));
        assertFalse(command.getPredicate().test(wrongName));
        assertFalse(command.getPredicate().test(wrongRate));
        assertFalse(command.getPredicate().test(wrongSubject));
    }

    @Test
    public void parse_multipleSubjects_returnsFindCommand() throws Exception {
        FindCommand command = parser.parse(" s/Bio s/Math");

        Person biologyTutor = new PersonBuilder().withSubject("Biology").build();
        Person mathTutor = new PersonBuilder().withSubject("Math").build();
        Person physicsTutor = new PersonBuilder().withSubject("Physics").build();

        assertTrue(command.getPredicate().test(biologyTutor));
        assertTrue(command.getPredicate().test(mathTutor));
        assertFalse(command.getPredicate().test(physicsTutor));
    }
}
