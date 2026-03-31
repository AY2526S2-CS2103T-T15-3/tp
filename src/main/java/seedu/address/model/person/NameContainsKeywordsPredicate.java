package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
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

        String fullName = getPersonFullName(person);

        return matchesAny(fullName);
    }

    private boolean isKeywordsEmpty() {
        return keywords == null || keywords.isEmpty();
    }

    private String getPersonFullName(Person person) {
        return person.getName().fullName;
    }

    private boolean matchesAny(String fullName) {
        return keywords.stream()
                .anyMatch(keyword -> matchesKeyword(fullName, keyword));
    }

    private boolean matchesKeyword(String fullName, String keyword) {
        return StringUtil.containsWordPrefixIgnoreCase(fullName, keyword);
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
