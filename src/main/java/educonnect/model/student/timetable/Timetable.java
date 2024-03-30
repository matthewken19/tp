package educonnect.model.student.timetable;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import educonnect.model.student.timetable.exceptions.InvalidDurationException;
import educonnect.model.student.timetable.exceptions.NumberOfDaysException;
import educonnect.model.student.timetable.exceptions.OverlapPeriodException;

/**
 * Represents the timetable of a student for a week.
 */
public class Timetable {
    public static final int NUMBER_OF_DAYS_TYPICAL = 5;
    public static final Period DEFAULT_TIMEFRAME = new Period(Period.DEFAULT_PERIOD_NAME,
            Day.DEFAULT_START_TIME_OF_DAY, Day.DEFAULT_END_TIME_OF_DAY);
    public static final HashSet<DayOfWeek> DEFAULT_ALL_DAYS = is7Days()
            ? new HashSet<>(List.of(DayOfWeek.values()))
            : new HashSet<>(List.of(Arrays.copyOf(DayOfWeek.values(), NUMBER_OF_DAYS_TYPICAL)));
    private static final boolean IS_TIMETABLE_7_DAYS = false; // default is 5 days
    private static final int NUMBER_OF_DAYS_MAX = 7;
    private final ArrayList<Day> days;
    private final int numOfDays;

    /**
     * Default constructor for {@code Timetable} class.
     * Checks against TIMETABLE_7_DAYS for 5 or 7 days in the week.
     */
    public Timetable() {
        this.numOfDays = is7Days() ? NUMBER_OF_DAYS_MAX : NUMBER_OF_DAYS_TYPICAL;
        this.days = createTimetable(this.numOfDays);
    }

    /**
     * Overloaded constructor where the number of days in a week can be specified.
     * @param numOfDays the number of days in a week to keep track of.
     */
    Timetable(int numOfDays) {
        this.numOfDays = numOfDays;
        this.days = createTimetable(this.numOfDays);
    }

    /**
     * Helper method to initialise empty timetable with the specified number of days.
     * Typical values in a week are 5 or 7. The week always start with Monday.
     *
     * @param numOfDays number of days in the week.
     * @return an {@code ArrayList<Day>} containing each day in the week.
     */
    private ArrayList<Day> createTimetable(int numOfDays) throws NumberOfDaysException {
        if (numOfDays < 1 || numOfDays > NUMBER_OF_DAYS_MAX) {
            throw new NumberOfDaysException();
        }

        ArrayList<Day> days = new ArrayList<>(numOfDays);
        for (int i = 1; i <= numOfDays; i++) {
            Day day = new Day(i);
            days.add(i - 1, day);
        }

        return days;
    }

    /**
     * Gets a {@code boolean} of whether the timetable is 5 or 7 days.
     * @return {@code true} if 7 days, {@code false} if 5 days.
     */
    public static boolean is7Days() {
        return IS_TIMETABLE_7_DAYS;
    }

    /**
     * Adds a {@code Period} to a specified day.
     *
     * @param dayNumber the {@code int} representing the day of the week, 1 represents Monday.
     * @param period a period of time to be added into the timetable.
     * @return true if successfully added.
     */
    public boolean addPeriodToDay(int dayNumber, Period period) throws NumberOfDaysException, OverlapPeriodException {
        if (dayNumber < 1 || dayNumber > this.numOfDays) {
            throw new NumberOfDaysException();
        }

        Day day = this.days.get(dayNumber - 1);
        return day.addPeriod(period);
    }

    /**
     * Adds a Collection of {@code Period} to a specified day.
     *
     * @param dayNumber the {@code int} representing the day of the week, 1 represents Monday.
     * @param periods a {@code Collection} of {@code Period} of time to be added into the timetable.
     * @return {@code false} if the day specified is not within the week, otherwise {@code true}.
     * @throws OverlapPeriodException if there is an overlap in the periods given.
     */
    public boolean addPeriodsToDay(int dayNumber, ArrayList<Period> periods) throws OverlapPeriodException {
        if (dayNumber < 1 || dayNumber > this.numOfDays) {
            throw new NumberOfDaysException();
        }

        for (Period period : periods) {
            addPeriodToDay(dayNumber, period);
        }
        return true;
    }

    /**
     * Finds all time slots of specified duration, with a specified timeframe in {@code Period},
     * and specified {@code Day}(s).
     *
     * @param duration specified time.
     * @param timeframe specified time frame.
     * @param daysOfWeek specified day(s) to be included.
     * @return an {@code List} of {@code Lists} of {@code Periods}.
     */
    public AvailableSlots findSlots(int duration, Period timeframe, HashSet<DayOfWeek> daysOfWeek) {
        if (duration > 24 || duration < 1) {
            throw new InvalidDurationException();
        }

        AvailableSlots allSlots = new AvailableSlots(daysOfWeek);

        for (Day eachDay : days) {
            if (daysOfWeek.contains(eachDay.getDayOfWeek())) {
                ArrayList<Period> list = eachDay.findSlots(duration, timeframe);
                if (!list.isEmpty()) {
                    allSlots.addPeriodsToDay(eachDay.getDayOfWeek(), list);
                } else {
                    allSlots.deleteDay(eachDay.getDayOfWeek());
                }
            }
        }
        return allSlots;
    }

    /**
     * Converts {@code Timetable} object back into its command {@code String}.
     *
     * @return {@code String} command, e.g. "mon: 13-15, 16-18 tue: 12-18 thu: 11-13, 19-21"
     */
    public String convertToCommandString() {
        StringBuilder sb = new StringBuilder();
        for (Day day : days) {
            sb.append(day.convertToCommandString());
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Timetable:\n");
        for (Day eachDay : this.days) {
            sb.append(eachDay).append("\n");
        }
        return sb.toString();
    }
}
