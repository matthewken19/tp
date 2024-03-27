package educonnect.model.student.timetable;

import static educonnect.testutil.TypicalTimetableAndValues.VALID_PERIOD_1;
import static educonnect.testutil.TypicalTimetableAndValues.VALID_PERIOD_3;
import static educonnect.testutil.TypicalTimetableAndValues.buildAvailableSlot;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import educonnect.model.student.timetable.exceptions.OverlapPeriodException;

public class AvailableSlotsTest {
    @Test
    public void deleteDay() throws OverlapPeriodException {
        AvailableSlots expectedAs = buildAvailableSlot("tue", "13-15", "16-18");
        AvailableSlots as = buildAvailableSlot("mon", "13-15", "16-18", "tue", "13-15", "16-18");

        as.deleteDay(DayOfWeek.MONDAY);

        // after delete, should be the same
        assertEquals(expectedAs, as);
    }

    @Test
    public void findAllCommonSlots() throws OverlapPeriodException {
        AvailableSlots expectedAvailableSlot = buildAvailableSlot("mon", "13-15", "16-18", "tue", "13-15", "16-18");
        expectedAvailableSlot.setCommonSlots();

        AvailableSlots availableSlot1 = new AvailableSlots(new HashSet<>(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)));
        AvailableSlots availableSlot2 = new AvailableSlots(new HashSet<>(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)));
        ArrayList<Period> periods1 = new ArrayList<>(List.of(VALID_PERIOD_1, VALID_PERIOD_3)); // 13-15 and 16-18

        availableSlot1.addPeriodsToDay(DayOfWeek.MONDAY, periods1);
        availableSlot1.addPeriodsToDay(DayOfWeek.TUESDAY, periods1);
        availableSlot2.addPeriodsToDay(DayOfWeek.MONDAY, periods1);
        availableSlot2.addPeriodsToDay(DayOfWeek.TUESDAY, periods1);

        ArrayList<AvailableSlots> allSlots = new ArrayList<>(List.of(availableSlot1, availableSlot2));

        assertEquals(expectedAvailableSlot, AvailableSlots.findAllCommonSlots(allSlots));
    }

    @Test
    public void test_toString() throws OverlapPeriodException {
        String expectedString1 = "Available Slots:\n";
        String expectedString2 = expectedString1
                                 + "For MONDAY, schedule is:\n"
                                 + "Period: (13:00 to 15:00)\n"
                                 + "Period: (16:00 to 18:00)\n"
                                 + "\n"
                                 + "For TUESDAY, schedule is:\n"
                                 + "Period: (13:00 to 15:00)\n"
                                 + "Period: (16:00 to 18:00)\n"
                                 + "\n";

        AvailableSlots as = new AvailableSlots();

        // empty AvailableSlot
        assertEquals(expectedString1, as.toString());

        // filled AvailableSlot
        as = buildAvailableSlot("mon", "13-15", "16-18", "tue", "13-15", "16-18");
        assertEquals(expectedString2, as.toString());
    }

    @Test
    public void equals() throws OverlapPeriodException {
        AvailableSlots as = buildAvailableSlot("mon", "13-15", "16-18", "tue", "13-15", "16-18");
        AvailableSlots asSame = buildAvailableSlot("mon", "13-15", "16-18", "tue", "13-15", "16-18");
        AvailableSlots asDiff = buildAvailableSlot("mon", "11-17", "12-18", "tue", "13-15", "16-18");
        AvailableSlots asCommonSlot = buildAvailableSlot(
                "mon", "13-15", "16-18", "tue", "13-15", "16-18", "isCommonSlot");

        // not same object
        assertNotEquals(as, null);
        assertNotEquals(as, new Object());

        // same object
        assertEquals(as, as);

        // same values inside
        assertEquals(as, asSame);

        // different values inside
        assertNotEquals(as, asDiff);

        // same values, but one is tagged with 'isCommonSlot'
        assertNotEquals(as, asCommonSlot);

        // check hash code
        assertEquals(as.hashCode(), asSame.hashCode());
        assertNotEquals(as.hashCode(), asDiff.hashCode());
    }
}
