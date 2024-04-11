package educonnect.logic.parser;

import static educonnect.commons.util.CollectionUtil.requireAllNonNull;
import static educonnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import educonnect.commons.core.index.Index;
import educonnect.commons.util.StringUtil;
import educonnect.logic.commands.SlotsCommand;
import educonnect.logic.parser.exceptions.ParseException;
import educonnect.model.student.Email;
import educonnect.model.student.Link;
import educonnect.model.student.Name;
import educonnect.model.student.StudentId;
import educonnect.model.student.Tag;
import educonnect.model.student.TelegramHandle;
import educonnect.model.student.timetable.Period;
import educonnect.model.student.timetable.Timetable;
import educonnect.model.student.timetable.exceptions.InvalidPeriodException;
import educonnect.model.student.timetable.exceptions.NumberOfDaysException;
import educonnect.model.student.timetable.exceptions.OverlapPeriodException;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";
    public static final String MESSAGE_INVALID_DURATION =
            "Invalid duration specified! "
            + "Duration should be between 1-23 hours, more typically 1-4 hours.";
    public static final String MESSAGE_INVALID_DAY =
            "Invalid day specified! "
            + "Each day is indicated by their 3-letter identifier, e.g. 'mon', or 'fri'.\n"
            + "(Hint: by default Saturdays and Sundays are not included.)";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        String capitalName = createCapitalName(trimmedName);
        return new Name(capitalName);
    }

    /**
     * Capitalises the names separated by a space
     * Removes unnecessary spaces in between names
     */
    public static String createCapitalName(String trimmedName) {
        String[] words = trimmedName.split("[\\s-]+");
        StringBuilder namesUnparsed = new StringBuilder(trimmedName);
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            int lengthOfWord = word.length();
            int indexOfWord = namesUnparsed.indexOf(word);

            //Appends the character behind the individual word
            if (indexOfWord != 0) {
                result.append(namesUnparsed.charAt(indexOfWord - 1));
            }

            result.append(Character.toUpperCase(word.charAt(0)));

            if (word.length() > 1) {
                result.append(word.substring(1));
            }
            namesUnparsed.delete(0, indexOfWord + lengthOfWord);
        }
        return result.toString();
    }

    /**
     * Parses a {@code String studentId} into a {@code StudentId}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code studentId} is invalid.
     */
    public static StudentId parseStudentId(String studentId) throws ParseException {
        requireNonNull(studentId);
        String trimmedId = studentId.trim();
        if (!StudentId.isValidStudentId(trimmedId)) {
            throw new ParseException(StudentId.MESSAGE_CONSTRAINTS);
        }
        String capitalised = createCapitalStudentId(trimmedId);
        return new StudentId(capitalised);
    }

    /**
     * Capitalises the 'A' and the letter at the start and end of the Student ID
     * @param trimmedId
     * @return Capitalised String of Student ID
     */
    public static String createCapitalStudentId(String trimmedId) {
        String capitalised = trimmedId.toUpperCase();
        return capitalised;
    }

    /**
     * Parses a {@code telegramHandle} into a {@code TelegramHandle}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code telegramHandle} is invalid.
     */
    public static TelegramHandle parseTelegramHandle(String telegramHandle) throws ParseException {
        requireNonNull(telegramHandle);
        String trimmedHandle = telegramHandle.trim();
        if (!TelegramHandle.isValidTelegramHandle(trimmedHandle)) {
            throw new ParseException(TelegramHandle.MESSAGE_CONSTRAINTS);
        }
        return new TelegramHandle(trimmedHandle);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses {@code ArrayList<String> allDays} into a {@code Timetable}.
     * Defaults to empty Timetable if no arguments (empty {@code allDays}).
     */
    public static Timetable parseTimetable(ArrayList<String> allDays) throws ParseException {
        requireNonNull(allDays);
        Timetable timetable = new Timetable();
        for (int i = 0; i < allDays.size(); i++) {
            parseDay(i + 1, timetable, parsePeriods(allDays.get(i)));
        }
        return timetable;
    }

    /**
     * Parses {@code Optional<ArrayList<String>> periods} into a {@code Day}. Helper method.
     */
    public static boolean parseDay(int dayNumber, Timetable timetable, Optional<ArrayList<Period>> allPeriods)
            throws NumberOfDaysException, OverlapPeriodException {
        requireAllNonNull(dayNumber, timetable, allPeriods);
        if (allPeriods.isPresent()) {
            for (Period period : allPeriods.get()) {
                timetable.addPeriodToDay(dayNumber, period);
            }
        }
        return true;
    }

    /**
     * Parses {@code String allPeriods} into a {@code Optional<ArrayList<String>> periods}. Helper method.
     */
    public static Optional<ArrayList<Period>> parsePeriods(String allPeriodsString) throws ParseException {
        requireNonNull(allPeriodsString);
        if (allPeriodsString.isBlank()) { // no period in this day
            return Optional.empty();
        }
        ArrayList<Period> periodsForDay = new ArrayList<>();
        String[] allPeriods = allPeriodsString.split(",");

        for (String eachPeriod : allPeriods) {
            periodsForDay.add(parsePeriod(eachPeriod));
        }
        return Optional.of(periodsForDay);
    }

    /**
     * Parses {@code String period} into a {@code Period}. Helper method.
     */
    public static Period parsePeriod(String eachPeriod) throws ParseException {
        String trimmedPeriod = eachPeriod.trim();
        if (!Period.isValidPeriod(trimmedPeriod)) {
            throw new ParseException(Period.PERIOD_CONSTRAINTS);
        }
        try {
            return new Period(Period.DEFAULT_PERIOD_NAME, trimmedPeriod);
        } catch (InvalidPeriodException e) {
            throw new ParseException(e.getMessage());
        }
    }

    /**
     * Parses {@code String duration} into a {@code int}. Helper method.
     */
    public static int parseDuration(String duration) throws ParseException {
        String trimmedDuration = duration.trim();
        try {
            int i = Integer.parseInt(trimmedDuration);
            if (i < 1 || i > 23) {
                throw new ParseException(MESSAGE_INVALID_DURATION);
            }
            return i;
        } catch (NumberFormatException e) {
            throw new ParseException(MESSAGE_INVALID_DURATION);
        }
    }

    /**
     * Parses {@code String days} into a {@code HashSet<DayOfWeek>}. Helper method.
     */
    public static HashSet<DayOfWeek> parseDaysSpecified(String days) throws ParseException {
        if (days.trim().isBlank()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SlotsCommand.MESSAGE_USAGE));
        }

        HashSet<DayOfWeek> daysContained = new HashSet<>();
        String[] args = days.split(",");

        for (String each : args) {
            switch (each.trim()) {
            case "mon":
                daysContained.add(DayOfWeek.MONDAY);
                break;
            case "tue":
                daysContained.add(DayOfWeek.TUESDAY);
                break;
            case "wed":
                daysContained.add(DayOfWeek.WEDNESDAY);
                break;
            case "thu":
                daysContained.add(DayOfWeek.THURSDAY);
                break;
            case "fri":
                daysContained.add(DayOfWeek.FRIDAY);
                break;
            case "sat":
                if (Timetable.is7Days()) {
                    daysContained.add(DayOfWeek.SATURDAY);
                } else {
                    throw new ParseException(MESSAGE_INVALID_DAY);
                }
                break;
            case "sun":
                if (Timetable.is7Days()) {
                    daysContained.add(DayOfWeek.SUNDAY);
                } else {
                    throw new ParseException(MESSAGE_INVALID_DAY);
                }
                break;
            default:
                throw new ParseException(MESSAGE_INVALID_DAY);
            }
        }
        return daysContained;
    }

    /**
     * Parses a {@code String link} into a {@code Link}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code link} is invalid.
     */
    public static Link parseLink(String s) throws ParseException {
        requireNonNull(s);
        String trimmedLink = s.trim();
        if (!Link.isValidLink(trimmedLink)) {
            System.out.println("Invalid link");
            throw new ParseException(Link.MESSAGE_CONSTRAINTS);
        }
        System.out.println("Valid link");
        return new Link(trimmedLink);
    }
}
