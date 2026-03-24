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
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        SubjectContainsKeywordsPredicate firstPredicate = new SubjectContainsKeywordsPredicate(firstPredicateKeywordList);
        SubjectContainsKeywordsPredicate secondPredicate = new SubjectContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        SubjectContainsKeywordsPredicate firstPredicateCopy = new SubjectContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different predicate -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));

        // different isMatchAll -> returns false
        assertFalse(firstPredicate.equals(new SubjectContainsKeywordsPredicate(firstPredicateKeywordList, true)));
    }

    @Test
    public void test_subjectContainsKeywords_returnsTrue() {
        // One keyword
        SubjectContainsKeywordsPredicate predicate = new SubjectContainsKeywordsPredicate(Collections.singletonList("Math"));
        assertTrue(predicate.test(new PersonBuilder().withSubject("Math").build()));

        // Multiple keywords (OR)
        predicate = new SubjectContainsKeywordsPredicate(Arrays.asList("Math", "Physics"));
        assertTrue(predicate.test(new PersonBuilder().withSubject("Math", "Biology").build()));

        // Mixed-case keywords
        predicate = new SubjectContainsKeywordsPredicate(Arrays.asList("mAtH", "pHySiCs"));
        assertTrue(predicate.test(new PersonBuilder().withSubject("Math").build()));
    }

    @Test
    public void test_subjectDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        SubjectContainsKeywordsPredicate predicate = new SubjectContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withSubject("Math").build()));

        // Non-matching keyword
        predicate = new SubjectContainsKeywordsPredicate(Arrays.asList("Physics"));
        assertFalse(predicate.test(new PersonBuilder().withSubject("Math").build()));
    }

    @Test
    public void test_subjectContainsKeywordsMatchAll_returnsCheck() {
        // Match All: "Math" AND "Physics"
        SubjectContainsKeywordsPredicate predicate = new SubjectContainsKeywordsPredicate(Arrays.asList("Math", "Physics"), true);

        // Matches both -> True
        assertTrue(predicate.test(new PersonBuilder().withSubject("Math", "Physics").build()));

        // Matches one -> False
        assertFalse(predicate.test(new PersonBuilder().withSubject("Math", "Biology").build()));

        // Matches neither -> False
        assertFalse(predicate.test(new PersonBuilder().withSubject("Biology", "Chemistry").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        SubjectContainsKeywordsPredicate predicate = new SubjectContainsKeywordsPredicate(keywords);

        String expected = SubjectContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + ", isMatchAll=false}";
        assertEquals(expected, predicate.toString());
    }
}

