package educonnect.model.student.timetable.exceptions;

public class InvalidDurationException extends IllegalArgumentException {
    private static final String MESSAGE = "Duration cannot be longer than 24 hours or shorter than 1 hour!";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
