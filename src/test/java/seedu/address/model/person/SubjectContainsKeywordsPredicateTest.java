package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class SubjectContainsKeywordsPredicateTest {

    @Test
    public void equals_differentConditions_returnsCorrectResult() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        SubjectContainsKeywordsPredicate firstPredicate =
                new SubjectContainsKeywordsPredicate(firstPredicateKeywordList);
        SubjectContainsKeywordsPredicate secondPredicate =
                new SubjectContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        SubjectContainsKeywordsPredicate firstPredicateCopy =
                new SubjectContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different predicate -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_subjectContainsKeywords_returnsTrue() {
        // [Equivalence Partitioning] Single keyword matching full subject
        SubjectContainsKeywordsPredicate predicate =
                new SubjectContainsKeywordsPredicate(Collections.singletonList("Math"));
        assertTrue(predicate.test(new PersonBuilder().withSubject("Math").build()));

        // [Equivalence Partitioning] Word-Prefix keyword matching at the start of a subsequent word
        predicate = new SubjectContainsKeywordsPredicate(Collections.singletonList("Math"));
        assertTrue(predicate.test(new PersonBuilder().withSubject("Advanced Math").build()));

        // [Equivalence Partitioning] Multi-word keyword exact phrase match with spaces
        predicate = new SubjectContainsKeywordsPredicate(Collections.singletonList("Advanced Math"));
        assertTrue(predicate.test(new PersonBuilder().withSubject("Advanced Math").build()));

        // [Boundary Testing] Subject with multiple consecutive spaces (edge case allowed by regex)
        predicate = new SubjectContainsKeywordsPredicate(Collections.singletonList("Math"));
        assertTrue(predicate.test(new PersonBuilder().withSubject("Advanced  Math").build()));

        // [Equivalence Partitioning] Multiple matching keywords (AND logic)
        predicate = new SubjectContainsKeywordsPredicate(Arrays.asList("Math", "Physics"));
        assertTrue(predicate.test(new PersonBuilder().withSubject("Math", "Physics").build()));

        // [Equivalence Partitioning] Case-insensitivity match
        predicate = new SubjectContainsKeywordsPredicate(Arrays.asList("mAtH", "pHySiCs"));
        assertTrue(predicate.test(new PersonBuilder().withSubject("Math", "Physics").build()));
    }

    @Test
    public void test_subjectDoesNotContainKeywords_returnsFalse() {
        // [Boundary Testing] Null keywords list
        SubjectContainsKeywordsPredicate predicate = new SubjectContainsKeywordsPredicate(null);
        assertFalse(predicate.test(new PersonBuilder().withSubject("Math").build()));

        // [Boundary Testing] Zero keywords (empty list)
        predicate = new SubjectContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withSubject("Math").build()));

        // [Boundary Testing] Keyword list containing a null keyword
        predicate = new SubjectContainsKeywordsPredicate(Collections.singletonList(null));
        assertFalse(predicate.test(new PersonBuilder().withSubject("Math").build()));

        // [Equivalence Partitioning] Keyword not matching subject prefix or any exact subsequent words
        predicate = new SubjectContainsKeywordsPredicate(Collections.singletonList("g"));
        assertFalse(predicate.test(new PersonBuilder().withSubject("Biology").build()));

        // [Equivalence Partitioning] Totally unmatching keyword
        predicate = new SubjectContainsKeywordsPredicate(Arrays.asList("Physics"));
        assertFalse(predicate.test(new PersonBuilder().withSubject("Math").build()));

        // [Equivalence Partitioning] Partial match in AND logic (one matches, one doesn't)
        predicate = new SubjectContainsKeywordsPredicate(Arrays.asList("Math", "Biology"));
        assertFalse(predicate.test(new PersonBuilder().withSubject("Math").build()));
    }

    @Test
    public void toString_validKeywords_returnsExpectedString() {
        List<String> keywords = List.of("keyword1", "keyword2");
        SubjectContainsKeywordsPredicate predicate = new SubjectContainsKeywordsPredicate(keywords);

        String expected = SubjectContainsKeywordsPredicate.class.getCanonicalName()
                + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
