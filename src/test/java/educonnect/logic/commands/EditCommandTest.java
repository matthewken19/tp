package educonnect.logic.commands;

import static educonnect.logic.Messages.MESSAGE_NO_STUDENT_FOUND;
import static educonnect.logic.commands.CommandTestUtil.DESC_AMY;
import static educonnect.logic.commands.CommandTestUtil.DESC_BOB;
import static educonnect.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static educonnect.logic.commands.CommandTestUtil.VALID_STUDENT_ID_BOB;
import static educonnect.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static educonnect.logic.commands.CommandTestUtil.assertCommandFailure;
import static educonnect.logic.commands.CommandTestUtil.assertCommandSuccess;
import static educonnect.logic.commands.CommandTestUtil.showStudentAtIndex;
import static educonnect.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static educonnect.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static educonnect.testutil.TypicalStudents.getTypicalAddressBook;
import static educonnect.testutil.TypicalTimetableAndValues.VALID_TIMETABLE_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import educonnect.commons.core.index.Index;
import educonnect.logic.Messages;
import educonnect.logic.commands.EditCommand.EditStudentDescriptor;
import educonnect.model.AddressBook;
import educonnect.model.Model;
import educonnect.model.ModelManager;
import educonnect.model.UserPrefs;
import educonnect.model.student.Student;
import educonnect.model.student.predicates.EmailContainsKeywordsPredicate;
import educonnect.model.student.predicates.IdContainsKeywordsPredicate;
import educonnect.model.student.predicates.TelegramContainsKeywordsPredicate;
import educonnect.testutil.EditStudentDescriptorBuilder;
import educonnect.testutil.StudentBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_indexIdentifierAllFieldsSpecifiedUnfilteredList_success() {
        Student editedStudent = new StudentBuilder().build();
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(editedStudent).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_STUDENT, List.of(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_STUDENT_SUCCESS,
                Messages.format(editedStudent));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(model.getFilteredStudentList().get(0), editedStudent);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_indexIdentifierSomeFieldsSpecifiedUnfilteredList_success() {
        Index indexLastStudent = Index.fromOneBased(model.getFilteredStudentList().size());
        Student lastStudent = model.getFilteredStudentList().get(indexLastStudent.getZeroBased());

        StudentBuilder studentInList = new StudentBuilder(lastStudent);
        Student editedStudent = studentInList.withName(VALID_NAME_BOB)
                .withStudentId(VALID_STUDENT_ID_BOB).withTags(VALID_TAG_HUSBAND)
                .withTimetable(VALID_TIMETABLE_1).build();

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB)
                .withStudentId(VALID_STUDENT_ID_BOB).withTags(VALID_TAG_HUSBAND)
                .withTimetable(VALID_TIMETABLE_1).build();
        EditCommand editCommand = new EditCommand(indexLastStudent, List.of(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_STUDENT_SUCCESS,
                Messages.format(editedStudent));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(lastStudent, editedStudent);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_studentIdIdentifierSomeFieldsSpecifiedUnfilteredList_success() {
        // use student ELLE for the identifiers
        Index indexStudent = Index.fromOneBased(1);
        List<Predicate<Student>> studentIdIdentifier = List.of(new IdContainsKeywordsPredicate("A9482224Y"));
        model.updateFilteredStudentList(studentIdIdentifier);
        Student studentToEdit = model.getFilteredStudentList().get(indexStudent.getZeroBased());

        StudentBuilder studentInList = new StudentBuilder(studentToEdit);
        Student editedStudent = studentInList.withName(VALID_NAME_BOB)
            .withStudentId(VALID_STUDENT_ID_BOB).withTags(VALID_TAG_HUSBAND)
            .withTimetable(VALID_TIMETABLE_1).build();

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB)
            .withStudentId(VALID_STUDENT_ID_BOB).withTags(VALID_TAG_HUSBAND)
            .withTimetable(VALID_TIMETABLE_1).build();
        EditCommand editCommand = new EditCommand(indexStudent, studentIdIdentifier, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_STUDENT_SUCCESS,
            Messages.format(editedStudent));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(studentToEdit, editedStudent);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_telegramHandleIdentifierSomeFieldsSpecifiedUnfilteredList_success() {
        // use student ELLE for the identifiers
        Index indexStudent = Index.fromOneBased(1);
        List<Predicate<Student>> telegramHandleIdentifier = List.of(
            new TelegramContainsKeywordsPredicate("@michegan"));
        model.updateFilteredStudentList(telegramHandleIdentifier);
        Student studentToEdit = model.getFilteredStudentList().get(indexStudent.getZeroBased());

        StudentBuilder studentInList = new StudentBuilder(studentToEdit);
        Student editedStudent = studentInList.withName(VALID_NAME_BOB)
            .withStudentId(VALID_STUDENT_ID_BOB).withTags(VALID_TAG_HUSBAND)
            .withTimetable(VALID_TIMETABLE_1).build();

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB)
            .withStudentId(VALID_STUDENT_ID_BOB).withTags(VALID_TAG_HUSBAND)
            .withTimetable(VALID_TIMETABLE_1).build();
        EditCommand editCommand = new EditCommand(indexStudent, telegramHandleIdentifier, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_STUDENT_SUCCESS,
            Messages.format(editedStudent));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(studentToEdit, editedStudent);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_emailIdentifierSomeFieldsSpecifiedList_success() {
        Index indexStudent = Index.fromOneBased(1);
        List<Predicate<Student>> emailIdentifier = List.of(
            new EmailContainsKeywordsPredicate("werner@example.com"));
        model.updateFilteredStudentList(emailIdentifier);
        Student studentToEdit = model.getFilteredStudentList().get(indexStudent.getZeroBased());

        StudentBuilder studentInList = new StudentBuilder(studentToEdit);
        Student editedStudent = studentInList.withName(VALID_NAME_BOB)
            .withStudentId(VALID_STUDENT_ID_BOB).withTags(VALID_TAG_HUSBAND)
            .withTimetable(VALID_TIMETABLE_1).build();

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB)
            .withStudentId(VALID_STUDENT_ID_BOB).withTags(VALID_TAG_HUSBAND)
            .withTimetable(VALID_TIMETABLE_1).build();
        EditCommand editCommand = new EditCommand(indexStudent, emailIdentifier, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_STUDENT_SUCCESS,
            Messages.format(editedStudent));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(studentToEdit, editedStudent);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_indexIdentifierNoFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_STUDENT, List.of(), new EditStudentDescriptor());
        Student editedStudent = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_STUDENT_SUCCESS,
                Messages.format(editedStudent));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_indexIdentifierFilteredList_success() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);

        Student studentInFilteredList = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        Student editedStudent = new StudentBuilder(studentInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_STUDENT, List.of(),
                new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_STUDENT_SUCCESS,
                Messages.format(editedStudent));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(model.getFilteredStudentList().get(0), editedStudent);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateStudentIdUnfilteredList_failure() {
        Student firstStudent = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        Student secondStudent = model.getFilteredStudentList().get(INDEX_SECOND_STUDENT.getZeroBased());

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(secondStudent)
                .withStudentId(firstStudent.getStudentId().value).build();

        EditCommand editCommand = new EditCommand(INDEX_SECOND_STUDENT, List.of(), descriptor);
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_STUDENT_ID);
    }

    @Test
    public void execute_duplicateEmailUnfilteredList_failure() {
        Student firstStudent = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        Student secondStudent = model.getFilteredStudentList().get(INDEX_SECOND_STUDENT.getZeroBased());

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(secondStudent)
                .withEmail(firstStudent.getEmail().value).build();

        EditCommand editCommand = new EditCommand(INDEX_SECOND_STUDENT, List.of(), descriptor);
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_EMAIL);
    }
    @Test
    public void execute_duplicateTelegramHandleUnfilteredList_failure() {
        Student firstStudent = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        Student secondStudent = model.getFilteredStudentList().get(INDEX_SECOND_STUDENT.getZeroBased());

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(secondStudent)
                .withTelegramHandle(firstStudent.getTelegramHandle().value).build();

        EditCommand editCommand = new EditCommand(INDEX_SECOND_STUDENT, List.of(), descriptor);
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_TELEGRAM_HANDLE);
    }

    @Test
    public void execute_invalidStudentIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder()
                .withName(CommandTestUtil.VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, List.of(), descriptor);

        assertCommandFailure(editCommand, model,
            String.format(EditCommand.MESSAGE_INDEX_OUT_OF_BOUNDS, model.getFilteredStudentList().size()));
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidStudentIndexFilteredList_failure() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);
        Index outOfBoundIndex = INDEX_SECOND_STUDENT;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getStudentList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex, List.of(),
                new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model,
            String.format(EditCommand.MESSAGE_INDEX_OUT_OF_BOUNDS, model.getFilteredStudentList().size()));
    }

    @Test
    public void execute_invalidEmailUnfilteredList_failure() {
        Index index = Index.fromOneBased(1);
        List<Predicate<Student>> predicate =
            List.of(new EmailContainsKeywordsPredicate("invalid@invalid.com"));
        model.updateFilteredStudentList(predicate);
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder()
            .withName(CommandTestUtil.VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(index, predicate, descriptor);

        assertCommandFailure(editCommand, model, MESSAGE_NO_STUDENT_FOUND);
    }

    @Test
    public void execute_invalidTelegramHandleUnfilteredList_failure() {
        Index index = Index.fromOneBased(1);
        List<Predicate<Student>> predicate =
            List.of(new TelegramContainsKeywordsPredicate("@invalid"));
        model.updateFilteredStudentList(predicate);
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder()
            .withName(CommandTestUtil.VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(index, predicate, descriptor);

        assertCommandFailure(editCommand, model, MESSAGE_NO_STUDENT_FOUND);
    }

    @Test
    public void execute_invalidStudentIdUnfilteredList_failure() {
        Index index = Index.fromOneBased(1);
        List<Predicate<Student>> predicate =
            List.of(new IdContainsKeywordsPredicate("A9999999A"));
        model.updateFilteredStudentList(predicate);
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder()
            .withName(CommandTestUtil.VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(index, predicate, descriptor);

        assertCommandFailure(editCommand, model, MESSAGE_NO_STUDENT_FOUND);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_STUDENT, List.of(), DESC_AMY);

        // same values -> returns true
        EditStudentDescriptor copyDescriptor = new EditStudentDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_STUDENT, List.of(), copyDescriptor);
        assertEquals(standardCommand, commandWithSameValues);

        // same object -> returns true
        assertEquals(standardCommand, standardCommand);

        // null -> returns false
        assertNotEquals(null, standardCommand);

        // different types -> returns false
        assertNotEquals(standardCommand, 1);

        // different index -> returns false
        assertNotEquals(standardCommand, new EditCommand(INDEX_SECOND_STUDENT, List.of(), DESC_AMY));

        // different descriptor -> returns false
        assertNotEquals(standardCommand, new EditCommand(INDEX_FIRST_STUDENT, List.of(), DESC_BOB));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditStudentDescriptor editStudentDescriptor = new EditStudentDescriptor();
        EditCommand editCommand = new EditCommand(index, List.of(), editStudentDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editStudentDescriptor="
                + editStudentDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }
}
