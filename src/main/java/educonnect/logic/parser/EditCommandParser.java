package educonnect.logic.parser;

import static educonnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static educonnect.logic.parser.CliSyntax.EDIT_ID_PREFIX_EMAIL;
import static educonnect.logic.parser.CliSyntax.EDIT_ID_PREFIX_INDEX;
import static educonnect.logic.parser.CliSyntax.EDIT_ID_PREFIX_STUDENT_ID;
import static educonnect.logic.parser.CliSyntax.EDIT_ID_PREFIX_TELEGRAM_HANDLE;
import static educonnect.logic.parser.CliSyntax.PREFIX_EMAIL;
import static educonnect.logic.parser.CliSyntax.PREFIX_LINK;
import static educonnect.logic.parser.CliSyntax.PREFIX_NAME;
import static educonnect.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static educonnect.logic.parser.CliSyntax.PREFIX_TAG;
import static educonnect.logic.parser.CliSyntax.PREFIX_TELEGRAM_HANDLE;
import static educonnect.logic.parser.CliSyntax.PREFIX_TIMETABLE;
import static educonnect.logic.parser.CliSyntax.PREFIX_TIMETABLE_FRIDAY;
import static educonnect.logic.parser.CliSyntax.PREFIX_TIMETABLE_MONDAY;
import static educonnect.logic.parser.CliSyntax.PREFIX_TIMETABLE_SATURDAY;
import static educonnect.logic.parser.CliSyntax.PREFIX_TIMETABLE_SUNDAY;
import static educonnect.logic.parser.CliSyntax.PREFIX_TIMETABLE_THURSDAY;
import static educonnect.logic.parser.CliSyntax.PREFIX_TIMETABLE_TUESDAY;
import static educonnect.logic.parser.CliSyntax.PREFIX_TIMETABLE_WEDNESDAY;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import educonnect.commons.core.index.Index;
import educonnect.logic.commands.EditCommand;
import educonnect.logic.parser.exceptions.ParseException;
import educonnect.model.student.Student;
import educonnect.model.student.Tag;
import educonnect.model.student.predicates.EmailMatchesKeywordsPredicate;
import educonnect.model.student.predicates.IdMatchesKeywordsPredicate;
import educonnect.model.student.predicates.TelegramMatchesKeywordsPredicate;
import educonnect.model.student.timetable.Timetable;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_STUDENT_ID, PREFIX_EMAIL,
                PREFIX_TELEGRAM_HANDLE, PREFIX_LINK, PREFIX_TAG, PREFIX_TIMETABLE);
        // get identifier
        String identifierArgs = " " + argMultimap.getPreamble();
        ArgumentMultimap identifierArgMultimap = ArgumentTokenizer.tokenize(identifierArgs, EDIT_ID_PREFIX_EMAIL,
                EDIT_ID_PREFIX_STUDENT_ID, EDIT_ID_PREFIX_INDEX, EDIT_ID_PREFIX_TELEGRAM_HANDLE);
        // check for any valid format
        if (argMultimap.size() < 2 && identifierArgMultimap.size() < 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditCommand.MESSAGE_USAGE));
        }
        // check for multiple unique identifier
        if (identifierArgMultimap.size() != 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditCommand.MESSAGE_INVALID_IDENTIFIER));
        }
        // check for empty field
        if (argMultimap.size() < 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_NOT_EDITED));
        }
        // check for duplicate field
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_STUDENT_ID, PREFIX_EMAIL, PREFIX_TELEGRAM_HANDLE,
                PREFIX_LINK, PREFIX_TIMETABLE);
        // check for duplicate identifier
        identifierArgMultimap.verifyNoDuplicatePrefixesFor(EDIT_ID_PREFIX_EMAIL, EDIT_ID_PREFIX_STUDENT_ID,
                EDIT_ID_PREFIX_INDEX, EDIT_ID_PREFIX_TELEGRAM_HANDLE);
        // check for invalid preamble
        if (!identifierArgMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }
        List<Predicate<Student>> predicates = new ArrayList<>();
        identifierArgMultimap.getValue(EDIT_ID_PREFIX_EMAIL).ifPresent(keywordEmail ->
                predicates.add(new EmailMatchesKeywordsPredicate(keywordEmail))
        );
        identifierArgMultimap.getValue(EDIT_ID_PREFIX_STUDENT_ID).ifPresent(keywordId ->
                predicates.add(new IdMatchesKeywordsPredicate(keywordId))
        );
        identifierArgMultimap.getValue(EDIT_ID_PREFIX_TELEGRAM_HANDLE).ifPresent(keywordTeleHandle ->
                predicates.add(new TelegramMatchesKeywordsPredicate(keywordTeleHandle))
        );
        Index index;
        try {
            index = ParserUtil.parseIndex(identifierArgMultimap.getValue(EDIT_ID_PREFIX_INDEX).orElse("1"));
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }
        //create edit student descriptor
        EditCommand.EditStudentDescriptor editPersonDescriptor = new EditCommand.EditStudentDescriptor();
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_STUDENT_ID).isPresent()) {
            editPersonDescriptor.setStudentId(ParserUtil.parseStudentId(argMultimap.getValue(PREFIX_STUDENT_ID).get()));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            editPersonDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_TELEGRAM_HANDLE).isPresent()) {
            editPersonDescriptor.setTelegramHandle(ParserUtil.parseTelegramHandle(argMultimap.getValue(
                    PREFIX_TELEGRAM_HANDLE).get()));
        }
        if (argMultimap.getValue(PREFIX_LINK).isPresent()) {
            editPersonDescriptor.setLink(ParserUtil.parseLink(argMultimap.getValue(PREFIX_LINK).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editPersonDescriptor::setTags);
        if (argMultimap.getValue(PREFIX_TIMETABLE).isPresent()) {
            editPersonDescriptor.setTimetable(ParserUtil.parseTimetable(
                    tokenizeForTimetable(argMultimap.getValue(PREFIX_TIMETABLE).orElse(""))));
        }
        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, predicates, editPersonDescriptor);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

    /**
     * Tokenizes the input arguments for Timetable under Add Command.
     *
     * @param fullTimetableString a full {@code String} containing the arguments. E.g. "mon: 1-4, 12-14 tue: 14-16 ..."
     * @return an {@code ArrayList<String>}, with each entry containing arguments for each day of the Timetable week.
     */
    public static ArrayList<String> tokenizeForTimetable(String fullTimetableString) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(" " + fullTimetableString.toLowerCase(), PREFIX_TIMETABLE_MONDAY,
                        PREFIX_TIMETABLE_TUESDAY, PREFIX_TIMETABLE_WEDNESDAY,
                        PREFIX_TIMETABLE_THURSDAY, PREFIX_TIMETABLE_FRIDAY,
                        PREFIX_TIMETABLE_SATURDAY, PREFIX_TIMETABLE_SUNDAY);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TIMETABLE_MONDAY, PREFIX_TIMETABLE_TUESDAY,
                PREFIX_TIMETABLE_WEDNESDAY, PREFIX_TIMETABLE_THURSDAY, PREFIX_TIMETABLE_FRIDAY,
                PREFIX_TIMETABLE_SATURDAY, PREFIX_TIMETABLE_SUNDAY);

        ArrayList<String> allDays = new ArrayList<>();
        allDays.add(argMultimap.getValue(PREFIX_TIMETABLE_MONDAY).orElse(""));
        allDays.add(argMultimap.getValue(PREFIX_TIMETABLE_TUESDAY).orElse(""));
        allDays.add(argMultimap.getValue(PREFIX_TIMETABLE_WEDNESDAY).orElse(""));
        allDays.add(argMultimap.getValue(PREFIX_TIMETABLE_THURSDAY).orElse(""));
        allDays.add(argMultimap.getValue(PREFIX_TIMETABLE_FRIDAY).orElse(""));

        if (Timetable.is7Days()) {
            allDays.add(argMultimap.getValue(PREFIX_TIMETABLE_SATURDAY).orElse(""));
            allDays.add(argMultimap.getValue(PREFIX_TIMETABLE_SUNDAY).orElse(""));
        }

        return allDays;
    }
}
