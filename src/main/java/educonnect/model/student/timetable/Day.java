package educonnect.model.student.timetable;

import static educonnect.logic.parser.CliSyntax.PREFIXES_TIMETABLE_DAYS;
import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import educonnect.model.student.timetable.exceptions.InvalidDurationException;
import educonnect.model.student.timetable.exceptions.OverlapPeriodException;

/**
 * Represents a day in a weekly timetable schedule.
 */
public class Day {
    private final DayOfWeek dayOfWeek;
    private final ArrayList<Period> periods;

    private static int DEFAULT_START_TIME_OF_DAY = 8; // 8 AM

    private static int DEFAULT_END_TIME_OF_DAY = 22; // 10 PM

    /**
     * Constructor for JSON Serialisation, included only for JSON to work, not intended as a constructor to be used!
     */
    private Day() {
        this.dayOfWeek = null;
        this.periods = new ArrayList<>();
    }

    /**
     * Constructor for {@code Day} objects, uses {@code int} as input.
     * @param day int representing the day of the week,
     *            starts from 1, representing MONDAY,
     *            till 7, representing SUNDAY.
     */
    public Day(int day) {
        this.dayOfWeek = DayOfWeek.of(day);
        this.periods = new ArrayList<>();
    }

    /**
     * Constructor for {@code Day} objects, uses {@code DayOfWeek} as input.
     * @param day {@code DayOfWeek} enum, e.g {@code DayOfWeek.MONDAY}.
     */
    public Day(DayOfWeek day) {
        requireNonNull(day);
        this.dayOfWeek = day;
        this.periods = new ArrayList<>();
    }

    /**
     * Checks if the days are the same.
     *
     * @param day Other day to be checked against.
     * @return {@code True} if it is the same day.
     */
    public boolean isSameDay(Day day) {
        return this.dayOfWeek.equals(day.dayOfWeek);
    }

    /**
     * Checks if the {@code Day} contains any {@code Period}.
     */
    public boolean hasPeriods() {
        return !this.periods.isEmpty();
    }

    /**
     * Adds a {@code Period} into this {@code Day}. The period cannot overlap with another period.
     * Automatically sorts all periods after each addition.
     *
     * @param period a {@code Period} object.
     * @return {@code true} if added successfully.
     */
    public boolean addPeriod(Period period) throws OverlapPeriodException {
        if (hasAnyOverlaps(period)) {
            throw new OverlapPeriodException();
        }
        periods.add(period);
        Collections.sort(periods);
        return true;
    }

    /**
     * Checks if a {@code Period} has any overlaps with existing periods in this {@code Day}
     *
     * @param periodToBeChecked period that is to be checked.
     * @return {@code true} if there are any overlaps.
     */
    public boolean hasAnyOverlaps(Period periodToBeChecked) {
        for (Period period : periods) {
            if (period.hasOverlap(periodToBeChecked)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds a time slot of specified duration, no specified timeframe.
     *
     * @param duration specified time.
     * @return a list of all {@code Period} objects that has no overlaps within this {@code Day}.
     */
    public ArrayList<Period> findSlot(int duration) {
        return findSlot(duration, Optional.empty());
    }

    /**
     * Finds a time slot of specified duration, with a specified timeframe in {@code int}.
     *
     * @param duration specified time.
     * @param startTime specified start time for the time frame.
     * @param endTime specified end time for the time frame.
     * @return a list of all {@code Period} objects that has no overlaps within this {@code Day}.
     */
    public ArrayList<Period> findSlot(int duration, int startTime, int endTime) {
        Period period = new Period("period", startTime, endTime);
        return findSlot(duration, Optional.of(period));
    }

    /**
     * Finds a time slot of specified duration, with a specified timeframe in {@code String}.
     *
     * @param duration specified time.
     * @param periodString specified timeframe in the {@code Period} format, e.g. "12-14".
     * @return a list of all {@code Period} objects that has no overlaps within this {@code Day}.
     */
    public ArrayList<Period> findSlot(int duration, String periodString) {
        Period period = new Period("period", periodString);
        return findSlot(duration, Optional.of(period));
    }

    /**
     * Finds a time slot of specified duration, optionally with a specified timeframe.
     *
     * @param duration specified time.
     * @param timeframe specified time frame.
     * @return a list of all {@code Period} objects that has no overlaps within this {@code Day}.
     */
    ArrayList<Period> findSlot(int duration, Optional<Period> timeframe) {
        if (duration > 24 || duration < 1) {
            throw new InvalidDurationException();
        }

        ArrayList<Period> allSlots = new ArrayList<>();
        int startTime = DEFAULT_START_TIME_OF_DAY;
        int endTime = DEFAULT_END_TIME_OF_DAY;

        if (timeframe.isPresent()) {
            startTime = timeframe.get().getStartTimeHour();
            endTime = timeframe.get().getEndTimeHour();
        }

        for (int i = startTime; i <= endTime - duration; i++) {
            Period period = new Period("period", i, i + duration);
            if (!hasAnyOverlaps(period)) {
                allSlots.add(period);
            }
        }
        return allSlots;
    }

    /**
     * Check if list of {@code Period} is sorted.
     *
     * @return {@code true} if the list of periods is sorted.
     */
    public boolean isSorted() {
        return this.periods.stream().sorted().collect(Collectors.toList()).equals(this.periods);
    }

    /**
     * Converts {@code Day} object back into its command {@code String}.
     *
     * @return {@code String} command, e.g. "mon: 13-15, 16-18"
     */
    String convertToCommandString() {
        if (periods.isEmpty()) {
            return ""; // returns empty string as no command specified will result in a day with no periods
        }

        StringBuilder sb = new StringBuilder();
        int index = dayOfWeek.getValue() - 1;
        sb.append(PREFIXES_TIMETABLE_DAYS[index]).append(" "); // appends the correct prefix

        for (Period period : periods) {
            if (sb.length() != 5) { // 5 because no matter which prefix, with space, the length is always 5
                sb.append(", ");
            }
            sb.append(period.convertToCommandString());
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("For ").append(dayOfWeek).append(", schedule is:\n");
        for (Period per : periods) {
            sb.append(per);
        }
        return sb.toString();
    }
}
