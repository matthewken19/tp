package educonnect.logic.parser;

import static educonnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static educonnect.logic.Messages.getErrorMessageForEmptyArguments;
import static educonnect.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static educonnect.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static educonnect.logic.commands.CommandTestUtil.STUDENT_ID_DESC_AMY;
import static educonnect.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static educonnect.logic.commands.CommandTestUtil.TELEGRAM_HANDLE_DESC_AMY;
import static educonnect.logic.commands.CommandTestUtil.VALID_EMAIL_PREDICATE;
import static educonnect.logic.commands.CommandTestUtil.VALID_NAME_PREDICATE;
import static educonnect.logic.commands.CommandTestUtil.VALID_STUDENT_ID_AMY;
import static educonnect.logic.commands.CommandTestUtil.VALID_STUDENT_ID_PREDICATE;
import static educonnect.logic.commands.CommandTestUtil.VALID_TAG_PREDICATE;
import static educonnect.logic.commands.CommandTestUtil.VALID_TELEGRAM_HANDLE_PREDICATE;
import static educonnect.logic.parser.CliSyntax.PREFIX_EMAIL;
import static educonnect.logic.parser.CliSyntax.PREFIX_NAME;
import static educonnect.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static educonnect.logic.parser.CliSyntax.PREFIX_TAG;
import static educonnect.logic.parser.CliSyntax.PREFIX_TELEGRAM_HANDLE;
import static educonnect.logic.parser.CommandParserTestUtil.assertParseFailure;
import static educonnect.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import educonnect.logic.commands.FindCommand;
import educonnect.model.student.Tag;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FindCommand.MESSAGE_USAGE));
    }
    @Test
    public void parse_invalidPrefix_throwsParseException() {
        assertParseFailure(parser, " m/" + VALID_STUDENT_ID_AMY,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }
    @Test
    public void parse_emptyPrefixArg_throwsParseException() {
        assertParseFailure(parser, " " + PREFIX_NAME.getPrefix(),
                getErrorMessageForEmptyArguments(PREFIX_NAME));
        assertParseFailure(parser, " " + PREFIX_STUDENT_ID.getPrefix(),
                getErrorMessageForEmptyArguments(PREFIX_STUDENT_ID));
        assertParseFailure(parser, " " + PREFIX_EMAIL.getPrefix(),
                getErrorMessageForEmptyArguments(PREFIX_EMAIL));
        assertParseFailure(parser, " " + PREFIX_TELEGRAM_HANDLE.getPrefix(),
                getErrorMessageForEmptyArguments(PREFIX_TELEGRAM_HANDLE));
        assertParseFailure(parser, " " + PREFIX_TAG.getPrefix(), Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_singleArg_success() {
        FindCommand expectedCommand = new FindCommand(List.of(VALID_NAME_PREDICATE));
        assertParseSuccess(parser, NAME_DESC_AMY, expectedCommand);

        expectedCommand = new FindCommand(List.of(VALID_STUDENT_ID_PREDICATE));
        assertParseSuccess(parser, STUDENT_ID_DESC_AMY, expectedCommand);

        expectedCommand = new FindCommand(List.of(VALID_EMAIL_PREDICATE));
        assertParseSuccess(parser, EMAIL_DESC_AMY, expectedCommand);

        expectedCommand = new FindCommand(List.of(VALID_TELEGRAM_HANDLE_PREDICATE));
        assertParseSuccess(parser, TELEGRAM_HANDLE_DESC_AMY, expectedCommand);

        expectedCommand = new FindCommand(List.of(VALID_TAG_PREDICATE));
        assertParseSuccess(parser, TAG_DESC_FRIEND, expectedCommand);
    }
    @Test
    public void parse_multipleArg_success() {
        String userInput = NAME_DESC_AMY + TAG_DESC_FRIEND;
        FindCommand expectedCommand = new FindCommand(List.of(VALID_TAG_PREDICATE, VALID_NAME_PREDICATE));
        assertParseSuccess(parser, userInput, expectedCommand);

        userInput = STUDENT_ID_DESC_AMY + NAME_DESC_AMY;
        expectedCommand = new FindCommand(List.of(VALID_NAME_PREDICATE, VALID_STUDENT_ID_PREDICATE));
        assertParseSuccess(parser, userInput, expectedCommand);

        userInput = TELEGRAM_HANDLE_DESC_AMY + TAG_DESC_FRIEND;
        expectedCommand = new FindCommand(List.of(VALID_TAG_PREDICATE, VALID_TELEGRAM_HANDLE_PREDICATE));
        assertParseSuccess(parser, userInput, expectedCommand);

        userInput = EMAIL_DESC_AMY + TELEGRAM_HANDLE_DESC_AMY;
        expectedCommand = new FindCommand(List.of(VALID_TELEGRAM_HANDLE_PREDICATE, VALID_EMAIL_PREDICATE));
        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
