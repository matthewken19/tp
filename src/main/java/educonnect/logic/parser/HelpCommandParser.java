package educonnect.logic.parser;

import educonnect.logic.commands.HelpCommand;
import educonnect.logic.parser.exceptions.ParseException;

import java.util.Arrays;

import static educonnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

/**
 * Parses input arguments and creates a new HelpCommand object
 */
public class HelpCommandParser implements Parser<HelpCommand>{

    private static String[] VALID_COMMANDS = {"add", "clear", "delete", "edit", "find", "list"};
    public HelpCommand parse(String args) throws ParseException {
        args = args.trim();
        if (args.isEmpty()) {
            return new HelpCommand();
        } else if (Arrays.asList(VALID_COMMANDS).contains(args)) {
            return new HelpCommand(args);
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }
    }
}
