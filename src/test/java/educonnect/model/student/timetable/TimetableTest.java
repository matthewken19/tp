package educonnect.model.student.timetable;

import static educonnect.testutil.Assert.assertThrows;
import static educonnect.testutil.TypicalTimetableAndValues.VALID_TIMETABLE_1;
import static educonnect.testutil.TypicalTimetableAndValues.buildAvailableSlot;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import educonnect.model.student.timetable.exceptions.InvalidDurationException;
import educonnect.model.student.timetable.exceptions.NumberOfDaysException;
import educonnect.model.student.timetable.exceptions.OverlapPeriodException;

public class TimetableTest {
    private static final String EMPTY_TIMETABLE_FIVE =
            "Timetable:\n"
            + "For MONDAY, no periods.\n\n"
            + "For TUESDAY, no periods.\n\n"
            + "For WEDNESDAY, no periods.\n\n"
            + "For THURSDAY, no periods.\n\n"
            + "For FRIDAY, no periods.\n\n";
    private static final String EMPTY_TIMETABLE_SEVEN =
            EMPTY_TIMETABLE_FIVE
            + "For SATURDAY, no periods.\n\n"
            + "For SUNDAY, no periods.\n\n";

    private static final Period PERIOD_1 = new Period("period1",
            LocalTime.of(13, 0, 0),
            LocalTime.of(14, 0, 0));
    private static final Period PERIOD_2 = new Period("period2",
            LocalTime.of(15, 0, 0),
            LocalTime.of(17, 0, 0));
    private static final Period PERIOD_3 = new Period("period3",
            LocalTime.of(14, 0, 0),
            LocalTime.of(16, 0, 0));

    @Test
    public void constructor() {
        Timetable timetable5 = new Timetable(5);
        Timetable timetable7 = new Timetable(7);
        Timetable timetableDefault = new Timetable();

        assertEquals(EMPTY_TIMETABLE_FIVE, timetable5.toString());
        assertEquals(EMPTY_TIMETABLE_SEVEN, timetable7.toString());
        assertEquals(EMPTY_TIMETABLE_FIVE, timetableDefault.toString());

        // Less than 1 day in the week -> throws NumberOfDaysException
        assertThrows(NumberOfDaysException.class, () -> new Timetable(0));
        // More than 7 days in the week -> throws NumberOfDaysException
        assertThrows(NumberOfDaysException.class, () -> new Timetable(8));
    }

    @Test
    public void addPeriodToDay_invalidInputs_throwsException() throws OverlapPeriodException {
        Timetable timetable5 = new Timetable(5);
        timetable5.addPeriodToDay(1, PERIOD_3);

        // adding to Monday, period from 2 PM to 4 PM, failure -> throws OverlapPeriodException
        assertThrows(OverlapPeriodException.class, () ->
                timetable5.addPeriodToDay(1, PERIOD_3));

        // adding to a day outside the normal 7 days, failure -> throws NumberOfDaysException
        assertThrows(NumberOfDaysException.class, () ->
                timetable5.addPeriodToDay(10, PERIOD_3));
    }

    @Test
    public void addPeriodToDay_validInputs_returnsTrue() throws OverlapPeriodException {
        Timetable timetable5 = new Timetable(5);

        // adding to Monday, period from 1 PM to 2 PM, success -> returns true
        assertTrue(timetable5.addPeriodToDay(1, PERIOD_1));

        // adding to Monday, period from 3 PM to 5 PM, success -> returns true
        assertTrue(timetable5.addPeriodToDay(1, PERIOD_2));

        // adding to Friday, period from 3 PM to 5 PM, success -> returns true
        assertTrue(timetable5.addPeriodToDay(5, PERIOD_2));

        // adding to Friday, period from 1 PM to 2 PM, success -> returns true
        assertTrue(timetable5.addPeriodToDay(5, PERIOD_1));
    }

    @Test
    public void addPeriodsToDay_invalidInputs_throwsException() {
        Timetable timetable5 = new Timetable(5);
        ArrayList<Period> periods = new ArrayList<>(List.of(PERIOD_1, PERIOD_2));

        // adding to a day outside the normal 7 days, failure -> throws NumberOfDaysException
        assertThrows(NumberOfDaysException.class, () -> timetable5.addPeriodsToDay(8, periods));

        periods.add(PERIOD_3);

        // adding to Monday, period from 2 PM to 4 PM, failure -> throws OverlapPeriodException
        assertThrows(OverlapPeriodException.class, () -> timetable5.addPeriodsToDay(1, periods));
    }

