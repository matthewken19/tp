package educonnect.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import educonnect.logic.Messages;
import educonnect.model.Model;
import educonnect.model.ModelManager;
import educonnect.model.UserPrefs;
import educonnect.model.student.Student;
import educonnect.model.student.Tag;
import educonnect.model.student.predicates.NameContainsKeywordsPredicate;
import educonnect.model.student.predicates.TagContainsKeywordsPredicate;
import educonnect.testutil.TypicalStudents;
import javafx.collections.ObservableList;

/**
 * Contains integration tests (interaction with the Model) for {@code CopyCommand}.
 */
public class CopyCommandTest {
    private Model model = new ModelManager(TypicalStudents.getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(TypicalStudents.getTypicalAddressBook(), new UserPrefs());

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void equals() {
        TagContainsKeywordsPredicate firstPredicate =
                new TagContainsKeywordsPredicate(new Tag("first"));
        TagContainsKeywordsPredicate secondPredicate =
                new TagContainsKeywordsPredicate(new Tag("second"));

        CopyCommand copyFirstCommand = new CopyCommand(List.of(firstPredicate));
        CopyCommand copySecondCommand = new CopyCommand(List.of(secondPredicate));

        // same object -> returns true
        assertTrue(copyFirstCommand.equals(copyFirstCommand));

        // same values -> returns true
        CopyCommand findFirstCommandCopy = new CopyCommand(List.of(firstPredicate));
        assertTrue(copyFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(copyFirstCommand.equals(1));

        // null -> returns false
        assertFalse(copyFirstCommand.equals(null));

        // different student -> returns false
        assertFalse(copyFirstCommand.equals(copySecondCommand));
    }

    @Test
    public void execute_zeroKeywords_allEmailsCopied() {
        expectedModel.updateWithAllStudents();
        ObservableList<Student> students = expectedModel.getFilteredStudentList();

        String expectedMessage = String.format(Messages.MESSAGE_STUDENT_EMAIL_COPIED_OVERVIEW,
                students.size());
        CopyCommand command = new CopyCommand(Collections.emptyList(), false);
        CommandTestUtil.assertCommandSuccess(command, model, expectedMessage.toString(), expectedModel);
    }

    @Test
    public void execute_oneTag_multipleEmailsCopied() {
        TagContainsKeywordsPredicate predicate = preparePredicate("tutorial-2");
        expectedModel.updateFilteredStudentList(List.of(predicate));
        ObservableList<Student> students = expectedModel.getFilteredStudentList();

        String expectedMessage = String.format(Messages.MESSAGE_STUDENT_EMAIL_COPIED_OVERVIEW, students.size());
        CopyCommand command = new CopyCommand(List.of(predicate), false);
        CommandTestUtil.assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(TypicalStudents.BENSON, TypicalStudents.FIONA,
                TypicalStudents.GEORGE), model.getFilteredStudentList());
    }

    @Test
    public void toStringMethod() {
        List<Predicate<Student>> predicates = List.of(new NameContainsKeywordsPredicate("keyword"));
        CopyCommand copyCommand = new CopyCommand(predicates);
        String expected = CopyCommand.class.getCanonicalName() + "{predicates=" + predicates + "}";
        assertEquals(expected, copyCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code TagContainsKeywordsPredicate}.
     */
    private TagContainsKeywordsPredicate preparePredicate(String userInput) {
        return new TagContainsKeywordsPredicate(new Tag(userInput));
    }
}
