package educonnect.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import educonnect.testutil.Assert;

public class UserPrefsTest {

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        UserPrefs userPref = new UserPrefs();
        Assert.assertThrows(NullPointerException.class, () -> userPref.setGuiSettings(null));
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        UserPrefs userPrefs = new UserPrefs();
        Assert.assertThrows(NullPointerException.class, () -> userPrefs.setAddressBookFilePath(null));
    }
    @Test
    public void getShowTimetable_defaultShowTimetable() {
        UserPrefs userPrefs = new UserPrefs();
        assertFalse(userPrefs.getShowTimetable());
    }
    @Test
    public void setShowTimetable_showTimetable_success() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setShowTimetable(true);
        assertTrue(userPrefs.getShowTimetable());
    }
}
