package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class UniversalSearchPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        UniversalSearchPredicate firstPredicate = new UniversalSearchPredicate(firstPredicateKeywordList);
        UniversalSearchPredicate secondPredicate = new UniversalSearchPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        UniversalSearchPredicate firstPredicateCopy = new UniversalSearchPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different person -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_universalSearch_returnsTrue() {
        // Name match (prefix)
        assertMatch("Ali", new PersonBuilder().withName("Alice Bob").build());

        // Phone match (prefix)
        assertMatch("123", new PersonBuilder().withPhone("12345").build());

        // Email match (prefix)
        assertMatch("ali", new PersonBuilder().withEmail("alice@example.com").build());

        // Address match (prefix)
        assertMatch("Jurong", new PersonBuilder().withAddress("Jurong West").build());

        // Subject match (prefix)
        assertMatch("Mat", new PersonBuilder().withSubject("Math").build());

        // Rate match (prefix)
        assertMatch("5", new PersonBuilder().withRate("50").build());

        // Tag match (prefix)
        assertMatch("fri", new PersonBuilder().withTags("friend").build());

        // Multiple keywords, one matches
        UniversalSearchPredicate predicate = new UniversalSearchPredicate(Arrays.asList("NoMatch", "Ali"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_universalSearch_returnsFalse() {
        // Non-matching keyword
        assertNoMatch("Carol", new PersonBuilder().withName("Alice Bob").build());
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        UniversalSearchPredicate predicate = new UniversalSearchPredicate(keywords);

        String expected = UniversalSearchPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }

    // ================= HELPER METHODS =================

    private void assertMatch(String keyword, Person person) {
        UniversalSearchPredicate predicate = new UniversalSearchPredicate(Collections.singletonList(keyword));
        assertTrue(predicate.test(person), "Expected predicate to match Person with keyword: " + keyword);
    }

    private void assertNoMatch(String keyword, Person person) {
        UniversalSearchPredicate predicate = new UniversalSearchPredicate(Collections.singletonList(keyword));
        assertFalse(predicate.test(person), "Expected predicate NOT to match Person with keyword: " + keyword);
    }
}
