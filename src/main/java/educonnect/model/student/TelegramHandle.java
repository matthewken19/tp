package educonnect.model.student;

import static educonnect.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Student's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidTelegramHandle(String)}
 */
public class TelegramHandle {

    public static final String MESSAGE_CONSTRAINTS =
            "Telegram handles should start with @, consist of only alphanumeric characters and underscores (_) "
            + "and have a minimum of 5 characters";
    public static final String VALIDATION_REGEX = "^@(?=(?:[0-9_]*[a-z]){3})[a-z0-9_]{5,}$";
    public final String value;

    /**
     * Constructs a {@code TelegramHandle}.
     * @param handle a valid {@code String} representing the telegram handle.
     */
    public TelegramHandle(String handle) {
        String lowerHandle = handle.toLowerCase();
        requireNonNull(handle);
        checkArgument(isValidTelegramHandle(lowerHandle), MESSAGE_CONSTRAINTS);
        value = lowerHandle;
    }

    /**
     * Returns true if a given string is a valid telegram handle.
     */
    public static boolean isValidTelegramHandle(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TelegramHandle)) {
            return false;
        }

        TelegramHandle otherHandle = (TelegramHandle) other;
        return value.equals(otherHandle.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
