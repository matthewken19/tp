package educonnect.model.student.predicates;

import educonnect.commons.util.ToStringBuilder;
import educonnect.model.student.Student;

import java.util.function.Predicate;

/**
 * Tests that a {@code Student}'s {@code Email} matches exactly the keywords given.
 */
public class EmailMatchesKeywordsPredicate implements Predicate<Student> {
    private final String keywordEmail;

    public EmailMatchesKeywordsPredicate(String keywordEmail) {
        this.keywordEmail = keywordEmail; //replace
    }

    @Override
    public boolean test(Student student) {
        return student.getEmail().toString().equals(keywordEmail);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EmailMatchesKeywordsPredicate)) {
            return false;
        }

        EmailMatchesKeywordsPredicate otherEmailMatchesKeywordsPredicate = (EmailMatchesKeywordsPredicate) other;
        return keywordEmail.equals(otherEmailMatchesKeywordsPredicate.keywordEmail);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywordEmail).toString();
    }
}
