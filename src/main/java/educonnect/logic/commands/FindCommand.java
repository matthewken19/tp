package educonnect.logic.commands;

import static educonnect.logic.parser.CliSyntax.PREFIX_EMAIL;
import static educonnect.logic.parser.CliSyntax.PREFIX_NAME;
import static educonnect.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static educonnect.logic.parser.CliSyntax.PREFIX_TAG;
import static educonnect.logic.parser.CliSyntax.PREFIX_TELEGRAM_HANDLE;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import educonnect.commons.util.ToStringBuilder;
import educonnect.logic.Messages;
import educonnect.model.Model;
import educonnect.model.student.Student;

/**
 * Finds and lists all students in address book whose name contains any of the argument keywords.
 * Keyword matching is case-insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all students whose attributes match "
            + "the specified attributed keywords (case-insensitive) and displays them as a list. "
            + "Finding by tags need to match the whole tag but finding by other attributes can match partially. "
            + "Finding by multiple prefixes will find students that satisfies all prefixes used.\n\n"

            + "Parameters: "
            + "<choose 1 or more>"
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_STUDENT_ID + "STUDENT_ID] "
            + "[" + PREFIX_EMAIL + " EMAIL] "
            + "[" + PREFIX_TELEGRAM_HANDLE + "TELEGRAM_HANDLE] "
            + "[" + PREFIX_TAG + "TAG]...\n\n"

            + "Example 1: " + COMMAND_WORD + " " + PREFIX_NAME + "alice " + PREFIX_TAG + "tutorial-1\n"
            + "Example 2: " + COMMAND_WORD + " " + PREFIX_NAME + "alex\n"
            + "Example 3: " + COMMAND_WORD + " " + PREFIX_STUDENT_ID + "A1";

    private final Collection<Predicate<Student>> predicates;

    public FindCommand(Collection<Predicate<Student>> predicates) {
        this.predicates = predicates;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredStudentList(predicates);
        return new CommandResult(
                String.format(Messages.MESSAGE_STUDENTS_LISTED_OVERVIEW, model.getFilteredStudentList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        Set<Predicate<Student>> thisPredicates = new HashSet<>(predicates);
        Set<Predicate<Student>> otherPredicates = new HashSet<>(otherFindCommand.predicates);
        return thisPredicates.equals(otherPredicates);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicates", predicates)
                .toString();
    }
}
