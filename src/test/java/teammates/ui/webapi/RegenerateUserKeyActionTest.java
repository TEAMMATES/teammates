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
import static teammates.ui.webapi.RegenerateUserKeyAction.SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED;
import static teammates.ui.webapi.RegenerateUserKeyAction.SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT;
import static teammates.ui.webapi.RegenerateUserKeyAction.UNSUCCESSFUL_REGENERATION;

import java.util.UUID;

import org.apache.http.HttpStatus;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UserUpdateException;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.User;
import teammates.ui.output.MessageOutput;
import teammates.ui.output.RegenerateKeyData;

/**
 * SUT: {@link RegenerateUserKeyAction}.
 */
public class RegenerateUserKeyActionTest extends BaseActionTest<RegenerateUserKeyAction> {

    private User student;
    private User instructor;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.USER_KEY;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        Mockito.reset(mockLogic, mockEmailGenerator);

        student = getTypicalStudent();
        instructor = getTypicalInstructor();
        mockEmailSender.setShouldFail(false);
    }

    @Test
    void testExecute_studentSuccessfulRegenerationWithEmailSent_success()
            throws EntityDoesNotExistException, UserUpdateException {
        setUpEmailGenerationForUser(student, EmailType.STUDENT_COURSE_LINKS_REGENERATED);
        when(mockLogic.regenerateUserRegistrationKey(student.getId())).thenReturn(student);

        String[] params = {
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };

        RegenerateUserKeyAction action = getAction(params);
        RegenerateKeyData actionOutput = (RegenerateKeyData) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).regenerateUserRegistrationKey(student.getId());
        verifyEmailGeneratedForUser(student, EmailType.STUDENT_COURSE_LINKS_REGENERATED);
        verifyNumberOfEmailsSent(1);
        verifyNoMoreInteractions(mockLogic, mockEmailGenerator);
        assertEquals(SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT, actionOutput.getMessage());
        assertNotNull(actionOutput.getNewRegistrationKey());
    }

    @Test
    void testExecute_instructorSuccessfulRegenerationWithEmailSent_success()
            throws EntityDoesNotExistException, UserUpdateException {
        setUpEmailGenerationForUser(instructor, EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED);
        when(mockLogic.regenerateUserRegistrationKey(instructor.getId())).thenReturn(instructor);

        String[] params = {
                Const.ParamsNames.USER_ID, instructor.getId().toString(),
        };

        RegenerateUserKeyAction action = getAction(params);
        RegenerateKeyData actionOutput = (RegenerateKeyData) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).regenerateUserRegistrationKey(instructor.getId());
        verifyEmailGeneratedForUser(instructor, EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED);
        verifyNumberOfEmailsSent(1);
        verifyNoMoreInteractions(mockLogic, mockEmailGenerator);
        assertEquals(SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT, actionOutput.getMessage());
        assertNotNull(actionOutput.getNewRegistrationKey());
    }

    @Test
    void testExecute_successfulRegenerationWithEmailFailed_success()
            throws EntityDoesNotExistException, UserUpdateException {
        setUpEmailGenerationForUser(student, EmailType.STUDENT_COURSE_LINKS_REGENERATED);
        when(mockLogic.regenerateUserRegistrationKey(student.getId())).thenReturn(student);
        mockEmailSender.setShouldFail(true);

        String[] params = {
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };

        RegenerateUserKeyAction action = getAction(params);
        RegenerateKeyData actionOutput = (RegenerateKeyData) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).regenerateUserRegistrationKey(student.getId());
        verifyEmailGeneratedForUser(student, EmailType.STUDENT_COURSE_LINKS_REGENERATED);
        verifyNoEmailsSent();
        verifyNoMoreInteractions(mockLogic, mockEmailGenerator);
        assertEquals(SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED, actionOutput.getMessage());
        assertNotNull(actionOutput.getNewRegistrationKey());
    }

    @Test
    void testExecute_nonExistentUserId_throwsEntityNotFoundException()
            throws EntityDoesNotExistException, UserUpdateException {
        UUID nonExistentUserId = UUID.randomUUID();

        when(mockLogic.regenerateUserRegistrationKey(nonExistentUserId))
                .thenThrow(new EntityDoesNotExistException("User ID not found"));

        String[] params = {
                Const.ParamsNames.USER_ID, nonExistentUserId.toString(),
        };

        verify(mockLogic, never()).regenerateUserRegistrationKey(any(UUID.class));
        verify(mockEmailGenerator, never()).generateFeedbackSessionSummaryOfCourse(any(), any(), any());
        verifyNoEmailsSent();
        verifyNoMoreInteractions(mockLogic, mockEmailGenerator);
        verifyEntityNotFound(params);
    }

    @Test
    void testExecute_userUpdateException_failure()
            throws EntityDoesNotExistException, UserUpdateException {
        when(mockLogic.regenerateUserRegistrationKey(student.getId()))
                .thenThrow(new UserUpdateException("User update failed"));

        String[] params = {
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };

        RegenerateUserKeyAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action, HttpStatus.SC_INTERNAL_SERVER_ERROR).getOutput();

        verify(mockLogic, times(1)).regenerateUserRegistrationKey(student.getId());
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

    private void setUpEmailGenerationForUser(User user, EmailType emailType) {
        EmailWrapper mockEmail = mock(EmailWrapper.class);

        when(mockEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                user.getCourseId(),
                user.getEmail(),
                emailType
        )).thenReturn(mockEmail);
    }

    private void verifyEmailGeneratedForUser(User user, EmailType emailType) {
        verify(mockEmailGenerator, times(1)).generateFeedbackSessionSummaryOfCourse(
                user.getCourseId(),
                user.getEmail(),
                emailType
        );
    }
}
