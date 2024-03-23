package educonnect.model.student.timetable;

import static educonnect.testutil.TypicalTimetableAndValues.VALID_PERIOD_1;
import static educonnect.testutil.TypicalTimetableAndValues.VALID_PERIOD_3;

import educonnect.model.student.timetable.exceptions.OverlapPeriodException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

public class AvailableSlotsTest {
    @Test
    public void findAllCommonSlots() throws OverlapPeriodException {
        AvailableSlots availableSlot1 = new AvailableSlots(new HashSet<>(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)));
        AvailableSlots availableSlot2 = new AvailableSlots(new HashSet<>(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)));
        ArrayList<Period> periods1 = new ArrayList<>(List.of(VALID_PERIOD_1, VALID_PERIOD_3));

        availableSlot1.addPeriodsToDay(DayOfWeek.MONDAY, periods1);
        availableSlot1.addPeriodsToDay(DayOfWeek.TUESDAY, periods1);
        availableSlot2.addPeriodsToDay(DayOfWeek.MONDAY, periods1);
        availableSlot2.addPeriodsToDay(DayOfWeek.TUESDAY, periods1);

        ArrayList<AvailableSlots> allSlots = new ArrayList<>(List.of(availableSlot1, availableSlot2));

        AvailableSlots.findAllCommonSlots(allSlots);
    }
}
