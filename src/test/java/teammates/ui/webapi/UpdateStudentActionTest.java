package teammates.ui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static teammates.ui.webapi.UpdateStudentAction.SUCCESSFUL_UPDATE_BUT_EMAIL_FAILED;
import static teammates.ui.webapi.UpdateStudentAction.SUCCESSFUL_UPDATE_WITH_EMAIL;

import java.util.UUID;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.StudentUpdateRequest;

/**
 * SUT: {@link UpdateStudentAction}.
 */
public class UpdateStudentActionTest extends BaseActionTest<UpdateStudentAction> {

    private Student student;
    private String newName = "new-student-name";
    private String newEmail = "new-validstudent@teammates.tmt";
    private Course course;
    private Section section;
    private Team team;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @BeforeMethod
    void setUp() {
        Mockito.reset(mockLogic, mockEmailGenerator);

        student = getTypicalStudent();
        course = getTypicalCourse();
        section = getTypicalSection();
        team = getTypicalTeam();

        when(mockLogic.getStudent(student.getId())).thenReturn(student);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
    }

    @Test
    void testExecute_isSessionSummarySendEmailTrue_successWithEmailSent()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException, EnrollException {
        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, newEmail, team.getName(),
                section.getName(), student.getComments(), true);

        Student updatedStudent = new Student(course, newName, newEmail, student.getComments());
        team.addUser(updatedStudent);
        updatedStudent.setId(student.getId());
        when(mockLogic.updateStudent(eq(student.getId()), any())).thenReturn(updatedStudent);

