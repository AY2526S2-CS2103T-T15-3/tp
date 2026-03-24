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
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class FindCommandParserTest {

    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_universalOnly_returnsFindCommand() throws Exception {
        FindCommand command = parser.parse(" Bob");
        // Preamble is "Bob"

        Person bob = new PersonBuilder().withName("Bob Tan").build();
        Person alice = new PersonBuilder().withName("Alice").build();

        assertTrue(command.getPredicate().test(bob));
        assertFalse(command.getPredicate().test(alice));
    }

    @Test
    public void parse_universalAndPrefix_returnsCombinedFindCommand() throws Exception {
        FindCommand command = parser.parse(" Bob s/Math");
        // Universal "Bob" AND Subject "Math"

        Person bobMath = new PersonBuilder().withName("Bob").withSubject("Math").build();

        // matches Universal but not Subject
        Person bobSci = new PersonBuilder().withName("Bob").withSubject("Science").build();

        // matches Subject but not Universal (Alice doesn't contain "Bob")
        Person aliceMath = new PersonBuilder().withName("Alice").withSubject("Math").build();

        assertTrue(command.getPredicate().test(bobMath));
        assertFalse(command.getPredicate().test(bobSci));
        assertFalse(command.getPredicate().test(aliceMath));
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
    public void parse_validTagArg_returnsFindCommand() throws Exception {
        FindCommand command = parser.parse(" t/friend");

        Person friend = new PersonBuilder().withTags("friend").build();
        Person stranger = new PersonBuilder().withTags("colleague").build();

        assertTrue(command.getPredicate().test(friend));
        assertFalse(command.getPredicate().test(stranger));
    }

    @Test
    public void parse_tagWithWhitespace_returnsFindCommand() throws Exception {
        FindCommand command = parser.parse(" t/  friend  ");

        Person friend = new PersonBuilder().withTags("friend").build();
        Person stranger = new PersonBuilder().withTags("colleague").build();

        assertTrue(command.getPredicate().test(friend));
        assertFalse(command.getPredicate().test(stranger));
    }

    @Test
    public void parse_invalidTag_throwsParseException() {
        assertParseFailure(parser, " t/!tag", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/ ", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_preambleWithTag_throwsParseException() throws Exception {
        // Now that t/ is allowed with a preamble, this should parse and produce a combined predicate.
        FindCommand command = parser.parse(" Alice t/friend");

        Person aliceFriend = new PersonBuilder().withName("Alice").withTags("friend").build();
        Person aliceOther = new PersonBuilder().withName("Alice").withTags("colleague").build();
        Person bobFriend = new PersonBuilder().withName("Bob").withTags("friend").build();

        assertTrue(command.getPredicate().test(aliceFriend));
        assertFalse(command.getPredicate().test(aliceOther));
        assertFalse(command.getPredicate().test(bobFriend));
    }

    @Test
    public void parse_preambleWithMultipleUnsupportedPrefixes_throwsParseException() throws Exception {
        String preambleMsg = "When using universal search (keywords before prefixes), only the following "
                + "prefixes may be used to further refine the search: n/, s/, r/, t/.\n"
                + "Note: any words appearing after a prefix are treated as that prefix's value (e.g. 'r/500 alice' "
                + "treats 'alice' as part of the r/ value).";

        String expected = preambleMsg + "\nUnsupported flags present: p/.\n"
                + "Remove these flags or place the keywords after the prefixes.\n\n"
                + FindCommand.MESSAGE_USAGE;

        assertParseFailure(parser, " Alice p/85355255 t/friend", expected);
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

    @Test
    public void parse_multipleTagKeywords_returnsFindCommand() throws Exception {
        // multiple t/ prefixes
        // No preamble -> specific find -> Inclusive (OR) logic
        FindCommand command1 = parser.parse(" t/friend t/colleague");

        Person friend = new PersonBuilder().withTags("friend").build();
        Person colleague = new PersonBuilder().withTags("colleague").build();
        Person both = new PersonBuilder().withTags("friend", "colleague").build();
        Person other = new PersonBuilder().withTags("acquaintance").build();

        // Inclusive: friend OR colleague
        assertTrue(command1.getPredicate().test(friend));
        assertTrue(command1.getPredicate().test(colleague));
        assertTrue(command1.getPredicate().test(both));
        assertFalse(command1.getPredicate().test(other));

        // multiple keywords in a single t/ value
        // No preamble -> Inclusive (OR) logic
        FindCommand command2 = parser.parse(" t/friend colleague");

        assertTrue(command2.getPredicate().test(friend));
        assertTrue(command2.getPredicate().test(colleague));
        assertTrue(command2.getPredicate().test(both));
        assertFalse(command2.getPredicate().test(other));
    }

    @Test
    public void parse_universalAndMultipleTagKeywords_returnsExclusiveFindCommand() throws Exception {
        // Preamble present -> Exclusive (AND) logic for tags
        // Find universal "Alice" AND (tag "friend" AND tag "colleague")
        FindCommand command = parser.parse(" Alice t/friend t/colleague");

        Person aliceBoth = new PersonBuilder().withName("Alice").withTags("friend", "colleague").build();
        Person aliceFriendOnly = new PersonBuilder().withName("Alice").withTags("friend").build();
        Person aliceColleagueOnly = new PersonBuilder().withName("Alice").withTags("colleague").build();
        Person bobBoth = new PersonBuilder().withName("Bob").withTags("friend", "colleague").build();

        assertTrue(command.getPredicate().test(aliceBoth));
        assertFalse(command.getPredicate().test(aliceFriendOnly)); // Missing colleague
        assertFalse(command.getPredicate().test(aliceColleagueOnly)); // Missing friend
        assertFalse(command.getPredicate().test(bobBoth)); // Missing Alice
    }
}
