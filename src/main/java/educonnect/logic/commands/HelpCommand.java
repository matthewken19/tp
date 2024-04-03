package educonnect.logic.commands;

import java.util.Objects;

import educonnect.model.Model;

/**
 * Format full help instructions for every command for display.
 */
public class HelpCommand extends Command {
    private final String args;
    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows usage instructions for "
            + "various commands in the application.\n\n"
            + "Parameters (Optional): One of the following: [add, clear, delete, edit, find, list]\n\n"
            + "Example:\n"
            + "1." + COMMAND_WORD + " add shows usage instructions for \'add\' command\n"
            + "2." + COMMAND_WORD + " opens a popup window briefly explaining all the commands.";

    public static final String SHOWING_HELP_MESSAGE = "Opened help window.";

    public static final String SHOWING_HELP_MESSAGE_ADD = "add - Adds a student to the address book.\n\n"
            + "Format: add n/NAME s/STUDENT_ID e/EMAIL h/TELEGRAM_HANDLE [c/TIMETABLE] [t/TAG]\n\n"
            + "Example:\n"
            + "1. add n/Anne-Marie Rose Nicholson t/singer t/songwriter e/rockabye@friends.uk "
            + "h/@AnneMarieofficial s/A7041991U\n\n"
            + "2. add n/John Doe s/A1234567X h/@john.doe e/johnd@example.com t/tutorial-1 t/high-ability "
            + "c/mon: 8-10, 10-12 tue: 11-13 thu: 12-15, 15-17";

    public static final String SHOWING_HELP_MESSAGE_LIST = "list - Shows a list of all students in the address book "
            + "with the option to show timetables.\n\n"
            + "Format: list [timetable]\n\n"
            + "Example:\n"
            + "1. list shows the list of students without timetable\n"
            + "2. list timetable shows the list of students with timetable";

    public static final String SHOWING_HELP_MESSAGE_DELETE = "delete - Deletes a specified student "
            + "from the address book.\n\n"
            + "Format: delete [s/STUDENT_ID] [e/EMAIL] [h/TELEGRAM_HANDLE]\n\n"
            + "Deletes a student with the specified STUDENT_ID or EMAIL or TELEGRAM_HANDLE, and only one field may be "
            + "used for each delete command\n\n"
            + "Example:\n"
            + "1. delete e/royb@gmail.com deletes a student with an email of royb@gmail.com\n"
            + "2. delete s/A1654327X deletes a student with a student id of A1654327X";

    public static final String SHOWING_HELP_MESSAGE_EDIT = "edit - Edits an existing student in the address book.\n\n"
            + "Format: edit INDEX [n/NAME] [s/STUDENT_ID] [e/EMAIL] [h/TELEGRAM_HANDLE] [l/WEBLINK] "
            + "[c/TIMETABLE] [t/TAG]\n\n"
            + "Example:\n"
            + "1. edit 1 s/A0001234A e/johndoe@example.com edits the student id and email address of the "
            + "1st student to be A0001234A and johndoe@example.com respectively.\n"
            + "2. edit 2 n/Betsy Crower t/ edits the name of the 2nd student to be Betsy Crower"
            + " and clears all existing tags.";

    public static final String SHOWING_HELP_MESSAGE_FIND = "find - Find students whose criteria match any of "
            + "the given keywords.\n\n"
            + "Format: find [n/NAME] [s/STUDENT_ID] [h/TELEGRAM_HANDLE] [t/TAG]\n\n"
            + "Example:\n"
            + "1. find n/John returns john and John Doe\n"
            + "2. find e/yahoo returns Alex Yeoh and David Li (both uses yahoo email)";

    public static final String SHOWING_HELP_MESSAGE_CLEAR = "clear - Clears all entries from the address book.";


    public HelpCommand() {
        this.args = null;
    }

    public HelpCommand(String args) {
        this.args = args;
    }

    @Override
    public CommandResult execute(Model model) {
        if (this.args == null) {
            return new CommandResult(SHOWING_HELP_MESSAGE, true, false, false);
        } else if (this.args.equals("add")) {
            return new CommandResult(SHOWING_HELP_MESSAGE_ADD);
        } else if (this.args.equals("list")) {
            return new CommandResult(SHOWING_HELP_MESSAGE_LIST);
        } else if (this.args.equals("find")) {
            return new CommandResult(SHOWING_HELP_MESSAGE_FIND);
        } else if (this.args.equals("edit")) {
            return new CommandResult(SHOWING_HELP_MESSAGE_EDIT);
        } else if (this.args.equals("delete")) {
            return new CommandResult(SHOWING_HELP_MESSAGE_DELETE);
        } else { // All other invalid arguments are already removed at the parsing stage
            return new CommandResult(SHOWING_HELP_MESSAGE_CLEAR);
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
