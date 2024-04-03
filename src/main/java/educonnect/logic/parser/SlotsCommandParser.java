package educonnect.logic.parser;

import static educonnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static educonnect.logic.parser.CliSyntax.PREFIX_DURATION;
import static educonnect.logic.parser.CliSyntax.PREFIX_ON_DAYS;
import static educonnect.logic.parser.CliSyntax.PREFIX_PERIOD;
import static educonnect.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import educonnect.logic.commands.SlotsCommand;
import educonnect.logic.parser.exceptions.ParseException;
import educonnect.model.student.Student;
import educonnect.model.student.Tag;
import educonnect.model.student.predicates.TagContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new SlotsCommand object.
 */
public class SlotsCommandParser implements Parser<SlotsCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the SlotsCommand
     * and returns a SlotsCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public SlotsCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SlotsCommand.MESSAGE_USAGE));
        }

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_DURATION, PREFIX_TAG, PREFIX_PERIOD, PREFIX_ON_DAYS);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_DURATION, PREFIX_PERIOD, PREFIX_ON_DAYS);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SlotsCommand.MESSAGE_USAGE));
        }

        SlotsCommand.SlotsFinderDescriptor slotsFinderDescriptor = new SlotsCommand.SlotsFinderDescriptor();

        // Parses and sets the duration
        if (argMultimap.getValue(PREFIX_DURATION).isPresent()) {
            slotsFinderDescriptor.setDuration(
                    ParserUtil.parseDuration(argMultimap.getValue(PREFIX_DURATION).get()));
        }

        // Parses and sets the timeframe, if any.
        if (argMultimap.getValue(PREFIX_PERIOD).isPresent()) {
            slotsFinderDescriptor.setTimeframe(
                    ParserUtil.parsePeriod(argMultimap.getValue(PREFIX_PERIOD).get()));
        }

        if (argMultimap.getValue(PREFIX_ON_DAYS).isPresent()) {
            slotsFinderDescriptor.setDays(
                    ParserUtil.parseDaysSpecified(argMultimap.getValue(PREFIX_ON_DAYS).get()));
        }

        // Handles tags if there are any
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        Set<Predicate<Student>> predicates = new HashSet<>();
        for (Tag keywordTag : tagList) {
            predicates.add(new TagContainsKeywordsPredicate(keywordTag));
        }
        slotsFinderDescriptor.setPredicates(predicates);

        return new SlotsCommand(slotsFinderDescriptor);
    }
}
