package seedu.address.model.tag;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;

/**
 * Tests that a {@code Person}'s {@code Tag}s matches any of the keywords given.
 */
public class TagContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;
    private final boolean matchAll;

    /**
     * Constructs a {@code TagContainsKeywordsPredicate}.
     *
     * @param keywords The list of keywords to search for.
     * @param matchAll If true, matches all keywords (AND logic). If false, matches any keyword (OR logic).
     */
    public TagContainsKeywordsPredicate(List<String> keywords, boolean matchAll) {
        this.keywords = keywords;
        this.matchAll = matchAll;
    }

    /**
     * Constructs a {@code TagContainsKeywordsPredicate} with default matching logic (OR).
     *
     * @param keywords The list of keywords to search for.
     */
    public TagContainsKeywordsPredicate(List<String> keywords) {
        this(keywords, false);
    }

    @Override
    public boolean test(Person person) {
        if (matchAll) {
            return keywords.stream()
                    .allMatch(keyword -> person.getTags().stream()
                            .map(tag -> tag.tagName.toLowerCase())
                            .anyMatch(personTag -> personTag.startsWith(keyword.toLowerCase())));
        } else {
            return keywords.stream()
                    .anyMatch(keyword -> person.getTags().stream()
                            .map(tag -> tag.tagName.toLowerCase())
                            .anyMatch(personTag -> personTag.startsWith(keyword.toLowerCase())));
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TagContainsKeywordsPredicate)) {
            return false;
        }

        TagContainsKeywordsPredicate otherTagContainsKeywordsPredicate = (TagContainsKeywordsPredicate) other;
        return keywords.equals(otherTagContainsKeywordsPredicate.keywords)
                && matchAll == otherTagContainsKeywordsPredicate.matchAll;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("keywords", keywords)
                .add("matchAll", matchAll)
                .toString();
    }
}
