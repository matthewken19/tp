package educonnect.logic.commands;

import java.util.Objects;

import educonnect.model.Model;


/**
 * Format full help instructions for every command for display.
 */
public class HelpCommand extends Command {
    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows usage instructions for "
            + "various commands in the application.\n\n"
            + "Parameters (Optional): One of the following: [add, clear, delete, edit, find, list, copy, slots]\n\n"
            + "Example:\n"
            + "1." + COMMAND_WORD + " add shows usage instructions for \'add\' command\n"
            + "2." + COMMAND_WORD + " opens a popup window briefly explaining all the commands.";

    public static final String SHOWING_HELP_MESSAGE = "Opened help window.";



    private final String args;
    public HelpCommand() {
        this.args = null;
    }

    public HelpCommand(String args) {
        this.args = args;
    }

    @Override
    public CommandResult execute(Model model) {
        if (this.args == null) {
            return new CommandResult(SHOWING_HELP_MESSAGE, true, false);
        } else if (this.args.equals("add")) {
            return new CommandResult(AddCommand.MESSAGE_USAGE);
        } else if (this.args.equals("list")) {
            return new CommandResult(ListCommand.MESSAGE_USAGE);
        } else if (this.args.equals("find")) {
            return new CommandResult(FindCommand.MESSAGE_USAGE);
        } else if (this.args.equals("edit")) {
            return new CommandResult(EditCommand.MESSAGE_USAGE);
        } else if (this.args.equals("delete")) {
            return new CommandResult(DeleteCommand.MESSAGE_USAGE);
        } else if (this.args.equals("copy")) {
            return new CommandResult(CopyCommand.MESSAGE_USAGE);
        } else if (this.args.equals("slots")) {
            return new CommandResult(SlotsCommand.MESSAGE_USAGE);
        } else { // All other invalid arguments are already removed at the parsing stage
            return new CommandResult(ClearCommand.MESSAGE_USAGE);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof HelpCommand)) {
            return false;
        }

        HelpCommand otherHelp = (HelpCommand) other;
        return Objects.equals(this.args, otherHelp.args);
    }
}
