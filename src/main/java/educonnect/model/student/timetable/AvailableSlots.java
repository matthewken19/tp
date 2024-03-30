package educonnect.model.student.timetable;

import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import educonnect.model.student.timetable.exceptions.OverlapPeriodException;

/**
 * Utility class for usage of finding common slots across Timetables ONLY.
 * Guarantees: all inputs are valid when creating object,
 * HashMap ensures only 1 of each Day object is added.
 */
public class AvailableSlots {
    private final HashMap<DayOfWeek, Day> days;
    private boolean isCommonSlots;

    /**
     * Default constructor, empty HashMap and not a common slots AvailableSlots object.
     */
    public AvailableSlots() {
        this.days = new HashMap<DayOfWeek, Day>();
        this.isCommonSlots = false;
    }

    /**
     * Overloaded constructor
     *
     * @param daysOfWeeks {@code HashSet} of {@code DayOfWeek} enums,
     *     to indicate which days to look for the common slot.
     */
    public AvailableSlots(HashSet<DayOfWeek> daysOfWeeks) {
        requireNonNull(daysOfWeeks);
        this.days = (HashMap<DayOfWeek, Day>) daysOfWeeks.stream().collect(Collectors.toMap(x -> x, Day::new));
        this.isCommonSlots = false;
    }

    /**
     * Gets the {@code Day} object tied to the specified {@code DayOfWeek} enum.
     *
     * @param dayOfWeek {@code DayOfWeek} enum.
     * @return {@code Day} object.
     */
    Day getDay(DayOfWeek dayOfWeek) {
        return this.days.get(dayOfWeek);
    }

    public void setCommonSlots() {
        this.isCommonSlots = true;
    }

    /**
     * Deletes a {@code Day} in the {@code HashMap}.
     *
     * @param dayOfWeek {@code DayOfWeek} enum.
     */
    public void deleteDay(DayOfWeek dayOfWeek) {
        this.days.remove(dayOfWeek);
    }

    /**
     * Adds a Collection of {@code Period} to a specified day.
     *
     * @param dayOfWeek {@code DayOfWeek} enum.
     * @param periods {@code ArrayList} of available {@code Period} objects.
     */
    public void addPeriodsToDay(DayOfWeek dayOfWeek, ArrayList<Period> periods) {
        this.days.put(dayOfWeek, new Day(dayOfWeek, periods, false));
    }

    /**
     * Adds a {@code Period} to a specified day.
     *
     * @param dayOfWeek {@code DayOfWeek} enum.
     * @param period {@code Period} objects to be added
     * @throws OverlapPeriodException if there are any overlaps, which is not checked for by default.
     */
    public void addPeriodToDay(DayOfWeek dayOfWeek, Period period) throws OverlapPeriodException {
        if (days.containsKey(dayOfWeek)) {
            days.get(dayOfWeek).addPeriod(period);
        } else {
            days.put(dayOfWeek, new Day(dayOfWeek, new ArrayList<>(List.of(period)), false));
        }
    }

    public boolean hasCommonSlots() {
        if (this.days.isEmpty()) {
            return false;
        }

        for (Day day: this.days.values()) {
            if (day.hasPeriods()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds all common available slots, given each {@code AvailableSlots} from each {@code Student}.
     *
     * @param allAvailableSlots {@code ArrayList} of {@code AvailableSlots} objects
     *     generated from {@code Student} objects.
     * @return an {@code AvailableSlot} object containing all common slots from multiple students.
     */
    public static AvailableSlots findAllCommonSlots(ArrayList<AvailableSlots> allAvailableSlots) {
        AvailableSlots result = new AvailableSlots(new HashSet<>());
        ArrayList<DayOfWeek> allDaysPossible = findAllDays(allAvailableSlots);

        for (DayOfWeek dayOfWeek : allDaysPossible) {
            ArrayList<Day> allDays = new ArrayList<>();

            for (AvailableSlots eachSlot : allAvailableSlots) {
                allDays.add(eachSlot.getDay(dayOfWeek));
            }
            Day day = Day.findAllCommonSlots(allDays);
            result.days.put(day.getDayOfWeek(), day);
        }
        result.setCommonSlots();
        return result;
    }

    /**
     * Finds all available {@code DayOfWeek} across all {@code AvailableSlots} objects.
     *
     * @param allAvailableSlots {@code ArrayList} of {@code AvailableSlots} objects
     *     generated from {@code Student} objects.
     * @return a list of DayOfWeek objects that appears in any of the {@code AvailableSlots}.
     */
    static ArrayList<DayOfWeek> findAllDays(ArrayList<AvailableSlots> allAvailableSlots) {
        Set<DayOfWeek> allDays = new HashSet<>(allAvailableSlots.get(0).days.keySet());

        for (AvailableSlots eachSlot : allAvailableSlots) {
            allDays.retainAll(eachSlot.days.keySet());
        }

        ArrayList<DayOfWeek> allDayArrayList = new ArrayList<>(allDays);
        Collections.sort(allDayArrayList);
        return allDayArrayList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.days.hashCode();
        result = prime * result + (this.isCommonSlots ? prime : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof AvailableSlots)) {
            return false;
        }

        AvailableSlots as = (AvailableSlots) obj;

        if (isCommonSlots != as.isCommonSlots) {
            return false;
        }

        return this.days.equals(as.days);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        ArrayList<Day> days = new ArrayList<>(this.days.values());
        Collections.sort(days);

        sb.append("Available Slots:\n");
        for (Day eachDay : days) {
            sb.append(eachDay).append("\n");
        }
        return sb.toString();
    }
}
