package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name}, {@code Phone}, {@code Email},
 * {@code Address}, {@code Subject}, {@code Rate}, or {@code Tag} matches any of the keywords given.
 */
public class UniversalSearchPredicate implements Predicate<Person> {
    private final List<String> keywords;

    /**
     * Constructs a {@code UniversalSearchPredicate} with the specified keywords.
     *
     * @param keywords The list of keywords to search for.
     */
    public UniversalSearchPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * Tests if a {@code Person} matches any of the keywords.
     *
     * @param person The person to test.
     * @return True if the person matches any of the keywords, false otherwise.
     */
    @Override
    public boolean test(Person person) {
        return keywords.stream()
                .anyMatch(keyword -> isPersonMatchingKeyword(person, keyword));
    }

    private boolean isPersonMatchingKeyword(Person person, String keyword) {
        return person.getName().isMatchingKeyword(keyword)
                || person.getPhone().isMatchingKeyword(keyword)
                || person.getEmail().isMatchingKeyword(keyword)
                || person.getAddress().isMatchingKeyword(keyword)
                || isSubjectMatching(person, keyword)
                || person.getRate().isMatchingKeyword(keyword)
                || isTagMatching(person, keyword);
    }

    private boolean isSubjectMatching(Person person, String keyword) {
        return person.getSubjects().stream()
                .anyMatch(subject -> StringUtil.containsWordPrefixIgnoreCase(subject.subject, keyword));
    }

    private boolean isTagMatching(Person person, String keyword) {
        return person.getTags().stream()
                .anyMatch(tag -> StringUtil.containsWordPrefixIgnoreCase(tag.tagName, keyword));
    }

    /**
     * Checks if this object is equal to another object.
     *
     * @param other The object to compare with.
     * @return True if the other object is an instance of {@code UniversalSearchPredicate} and has the same keywords.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UniversalSearchPredicate)) {
            return false;
        }

        UniversalSearchPredicate otherUniversalSearchPredicate = (UniversalSearchPredicate) other;
        return keywords.equals(otherUniversalSearchPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("keywords", keywords)
                .toString();
    }
}
