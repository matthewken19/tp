package educonnect.model.student.predicates;

import java.util.function.Predicate;

import educonnect.commons.util.ToStringBuilder;
import educonnect.model.student.Student;

/**
 * Tests that a {@code Student}'s {@code Id} matches exactly the keywords given.
 */
public class IdMatchesKeywordsPredicate implements Predicate<Student> {
    private final String keywordId;

    public IdMatchesKeywordsPredicate(String keywordId) {
        this.keywordId = keywordId; //replace
    }

    @Override
    public boolean test(Student student) {
        return student.getStudentId().toString().equals(keywordId);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof IdMatchesKeywordsPredicate)) {
            return false;
        }

        IdMatchesKeywordsPredicate otherIdMatchesKeywordsPredicate = (IdMatchesKeywordsPredicate) other;
        return keywordId.equals(otherIdMatchesKeywordsPredicate.keywordId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywordId).toString();
    }
}
