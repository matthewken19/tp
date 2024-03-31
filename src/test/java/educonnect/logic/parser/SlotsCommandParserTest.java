package educonnect.logic.parser;

import static educonnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static educonnect.logic.parser.CliSyntax.PREFIX_DURATION;
import static educonnect.logic.parser.CliSyntax.PREFIX_ON_DAYS;
import static educonnect.logic.parser.CliSyntax.PREFIX_PERIOD;
import static educonnect.logic.parser.CliSyntax.PREFIX_TAG;
import static educonnect.logic.parser.CommandParserTestUtil.assertParseFailure;
import static educonnect.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static educonnect.logic.parser.ParserUtil.MESSAGE_INVALID_DURATION;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import educonnect.logic.Messages;
import educonnect.logic.commands.SlotsCommand;
import educonnect.model.student.Tag;
import educonnect.model.student.predicates.TagContainsKeywordsPredicate;
import educonnect.model.student.timetable.Period;

public class SlotsCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, SlotsCommand.MESSAGE_USAGE);
    private static final String VALID_DURATION = "5";
    private static final String VALID_TIMEFRAME = "10-18";
    private static final String VALID_ON_DAYS = "mon tue thu";
    private static final String VALID_TAG = "tutorial-1";
    private static final String validCommand = " " + PREFIX_DURATION + VALID_DURATION;

    private final SlotsCommandParser parser = new SlotsCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no duration specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " " + PREFIX_DURATION, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidMandatoryValues_failure() {
        // negative duration
        assertParseFailure(parser, " " + PREFIX_DURATION + "-1" , MESSAGE_INVALID_DURATION);

        // duration too large
        assertParseFailure(parser, " " + PREFIX_DURATION + "24" , MESSAGE_INVALID_DURATION);

        // invalid String parsed as duration
        assertParseFailure(parser, " " + PREFIX_DURATION + "abc" , MESSAGE_INVALID_DURATION);

        // invalid prefix
        assertParseFailure(parser, " i/24" , MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidOptionalValues_failure() {
        // invalid timeframe
        assertParseFailure(parser, validCommand + " "
                                   + PREFIX_PERIOD + "abc", Period.PERIOD_CONSTRAINTS);
        assertParseFailure(parser, validCommand + " "
                                   + PREFIX_PERIOD + "0-25", Period.PERIOD_CONSTRAINTS);
    }

    @Test
    public void parse_multipleRepeatedValues_failure() {
        // repeated duration
        assertParseFailure(parser,
                validCommand + " " + PREFIX_DURATION + VALID_DURATION,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DURATION));

        // repeated timeframe
        assertParseFailure(parser,
                validCommand + " " + PREFIX_PERIOD + VALID_TIMEFRAME + " " + PREFIX_PERIOD + VALID_TIMEFRAME,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PERIOD));

        // repeated on days
        assertParseFailure(parser,
                validCommand + " " + PREFIX_ON_DAYS + VALID_ON_DAYS + " " + PREFIX_ON_DAYS + VALID_ON_DAYS,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ON_DAYS));
    }

    @Test
    public void parse_validInputMandatory_success() {
        // only duration specified
        SlotsCommand.SlotsFinderDescriptor descriptor = new SlotsCommand.SlotsFinderDescriptor();
        descriptor.setDuration(Integer.parseInt(VALID_DURATION));
        SlotsCommand slotsCommand = new SlotsCommand(descriptor);

        assertParseSuccess(parser, validCommand, slotsCommand);
    }

    @Test
    public void parse_validInputsAllValues_success() {
        String validFullCommand = validCommand + " "
                + PREFIX_PERIOD + VALID_TIMEFRAME + " "
                + PREFIX_ON_DAYS + VALID_ON_DAYS + " "
                + PREFIX_TAG + VALID_TAG;

        SlotsCommand.SlotsFinderDescriptor descriptor = new SlotsFinderDescriptorBuilder()
                .withDuration()
                .withTimeframe()
                .withOnDays()
                .withPredicates()
                .build();
        SlotsCommand slotsCommand = new SlotsCommand(descriptor);

        assertParseSuccess(parser, validFullCommand, slotsCommand);
    }

    @Test
    public void parse_validInputsOneOptionalValue_success() {
        // only timeframe specified
        SlotsCommand.SlotsFinderDescriptor descriptor =
                new SlotsFinderDescriptorBuilder().withDuration().withTimeframe().build();
        SlotsCommand slotsCommand = new SlotsCommand(descriptor);

        assertParseSuccess(parser, validCommand + " " + PREFIX_PERIOD + VALID_TIMEFRAME, slotsCommand);

        // only on days specified
        descriptor = new SlotsFinderDescriptorBuilder().withDuration().withOnDays().build();
        slotsCommand = new SlotsCommand(descriptor);

        assertParseSuccess(parser, validCommand + " " + PREFIX_ON_DAYS + VALID_ON_DAYS, slotsCommand);

        // only tags specified
        descriptor = new SlotsFinderDescriptorBuilder().withDuration().withPredicates().build();
        slotsCommand = new SlotsCommand(descriptor);

        assertParseSuccess(parser, validCommand + " " + PREFIX_TAG + VALID_TAG, slotsCommand);
    }

    @Test
    public void parse_validInputsMultipleOptionalValues_success() {
        // timeframe and on days specified
        SlotsCommand.SlotsFinderDescriptor descriptor =
                new SlotsFinderDescriptorBuilder().withDuration().withTimeframe().withOnDays().build();
        SlotsCommand slotsCommand = new SlotsCommand(descriptor);

        assertParseSuccess(parser,
                validCommand + " " + PREFIX_PERIOD + VALID_TIMEFRAME + " " + PREFIX_ON_DAYS + VALID_ON_DAYS,
                slotsCommand);

        // timeframe and tags specified
        descriptor =
                new SlotsFinderDescriptorBuilder().withDuration().withTimeframe().withPredicates().build();
        slotsCommand = new SlotsCommand(descriptor);

        assertParseSuccess(parser,
                validCommand + " " + PREFIX_PERIOD + VALID_TIMEFRAME + " " + PREFIX_TAG + VALID_TAG,
                slotsCommand);


        // days and tag specified
        descriptor =
                new SlotsFinderDescriptorBuilder().withDuration().withOnDays().withPredicates().build();
        slotsCommand = new SlotsCommand(descriptor);

        assertParseSuccess(parser,
                validCommand + " " + PREFIX_ON_DAYS + VALID_ON_DAYS + " " + PREFIX_TAG + VALID_TAG ,
                slotsCommand);
    }

    private static class SlotsFinderDescriptorBuilder {
        private final SlotsCommand.SlotsFinderDescriptor descriptor;

        SlotsFinderDescriptorBuilder() {
            this.descriptor = new SlotsCommand.SlotsFinderDescriptor();
        }

        SlotsFinderDescriptorBuilder withDuration() {
            descriptor.setDuration(Integer.parseInt(VALID_DURATION));
            return this;
        }

        SlotsFinderDescriptorBuilder withTimeframe() {
            descriptor.setTimeframe(new Period(Period.DEFAULT_PERIOD_NAME, VALID_TIMEFRAME));
            return this;
        }

        SlotsFinderDescriptorBuilder withOnDays() {
            descriptor.setDays(new HashSet<>(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)));
            return this;
        }

        SlotsFinderDescriptorBuilder withPredicates() {
            descriptor.setPredicates(Collections.singleton(new TagContainsKeywordsPredicate(new Tag(VALID_TAG))));
            return this;
        }

        SlotsCommand.SlotsFinderDescriptor build() {
            return descriptor;
        }
    }
}
