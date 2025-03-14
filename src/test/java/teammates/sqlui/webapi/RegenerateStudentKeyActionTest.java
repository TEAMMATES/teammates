package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static teammates.ui.webapi.RegenerateStudentKeyAction.SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED;
import static teammates.ui.webapi.RegenerateStudentKeyAction.SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT;
import static teammates.ui.webapi.RegenerateStudentKeyAction.UNSUCCESSFUL_REGENERATION;

import org.apache.http.HttpStatus;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.StudentUpdateException;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.output.RegenerateKeyData;
import teammates.ui.webapi.RegenerateStudentKeyAction;

/**
 * SUT: {@link RegenerateStudentKeyAction}.
 */
public class RegenerateStudentKeyActionTest extends BaseActionTest<RegenerateStudentKeyAction> {

    private Student student;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_KEY;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        Mockito.reset(mockLogic, mockSqlEmailGenerator);

        student = getTypicalStudent();
        EmailWrapper mockEmail = mock(EmailWrapper.class);

        when(mockSqlEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                student.getCourseId(),
                student.getEmail(),
                EmailType.STUDENT_COURSE_LINKS_REGENERATED
        )).thenReturn(mockEmail);
        mockEmailSender.setShouldFail(false);
    }

    @Test
    void testExecute_successfulRegenerationWithEmailSent_success()
            throws EntityDoesNotExistException, StudentUpdateException {
        when(mockLogic.regenerateStudentRegistrationKey(student.getCourseId(), student.getEmail())).thenReturn(student);

        String[] params = {
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        RegenerateStudentKeyAction action = getAction(params);
        RegenerateKeyData actionOutput = (RegenerateKeyData) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).regenerateStudentRegistrationKey(student.getCourseId(), student.getEmail());
        verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionSummaryOfCourse(
                student.getCourseId(),
                student.getEmail(),
                EmailType.STUDENT_COURSE_LINKS_REGENERATED
        );
        verifyNumberOfEmailsSent(1);
        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
        assertEquals(SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT, actionOutput.getMessage());
        assertNotNull(actionOutput.getNewRegistrationKey());
    }

    @Test
    void testExecute_successfulRegenerationWithEmailFailed_success()
            throws EntityDoesNotExistException, StudentUpdateException {
        when(mockLogic.regenerateStudentRegistrationKey(student.getCourseId(), student.getEmail()))
                .thenReturn(student);
        mockEmailSender.setShouldFail(true);

        String[] params = {
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        RegenerateStudentKeyAction action = getAction(params);
        RegenerateKeyData actionOutput = (RegenerateKeyData) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).regenerateStudentRegistrationKey(student.getCourseId(), student.getEmail());
        verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionSummaryOfCourse(
                student.getCourseId(),
                student.getEmail(),
                EmailType.STUDENT_COURSE_LINKS_REGENERATED
        );
        verifyNoEmailsSent();
        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
        assertEquals(SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED, actionOutput.getMessage());
        assertNotNull(actionOutput.getNewRegistrationKey());
    }

    @Test
    void testExecute_nonExistentStudentEmail_throwsEntityNotFoundException()
            throws EntityDoesNotExistException, StudentUpdateException {
        String nonExistentStudentEmail = "RANDOM_EMAIL";

        when(mockLogic.regenerateStudentRegistrationKey(student.getCourseId(), nonExistentStudentEmail))
                .thenThrow(new EntityDoesNotExistException("Student email not found"));

        when(mockLogic.regenerateStudentRegistrationKey(student.getCourseId(), student.getEmail()))
                .thenThrow(new EntityDoesNotExistException("Student email not found"));

        String[] params = {
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, nonExistentStudentEmail,
        };

        verify(mockLogic, never()).regenerateStudentRegistrationKey(any(), any());
        verify(mockSqlEmailGenerator, never()).generateFeedbackSessionSummaryOfCourse(any(), any(), any());
        verifyNoEmailsSent();
        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
        verifyEntityNotFound(params);
    }

    @Test
    void testExecute_nonExistentCourseId_throwsEntityNotFoundException()
            throws EntityDoesNotExistException, StudentUpdateException {
        String nonExistentCourseId = "RANDOM_COURSE";

        when(mockLogic.regenerateStudentRegistrationKey(nonExistentCourseId, student.getEmail()))
                .thenThrow(new EntityDoesNotExistException("Course id not found"));

        String[] params = {
                Const.ParamsNames.COURSE_ID, nonExistentCourseId,
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verify(mockLogic, never()).regenerateStudentRegistrationKey(any(), any());
        verify(mockSqlEmailGenerator, never()).generateFeedbackSessionSummaryOfCourse(any(), any(), any());
        verifyNoEmailsSent();
        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
        verifyEntityNotFound(params);
    }

    @Test
    void testExecute_studentUpdateException_failure()
            throws EntityDoesNotExistException, StudentUpdateException {
        when(mockLogic.regenerateStudentRegistrationKey(student.getCourseId(), student.getEmail()))
                .thenThrow(new StudentUpdateException("Student update failed"));

        String[] params = {
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        RegenerateStudentKeyAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action, HttpStatus.SC_INTERNAL_SERVER_ERROR).getOutput();

        verify(mockLogic, times(1)).regenerateStudentRegistrationKey(student.getCourseId(), student.getEmail());
        verify(mockSqlEmailGenerator, never()).generateFeedbackSessionSummaryOfCourse(any(), any(), any());
        verifyNoEmailsSent();
        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
        assertEquals(UNSUCCESSFUL_REGENERATION, actionOutput.getMessage());
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_missingStudentEmail_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingCourseId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testSpecificAccessControl_admin_canAccess() {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructor_cannotAccess() {
        loginAsInstructor("instructor-googleId");

        String[] params = {
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        loginAsStudent("student-googleId");

        String[] params = {
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();

        String[] params = {
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyCannotAccess(params);
    }
}
