package educonnect.model.student.timetable;

import static educonnect.commons.util.CollectionUtil.requireAllNonNull;
import static educonnect.logic.parser.CliSyntax.PREFIXES_TIMETABLE_DAYS;
import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import educonnect.model.student.timetable.exceptions.InvalidDurationException;
import educonnect.model.student.timetable.exceptions.OverlapPeriodException;

/**
 * Represents a day in a weekly timetable schedule.
 */
public class Day implements Comparable<Day> {
    static final int DEFAULT_START_TIME_OF_DAY = 8; // 8 AM

    static final int DEFAULT_END_TIME_OF_DAY = 22; // 10 PM
    final ArrayList<Period> periods;
    private final DayOfWeek dayOfWeek;
    private final boolean checksForOverlaps;


    /**
     * Constructor for JSON Serialisation, included only for JSON to work, not intended as a constructor to be used!
     */
    private Day() {
        this.dayOfWeek = null;
        this.periods = new ArrayList<>();
        this.checksForOverlaps = true;
    }

    /**
     * Constructor for {@code Day} objects, uses {@code int} as input.
     *
     * @param day int representing the day of the week,
     *            starts from 1, representing MONDAY,
     *            till 7, representing SUNDAY.
     */
    Day(int day) {
        this.dayOfWeek = DayOfWeek.of(day);
        this.periods = new ArrayList<>();
        this.checksForOverlaps = true;
    }

    /**
     * Constructor for {@code Day} objects, uses {@code DayOfWeek} as input.
     *
     * @param day {@code DayOfWeek} enum, e.g {@code DayOfWeek.MONDAY}.
     */
    public Day(DayOfWeek day) {
        requireNonNull(day);
        this.dayOfWeek = day;
        this.periods = new ArrayList<>();
        this.checksForOverlaps = true;
    }

    /**
     * Constructor for {@code Day} objects, uses {@code DayOfWeek} as input.
     * Does not check for Overlaps, used for {@code AvailableSlots}.
     *
     * @param day {@code DayOfWeek} enum, e.g {@code DayOfWeek.MONDAY}.
     * @param checksForOverlaps signals whether this {@code Day} checks for overlapping {@code Period},
     *     false only for generating common slots.
     */
    public Day(DayOfWeek day, boolean checksForOverlaps) {
        requireNonNull(day);
        this.dayOfWeek = day;
        this.periods = new ArrayList<>();
        this.checksForOverlaps = checksForOverlaps;
    }

    /**
     * Constructor for {@code Day} objects, uses {@code DayOfWeek} as input.
     *
     * @param day {@code DayOfWeek} enum, e.g {@code DayOfWeek.MONDAY}.
     * @param periods list of periods to be added to the day.
     * @throws OverlapPeriodException when there is an overlap in the period list
     */
    Day(DayOfWeek day, ArrayList<Period> periods) throws OverlapPeriodException {
        requireAllNonNull(day, periods);
        this.dayOfWeek = day;
        this.periods = new ArrayList<>();
        this.checksForOverlaps = true;

        for (Period period : periods) {
            this.addPeriod(period);
        }
    }

