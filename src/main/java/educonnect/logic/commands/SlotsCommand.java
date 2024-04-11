package educonnect.logic.commands;

import static educonnect.logic.parser.CliSyntax.PREFIX_DURATION;
import static educonnect.logic.parser.CliSyntax.PREFIX_ON_DAYS;
import static educonnect.logic.parser.CliSyntax.PREFIX_PERIOD;
import static educonnect.logic.parser.CliSyntax.PREFIX_TAG;
import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Predicate;

import educonnect.commons.util.ToStringBuilder;
import educonnect.logic.commands.exceptions.CommandException;
import educonnect.model.Model;
import educonnect.model.student.Student;
import educonnect.model.student.timetable.AvailableSlots;
import educonnect.model.student.timetable.Period;
import educonnect.model.student.timetable.Timetable;

/**
 * Finds a common slot amongst the list of students (a subgroup can be specified, identified using tags).
 */
public class SlotsCommand extends Command {
    public static final String COMMAND_WORD = "slots";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds a common slot of time amongst a list of students.\n"
            + "The list of students can be narrowed down by tag(s).\n"
            + "The period of time to look for the common slot can be specified, "
            + "otherwise, the default period is between 8 AM to 10 PM.\n"
            + "The search on which days can be specified, "
            + "otherwise, the default will be from Monday to Friday.\n"
            + "Parameters: "
            + PREFIX_DURATION + "DURATION (1-23) "
            + "[" + PREFIX_TAG + "TAG]"
            + "[" + PREFIX_PERIOD + "PERIOD]"
            + "[" + PREFIX_ON_DAYS + "DAYS]\n\n"
            + "Example 1: " + COMMAND_WORD + " " + PREFIX_DURATION + "1 \n\n"
            + "Example 2: " + COMMAND_WORD + " " + PREFIX_DURATION + "2 "
            + PREFIX_PERIOD + "10-6 " + PREFIX_ON_DAYS + "mon, tue, fri\n\n"
            + "Example 3: " + COMMAND_WORD + " " + PREFIX_DURATION + "1 "
            + PREFIX_TAG + "tutorial-1 " + PREFIX_PERIOD + "10-16 \n\n";

    public static final String MESSAGE_FOUND_SLOTS_SUCCESS = "Found a few slots, they are displayed below.\n\n%1$s";
    public static final String MESSAGE_NO_SLOTS_FOUND = "No slots found.";
    private final SlotsFinderDescriptor slotsFinderDescriptor;

    public SlotsCommand(SlotsFinderDescriptor slotsFinderDescriptor) {
        this.slotsFinderDescriptor = slotsFinderDescriptor;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        int duration = slotsFinderDescriptor.getDuration();
        Period timeframe;
        HashSet<DayOfWeek> days;

        // if tags are present to filter the list of students being searched.
        if (slotsFinderDescriptor.getPredicates().isPresent()) {
            model.updateFilteredStudentList(slotsFinderDescriptor.getPredicates().get());
        }

        // if timeframe is specified
        if (slotsFinderDescriptor.getTimeframe().isPresent()) {
            timeframe = slotsFinderDescriptor.getTimeframe().get();
        } else {
            timeframe = Timetable.DEFAULT_TIMEFRAME;
        }

        // if the days to be searched is specified
        if (slotsFinderDescriptor.getDays().isPresent()) {
            days = slotsFinderDescriptor.getDays().get();
        } else {
            days = Timetable.DEFAULT_ALL_DAYS;
        }

        AvailableSlots availableSlots = model.findAllCommonSlots(duration, timeframe, days);

        if (availableSlots.hasCommonSlots()) {
            return new CommandResult(String.format(MESSAGE_FOUND_SLOTS_SUCCESS, availableSlots));
        } else {
            return new CommandResult(MESSAGE_NO_SLOTS_FOUND);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof SlotsCommand)) {
            return false;
        }

        SlotsCommand slotsCommand = (SlotsCommand) obj;
        return this.slotsFinderDescriptor.equals(slotsCommand.slotsFinderDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("slotsFinderDescriptor", slotsFinderDescriptor)
                .toString();
    }

    /**
     * Stores the details of the slots that is being searched for.
     */
    public static class SlotsFinderDescriptor {
        private int duration;
        private Period timeframe;
        private HashSet<DayOfWeek> days;
        private Collection<Predicate<Student>> predicates;

        public SlotsFinderDescriptor() {}

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getDuration() {
            return this.duration;
        }

        public void setTimeframe(Period timeframe) {
            this.timeframe = timeframe;
        }

        public Optional<Period> getTimeframe() {
            return Optional.ofNullable(timeframe);
        }

        public void setDays(HashSet<DayOfWeek> days) {
            this.days = days;
        }

        public Optional<HashSet<DayOfWeek>> getDays() {
            return Optional.ofNullable(days);
        }

        public void setPredicates(Collection<Predicate<Student>> predicates) {
            this.predicates = predicates;
        }

        public Optional<Collection<Predicate<Student>>> getPredicates() {
            return Optional.ofNullable(predicates);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof SlotsFinderDescriptor)) {
                return false;
            }

            SlotsFinderDescriptor slotsFinderDescriptor = (SlotsFinderDescriptor) obj;
            return this.duration == slotsFinderDescriptor.duration
                    && this.getTimeframe().equals(slotsFinderDescriptor.getTimeframe())
                    && this.getDays().equals(slotsFinderDescriptor.getDays());
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("duration", duration)
                    .add("timeframe", timeframe)
                    .add("days", days)
                    .toString();
        }
    }
}
