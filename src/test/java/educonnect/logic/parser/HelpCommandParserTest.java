package educonnect.logic.parser;

import static educonnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static educonnect.logic.parser.CommandParserTestUtil.assertParseFailure;
import static educonnect.logic.parser.CommandParserTestUtil.assertParseSuccess;

import educonnect.logic.commands.HelpCommand;
import org.junit.jupiter.api.Test;

public class HelpCommandParserTest {

    private final HelpCommandParser parser = new HelpCommandParser();
    private final static String[] VALID_COMMANDS = {"add", "clear", "delete", "edit", "find", "list"};
    private final String INVALID_ARGS = "pin";

    @Test
    public void parse_noArguments_success() {
        assertParseSuccess(parser, "", new HelpCommand());
    }

    @Test
    public void parse_withArguments_success() {
        String valid_args = VALID_COMMANDS[1];
        assertParseSuccess(parser, valid_args, new HelpCommand(valid_args));
    }

    @Test
    public void parse_unidentifiedArgument_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE);
        assertParseFailure(parser, INVALID_ARGS, expectedMessage);
    }

}
