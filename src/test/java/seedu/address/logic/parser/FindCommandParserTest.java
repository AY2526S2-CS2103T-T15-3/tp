package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RATE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.Person;
import seedu.address.model.person.Subject;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class FindCommandParserTest {

    private final FindCommandParser parser = new FindCommandParser();

    // ================= HELPER METHODS =================

    private void assertMatch(FindCommand command, Person... persons) {
        for (Person person : persons) {
            assertTrue(command.getPredicate().test(person),
                    "Expected match for person: " + person);
        }
    }

    private void assertNoMatch(FindCommand command, Person... persons) {
        for (Person person : persons) {
            assertFalse(command.getPredicate().test(person),
                    "Expected NO match for person: " + person);
        }
    }

    // ================= FAILURE TESTS =================

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidValue_throwsParseException() {
        // Invalid Subject (cannot be empty, must be alphabetic)
        assertParseFailure(parser, " s/ ", Subject.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " s/!bio", Subject.MESSAGE_CONSTRAINTS);

        // Invalid Tag (alphanumeric)
        assertParseFailure(parser, " t/!tag", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/ ", Tag.MESSAGE_CONSTRAINTS);
        // "friend!" is invalid, "friend colleague" is valid (2 keywords)
        assertParseFailure(parser, " t/friend!", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicatePrefix_throwsParseException() {
        // Duplicate Name not allowed
        assertParseFailure(parser, " n/Alice n/Bob",
                getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        // Duplicate Rate not allowed
        assertParseFailure(parser, " r/10 r/20",
                getErrorMessageForDuplicatePrefixes(PREFIX_RATE));
    }

    @Test
    public void parse_unsupportedPrefixWithPreamble_throwsParseException() {
        String preambleMsg = "When using universal search (keywords before prefixes), only the following "
                + "prefixes may be used to further refine the search: n/, s/, r/, t/.\n"
                + "Note: any words appearing after a prefix are treated as that prefix's value (e.g. 'r/500 alice' "
                + "treats 'alice' as part of the r/ value).";

        String expected = preambleMsg + "\nUnsupported flags present: p/.\n"
                + "Remove these flags or place the keywords after the prefixes.\n\n"
                + FindCommand.MESSAGE_USAGE;

        assertParseFailure(parser, " Alice p/85355255 t/friend", expected);
    }

    // ================= SUCCESS TESTS =================

    @Test
    public void parse_validArgs_returnsFindCommand() throws Exception {
        // This method intentionally left blank — tests split into focused methods below.
    }

    @Test
    public void parse_universalOnly_returnsFindCommand() throws Exception {
        FindCommand commandUniversal = parser.parse(" Bob");
        assertMatch(commandUniversal, personWithName("Bob Tan"));
        assertNoMatch(commandUniversal, personWithName("Alice"));
    }

    @Test
    public void parse_specificNameMultipleKeywords_returnsFindCommand() throws Exception {
        FindCommand commandName = parser.parse(" n/Ali Paul");
        assertMatch(commandName, personWithName("Alice Pauline"));
        assertNoMatch(commandName, personWithName("Bob Tan"));
        assertNoMatch(commandName, personWithName("Charlie"));
    }

    @Test
    public void parse_specificRateLeadingZeroes() throws Exception {
        FindCommand commandRate = parser.parse(" r/007");
        assertMatch(commandRate, personWithRate("007"));
        assertNoMatch(commandRate, personWithRate("7"));
    }

    @Test
    public void parse_specificSubjectWhitespace() throws Exception {
        FindCommand commandSubject = parser.parse(" s/  Bio  ");
        assertMatch(commandSubject, personWithSubject("Biology"));
        assertNoMatch(commandSubject, personWithSubject("Chemistry"));
    }

    @Test
    public void parse_specificTagWhitespace() throws Exception {
        FindCommand commandTag = parser.parse(" t/  friend  ");
        assertMatch(commandTag, personWithTags("friend"));
        assertNoMatch(commandTag, personWithTags("stranger"));
    }

    @Test
    public void parse_multipleFields_returnsCombinedFindCommand() throws Exception {
        // Intentionally left blank; scenarios split into focused tests below.
    }

    @Test
    public void parse_universalPlusSubject_returnsAndLogic() throws Exception {
        FindCommand command1 = parser.parse(" Bob s/Math");
        assertMatch(command1, personWithNameAndSubject("Bob", "Math"));
        assertNoMatch(command1, personWithNameAndSubject("Bob", "Science"), personWithNameAndSubject("Alice", "Math"));
    }

    @Test
    public void parse_multiplePrefixesNameRateSubject() throws Exception {
        FindCommand command2 = parser.parse(" n/Ali r/17 s/Bio");
        assertMatch(command2, personWithNameRateSubject("Alice", "17", "Biology"));
        assertNoMatch(command2,
                personWithNameRateSubject("Brenda", "17", "Biology"),
                personWithNameRateSubject("Alice", "18", "Biology"),
                personWithNameRateSubject("Alice", "17", "Math"));
    }

    @Test
    public void parse_multipleValuesForField_returnsFindCommand() throws Exception {
        // Intentionally blank; split into focused tests below.
    }

    @Test
    public void parse_multipleSubjectsAndLogic() throws Exception {
        FindCommand commandSubject = parser.parse(" s/Bio s/Math");
        assertMatch(commandSubject, personWithSubjects("Biology", "Math"));
        assertNoMatch(commandSubject, personWithSubject("Physics"), personWithSubject("Biology"));
    }

    @Test
    public void parse_multipleTagsAndLogic() throws Exception {
        FindCommand commandTagPrefixes = parser.parse(" t/friend t/colleague");
        FindCommand commandTagTokens = parser.parse(" t/friend colleague");

        Person friend = personWithTags("friend");
        Person colleague = personWithTags("colleague");
        Person both = personWithTags("friend", "colleague");
        Person other = personWithTags("stranger");

        for (FindCommand cmd : Arrays.asList(commandTagPrefixes, commandTagTokens)) {
            assertMatch(cmd, both);
            assertNoMatch(cmd, friend, colleague, other);
        }
    }

    @Test
    public void parse_hybridTagLogic_returnsCorrectFindCommand() throws Exception {
        // Universal Search (Preamble) + Tags -> Exclusive (AND) Logic for tags
        // Find is filtered by "Alice" AND has ALL tags ("friend" AND "colleague")
        FindCommand command = parser.parse(" Alice t/friend t/colleague");

        Person aliceBoth = personWithNameAndTags("Alice", "friend", "colleague");
        Person aliceFriend = personWithNameAndTags("Alice", "friend");
        Person aliceColleague = personWithNameAndTags("Alice", "colleague");
        Person bobBoth = personWithNameAndTags("Bob", "friend", "colleague");

        assertMatch(command, aliceBoth);
        assertNoMatch(command, aliceFriend, aliceColleague, bobBoth);
    }

    @Test
    public void parse_hybridNameLogic_returnsCorrectFindCommand() throws Exception {
        // Universal Search + Name -> Inclusive (OR) Logic for name keywords
        String preamble = "keyword";
        FindCommand command = parser.parse(preamble + " n/Alice Pauline");

        Person alicePauline = personWithNameAndRate("Alice Pauline", "25");
        Person aliceTan = personWithNameAndRate("Alice Tan", "25");
        Person paulineLim = personWithNameAndRate("Pauline Lim", "25");
        Person bobChoo = personWithNameAndRate("Bob Choo", "25");

        command = parser.parse(" 25 n/Alice Pauline");

        assertMatch(command, alicePauline, aliceTan, paulineLim);
        assertNoMatch(command, bobChoo);
    }

    @Test
    public void parse_hybridSubjectLogic_returnsCorrectFindCommand() throws Exception {
        // Universal Search + Subject -> Exclusive (AND) Logic for subjects
        Person mathPhysics = personWithRateAndSubjects("50", "Math", "Physics");
        Person mathOnly = personWithRateAndSubjects("50", "Math");
        Person physicsOnly = personWithRateAndSubjects("50", "Physics");

        // Preamble "50" matches all. s/Math s/Physics means must have Math AND Physics
        FindCommand command = parser.parse(" 50 s/Math s/Physics");

        assertMatch(command, mathPhysics);
        assertNoMatch(command, mathOnly, physicsOnly);
    }

    // ================= PERSON FACTORY HELPERS =================

    private Person personWithName(String name) {
        return new PersonBuilder().withName(name).build();
    }

    private Person personWithRate(String rate) {
        return new PersonBuilder().withRate(rate).build();
    }

    private Person personWithSubject(String subject) {
        return new PersonBuilder().withSubject(subject).build();
    }

    private Person personWithSubjects(String... subjects) {
        return new PersonBuilder().withSubject(subjects).build();
    }

    private Person personWithTags(String... tags) {
        return new PersonBuilder().withTags(tags).build();
    }

    private Person personWithNameAndSubject(String name, String subject) {
        return new PersonBuilder().withName(name).withSubject(subject).build();
    }

    private Person personWithNameRateSubject(String name, String rate, String subject) {
        return new PersonBuilder().withName(name).withRate(rate).withSubject(subject).build();
    }

    private Person personWithNameAndTags(String name, String... tags) {
        return new PersonBuilder().withName(name).withTags(tags).build();
    }

    private Person personWithNameAndRate(String name, String rate) {
        return new PersonBuilder().withName(name).withRate(rate).build();
    }

    private Person personWithRateAndSubjects(String rate, String... subjects) {
        return new PersonBuilder().withRate(rate).withSubject(subjects).build();
    }
}
