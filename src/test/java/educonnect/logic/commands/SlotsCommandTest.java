package educonnect.logic.commands;

import static educonnect.logic.commands.CommandTestUtil.assertCommandSuccess;
import static educonnect.testutil.TypicalStudents.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import educonnect.model.AddressBook;
import educonnect.model.Model;
import educonnect.model.ModelManager;
import educonnect.model.UserPrefs;
import educonnect.model.student.Tag;
import educonnect.model.student.predicates.TagContainsKeywordsPredicate;
import educonnect.model.student.timetable.AvailableSlots;
import educonnect.model.student.timetable.Period;
import educonnect.model.student.timetable.Timetable;

public class SlotsCommandTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecified_success() {
        SlotsCommand.SlotsFinderDescriptor slotsFinderDescriptor = getSlotsFinderDescriptor_allFields();
        SlotsCommand slotsCommand = new SlotsCommand(slotsFinderDescriptor);

        AvailableSlots expectedAvailableSlots = getExpectedAvailableSlots_allFields();

        String expectedMessage = String.format(SlotsCommand.MESSAGE_FOUND_SLOTS_SUCCESS, expectedAvailableSlots);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredStudentList(Collections.singleton(
                new TagContainsKeywordsPredicate(new Tag("tutorial-1"))));

        assertCommandSuccess(slotsCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecified_success() {
        SlotsCommand.SlotsFinderDescriptor slotsFinderDescriptor = new SlotsCommand.SlotsFinderDescriptor();
        slotsFinderDescriptor.setDuration(5);
        slotsFinderDescriptor.setDays(new HashSet<>(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)));
        SlotsCommand slotsCommand = new SlotsCommand(slotsFinderDescriptor);

        AvailableSlots expectedAvailableSlots =
                new AvailableSlots(new HashSet<>(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)));
        expectedAvailableSlots.addPeriodsToDay(DayOfWeek.MONDAY,
                new ArrayList<>(List.of(new Period(Period.DEFAULT_PERIOD_NAME, "8-13"))));
        expectedAvailableSlots.addPeriodsToDay(DayOfWeek.TUESDAY,
                new ArrayList<>(List.of(new Period(Period.DEFAULT_PERIOD_NAME, "8-13"))));

        String expectedMessage = String.format(SlotsCommand.MESSAGE_FOUND_SLOTS_SUCCESS, expectedAvailableSlots);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(slotsCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noSlotsFound_success() {
        SlotsCommand.SlotsFinderDescriptor slotsFinderDescriptor = getSlotsFinderDescriptor_noSlots();
        SlotsCommand slotsCommand = new SlotsCommand(slotsFinderDescriptor);

        String expectedMessage = String.format(SlotsCommand.MESSAGE_NO_SLOTS_FOUND);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(slotsCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        SlotsCommand slotsCommand = new SlotsCommand(getSlotsFinderDescriptor_allFields());
        SlotsCommand slotsCommandSame = new SlotsCommand(getSlotsFinderDescriptor_allFields());
        SlotsCommand slotsCommandDiff = new SlotsCommand(getSlotsFinderDescriptor_noSlots());

        // same object -> returns true
        assertEquals(slotsCommand, slotsCommand);

        // null -> returns false
        assertNotEquals(null, slotsCommand);

        // different types -> returns false
        assertNotEquals(slotsCommand, new ClearCommand());

        // different descriptor
        assertNotEquals(slotsCommand, slotsCommandDiff);

        // same descriptor -> returns true
        assertEquals(slotsCommand, slotsCommandSame);
    }

    @Test
    public void toStringMethod() {
        SlotsCommand.SlotsFinderDescriptor slotsFinderDescriptor = getSlotsFinderDescriptor_allFields();
        SlotsCommand slotsCommand = new SlotsCommand(slotsFinderDescriptor);
        String expected = SlotsCommand.class.getCanonicalName() + "{"
                + "slotsFinderDescriptor=" + slotsFinderDescriptor + "}";

        assertEquals(expected, slotsCommand.toString());
    }

    private static SlotsCommand.SlotsFinderDescriptor getSlotsFinderDescriptor_allFields() {
        SlotsCommand.SlotsFinderDescriptor slotsFinderDescriptor = new SlotsCommand.SlotsFinderDescriptor();
        slotsFinderDescriptor.setDuration(1);
        slotsFinderDescriptor.setTimeframe(new Period(Period.DEFAULT_PERIOD_NAME, "11-17"));
        slotsFinderDescriptor.setDays(new HashSet<>(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)));
        slotsFinderDescriptor.setPredicates(Collections.singleton(
                new TagContainsKeywordsPredicate(new Tag("tutorial-1"))));
        return slotsFinderDescriptor;
    }

    private static AvailableSlots getExpectedAvailableSlots_allFields() {
        AvailableSlots as =
                new AvailableSlots(new HashSet<>(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)));
        as.addPeriodsToDay(DayOfWeek.MONDAY,
                new ArrayList<>(List.of(
                        new Period(Period.DEFAULT_PERIOD_NAME, "11-12"),
                        new Period(Period.DEFAULT_PERIOD_NAME, "12-13"),
                        new Period(Period.DEFAULT_PERIOD_NAME, "15-16"))));
        as.addPeriodsToDay(DayOfWeek.TUESDAY,
                new ArrayList<>(List.of(
                        new Period(Period.DEFAULT_PERIOD_NAME, "11-12"),
                        new Period(Period.DEFAULT_PERIOD_NAME, "12-13"),
                        new Period(Period.DEFAULT_PERIOD_NAME, "15-16"))));
        return as;
    }

    private static SlotsCommand.SlotsFinderDescriptor getSlotsFinderDescriptor_noSlots() {
        SlotsCommand.SlotsFinderDescriptor slotsFinderDescriptor = new SlotsCommand.SlotsFinderDescriptor();
        slotsFinderDescriptor.setDuration(3);
        slotsFinderDescriptor.setTimeframe(new Period(Period.DEFAULT_PERIOD_NAME, "11-13"));
        slotsFinderDescriptor.setDays(Timetable.DEFAULT_ALL_DAYS);
        return slotsFinderDescriptor;
    }
}
