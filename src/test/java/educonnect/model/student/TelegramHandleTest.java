package educonnect.model.student;

import static educonnect.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TelegramHandleTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TelegramHandle(null));
    }

    @Test
    public void constructor_invalidTelegramHandle_throwsIllegalArgumentException() {
        String invalidHandle = "";
        assertThrows(IllegalArgumentException.class, () -> new TelegramHandle(invalidHandle));
    }

    @Test
    public void isValidTelegramHandle() {
        // null telegram handle
        assertThrows(NullPointerException.class, () -> TelegramHandle.isValidTelegramHandle(null));

        // invalid telegram handles
        // No @ in front
        assertFalse(TelegramHandle.isValidTelegramHandle(""));
        assertFalse(TelegramHandle.isValidTelegramHandle(" "));
        assertFalse(TelegramHandle.isValidTelegramHandle("91"));
        assertFalse(TelegramHandle.isValidTelegramHandle("phone"));
        assertFalse(TelegramHandle.isValidTelegramHandle("9011p041"));
        assertFalse(TelegramHandle.isValidTelegramHandle("9312 1534"));

        // '.' is not a valid character
        assertFalse(TelegramHandle.isValidTelegramHandle("@john.doe"));
        // only 1 char, minimum 5
        assertFalse(TelegramHandle.isValidTelegramHandle("@J"));
        // only @ symbol
        assertFalse(TelegramHandle.isValidTelegramHandle("@"));
        // no alphanumeric characters, min 3
        assertFalse(TelegramHandle.isValidTelegramHandle("@_______"));


        // valid telegram handles
        assertTrue(TelegramHandle.isValidTelegramHandle("@bobthebuilder"));
        assertTrue(TelegramHandle.isValidTelegramHandle("@hello123goodbye456"));
        assertTrue(TelegramHandle.isValidTelegramHandle("@john_doe123"));
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void equals() {
        TelegramHandle handle = new TelegramHandle("@linus");

        // same values -> returns true
        assertTrue(handle.equals(new TelegramHandle("@linus")));

        // handles are case insensitive -> returns true
        assertTrue(handle.equals(new TelegramHandle("@LinUs")));

        // same object -> returns true
        assertTrue(handle.equals(handle));

        // null -> returns false
        assertFalse(handle.equals(null));

        // different types -> returns false
        assertFalse(handle.equals(5.0f));

        // different values -> returns false
        assertFalse(handle.equals(new TelegramHandle("@linux")));
    }
}
