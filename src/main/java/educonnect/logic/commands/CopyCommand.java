package educonnect.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Predicate;

import educonnect.commons.util.ToStringBuilder;
import educonnect.logic.Messages;
import educonnect.logic.commands.exceptions.CommandException;
import educonnect.model.Model;
import educonnect.model.student.Student;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * Copies all student emails whose tags match all argument keywords to the user's clipboard.
 */
public class CopyCommand extends Command {

    public static final String COMMAND_WORD = "copy";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Copies all student emails whose tags match "
            + "the specified tags into the clipboard.\n"
            + "Parameters: [t/TAG]...\n\n"
            + "Example 1: " + COMMAND_WORD + "\n"
            + "Example 2: " + COMMAND_WORD + " t/tutorial-1";

    private final Collection<Predicate<Student>> predicates;

    public CopyCommand(Collection<Predicate<Student>> predicates) {
        this.predicates = predicates;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        model.updateFilteredStudentList(predicates);
        ObservableList<Student> filteredStudents = model.getFilteredStudentList();
        if (filteredStudents.isEmpty()) {
            throw new CommandException(Messages.MESSAGE_NO_STUDENT_FOUND);
        }

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        StringJoiner emails = new StringJoiner(", ");
        filteredStudents.forEach(s -> emails.add(s.getEmail().value));
        content.putString(emails.toString());
        clipboard.setContent(content);

        String response = String.format(Messages.MESSAGE_STUDENT_EMAIL_COPIED_OVERVIEW,
                filteredStudents.size());
        return new CommandResult(response.toString());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CopyCommand)) {
            return false;
        }

        CopyCommand otherCopyCommand = (CopyCommand) other;
        Set<Predicate<Student>> thisPredicates = new HashSet<>(predicates);
        Set<Predicate<Student>> otherPredicates = new HashSet<>(otherCopyCommand.predicates);
        return thisPredicates.equals(otherPredicates);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicates", predicates)
                .toString();
    }
}
