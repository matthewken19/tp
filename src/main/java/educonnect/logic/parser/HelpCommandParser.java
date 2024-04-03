package educonnect.logic.parser;

import static educonnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;

import educonnect.logic.commands.HelpCommand;
import educonnect.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new HelpCommand object
 */
public class HelpCommandParser implements Parser<HelpCommand> {

    private static final String[] VALID_COMMANDS =
    {"add", "clear", "delete", "edit", "find", "list", "", "slots", "copy"};

    /**
     * Parses the given {@code String} of arguments in the context of the HelpCommand
     * and returns a HelpCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
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
