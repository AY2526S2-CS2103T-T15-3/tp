package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    /**
     * Constructs a {@code NameContainsKeywordsPredicate}.
     *
     * @param keywords The list of keywords to search for.
     */
    public NameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        // Single-level-of-abstraction: validate -> prepare -> delegate to matching strategy.
        if (isKeywordsEmpty()) {
            return false;
        }

        return matchesAny(person);
    }

    private boolean isKeywordsEmpty() {
        return keywords == null || keywords.isEmpty();
    }

    private boolean matchesAny(Person person) {
        return keywords.stream()
                .anyMatch(keyword -> matchesKeyword(person, keyword));
    }

    private boolean matchesKeyword(Person person, String keyword) {
        return person.getName().isMatchingKeyword(keyword);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsKeywordsPredicate)) {
            return false;
        }

        NameContainsKeywordsPredicate otherNameContainsKeywordsPredicate = (NameContainsKeywordsPredicate) other;
        return keywords.equals(otherNameContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("keywords", keywords)
                .toString();
    }
}
