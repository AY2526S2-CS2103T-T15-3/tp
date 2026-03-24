package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Subject}s match any of the keywords given.
 */
public class SubjectContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;
    private final boolean isMatchAll;

    /**
     * Constructs a {@code SubjectContainsKeywordsPredicate}.
     *
     * @param keywords The list of keywords to search for.
     * @param isMatchAll If true, matches all keywords (AND logic). If false, matches any keyword (OR logic).
     */
    public SubjectContainsKeywordsPredicate(List<String> keywords, boolean isMatchAll) {
        this.keywords = keywords;
        this.isMatchAll = isMatchAll;
    }

    /**
     * Constructs a {@code SubjectContainsKeywordsPredicate} with default matching logic (OR).
     *
     * @param keywords The list of keywords to search for.
     */
    public SubjectContainsKeywordsPredicate(List<String> keywords) {
        this(keywords, false);
    }

    @Override
    public boolean test(Person person) {
        // High-level flow: validate keywords, prepare lowercase subjects, then delegate to matching strategy.
        if (isKeywordsEmpty()) {
            return false;
        }

        List<String> personSubjectsLower = getPersonSubjectsLower(person);

        return isMatchAll ? matchesAll(personSubjectsLower) : matchesAny(personSubjectsLower);
    }

    private boolean isKeywordsEmpty() {
        return keywords == null || keywords.isEmpty();
    }

    private List<String> getPersonSubjectsLower(Person person) {
        return person.getSubjects().stream()
                .map(subject -> subject.subject == null ? "" : subject.subject.toLowerCase())
                .toList();
    }

    private boolean matchesAll(List<String> personSubjectsLower) {
        return keywords.stream()
                .allMatch(keyword -> hasSubjectMatchingKeywordLower(personSubjectsLower, keyword));
    }

    private boolean matchesAny(List<String> personSubjectsLower) {
        return keywords.stream()
                .anyMatch(keyword -> hasSubjectMatchingKeywordLower(personSubjectsLower, keyword));
    }

    private boolean hasSubjectMatchingKeywordLower(List<String> personSubjectsLower, String keyword) {
        String kwLower = keyword == null ? "" : keyword.toLowerCase();
        return personSubjectsLower.stream()
                .anyMatch(personSubject -> personSubject.startsWith(kwLower));
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
        return keywords.equals(otherSubjectContainsKeywordsPredicate.keywords)
                && isMatchAll == otherSubjectContainsKeywordsPredicate.isMatchAll;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("keywords", keywords)
                .add("isMatchAll", isMatchAll)
                .toString();
    }
}

