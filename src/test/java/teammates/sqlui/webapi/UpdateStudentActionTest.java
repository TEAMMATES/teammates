package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static teammates.ui.webapi.UpdateStudentAction.ERROR_EMAIL_ALREADY_EXISTS;
import static teammates.ui.webapi.UpdateStudentAction.SUCCESSFUL_UPDATE;
import static teammates.ui.webapi.UpdateStudentAction.SUCCESSFUL_UPDATE_BUT_EMAIL_FAILED;
import static teammates.ui.webapi.UpdateStudentAction.SUCCESSFUL_UPDATE_WITH_EMAIL;

import org.mockito.ArgumentCaptor;
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
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.StudentUpdateRequest;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.UpdateStudentAction;

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
        Mockito.reset(mockLogic, mockSqlEmailGenerator);

        student = getTypicalStudent();
        course = getTypicalCourse();
        section = getTypicalSection();
        team = getTypicalTeam();

        when(mockLogic.getStudentForEmail(course.getId(), student.getEmail())).thenReturn(student);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getSectionOrCreate(course.getId(), section.getName())).thenReturn(section);
        when(mockLogic.getTeamOrCreate(section, team.getName())).thenReturn(team);
    }

    private void verifyStudentToUpdate(Student expectedStudentToUpdate, Student studentToUpdate) {
        assertEquals(expectedStudentToUpdate.getCourse(), studentToUpdate.getCourse());
        assertEquals(expectedStudentToUpdate.getName(), studentToUpdate.getName());
        assertEquals(expectedStudentToUpdate.getEmail(), studentToUpdate.getEmail());
        assertEquals(expectedStudentToUpdate.getSectionName(), studentToUpdate.getSectionName());
        assertEquals(expectedStudentToUpdate.getTeamName(), studentToUpdate.getTeamName());
        assertEquals(expectedStudentToUpdate.getComments(), studentToUpdate.getComments());
        assertEquals(expectedStudentToUpdate.getId(), studentToUpdate.getId());
    }

    @Test
    void testExecute_emailChangedAndIsSessionSummarySendEmailTrue_successWithEmailSent()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException, EnrollException {
        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, newEmail, team.getName(),
                section.getName(), student.getComments(), true);

        Student updatedStudent = new Student(course, newName, newEmail, student.getComments(), team);
        updatedStudent.setId(student.getId());
        when(mockLogic.updateStudentCascade(any(Student.class))).thenReturn(updatedStudent);

        EmailWrapper mockEmail = mock(EmailWrapper.class);
        when(mockSqlEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                course.getId(),
                updatedStudent.getEmail(),
                EmailType.STUDENT_EMAIL_CHANGED
        )).thenReturn(mockEmail);
        mockEmailSender.setShouldFail(false);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        UpdateStudentAction action = getAction(studentUpdateRequest, params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).getStudentForEmail(course.getId(), student.getEmail());
        verify(mockLogic, times(1)).getCourse(course.getId());
        verify(mockLogic, times(1)).getSectionOrCreate(course.getId(), studentUpdateRequest.getSection());
        verify(mockLogic, times(1)).getTeamOrCreate(section, studentUpdateRequest.getTeam());

        verify(mockLogic, times(1)).validateSectionsAndTeams(anyList(), eq(course.getId()));
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(mockLogic, times(1)).updateStudentCascade(studentCaptor.capture());
        Student studentToUpdate = studentCaptor.getValue();
        verifyStudentToUpdate(updatedStudent, studentToUpdate);
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionSummaryOfCourse(
                course.getId(),
                updatedStudent.getEmail(),
                EmailType.STUDENT_EMAIL_CHANGED
        );
        verifyNumberOfEmailsSent(1);

        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
        assertEquals(SUCCESSFUL_UPDATE_WITH_EMAIL, actionOutput.getMessage());
    }

    @Test
    void testExecute_emailChangedAndIsSessionSummarySendEmailTrue_successWithEmailFailed()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException, EnrollException {
        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, newEmail, team.getName(),
                section.getName(), student.getComments(), true);

        Student updatedStudent = new Student(course, newName, newEmail, student.getComments(), team);
        updatedStudent.setId(student.getId());
        when(mockLogic.updateStudentCascade(any(Student.class))).thenReturn(updatedStudent);

        EmailWrapper mockEmail = mock(EmailWrapper.class);
        when(mockSqlEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                course.getId(),
                updatedStudent.getEmail(),
                EmailType.STUDENT_EMAIL_CHANGED
        )).thenReturn(mockEmail);
        mockEmailSender.setShouldFail(true);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        UpdateStudentAction action = getAction(studentUpdateRequest, params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).getStudentForEmail(course.getId(), student.getEmail());
        verify(mockLogic, times(1)).getCourse(course.getId());
        verify(mockLogic, times(1)).getSectionOrCreate(course.getId(), studentUpdateRequest.getSection());
        verify(mockLogic, times(1)).getTeamOrCreate(section, studentUpdateRequest.getTeam());

        verify(mockLogic, times(1)).validateSectionsAndTeams(anyList(), eq(course.getId()));
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(mockLogic, times(1)).updateStudentCascade(studentCaptor.capture());
        Student studentToUpdate = studentCaptor.getValue();
        verifyStudentToUpdate(updatedStudent, studentToUpdate);
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionSummaryOfCourse(
                course.getId(),
                updatedStudent.getEmail(),
                EmailType.STUDENT_EMAIL_CHANGED
        );
        verifyNoEmailsSent();

        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
        assertEquals(SUCCESSFUL_UPDATE_BUT_EMAIL_FAILED, actionOutput.getMessage());
    }

    @Test
    void testExecute_emailChangedAndIsSessionSummarySendEmailFalse_successWithNoEmailSent()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException, EnrollException {
        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, newEmail, team.getName(),
                section.getName(), student.getComments(), false);

        Student updatedStudent = new Student(course, newName, newEmail, student.getComments(), team);
        updatedStudent.setId(student.getId());
        when(mockLogic.updateStudentCascade(any(Student.class))).thenReturn(updatedStudent);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        UpdateStudentAction action = getAction(studentUpdateRequest, params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).getStudentForEmail(course.getId(), student.getEmail());
        verify(mockLogic, times(1)).getCourse(course.getId());
        verify(mockLogic, times(1)).getSectionOrCreate(course.getId(), studentUpdateRequest.getSection());
        verify(mockLogic, times(1)).getTeamOrCreate(section, studentUpdateRequest.getTeam());

        verify(mockLogic, times(1)).validateSectionsAndTeams(anyList(), eq(course.getId()));
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(mockLogic, times(1)).updateStudentCascade(studentCaptor.capture());
        Student studentToUpdate = studentCaptor.getValue();
        verifyStudentToUpdate(updatedStudent, studentToUpdate);
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        verifyNoEmailsSent();

        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
        assertEquals(SUCCESSFUL_UPDATE, actionOutput.getMessage());
    }

    @Test
    void testExecute_emailNotChangedAndIsSessionSummarySendEmailTrue_successWithNoEmailSent()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException, EnrollException {
        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, student.getEmail(), team.getName(),
                section.getName(), student.getComments(), true);

        Student updatedStudent = new Student(course, newName, student.getEmail(), student.getComments(), team);
        updatedStudent.setId(student.getId());
        when(mockLogic.updateStudentCascade(any(Student.class))).thenReturn(updatedStudent);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        UpdateStudentAction action = getAction(studentUpdateRequest, params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).getStudentForEmail(course.getId(), student.getEmail());
        verify(mockLogic, times(1)).getCourse(course.getId());
        verify(mockLogic, times(1)).getSectionOrCreate(course.getId(), studentUpdateRequest.getSection());
        verify(mockLogic, times(1)).getTeamOrCreate(section, studentUpdateRequest.getTeam());

        verify(mockLogic, times(1)).validateSectionsAndTeams(anyList(), eq(course.getId()));
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(mockLogic, times(1)).updateStudentCascade(studentCaptor.capture());
        Student studentToUpdate = studentCaptor.getValue();
        verifyStudentToUpdate(updatedStudent, studentToUpdate);
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        verifyNoEmailsSent();

        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
        assertEquals(SUCCESSFUL_UPDATE, actionOutput.getMessage());
    }

    @Test
    void testExecute_emailNotChangedAndIsSessionSummarySendEmailFalse_successWithNoEmailSent()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException, EnrollException {
        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, student.getEmail(), team.getName(),
                section.getName(), student.getComments(), false);

        Student updatedStudent = new Student(course, newName, student.getEmail(), student.getComments(), team);
        updatedStudent.setId(student.getId());
        when(mockLogic.updateStudentCascade(any(Student.class))).thenReturn(updatedStudent);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        UpdateStudentAction action = getAction(studentUpdateRequest, params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).getStudentForEmail(course.getId(), student.getEmail());
        verify(mockLogic, times(1)).getCourse(course.getId());
        verify(mockLogic, times(1)).getSectionOrCreate(course.getId(), studentUpdateRequest.getSection());
        verify(mockLogic, times(1)).getTeamOrCreate(section, studentUpdateRequest.getTeam());

        verify(mockLogic, times(1)).validateSectionsAndTeams(anyList(), eq(course.getId()));
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(mockLogic, times(1)).updateStudentCascade(studentCaptor.capture());
        Student studentToUpdate = studentCaptor.getValue();
        verifyStudentToUpdate(updatedStudent, studentToUpdate);
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        verifyNoEmailsSent();

        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
        assertEquals(SUCCESSFUL_UPDATE, actionOutput.getMessage());
    }

    @Test
    void testExecute_nonExistentStudentEmail_throwsEntityNotFoundException() {
        String nonExistentStudentEmail = "RANDOM_EMAIL";

        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, newEmail, team.getName(),
                section.getName(), student.getComments(), true);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, nonExistentStudentEmail,
        };

        EntityNotFoundException enfe = verifyEntityNotFound(studentUpdateRequest, params);
        assertEquals(UpdateStudentAction.STUDENT_NOT_FOUND_FOR_EDIT, enfe.getMessage());

        verify(mockLogic, times(1)).getStudentForEmail(course.getId(), nonExistentStudentEmail);
        verifyNoTasksAdded();
        verifyNoEmailsSent();
        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
    }

    @Test
    void testExecute_nonExistentCourseId_throwsEntityNotFoundException() {
        String nonExistentCourseId = "RANDOM_COURSE";

        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, newEmail, team.getName(),
                section.getName(), student.getComments(), true);

        String[] params = {
                Const.ParamsNames.COURSE_ID, nonExistentCourseId,
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        EntityNotFoundException enfe = verifyEntityNotFound(studentUpdateRequest, params);
        assertEquals(UpdateStudentAction.STUDENT_NOT_FOUND_FOR_EDIT, enfe.getMessage());

        verify(mockLogic, times(1)).getStudentForEmail(nonExistentCourseId, student.getEmail());
        verifyNoTasksAdded();
        verifyNoEmailsSent();
        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
    }

    @Test
    void testExecute_invalidUpdateAttributes_throwsInvalidHttpRequestBodyException()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException, EnrollException {
        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, newEmail, team.getName(),
                section.getName(), student.getComments(), true);

        Student updatedStudent = new Student(course, newName, newEmail, student.getComments(), team);
        updatedStudent.setId(student.getId());
        when(mockLogic.updateStudentCascade(any(Student.class))).thenThrow(InvalidParametersException.class);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyHttpRequestBodyFailure(studentUpdateRequest, params);

        verify(mockLogic, times(1)).getStudentForEmail(course.getId(), student.getEmail());
        verify(mockLogic, times(1)).getCourse(course.getId());
        verify(mockLogic, times(1)).getSectionOrCreate(course.getId(), studentUpdateRequest.getSection());
        verify(mockLogic, times(1)).getTeamOrCreate(section, studentUpdateRequest.getTeam());

        verify(mockLogic, times(1)).validateSectionsAndTeams(anyList(), eq(course.getId()));
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(mockLogic, times(1)).updateStudentCascade(studentCaptor.capture());
        Student studentToUpdate = studentCaptor.getValue();
        verifyStudentToUpdate(updatedStudent, studentToUpdate);
        verifyNoTasksAdded();

        verifyNoEmailsSent();

        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
    }

    @Test
    void testExecute_invalidSectionOrTeam_throwsInvalidOperationException() throws EnrollException {
        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, newEmail, team.getName(),
                section.getName(), student.getComments(), true);

        doThrow(EnrollException.class)
                .when(mockLogic)
                .validateSectionsAndTeams(anyList(), eq(course.getId()));

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyInvalidOperation(studentUpdateRequest, params);

        verify(mockLogic, times(1)).getStudentForEmail(course.getId(), student.getEmail());
        verify(mockLogic, times(1)).getCourse(course.getId());
        verify(mockLogic, times(1)).getSectionOrCreate(course.getId(), studentUpdateRequest.getSection());
        verify(mockLogic, times(1)).getTeamOrCreate(section, studentUpdateRequest.getTeam());

        verify(mockLogic, times(1)).validateSectionsAndTeams(anyList(), eq(course.getId()));
        verifyNoTasksAdded();

        verifyNoEmailsSent();

        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
    }

    @Test
    void testExecute_nonExistentStudent_throwsEntityNotFoundException()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException, EnrollException {
        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, newEmail, team.getName(),
                section.getName(), student.getComments(), true);

        Student updatedStudent = new Student(course, newName, newEmail, student.getComments(), team);
        updatedStudent.setId(student.getId());
        when(mockLogic.updateStudentCascade(any(Student.class))).thenThrow(EntityDoesNotExistException.class);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyEntityNotFound(studentUpdateRequest, params);

        verify(mockLogic, times(1)).getStudentForEmail(course.getId(), student.getEmail());
        verify(mockLogic, times(1)).getCourse(course.getId());
        verify(mockLogic, times(1)).getSectionOrCreate(course.getId(), studentUpdateRequest.getSection());
        verify(mockLogic, times(1)).getTeamOrCreate(section, studentUpdateRequest.getTeam());

        verify(mockLogic, times(1)).validateSectionsAndTeams(anyList(), eq(course.getId()));
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(mockLogic, times(1)).updateStudentCascade(studentCaptor.capture());
        Student studentToUpdate = studentCaptor.getValue();
        verifyStudentToUpdate(updatedStudent, studentToUpdate);
        verifyNoTasksAdded();

        verifyNoEmailsSent();

        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
    }

    @Test
    void testExecute_emailAlreadyExists_throwsInvalidOperationException()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException, EnrollException {
        StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(newName, newEmail, team.getName(),
                section.getName(), student.getComments(), true);

        Student updatedStudent = new Student(course, newName, newEmail, student.getComments(), team);
        updatedStudent.setId(student.getId());
        when(mockLogic.updateStudentCascade(any(Student.class))).thenThrow(EntityAlreadyExistsException.class);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(studentUpdateRequest, params);
        assertTrue(ioe.getMessage().startsWith(ERROR_EMAIL_ALREADY_EXISTS));

        verify(mockLogic, times(1)).getStudentForEmail(course.getId(), student.getEmail());
        verify(mockLogic, times(1)).getCourse(course.getId());
        verify(mockLogic, times(1)).getSectionOrCreate(course.getId(), studentUpdateRequest.getSection());
        verify(mockLogic, times(1)).getTeamOrCreate(section, studentUpdateRequest.getTeam());

        verify(mockLogic, times(1)).validateSectionsAndTeams(anyList(), eq(course.getId()));
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(mockLogic, times(1)).updateStudentCascade(studentCaptor.capture());
        Student studentToUpdate = studentCaptor.getValue();
        verifyStudentToUpdate(updatedStudent, studentToUpdate);
        verifyNoTasksAdded();

        verifyNoEmailsSent();

        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_missingStudentEmail_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
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
    void testSpecificAccessControl_admin_cannotAccess() {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyCannotAccess(params);
        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
    }

    @Test
    void testSpecificAccessControl_missingCourseId_throwsInvalidHttpParameterException() {
        loginAsInstructor("instructor-googleId");

        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyHttpParameterFailureAcl(params);
        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
    }

    @Test
    void testSpecificAccessControl_nonExistentInstructorId_cannotAccess() {
        String nonExistentInstructorId = "RANDOM_ID";
        when(mockLogic.getInstructorByGoogleId(course.getId(), nonExistentInstructorId)).thenReturn(null);
        loginAsInstructor(nonExistentInstructorId);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyCannotAccess(params);
        verify(mockLogic, times(1)).getInstructorByGoogleId(course.getId(), nonExistentInstructorId);
        verify(mockLogic, times(1)).getCourse(course.getId());
        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
    }

    @Test
    void testSpecificAccessControl_instructorWithPermission_canAccess() {
        String instructorId = "instructor-googleId";
        // Instructor with co-owner role can modify student
        Instructor instructor = getTypicalInstructor();
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorId)).thenReturn(instructor);
        loginAsInstructor(instructorId);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyCanAccess(params);
        verify(mockLogic, times(1)).getInstructorByGoogleId(course.getId(), instructorId);
        verify(mockLogic, times(1)).getCourse(course.getId());
        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
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
        loginAsInstructor(instructorId);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyCannotAccess(params);
        verify(mockLogic, times(1)).getInstructorByGoogleId(course.getId(), instructorId);
        verify(mockLogic, times(1)).getCourse(course.getId());
        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        loginAsStudent("student-googleId");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyCannotAccess(params);
        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
    }

    @Test
    void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyCannotAccess(params);
        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
    }

    @Test
    void testSpecificAccessControl_unregistered_cannotAccess() {
        loginAsUnregistered("instructor-googleId");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyCannotAccess(params);
        verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator);
    }
}
