package educonnect.logic.parser;

import static educonnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static educonnect.logic.parser.CommandParserTestUtil.assertParseFailure;
import static educonnect.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import educonnect.logic.commands.HelpCommand;

public class HelpCommandParserTest {
    private static final String[] VALID_COMMANDS = {"add", "clear", "delete", "edit", "find", "list"};
    private static final String INVALID_ARGS = "pin";
    private final HelpCommandParser parser = new HelpCommandParser();

    @Test
    public void parse_noArguments_success() {
        assertParseSuccess(parser, "", new HelpCommand());
    }

    @Test
    public void parse_withArguments_success() {
        String validArgs = VALID_COMMANDS[1];
        assertParseSuccess(parser, validArgs, new HelpCommand(validArgs));
    }

    @Test
    public void parse_unidentifiedArgument_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE);
        assertParseFailure(parser, INVALID_ARGS, expectedMessage);
    }

}
