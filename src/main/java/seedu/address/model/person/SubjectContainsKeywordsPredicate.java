package seedu.address.model.person;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Subject}s match any of the keywords given.
 */
public class SubjectContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    /**
     * Constructs a {@code SubjectContainsKeywordsPredicate}.
     *
     * @param keywords The list of keywords to search for.
     */
    public SubjectContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        // High-level flow: validate keywords, prepare lowercase subjects, then delegate to matching strategy.
        if (isKeywordsEmpty()) {
            return false;
        }

        List<String> personSubjectsLower = getPersonSubjectsLower(person);

        return matchesAll(personSubjectsLower);
    }

    private boolean isKeywordsEmpty() {
        return keywords == null || keywords.isEmpty();
    }

    private List<String> getPersonSubjectsLower(Person person) {
        return person.getSubjects().stream()
                .map(subject -> getNormalizedLowerSubject(subject))
                .toList();
    }

    private String getNormalizedLowerSubject(Subject subject) {
        if (subject.subject == null) {
            return "";
        }
        return normalizeSpaces(subject.subject.toLowerCase());
    }

    private String normalizeSpaces(String subject) {
        return subject.trim().replaceAll("\\s+", " ");
    }

    private boolean matchesAll(List<String> personSubjectsLower) {
        return keywords.stream()
                .allMatch(keyword -> hasSubjectMatchingKeywordLower(personSubjectsLower, keyword));
    }

    private boolean hasSubjectMatchingKeywordLower(List<String> personSubjectsLower, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return false;
        }
        String kwLower = keyword.toLowerCase().trim().replaceAll("\\s+", " ");

        return personSubjectsLower.stream()
                .anyMatch(normalizedSubject -> isSubjectMatchingNormalizedKeyword(normalizedSubject, kwLower));
    }

    private boolean isSubjectMatchingNormalizedKeyword(String normalizedSubject, String kwLower) {
        if (normalizedSubject.startsWith(kwLower)) {
            return true;
        }
        return normalizedSubject.contains(" " + kwLower);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SubjectContainsKeywordsPredicate)) {
            return false;
        }

        SubjectContainsKeywordsPredicate otherSubjectContainsKeywordsPredicate =
                (SubjectContainsKeywordsPredicate) other;
        return Objects.equals(keywords, otherSubjectContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("keywords", keywords)
                .toString();
    }
}
