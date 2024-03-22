package educonnect.testutil;

import static educonnect.logic.parser.CliSyntax.PREFIX_TIMETABLE_MONDAY;
import static educonnect.logic.parser.CliSyntax.PREFIX_TIMETABLE_THURSDAY;
import static educonnect.logic.parser.CliSyntax.PREFIX_TIMETABLE_TUESDAY;
import static educonnect.logic.parser.CliSyntax.PREFIX_TIMETABLE_WEDNESDAY;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import educonnect.model.student.timetable.Day;
import educonnect.model.student.timetable.Period;
import educonnect.model.student.timetable.Timetable;
import educonnect.model.student.timetable.exceptions.OverlapPeriodException;

/**
 * A utility class containing a list of {@code String, Period, Timetable}
 * and other peripheral objects to be used in tests.
 */
public class TypicalTimetableAndValues {
    // additional strings
    public static final String PERIOD_SPACER = ", ";
    public static final String WHITESPACE = " \t\r\n";
    public static final String SPACE = " ";
    public static final String EMPTY = "";

    // invalid period strings used in parsing
    public static final String INVALID_PERIOD1 = "25-30";
    public static final String INVALID_PERIOD2 = "a14-16";

    // valid period values used in tests for assertions
    public static final int VALID_PERIOD1_VALUE1 = 13;
    public static final int VALID_PERIOD1_VALUE2 = 15;
    public static final int VALID_PERIOD2_VALUE1 = 0;
    public static final int VALID_PERIOD2_VALUE2 = 23;
    public static final int VALID_PERIOD3_VALUE1 = 16;
    public static final int VALID_PERIOD3_VALUE2 = 18;

    // valid period strings used in parsing
    public static final String VALID_PERIOD_NAME = "period";
    public static final String VALID_PERIOD1_STRING = VALID_PERIOD1_VALUE1 + "-" + VALID_PERIOD1_VALUE2; // "13-15"
    public static final String VALID_PERIOD2_STRING = VALID_PERIOD2_VALUE1 + "-" + VALID_PERIOD2_VALUE2; // "0-23"
    public static final String VALID_PERIOD3_STRING = VALID_PERIOD3_VALUE1 + "-" + VALID_PERIOD3_VALUE2; // "16-18"
    public static final String VALID_PERIODS_STRING1 = // "13-15, 16-18"
            VALID_PERIOD1_STRING + PERIOD_SPACER + VALID_PERIOD3_STRING;
    public static final String VALID_PERIODS_STRING2 = // "16-18, 13-15"
            VALID_PERIOD3_STRING + PERIOD_SPACER + VALID_PERIOD1_STRING;
    public static final String FULL_PERIODS_STRING_WITH_INVALID_INPUT = // "25-30, a14-16, 13-15"
            INVALID_PERIOD1 + PERIOD_SPACER + INVALID_PERIOD2 + PERIOD_SPACER + VALID_PERIOD1_STRING;
    public static final String FULL_PERIODS_STRING_VALID_INPUT = // "13-15, 16-18"
            VALID_PERIOD1_STRING + PERIOD_SPACER + VALID_PERIOD3_STRING;
    public static final String FULL_PERIODS_STRING_WITH_WHITESPACE_VALID_INPUT =
            WHITESPACE + VALID_PERIOD1_STRING + WHITESPACE + PERIOD_SPACER + WHITESPACE
            + VALID_PERIOD3_STRING + WHITESPACE;

    // add command arguments for Timetable
    /** " mon: 13-15, 16-18 thu: 16-18, 13-15" */
    public static final String VALID_ADD_COMMAND_TIMETABLE_ARGUMENTS_1 =
            SPACE + PREFIX_TIMETABLE_MONDAY + SPACE + VALID_PERIODS_STRING1
            + SPACE + PREFIX_TIMETABLE_THURSDAY + SPACE + VALID_PERIODS_STRING2;

    /** " tue: 16-18, 13-15 wed: 13-15, 16-18" */
    public static final String VALID_ADD_COMMAND_TIMETABLE_ARGUMENTS_2 =
            SPACE + PREFIX_TIMETABLE_TUESDAY + SPACE + VALID_PERIODS_STRING2
            + SPACE + PREFIX_TIMETABLE_WEDNESDAY + SPACE + VALID_PERIODS_STRING1;

    // valid period objects used in assertions.
    public static final Period VALID_PERIOD_1 = // Period of 13-15
            new Period(VALID_PERIOD_NAME,
            LocalTime.of(VALID_PERIOD1_VALUE1, 0, 0),
            LocalTime.of(VALID_PERIOD1_VALUE2, 0, 0));
    public static final Period VALID_PERIOD_2 = // Period of 0-23
            new Period(VALID_PERIOD_NAME,
            LocalTime.of(VALID_PERIOD2_VALUE1, 0, 0),
            LocalTime.of(VALID_PERIOD2_VALUE2, 0, 0));
    public static final Period VALID_PERIOD_3 = // Period of 16-18
            new Period(VALID_PERIOD_NAME,
            LocalTime.of(VALID_PERIOD3_VALUE1, 0, 0),
            LocalTime.of(VALID_PERIOD3_VALUE2, 0, 0));

