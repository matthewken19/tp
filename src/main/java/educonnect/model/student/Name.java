package educonnect.model.student;

import static educonnect.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a Student's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain alphanumeric characters, spaces and hyphens, and it should not be blank. "
            + "Cannot have two dashes or whitespaces or a combination of both";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} -]*";
    public static final String INVALID_REGEX_COMBINATION_WHITESPACE_AND_DASH = "(\\s{2}|--|\\s-|-\\s)";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        Pattern invalidPattern = Pattern.compile(INVALID_REGEX_COMBINATION_WHITESPACE_AND_DASH);
        Matcher invalidPatternMatcher = invalidPattern.matcher(test);
        return test.matches(VALIDATION_REGEX) && !invalidPatternMatcher.find();
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name)) {
            return false;
        }

        Name otherName = (Name) other;
        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
