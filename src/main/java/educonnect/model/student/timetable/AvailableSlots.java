package educonnect.model.student.timetable;

import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
    AvailableSlots(HashSet<DayOfWeek> daysOfWeeks) {
        requireNonNull(daysOfWeeks);
        this.days = (HashMap<DayOfWeek, Day>) daysOfWeeks.stream().collect(Collectors.toMap(x -> x, Day::new));
    }

    Day getDay(DayOfWeek dayOfWeek) {
        return this.days.get(dayOfWeek);
    }

    /**
     * Adds a Collection of {@code Period} to a specified day.
     *
     * @param dayOfWeek {@code DayOfWeek} enum.
     * @param periods {@code ArrayList} of available {@code Period} objects.
     */
    void addPeriodsToDay(DayOfWeek dayOfWeek, ArrayList<Period> periods) throws OverlapPeriodException {
        this.days.put(dayOfWeek, new Day(dayOfWeek, periods));
    }

    /**
     * Finds all common available slots, given each {@code AvailableSlots} from each {@code Student}.
     *
     * @param allAvailableSlots {@code ArrayList} of {@code AvailableSlots} objects
     *     generated from {@code Student} objects.
     * @return an {@code AvailableSlot} object containing all common slots from multiple students.
     * @throws OverlapPeriodException if there is an overlap in the period,
     *     but should not happen under normal circumstances.
     */
    static AvailableSlots findAllCommonSlots(ArrayList<AvailableSlots> allAvailableSlots)
            throws OverlapPeriodException {
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Available Slots:\n");
        for (Day eachDay : this.days.values()) {
            sb.append(eachDay).append("\n");
        }
        return sb.toString();
    }
}
