package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class TagContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        TagContainsKeywordsPredicate firstPredicate = predicate(firstPredicateKeywordList);
        TagContainsKeywordsPredicate secondPredicate = predicate(secondPredicateKeywordList);
        // Test different isMatchAll property
        TagContainsKeywordsPredicate thirdPredicate = predicateAll(firstPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        TagContainsKeywordsPredicate firstPredicateCopy = predicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));

        // different isMatchAll -> returns false
        assertFalse(firstPredicate.equals(thirdPredicate));
    }

    @Test
    public void test_inclusiveSearch_returnsCorrectResult() {
        // One keyword matching
        TagContainsKeywordsPredicate predicate = predicate("friend");
        assertMatch(predicate, "friend", "colleague");

        // Multiple keywords, one matching (OR logic)
        predicate = predicate(Arrays.asList("friend", "family"));
        assertMatch(predicate, "friend", "colleague");

        // Multiple keywords, mixed case
        predicate = predicate(Arrays.asList("frIeNd", "coLLeaGue"));
        assertMatch(predicate, "friend", "colleague");

        // Zero keywords -> False
        predicate = predicate(Collections.emptyList());
        assertNoMatch(predicate, "friend");

        // Non-matching keyword -> False
        predicate = predicate(Arrays.asList("classmate"));
        assertNoMatch(predicate, "friend", "colleague");
    }

    @Test
    public void test_exclusiveSearch_returnsCorrectResult() {

        // One keyword matching
        TagContainsKeywordsPredicate predicate = predicateAll(Collections.singletonList("friend"));
        assertMatch(predicate, "friend", "colleague");

        // All keywords matching (AND logic)
        predicate = predicateAll(Arrays.asList("friend", "colleague"));
        assertMatch(predicate, "friend", "colleague", "neighbor");

        // Only one matching keyword -> False (AND logic requirements not met)
        predicate = predicateAll(Arrays.asList("friend", "family"));
        assertNoMatch(predicate, "friend", "colleague");

        // No matching keywords -> False
        predicate = predicateAll(Arrays.asList("classmate"));
        assertNoMatch(predicate, "friend");
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(keywords);
        String expected = TagContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + ", isMatchAll=false}";
        assertEquals(expected, predicate.toString());

        TagContainsKeywordsPredicate predicateAll = new TagContainsKeywordsPredicate(keywords, true);
        String expectedAll = TagContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + ", isMatchAll=true}";
        assertEquals(expectedAll, predicateAll.toString());
    }

    // ================= HELPER METHODS =================

    private void assertMatch(TagContainsKeywordsPredicate predicate, String... personTags) {
        Person person = new PersonBuilder().withTags(personTags).build();
        assertTrue(predicate.test(person), "Expected predicate to match Person with tags: " + Arrays.toString(personTags));
    }

    private void assertNoMatch(TagContainsKeywordsPredicate predicate, String... personTags) {
        Person person = new PersonBuilder().withTags(personTags).build();
        assertFalse(predicate.test(person), "Expected predicate NOT to match Person with tags: " + Arrays.toString(personTags));
    }

    private static TagContainsKeywordsPredicate predicate(String singleKeyword) {
        return new TagContainsKeywordsPredicate(Collections.singletonList(singleKeyword));
    }

    private static TagContainsKeywordsPredicate predicate(List<String> keywords) {
        return new TagContainsKeywordsPredicate(keywords);
    }

    private static TagContainsKeywordsPredicate predicateAll(List<String> keywords) {
        return new TagContainsKeywordsPredicate(keywords, true);
    }

    private static TagContainsKeywordsPredicate predicateAll(String singleKeyword) {
        return new TagContainsKeywordsPredicate(Collections.singletonList(singleKeyword), true);
    }
}
