package educonnect.logic;

import static educonnect.logic.parser.CliSyntax.PREFIX_EMAIL;
import static educonnect.logic.parser.CliSyntax.PREFIX_LINK;
import static educonnect.logic.parser.CliSyntax.PREFIX_NAME;
import static educonnect.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static educonnect.logic.parser.CliSyntax.PREFIX_TELEGRAM_HANDLE;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import educonnect.logic.parser.Prefix;
import educonnect.model.student.Student;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n\n%1$s";
    public static final String MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX = "The student index provided is invalid";
    public static final String MESSAGE_STUDENTS_LISTED_OVERVIEW = "%1$d student(s) listed!";
    public static final String MESSAGE_STUDENT_EMAIL_COPIED_OVERVIEW = "%1$d students' email(s) copied!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_NO_STUDENT_FOUND = "No student found.";
    public static final String MESSAGE_EMPTY_PREFIX_ARGUMENT =
            " The [%s] argument cannot be empty! Please provide a valid value for [%s].";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    public static String getErrorMessageForEmptyArguments(Prefix prefix) {
        String prefixMessage;
        if (prefix.equals(PREFIX_NAME)) {
            prefixMessage = "NAME";
        } else if (prefix.equals(PREFIX_EMAIL)) {
            prefixMessage = "EMAIL";
        } else if (prefix.equals(PREFIX_STUDENT_ID)) {
            prefixMessage = "STUDENT ID";
        } else if (prefix.equals(PREFIX_TELEGRAM_HANDLE)) {
            prefixMessage = "TELEGRAM HANDLE";
        } else if (prefix.equals(PREFIX_LINK)) {
            prefixMessage = "LINK";
        } else {
            throw new AssertionError();
        }
        return String.format(MESSAGE_EMPTY_PREFIX_ARGUMENT, prefixMessage, prefixMessage);
    }

    /**
     * Formats the {@code student} for display to the user.
     */
    public static String format(Student student) {
        final StringBuilder builder = new StringBuilder();
        builder.append(student.getName())
                .append(";\nStudent ID: ")
                .append(student.getStudentId())
                .append(";\nEmail: ")
                .append(student.getEmail())
                .append(";\nTelegram Handle: ")
                .append(student.getTelegramHandle())
                .append(";\nLink: ")
                .append(student.getLink().map(link -> link.url).orElse(""))
                .append(";\nTags: ");
        student.getTags().forEach(builder::append);
        builder.append(";\n").append(student.getTimetable());
        return builder.toString();
    }

    /**
     * Formats the {@code student} and displays the student's name and email.
     */
    public static String formatNameAndEmail(Student student) {
        final StringBuilder builder = new StringBuilder();
        builder.append(student.getName())
                .append("\nEmail: ")
                .append(student.getEmail());
        return builder.toString();
    }
}
