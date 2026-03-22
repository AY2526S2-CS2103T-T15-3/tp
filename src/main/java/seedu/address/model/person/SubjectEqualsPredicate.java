package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Tests whether a {@code Person}'s subject matches any of the specified subjects.
 * Matching is performed using prefix matching and is case-insensitive.
 */
public class SubjectEqualsPredicate implements Predicate<Person> {
    private final Set<Subject> subjects;
    private final Set<String> normalizedTargetStrings;

    /**
     * Constructs a {@code SubjectEqualsPredicate} from a set of subjects.
     */
    public SubjectEqualsPredicate(Set<Subject> subjects) {
        requireNonNull(subjects);
        this.subjects = Collections.unmodifiableSet(new HashSet<>(subjects));
        this.normalizedTargetStrings = Collections.unmodifiableSet(normalizeSubjects(this.subjects));
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        return personMatchesAnyTarget(person);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SubjectEqualsPredicate)) {
            return false;
        }
        SubjectEqualsPredicate otherPredicate = (SubjectEqualsPredicate) other;
        return Objects.equals(this.normalizedTargetStrings, otherPredicate.normalizedTargetStrings);
    }

    @Override
    public String toString() {
        return subjects.toString();
    }

    // ----------------- Helper methods (single level of abstraction) -----------------

    private Set<String> normalizeSubjects(Set<Subject> subjectsToNormalize) {
        Set<String> normalized = new HashSet<>();
        for (Subject s : subjectsToNormalize) {
            normalized.add(s.toString().toLowerCase());
        }
        return normalized;
    }

    private boolean personMatchesAnyTarget(Person person) {
        for (Subject personSub : person.getSubjects()) {
            if (personSubjectMatchesAnyTarget(personSub.toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean personSubjectMatchesAnyTarget(String personSubjectString) {
        String lowerPerson = personSubjectString.toLowerCase();
        for (String target : normalizedTargetStrings) {
            if (matchesMultiWordPrefix(lowerPerson, target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the lower-cased personSubjectString contains a sequence of words
     * whose prefixes match the sequence of target words.
     * Example: personSubjectString="biology geography lab", target="bio geo" -> true
     */
    private boolean matchesMultiWordPrefix(String lowerPerson, String targetLower) {
        if (targetLower == null || targetLower.isEmpty()) {
            return false;
        }

        String[] personWords = splitLowerWords(lowerPerson);
        String[] targetWords = splitLowerWords(targetLower);

        // try every possible alignment in personWords
        for (int i = 0; i + targetWords.length <= personWords.length; i++) {
            if (startsWithSequenceAt(personWords, targetWords, i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Split the given string into lower-cased words, trimming whitespace.
     * Returns an empty array for null or blank input.
     */
    private String[] splitLowerWords(String s) {
        if (s == null) {
            return new String[0];
        }
        String trimmed = s.trim();
        if (trimmed.isEmpty()) {
            return new String[0];
        }
        String[] raw = trimmed.split("\\s+");
        for (int i = 0; i < raw.length; i++) {
            raw[i] = raw[i].toLowerCase();
        }
        return raw;
    }

    /**
     * Returns true if personWords starting at startIndex contains a sequence whose words start with
     * the corresponding targetWords.
     */
    private boolean startsWithSequenceAt(String[] personWords, String[] targetWords, int startIndex) {
        for (int j = 0; j < targetWords.length; j++) {
            String pw = personWords[startIndex + j];
            String tw = targetWords[j];
            if (!pw.startsWith(tw)) {
                return false;
            }
        }
        return true;
    }

}