    /**
     * Constructor for {@code Day} objects, uses {@code DayOfWeek} as input.
     *
     * @param day {@code DayOfWeek} enum, e.g {@code DayOfWeek.MONDAY}.
     * @param periods list of periods to be added to the day.
     * @param checksForOverlaps signals whether this {@code Day} checks for overlapping {@code Period},
     *     false only for generating common slots.
     */
    Day(DayOfWeek day, ArrayList<Period> periods, boolean checksForOverlaps) {
        requireAllNonNull(day, periods);
        this.dayOfWeek = day;
        this.periods = periods;
        this.checksForOverlaps = checksForOverlaps;
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
     * Checks if the day is the same as the {@code DayOfWeek}.
     *
     * @param dayOfWeek to be checked against.
     * @return {@code True} if it is the same day.
     */
    public boolean isSameDay(DayOfWeek dayOfWeek) {
        return this.dayOfWeek.equals(dayOfWeek);
    }

    /**
     * Checks if the {@code Day} contains any {@code Period}.
     */
    public boolean hasPeriods() {
        return !this.periods.isEmpty();
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
     * Gets the {@code DayOfWeek} of this {@code Day}
     *
     * @return {@code DayOfWeek}
     */
    public DayOfWeek getDayOfWeek() {
        return this.dayOfWeek;
    }

    /**
     * Adds a {@code Period} into this {@code Day}. The period cannot overlap with another period.
     * Automatically sorts all periods after each addition.
     *
     * @param period a {@code Period} object.
     * @return {@code true} if added successfully.
     */
    public boolean addPeriod(Period period) throws OverlapPeriodException {
        if (checksForOverlaps && hasAnyOverlaps(period)) {
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
    ArrayList<Period> findSlots(int duration) {
        return findSlots(duration, DEFAULT_START_TIME_OF_DAY, DEFAULT_END_TIME_OF_DAY);
    }

    /**
     * Finds all time slots of specified duration, with a specified timeframe in {@code int}.
     *
     * @param duration specified time.
     * @param startTime specified start time for the time frame.
     * @param endTime specified end time for the time frame.
     * @return a list of all {@code Period} objects that has no overlaps within this {@code Day}.
     */
    ArrayList<Period> findSlots(int duration, int startTime, int endTime) {
        Period period = new Period("period", startTime, endTime);
        return findSlots(duration, period);
    }

    /**
     * Finds all time slots of specified duration, with a specified timeframe in {@code String}.
     *
     * @param duration specified time.
     * @param periodString specified timeframe in the {@code Period} format, e.g. "12-14".
     * @return a list of all {@code Period} objects that has no overlaps within this {@code Day}.
     */
    ArrayList<Period> findSlots(int duration, String periodString) {
        Period period = new Period("period", periodString);
        return findSlots(duration, period);
    }

    /**
     * Finds all time slots of specified duration, with a specified timeframe in {@code Period}.
     *
     * @param duration specified time.
     * @param timeframe specified time frame.
     * @return a list of all {@code Period} objects that has no overlaps within this {@code Day}.
     */
    ArrayList<Period> findSlots(int duration, Period timeframe) {
        if (duration > 24 || duration < 1) {
            throw new InvalidDurationException();
        }

        ArrayList<Period> allSlots = new ArrayList<>();
        int startTime = timeframe.getStartTimeHour();
        int endTime = timeframe.getEndTimeHour();

        for (int i = startTime; i <= endTime - duration; i++) {
            Period period = new Period("period", i, i + duration);
            if (!hasAnyOverlaps(period)) {
                allSlots.add(period);
            }
        }
        return allSlots;
    }

    /**
     * Finds all the CommonSlots
     *
     * @param allDays all the same {@code DayOfWeek}, from different {@code Timetable}.
     * @return {@code Day} containing all common Periods.
     */
    static Day findAllCommonSlots(ArrayList<Day> allDays) {
        Set<Period> result = new HashSet<>(allDays.get(0).periods);
        DayOfWeek dayCheck = allDays.get(0).getDayOfWeek();

        for (Day day : allDays) {
            result.retainAll(new HashSet<>(day.periods));
            if (!day.isSameDay(dayCheck)) {
                // should never happen if this method is called properly
                throw new RuntimeException("Not all same Days passed into finding common slots.");
            }
        }

        ArrayList<Period> periodArrayList = new ArrayList<>(result);
        Collections.sort(periodArrayList);
        return new Day(dayCheck, periodArrayList, false);
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
    public int compareTo(Day day) {
        return this.dayOfWeek.compareTo(day.dayOfWeek);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.dayOfWeek.hashCode();
        for (Period period : periods) {
            result = prime * result + period.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Day)) {
            return false;
        }

        Day day = (Day) obj;

        if ((!isSameDay(day)) || (this.periods.size() != day.periods.size())) {
            return false;
        }

        for (int i = 0; i < periods.size(); i++) {
            if (!this.periods.get(i).equals(day.periods.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("For ").append(dayOfWeek);

        if (hasPeriods()) {
            sb.append(", schedule is:\n");
        } else {
            sb.append(", no periods.\n");
        }

        for (Period per : periods) {
            sb.append(per);
        }
        return sb.toString();
    }
}
