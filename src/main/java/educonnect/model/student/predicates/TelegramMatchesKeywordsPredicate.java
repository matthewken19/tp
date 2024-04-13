package educonnect.model.student.predicates;

import java.util.function.Predicate;

import educonnect.commons.util.ToStringBuilder;
import educonnect.model.student.Student;

/**
 * Tests that a {@code Student}'s {@code Email} matches any of the keywords given.
 */
public class TelegramMatchesKeywordsPredicate implements Predicate<Student> {
    private final String keywordTelegram;

    public TelegramMatchesKeywordsPredicate(String keywordTelegram) {
        this.keywordTelegram = keywordTelegram; //replace
    }

    @Override
    public boolean test(Student student) {
        return student.getTelegramHandle().toString().equals(keywordTelegram);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TelegramMatchesKeywordsPredicate)) {
            return false;
        }

        TelegramMatchesKeywordsPredicate otherTelegramMatchesKeywordsPredicate =
                (TelegramMatchesKeywordsPredicate) other;
        return keywordTelegram.equals(otherTelegramMatchesKeywordsPredicate.keywordTelegram);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywordTelegram).toString();
    }
}
