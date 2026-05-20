package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import java.util.UUID;

import org.apache.http.HttpStatus;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.StudentUpdateException;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.output.RegenerateKeyData;

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
        Mockito.reset(mockLogic, mockEmailGenerator);

        student = getTypicalStudent();
        EmailWrapper mockEmail = mock(EmailWrapper.class);

        when(mockEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                student.getCourseId(),
                student.getEmail(),
                EmailType.STUDENT_COURSE_LINKS_REGENERATED
        )).thenReturn(mockEmail);
        mockEmailSender.setShouldFail(false);
    }

    @Test
    void testExecute_successfulRegenerationWithEmailSent_success()
            throws EntityDoesNotExistException, StudentUpdateException {
        when(mockLogic.regenerateStudentRegistrationKey(student.getId())).thenReturn(student);

        String[] params = {
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };

        RegenerateStudentKeyAction action = getAction(params);
        RegenerateKeyData actionOutput = (RegenerateKeyData) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).regenerateStudentRegistrationKey(student.getId());
        verify(mockEmailGenerator, times(1)).generateFeedbackSessionSummaryOfCourse(
                student.getCourseId(),
                student.getEmail(),
                EmailType.STUDENT_COURSE_LINKS_REGENERATED
        );
        verifyNumberOfEmailsSent(1);
        verifyNoMoreInteractions(mockLogic, mockEmailGenerator);
        assertEquals(SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT, actionOutput.getMessage());
        assertNotNull(actionOutput.getNewRegistrationKey());
    }

    @Test
    void testExecute_successfulRegenerationWithEmailFailed_success()
            throws EntityDoesNotExistException, StudentUpdateException {
        when(mockLogic.regenerateStudentRegistrationKey(student.getId())).thenReturn(student);
        mockEmailSender.setShouldFail(true);

        String[] params = {
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };

        RegenerateStudentKeyAction action = getAction(params);
        RegenerateKeyData actionOutput = (RegenerateKeyData) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).regenerateStudentRegistrationKey(student.getId());
        verify(mockEmailGenerator, times(1)).generateFeedbackSessionSummaryOfCourse(
                student.getCourseId(),
                student.getEmail(),
                EmailType.STUDENT_COURSE_LINKS_REGENERATED
        );
        verifyNoEmailsSent();
        verifyNoMoreInteractions(mockLogic, mockEmailGenerator);
        assertEquals(SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED, actionOutput.getMessage());
        assertNotNull(actionOutput.getNewRegistrationKey());
    }

    @Test
    void testExecute_nonExistentStudentId_throwsEntityNotFoundException()
            throws EntityDoesNotExistException, StudentUpdateException {
        UUID nonExistentStudentId = UUID.randomUUID();

        when(mockLogic.regenerateStudentRegistrationKey(nonExistentStudentId))
                .thenThrow(new EntityDoesNotExistException("Student ID not found"));

        String[] params = {
                Const.ParamsNames.USER_ID, nonExistentStudentId.toString(),
        };

        verify(mockLogic, never()).regenerateStudentRegistrationKey(any(UUID.class));
        verify(mockEmailGenerator, never()).generateFeedbackSessionSummaryOfCourse(any(), any(), any());
        verifyNoEmailsSent();
        verifyNoMoreInteractions(mockLogic, mockEmailGenerator);
        verifyEntityNotFound(params);
    }

    @Test
    void testExecute_studentUpdateException_failure()
            throws EntityDoesNotExistException, StudentUpdateException {
        when(mockLogic.regenerateStudentRegistrationKey(student.getId()))
                .thenThrow(new StudentUpdateException("Student update failed"));

        String[] params = {
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };

        RegenerateStudentKeyAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action, HttpStatus.SC_INTERNAL_SERVER_ERROR).getOutput();

        verify(mockLogic, times(1)).regenerateStudentRegistrationKey(student.getId());
        verify(mockEmailGenerator, never()).generateFeedbackSessionSummaryOfCourse(any(), any(), any());
        verifyNoEmailsSent();
        verifyNoMoreInteractions(mockLogic, mockEmailGenerator);
        assertEquals(UNSUCCESSFUL_REGENERATION, actionOutput.getMessage());
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testAccessControl() {
        String[] params = {
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };
        verifyOnlyAdminsCanAccess(params);
    }
}
