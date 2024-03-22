package educonnect.model.student.timetable;

import static educonnect.model.student.timetable.Period.DEFAULT_PERIOD_NAME;
import static educonnect.testutil.Assert.assertThrows;
import static educonnect.testutil.TypicalTimetableAndValues.VALID_DAY1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import educonnect.model.student.timetable.exceptions.InvalidDurationException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import educonnect.model.student.timetable.exceptions.OverlapPeriodException;

public class DayTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Day(null));
    }

    @Test
    public void isSameDay() {
        Day day = new Day(DayOfWeek.SUNDAY);

        // same Day -> returns true
        assertTrue(day.isSameDay(new Day(DayOfWeek.SUNDAY)));

        // same object -> returns true
        assertTrue(day.isSameDay(day));

        // different Day -> returns false
        assertFalse(day.isSameDay(new Day(DayOfWeek.MONDAY)));
    }

    @Test
    public void addPeriod_invalidInputs_throwsOverlapPeriodException() throws OverlapPeriodException {
        Day day = new Day(DayOfWeek.SUNDAY);
        Period period1 = // 1 AM to 3 AM
                new Period("period1", LocalTime.of(1, 0, 0), LocalTime.of(3, 0, 0));
        Period period2 = // 3 AM to 5 AM
                new Period("period2", LocalTime.of(3, 0, 0), LocalTime.of(5, 0, 0));
        Period period3 = // 2 AM to 4 PM
                new Period("period3", LocalTime.of(2, 0, 0), LocalTime.of(4, 0, 0));

        day.addPeriod(period1);
        day.addPeriod(period2);

        // has overlap, not successfully added -> throws OverlapPeriodException
        assertThrows(OverlapPeriodException.class, () -> day.addPeriod(period3));
    }
    @Test
    public void addPeriod_validInputs_returnsTrue() throws OverlapPeriodException {
        Day day = new Day(DayOfWeek.SUNDAY);
        Period period1 = // 1 AM to 3 AM
                new Period("period1", LocalTime.of(1, 0, 0), LocalTime.of(3, 0, 0));
        Period period2 = // 3 AM to 5 AM
                new Period("period2", LocalTime.of(3, 0, 0), LocalTime.of(5, 0, 0));

        // no overlap, successfully added -> returns true
        assertTrue(day.addPeriod(period2));
        assertTrue(day.addPeriod(period1));

        // check if the periods added are sorted automatically.
        assertTrue(day.isSorted());
    }

    @Test
    public void hasAnyOverlaps() {
        Day day = VALID_DAY1; // mon: 13-15, 16-18
        Period periodNoOverlap =
                new Period("periodNoOverlap", LocalTime.of(11, 0, 0), LocalTime.of(13, 0, 0));
        Period periodOverlap =
                new Period("periodOverlap", LocalTime.of(12, 0, 0), LocalTime.of(14, 0, 0));

        // no overlaps -> returns false
        assertFalse(day.hasAnyOverlaps(periodNoOverlap));

        // has overlaps with at least one of the existing periods in the day -> returns true
        assertTrue(day.hasAnyOverlaps(periodOverlap));
    }

    @Test
    public void findSlots_invalidInputs() {
        Day day = VALID_DAY1; // mon: 13-15, 16-18

        // find slot of 0 hour, invalid input -> throws InvalidDurationException
        assertThrows(InvalidDurationException.class,() -> day.findSlot(0));

        // find slot of 25 hours, invalid input -> throws InvalidDurationException
        assertThrows(InvalidDurationException.class,() -> day.findSlot(25));
    }

    @Test
    public void findSlots_validInputs() {
        Day day = VALID_DAY1; // mon: 13-15, 16-18
        ArrayList<Period> expectedPeriods1 = new ArrayList<>(List.of(
                new Period(DEFAULT_PERIOD_NAME, "12-13"),
                new Period(DEFAULT_PERIOD_NAME, "15-16")));
        ArrayList<Period> expectedPeriods2 = new ArrayList<>(List.of(
                new Period(DEFAULT_PERIOD_NAME, "8-10"),
                new Period(DEFAULT_PERIOD_NAME, "9-11"),
                new Period(DEFAULT_PERIOD_NAME, "10-12"),
                new Period(DEFAULT_PERIOD_NAME, "11-13"),
                new Period(DEFAULT_PERIOD_NAME, "18-20"),
                new Period(DEFAULT_PERIOD_NAME, "19-21"),
                new Period(DEFAULT_PERIOD_NAME, "20-22")));

        // find slot of 1 hour, between 12 PM to 6 PM, specified using Period object
        assertEquals(expectedPeriods1, day.findSlot(1, Optional.of(new Period(DEFAULT_PERIOD_NAME, "12-18"))));
        // find slot of 1 hour, between 12 PM to 6 PM, specified using String
        assertEquals(expectedPeriods1, day.findSlot(1, "12-18"));
        // find slot of 1 hour, between 12 PM to 6 PM, specified using int
        assertEquals(expectedPeriods1, day.findSlot(1, 12, 18));

        // find slot of 2 hours, no specified timeframe (defaults to 8-22)
        assertEquals(expectedPeriods2, day.findSlot(2));

        // find slot of 4 hours, between 12 PM to 6 PM -> no slots, returns empty ArrayList
        assertEquals(new ArrayList<>(), day.findSlot(4, 12, 18));
    }

    @Test
    public void convertToCommandString() throws OverlapPeriodException {
        String expectedString1 = "mon: 1-3";
        String expectedString2 = expectedString1 + ", 3-5";
        String expectedString3 = expectedString2 + ", 7-11";

        Day day = new Day(DayOfWeek.MONDAY);
        Period period1 = // 1 AM to 3 AM
                new Period("period1", LocalTime.of(1, 0, 0), LocalTime.of(3, 0, 0));
        Period period2 = // 3 AM to 5 AM
                new Period("period2", LocalTime.of(3, 0, 0), LocalTime.of(5, 0, 0));
        Period period3 = // 7 AM to 11 AM
                new Period("period3", LocalTime.of(7, 0, 0), LocalTime.of(11, 0, 0));
        day.addPeriod(period1);

        // only 1 period
        assertEquals(expectedString1, day.convertToCommandString());

        // multiple periods
        day.addPeriod(period2);
        assertEquals(expectedString2, day.convertToCommandString());

        day.addPeriod(period3);
        assertEquals(expectedString3, day.convertToCommandString());
    }
}