    @Test
    public void addPeriodsToDay_validInputs_returnsTrue() throws OverlapPeriodException {
        Timetable timetable5 = new Timetable(5);
        ArrayList<Period> periods = new ArrayList<>(List.of(PERIOD_1, PERIOD_2));

        // adding two Periods to empty Timetable, no Overlaps -> returns true
        assertTrue(timetable5.addPeriodsToDay(1, periods));
    }

    @Test
    public void findSlots_invalidInputs() {
        Timetable timetable = VALID_TIMETABLE_1;

        // find slot of 0 hour, invalid input -> throws InvalidDurationException
        assertThrows(InvalidDurationException.class, () ->
                timetable.findSlots(0, Timetable.DEFAULT_TIMEFRAME, Timetable.DEFAULT_ALL_DAYS));

        // find slot of 25 hours, invalid input -> throws InvalidDurationException
        assertThrows(InvalidDurationException.class, () ->
                timetable.findSlots(25, Timetable.DEFAULT_TIMEFRAME, Timetable.DEFAULT_ALL_DAYS));
    }

    @Test
    public void findSlots_validInputs() throws OverlapPeriodException {
        Timetable timetable = VALID_TIMETABLE_1;
        timetable.addPeriodToDay(3, new Period(Period.DEFAULT_PERIOD_NAME, "12-18"));
        timetable.addPeriodToDay(5, new Period(Period.DEFAULT_PERIOD_NAME, "11-12"));

        AvailableSlots expectedAvailableSlot1 = buildAvailableSlot("mon", "8-13",
                "tue", "8-13", "9-14", "10-15", "11-16", "12-17", "13-18", "14-19", "15-20", "16-21", "17-22",
                "thu", "8-13", "fri", "12-17", "13-18", "14-19", "15-20", "16-21", "17-22");

        AvailableSlots expectedAvailableSlot2 = buildAvailableSlot(
                "tue", "10-16", "11-17", "12-18", "13-19", "14-20",
                "fri", "12-18", "13-19", "14-20");

        AvailableSlots expectedAvailableSlot3 = buildAvailableSlot("tue", "8-13", "9-14", "10-15", "11-16",
                "12-17", "13-18", "14-19", "15-20", "16-21", "17-22");

        AvailableSlots expectedAvailableSlot4 = buildAvailableSlot(
                "tue", "9-13", "10-14", "11-15", "12-16", "13-17", "14-18",
                "thu", "9-13");


        // only duration specified, default 8 AM - 10 PM timeframe, default every day of the week.
        assertEquals(expectedAvailableSlot1,
                timetable.findSlots(5, Timetable.DEFAULT_TIMEFRAME, Timetable.DEFAULT_ALL_DAYS));

        // duration, timeframe of 10 AM to 8 PM specified, default every day of the week.
        assertEquals(expectedAvailableSlot2, timetable.findSlots(6,
                new Period(Period.DEFAULT_PERIOD_NAME, "10-20"), Timetable.DEFAULT_ALL_DAYS));

        // duration, and Tuesday specified, default 8 AM - 10 PM timeframe
        assertEquals(expectedAvailableSlot3, timetable.findSlots(5, Timetable.DEFAULT_TIMEFRAME,
                new HashSet<>(Collections.singleton(DayOfWeek.TUESDAY))));

        // duration, timeframe of 9 AM to 6 PM, only tues-thu specified.
        assertEquals(expectedAvailableSlot4, timetable.findSlots(4,
                new Period(Period.DEFAULT_PERIOD_NAME, "9-18"),
                new HashSet<>(List.of(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY))));

    }

    @Test
    public void convertToCommandString() throws OverlapPeriodException {
        String expectedString1 = "mon: 14-16";
        String expectedString2 = expectedString1 + "tue: 13-14, 15-17";
        String expectedString3 = expectedString2 + "fri: 13-14, 14-16";

        Timetable timetable5 = new Timetable(5);

        // one day only
        timetable5.addPeriodToDay(1, PERIOD_3);
        assertEquals(expectedString1, timetable5.convertToCommandString());

        // multiple days
        timetable5.addPeriodToDay(2, PERIOD_1);
        timetable5.addPeriodToDay(2, PERIOD_2);
        assertEquals(expectedString2, timetable5.convertToCommandString());

        timetable5.addPeriodToDay(5, PERIOD_1);
        timetable5.addPeriodToDay(5, PERIOD_3);
        assertEquals(expectedString3, timetable5.convertToCommandString());
    }
}
