package educonnect.logic.parser;

import static educonnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static educonnect.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static educonnect.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static educonnect.logic.commands.CommandTestUtil.STUDENT_ID_DESC_AMY;
import static educonnect.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static educonnect.logic.commands.CommandTestUtil.TELEGRAM_HANDLE_DESC_AMY;
import static educonnect.logic.commands.CommandTestUtil.TIMETABLE_DESC_VALID1;
import static educonnect.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;

import java.util.Collections;
import java.util.HashSet;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import educonnect.logic.commands.CopyCommand;
import educonnect.model.student.Student;
import educonnect.model.student.Tag;
import educonnect.model.student.predicates.TagContainsKeywordsPredicate;


public class CopyCommandParserTest {

    private CopyCommandParser parser = new CopyCommandParser();

    @Test
    public void parse_emptyArg_success() {
        CommandParserTestUtil.assertParseSuccess(parser, "     ", new CopyCommand(Collections.emptyList()));
    }

    @Test
    public void parse_preamblePresent_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(parser, "1" + TAG_DESC_FRIEND,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidFields_throwsParseException() {
        // Invalid prefix name
        CommandParserTestUtil.assertParseFailure(parser, NAME_DESC_AMY,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));
        // Invalid prefix student id
        CommandParserTestUtil.assertParseFailure(parser, STUDENT_ID_DESC_AMY,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));
        // Invalid prefix email
        CommandParserTestUtil.assertParseFailure(parser, EMAIL_DESC_AMY,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));
        // Invalid prefix telegram handle
        CommandParserTestUtil.assertParseFailure(parser, TELEGRAM_HANDLE_DESC_AMY,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));
        // Invalid prefix timetable
        CommandParserTestUtil.assertParseFailure(parser, TIMETABLE_DESC_VALID1,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_oneTag_success() {
        Predicate<Student> checkFriend = new TagContainsKeywordsPredicate(new Tag(VALID_TAG_FRIEND));
        HashSet<Predicate<Student>> predicates = new HashSet<>();
        predicates.add(checkFriend);
        CommandParserTestUtil.assertParseSuccess(parser, TAG_DESC_FRIEND, new CopyCommand(predicates));
    }
}