        EmailWrapper mockEmail = mock(EmailWrapper.class);
        when(mockEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                course.getId(),
                updatedStudent.getEmail(),
                EmailType.STUDENT_EMAIL_CHANGED
        )).thenReturn(mockEmail);
        mockEmailSender.setShouldFail(false);

        String[] params = {
                Const.ParamsNames.STUDENT_SQL_ID, student.getId().toString(),
        };

        UpdateStudentAction action = getAction(studentUpdateRequest, params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).updateStudent(eq(student.getId()), any());

        verify(mockEmailGenerator, times(1)).generateFeedbackSessionSummaryOfCourse(
                course.getId(),
                updatedStudent.getEmail(),
                EmailType.STUDENT_EMAIL_CHANGED
        );
        verifyNumberOfEmailsSent(1);
        assertEquals(SUCCESSFUL_UPDATE_WITH_EMAIL, actionOutput.getMessage());
    }

    @Test
    void testExecute_isSessionSummarySendEmailTrue_successWithEmailFailedToSend()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException, EnrollException {
        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, newEmail, team.getName(),
                section.getName(), student.getComments(), true);

        Student updatedStudent = new Student(course, newName, newEmail, student.getComments());
        team.addUser(updatedStudent);
        updatedStudent.setId(student.getId());
        when(mockLogic.updateStudent(eq(student.getId()), any())).thenReturn(updatedStudent);

        EmailWrapper mockEmail = mock(EmailWrapper.class);
        when(mockEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                course.getId(),
                updatedStudent.getEmail(),
                EmailType.STUDENT_EMAIL_CHANGED
        )).thenReturn(mockEmail);
        mockEmailSender.setShouldFail(true);

        String[] params = {
                Const.ParamsNames.STUDENT_SQL_ID, student.getId().toString(),
        };

        UpdateStudentAction action = getAction(studentUpdateRequest, params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).updateStudent(eq(student.getId()), any());

        verify(mockEmailGenerator, times(1)).generateFeedbackSessionSummaryOfCourse(
                course.getId(),
                updatedStudent.getEmail(),
                EmailType.STUDENT_EMAIL_CHANGED
        );

        verifyNoEmailsSent();
        assertEquals(SUCCESSFUL_UPDATE_BUT_EMAIL_FAILED, actionOutput.getMessage());
    }

    @Test
    void testExecute_nonExistentStudentEmail_throwsEntityNotFoundException() {
        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, newEmail, team.getName(),
                section.getName(), student.getComments(), true);

        String[] params = {
                Const.ParamsNames.STUDENT_SQL_ID, UUID.randomUUID().toString(),
        };

        EntityNotFoundException enfe = verifyEntityNotFound(studentUpdateRequest, params);
        assertEquals(UpdateStudentAction.STUDENT_NOT_FOUND_FOR_EDIT, enfe.getMessage());

        verify(mockLogic, times(1)).getStudent(any());
        verifyNoTasksAdded();
        verifyNoEmailsSent();
        verifyNoMoreInteractions(mockLogic, mockEmailGenerator);
    }

    @Test
    void testExecute_invalidUpdateAttributes_throwsInvalidHttpRequestBodyException()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException, EnrollException {
        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, newEmail, team.getName(),
                section.getName(), student.getComments(), true);

        Student updatedStudent = new Student(course, newName, newEmail, student.getComments());
        team.addUser(updatedStudent);
        updatedStudent.setId(student.getId());
        when(mockLogic.updateStudent(eq(student.getId()), any())).thenThrow(InvalidParametersException.class);

        String[] params = {
                Const.ParamsNames.STUDENT_SQL_ID, student.getId().toString(),
        };

        verifyHttpRequestBodyFailure(studentUpdateRequest, params);

        verifyNoTasksAdded();
        verifyNoEmailsSent();
    }

    @Test
    void testExecute_whitespaceOnlyTeamName_throwsInvalidHttpRequestBodyException() {
        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, newEmail, "   ",
                section.getName(), student.getComments(), true);

        String[] params = {
                Const.ParamsNames.STUDENT_SQL_ID, student.getId().toString(),
        };

        verifyHttpRequestBodyFailure(studentUpdateRequest, params);
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testSpecificAccessControl_nonExistentInstructorId_cannotAccess() {
        String nonExistentInstructorId = "RANDOM_ID";
        when(mockLogic.getInstructorByGoogleId(course.getId(), nonExistentInstructorId)).thenReturn(null);
        when(mockLogic.getStudent(student.getId())).thenReturn(student);
        loginAsInstructor(nonExistentInstructorId);

        String[] params = {
                Const.ParamsNames.STUDENT_SQL_ID, student.getId().toString(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithPermission_canAccess() {
        String instructorId = "instructor-googleId";
        // Instructor with co-owner role can modify student
        Instructor instructor = getTypicalInstructor();
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorId)).thenReturn(instructor);
        when(mockLogic.getStudent(student.getId())).thenReturn(student);
        loginAsInstructor(instructorId);

        String[] params = {
                Const.ParamsNames.STUDENT_SQL_ID, student.getId().toString(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithoutPermission_cannotAccess() {
        String instructorId = "instructor-googleId";
        // Instructor with observer role cannot modify student
        Instructor instructor = getTypicalInstructor();
        InstructorPrivileges instructorPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER);
        instructor.setPrivileges(instructorPrivileges);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorId)).thenReturn(instructor);
        when(mockLogic.getStudent(student.getId())).thenReturn(student);
        loginAsInstructor(instructorId);

        String[] params = {
                Const.ParamsNames.STUDENT_SQL_ID, student.getId().toString(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        loginAsStudent("student-googleId");

        String[] params = {
                Const.ParamsNames.STUDENT_SQL_ID, student.getId().toString(),
        };
        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(null);

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();

        String[] params = {
                Const.ParamsNames.STUDENT_SQL_ID, student.getId().toString(),
        };

        verifyCannotAccess(params);
        verifyNoMoreInteractions(mockLogic, mockEmailGenerator);
    }

    @Test
    void testSpecificAccessControl_unregistered_cannotAccess() {
        loginAsUnregistered("instructor-googleId");

        String[] params = {
                Const.ParamsNames.STUDENT_SQL_ID, student.getId().toString(),
        };
        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(null);

        verifyCannotAccess(params);
    }
}
