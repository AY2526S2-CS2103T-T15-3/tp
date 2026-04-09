package seedu.address.model.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;

/**
 * Tests that a {@code Person}'s {@code Tag}s matches given keywords.
 */
public class TagContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;
    private final List<String> normalizedKeywordsLower;

    /**
     * Constructs a {@code TagContainsKeywordsPredicate}.
     *
     * @param keywords The list of keywords to search for (must not be null).
     */
    public TagContainsKeywordsPredicate(List<String> keywords) {
        Objects.requireNonNull(keywords, "keywords must not be null");
        this.keywords = Collections.unmodifiableList(new ArrayList<>(keywords));
        this.normalizedKeywordsLower = normalizeKeywordsLower(this.keywords);
    }

    /**
     * Normalizes the supplied keywords to a lower-cased, trimmed, unmodifiable list.
     * This keeps the constructor at a higher level of abstraction.
     */
    private static List<String> normalizeKeywordsLower(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(
                keywords.stream()
                        .map(s -> s == null ? "" : s.trim().toLowerCase())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public boolean test(Person person) {
        // Single-level-of-abstraction entry: validate, prepare, then delegate to the chosen matching strategy.
        if (isKeywordsEmpty()) {
            return false;
        }

        List<String> personTagNamesLower = getPersonTagNamesLower(person);

        return matchesAll(personTagNamesLower);
    }

    private boolean isKeywordsEmpty() {
        return normalizedKeywordsLower.isEmpty();
    }

    private List<String> getPersonTagNamesLower(Person person) {
        // Compute lowercase tag names once per invocation to avoid repeated toLowerCase calls.
        return person.getTags().stream()
                .map(tag -> tag.tagName == null ? "" : tag.tagName.toLowerCase())
                .collect(Collectors.toList());
    }

    private boolean matchesAll(List<String> personTagNamesLower) {
        return normalizedKeywordsLower.stream()
                .allMatch(keywordLower -> hasTagMatchingKeywordLower(personTagNamesLower, keywordLower));
    }

    private boolean hasTagMatchingKeywordLower(List<String> personTagNamesLower, String keywordLower) {
        return personTagNamesLower.stream()
                .anyMatch(personTag -> personTag.startsWith(keywordLower));
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
        return keywords.equals(otherTagContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("keywords", keywords)
                .toString();
    }
}
