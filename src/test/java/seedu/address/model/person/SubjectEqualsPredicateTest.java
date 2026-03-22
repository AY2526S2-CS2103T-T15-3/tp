package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.util.SampleDataUtil;
import seedu.address.testutil.PersonBuilder;

/**
 * Unit tests for {@link SubjectEqualsPredicate}.
 */
public class SubjectEqualsPredicateTest {

    @Test
    public void equals_sameObject_true() {
        Subject subj = new Subject("Biology");
        SubjectEqualsPredicate p = new SubjectEqualsPredicate(Collections.singleton(subj));
        assertTrue(p.equals(p));
    }

    @Test
    public void equals_sameValues_true() {
        SubjectEqualsPredicate p1 = new SubjectEqualsPredicate(Collections.singleton(new Subject("Biology")));
        SubjectEqualsPredicate p2 = new SubjectEqualsPredicate(Collections.singleton(new Subject("Biology")));
        assertTrue(p1.equals(p2));
    }

    @Test
    public void equals_differentValues_false() {
        SubjectEqualsPredicate p1 = new SubjectEqualsPredicate(Collections.singleton(new Subject("Biology")));
        SubjectEqualsPredicate p2 = new SubjectEqualsPredicate(Collections.singleton(new Subject("Math")));
        assertFalse(p1.equals(p2));
    }

    @Test
    public void equals_multipleSubjectsOrderIrrelevant_true() {
        Set<Subject> a = new HashSet<>();
        a.add(new Subject("Math"));
        a.add(new Subject("Biology"));
        Set<Subject> b = new HashSet<>();
        b.add(new Subject("Biology"));
        b.add(new Subject("Math"));

        SubjectEqualsPredicate p1 = new SubjectEqualsPredicate(a);
        SubjectEqualsPredicate p2 = new SubjectEqualsPredicate(b);
        assertTrue(p1.equals(p2));
    }

    @Test
    public void test_personWithMultipleSubjects_matchesIfAnyMatch() {
        // person has Biology and Math
        Person person = new PersonBuilder().withSubject("Biology").withName("Bob").build();
        // PersonBuilder.withSubject replaces the subject set; to create multiple subjects use SampleDataUtil
        Person multi = new Person(new Name("Bob"), new Phone("123"), new Email("a@b.com"), new Address("addr"),
                SampleDataUtil.getSubjectSet("Biology", "Math"), new Rate("10"), Collections.emptySet());

        SubjectEqualsPredicate predicate = new SubjectEqualsPredicate(Collections.singleton(new Subject("Bio")));
        assertTrue(predicate.test(multi));
    }

    @Test
    public void test_subjectPrefixMatch_returnsTrue() {
        SubjectEqualsPredicate predicate = new SubjectEqualsPredicate(Collections.singleton(new Subject("Bio")));
        assertTrue(predicate.test(new PersonBuilder().withSubject("Biology").build()));
        assertTrue(predicate.test(new PersonBuilder().withSubject("Biology Advanced").build()));
        assertTrue(predicate.test(new PersonBuilder().withSubject("bio lab").build())); // case-insensitive
    }

    @Test
    public void test_subjectPrefixMatch_returnsFalse() {
        SubjectEqualsPredicate predicate = new SubjectEqualsPredicate(Collections.singleton(new Subject("Bio")));
        // mid-word substring should not match
        assertFalse(predicate.test(new PersonBuilder().withSubject("Microbiology").build()));
        // completely different
        assertFalse(predicate.test(new PersonBuilder().withSubject("Math").build()));
    }

    @Test
    public void equals_emptySets_true() {
        SubjectEqualsPredicate p1 = new SubjectEqualsPredicate(Collections.emptySet());
        SubjectEqualsPredicate p2 = new SubjectEqualsPredicate(Collections.emptySet());
        assertTrue(p1.equals(p2));
    }

    @Test
    public void equals_transitive_true() {
        SubjectEqualsPredicate p1 = new SubjectEqualsPredicate(Collections.singleton(new Subject("Biology")));
        SubjectEqualsPredicate p2 = new SubjectEqualsPredicate(Collections.singleton(new Subject("biology")));
        SubjectEqualsPredicate p3 = new SubjectEqualsPredicate(Collections.singleton(new Subject("BIOLOGY")));
        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p3));
        assertTrue(p1.equals(p3));
    }

    @Test
    public void equals_differentSize_false() {
        Set<Subject> one = new HashSet<>();
        one.add(new Subject("Biology"));
        Set<Subject> two = new HashSet<>();
        two.add(new Subject("Biology"));
        two.add(new Subject("Math"));

        SubjectEqualsPredicate p1 = new SubjectEqualsPredicate(one);
        SubjectEqualsPredicate p2 = new SubjectEqualsPredicate(two);
        assertFalse(p1.equals(p2));
    }

    @Test
    public void equals_trailingSpaceDifference_false() {
        // Subject accepts internal/trailing spaces; equality should be sensitive to the actual string
        SubjectEqualsPredicate p1 = new SubjectEqualsPredicate(Collections.singleton(new Subject("Biology")));
        SubjectEqualsPredicate p2 = new SubjectEqualsPredicate(Collections.singleton(new Subject("Biology ")));
        assertFalse(p1.equals(p2));
    }

    @Test
    public void toString_singleton_exact() {
        SubjectEqualsPredicate predicate = new SubjectEqualsPredicate(Collections.singleton(new Subject("Bio")));
        assertEquals("[Bio]", predicate.toString());
    }

    @Test
    public void toString_multiple_containsAll() {
        Set<Subject> targets = new HashSet<>();
        targets.add(new Subject("Math"));
        targets.add(new Subject("Biology"));
        SubjectEqualsPredicate predicate = new SubjectEqualsPredicate(targets);
        String s = predicate.toString();
        assertTrue(s.contains("Math"));
        assertTrue(s.contains("Biology"));
    }

    @Test
    public void equals_differentTypeObject_false() {
        SubjectEqualsPredicate p = new SubjectEqualsPredicate(Collections.singleton(new Subject("Bio")));
        assertFalse(p.equals(new Object()));
    }
}