    // ArrayLists of valid Periods
    public static final ArrayList<Period> VALID_PERIODS_LIST1 = // Periods {13-15, 16-18}
            buildPeriodList(new Period[] {VALID_PERIOD_1, VALID_PERIOD_3});
    public static final ArrayList<Period> VALID_PERIODS_LIST2 = // Periods {16-18, 13-15}
            buildPeriodList(new Period[] {VALID_PERIOD_3, VALID_PERIOD_1});

    // Optional ArrayList of Period objects, used in assertions.
    public static final Optional<ArrayList<Period>> VALID_PERIOD_OPTIONAL_ARRAYLIST = // Periods {13-15, 16-18}
            Optional.of(VALID_PERIODS_LIST1);
    public static final Optional<ArrayList<Period>> INVALID_PERIOD_OPTIONAL_ARRAYLIST = // Periods {13-15, 0-23}
            Optional.of(new ArrayList<>(List.of(VALID_PERIOD_1, VALID_PERIOD_2)));

    // valid List of Period Strings for all days
    public static final ArrayList<String> VALID_TIMETABLE_INPUT_1 = new ArrayList<>(
            getListOfPeriods(List.of(VALID_PERIODS_STRING1, EMPTY, EMPTY, VALID_PERIODS_STRING2, EMPTY)));
    public static final ArrayList<String> VALID_TIMETABLE_INPUT_2 = new ArrayList<>(
            getListOfPeriods(List.of(EMPTY, VALID_PERIODS_STRING2, VALID_PERIODS_STRING1, EMPTY, EMPTY)));

    // valid Days
    /** mon: 13-15, 16-18 */
    public static final Day VALID_DAY1 = buildDay(DayOfWeek.MONDAY, VALID_PERIODS_LIST1);

    /** thu: 16-18, 13-15 */
    public static final Day VALID_DAY2 = buildDay(DayOfWeek.THURSDAY, VALID_PERIODS_LIST2);

    // valid Timetable
    public static final Timetable VALID_TIMETABLE_1 = buildTimetable(new int[] {1, 4},
            new ArrayList<>(List.of(VALID_PERIODS_LIST1, VALID_PERIODS_LIST2)));
    public static final Timetable VALID_TIMETABLE_2 = buildTimetable(new int[] {2, 3},
            new ArrayList<>(List.of(VALID_PERIODS_LIST2, VALID_PERIODS_LIST1)));

    public static final Timetable DEFAULT_EMPTY_TIMETABLE = new Timetable();

    /**
     * Helper method to create List of Periods
     *
     * @param periods an array pf {@code Period} objects.
     * @return an {@code ArrayList} of {@code Period} objects.
     */
    public static ArrayList<Period> buildPeriodList(Period[] periods) {
        return new ArrayList<>(Arrays.asList(periods));
    }

    /**
     * Helper method to get List of Period Strings, according to number of days in Timetable
     *
     * @param original List of Strings for 5 days in the week.
     * @return List of Strings for 7 days of the week, if {@code Timetable.getTimetable7Days()} is {@code true}.
     */
    public static List<String> getListOfPeriods(List<String> original) {
        ArrayList<String> newList = new ArrayList<>(original);
        if (Timetable.is7Days()) {
            newList.addAll(List.of(EMPTY, EMPTY));
        }
        return newList;
    }

    /**
     * Helper method to build day.
     *
     * @param dayOfWeek enum for day of the week.
     * @param periods list of periods to be added to the day.
     * @return {@code Day} object.
     */
    public static Day buildDay(DayOfWeek dayOfWeek, ArrayList<Period> periods) {
        Day day = new Day(dayOfWeek);
        for (Period period : periods) {
            try {
                day.addPeriod(period);
            } catch (OverlapPeriodException e) {
                throw new RuntimeException("Error adding period during test, check hard-coded accepted inputs");
            }
        }
        return day;
    }

    /**
     * Helper method to build timetable.
     *
     * @param indexesOfDays indexes containing days which has {@code Period} to add.
     * @param periodsEachDay {@code ArrayList} of {@code ArrayList<Period>} objects corresponding to the indexes.
     * @return a {@code Timetable} object.
     */
    public static Timetable buildTimetable(int[] indexesOfDays, ArrayList<ArrayList<Period>> periodsEachDay) {
        Timetable timetable = new Timetable();

        for (int i = 0; i < indexesOfDays.length; i++) {
            try {
                timetable.addPeriodsToDay(indexesOfDays[i], periodsEachDay.get(i));
            } catch (OverlapPeriodException e) {
                throw new RuntimeException("Error adding period during test, check hard-coded accepted inputs");
            }
        }
        return timetable;
    }
}
