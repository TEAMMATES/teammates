package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static teammates.common.datatransfer.InstructorPermissionRole.getEnum;
import static teammates.common.util.Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TaskWrapper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorCreateRequest;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.UpdateInstructorAction;

/**
 * SUT: {@link UpdateInstructorAction}.
 */
public class UpdateInstructorActionTest extends BaseActionTest<UpdateInstructorAction> {

    private Course typicalCourse;
    private Instructor typicalInstructorToUpdate;

    @Override
    String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    String getRequestMethod() {
        return PUT;
    }

    @BeforeMethod
    void setUpMethod() {
        typicalCourse = getTypicalCourse();
        typicalInstructorToUpdate = getTypicalInstructor();
    }

    @Test
    void testExecute_typicalCase_success() throws Exception {
        String instructorToUpdateId = typicalInstructorToUpdate.getGoogleId();
        String instructorToUpdateDisplayName = typicalInstructorToUpdate.getDisplayName();

        loginAsInstructor(instructorToUpdateId);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        String updatedInstructorName = "New Instructor";
        String updatedInstructorEmail = "newinstructor@teammates.tmt";
        String updatedInstructorRole = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        Instructor updatedInstructor = new Instructor(typicalCourse, updatedInstructorName, updatedInstructorEmail,
                false, null, getEnum(updatedInstructorRole),
                new InstructorPrivileges(updatedInstructorRole));

        InstructorCreateRequest requestBody = new InstructorCreateRequest(instructorToUpdateId, updatedInstructorName,
                updatedInstructorEmail, updatedInstructorRole,
                instructorToUpdateDisplayName, typicalInstructorToUpdate.isDisplayedToStudents());

        when(mockLogic.updateInstructorCascade(any(String.class), any(InstructorCreateRequest.class)))
                .thenReturn(updatedInstructor);

        UpdateInstructorAction updateInstructorAction = getAction(requestBody, params);
        JsonResult r = getJsonResult(updateInstructorAction);

        InstructorData response = (InstructorData) r.getOutput();

        assertEquals(updatedInstructor.getName(), response.getName());
        assertEquals(updatedInstructor.getEmail(), response.getEmail());

        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        TaskWrapper taskAdded = mockTaskQueuer.getTasksAdded().get(0);

        assertEquals(typicalCourse.getId(), taskAdded.getParamMap().get(Const.ParamsNames.COURSE_ID));
        assertEquals(updatedInstructor.getEmail(), taskAdded.getParamMap().get(Const.ParamsNames.INSTRUCTOR_EMAIL));

        verify(mockLogic, times(1))
                .updateToEnsureValidityOfInstructorsForTheCourse(typicalCourse.getId(), updatedInstructor);
    }

    @Test
    void testExecute_invalidEmail_throwsInvalidHttpRequestBodyException() throws Exception {
        String instructorToUpdateId = typicalInstructorToUpdate.getGoogleId();
        String instructorToUpdateDisplayName = typicalInstructorToUpdate.getDisplayName();

        loginAsInstructor(instructorToUpdateId);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        String updatedInstructorName = "New Instructor";
        String invalidEmail = "invalidemail.com";
        String updatedInstructorRole = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorCreateRequest requestBody = new InstructorCreateRequest(instructorToUpdateId, updatedInstructorName,
                invalidEmail, updatedInstructorRole,
                instructorToUpdateDisplayName, typicalInstructorToUpdate.isDisplayedToStudents());

        when(mockLogic.updateInstructorCascade(any(String.class), any(InstructorCreateRequest.class)))
                .thenThrow(InvalidParametersException.class);

        verifyHttpRequestBodyFailure(requestBody, params);

        verifyNoTasksAdded();
    }

    @Test
    void testExecute_noInstructorDisplayed_throwsInvalidOperationException() throws Exception {
        String instructorToUpdateId = typicalInstructorToUpdate.getGoogleId();

        loginAsInstructor(instructorToUpdateId);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        String updatedInstructorName = "New Instructor";
        String updatedInstructorEmail = "newinstructor@teammates.tmt";
        String updatedInstructorRole = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorCreateRequest requestBody = new InstructorCreateRequest(instructorToUpdateId, updatedInstructorName,
                updatedInstructorEmail, updatedInstructorRole,
                null, false);

        when(mockLogic.updateInstructorCascade(any(String.class), any(InstructorCreateRequest.class)))
                .thenThrow(InstructorUpdateException.class);

        verifyInvalidOperation(requestBody, params);

        verifyNoTasksAdded();
    }

    @Test
    void testExecute_adminToMasqueradeAsInstructor_success() throws Exception {
        String instructorToUpdateId = typicalInstructorToUpdate.getGoogleId();
        String instructorToUpdateDisplayName = typicalInstructorToUpdate.getDisplayName();

        loginAsAdmin();

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        String updatedInstructorName = "New Instructor";
        String updatedInstructorEmail = "newinstructor@teammates.tmt";
        String updatedInstructorRole = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        Instructor updatedInstructor = new Instructor(typicalCourse, updatedInstructorName, updatedInstructorEmail,
                false, null, getEnum(updatedInstructorRole),
                new InstructorPrivileges(updatedInstructorRole));

        InstructorCreateRequest requestBody = new InstructorCreateRequest(instructorToUpdateId, updatedInstructorName,
                updatedInstructorEmail, updatedInstructorRole,
                instructorToUpdateDisplayName, true);

        when(mockLogic.updateInstructorCascade(any(String.class), any(InstructorCreateRequest.class)))
                .thenReturn(updatedInstructor);

        UpdateInstructorAction updateInstructorAction = getAction(requestBody, params);
        JsonResult r = getJsonResult(updateInstructorAction);

        InstructorData response = (InstructorData) r.getOutput();

        assertEquals(updatedInstructor.getName(), response.getName());
        assertEquals(updatedInstructor.getEmail(), response.getEmail());

        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        TaskWrapper taskAdded = mockTaskQueuer.getTasksAdded().get(0);

        assertEquals(typicalCourse.getId(), taskAdded.getParamMap().get(Const.ParamsNames.COURSE_ID));
        assertEquals(updatedInstructor.getEmail(), taskAdded.getParamMap().get(Const.ParamsNames.INSTRUCTOR_EMAIL));

        verify(mockLogic, times(1))
                .updateToEnsureValidityOfInstructorsForTheCourse(typicalCourse.getId(), updatedInstructor);
    }

    @Test
    void testExecute_nullHttpParameters_throwsInvalidHttpParameterException() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, null,
        };
        verifyHttpParameterFailure(params);

        verifyNoTasksAdded();
    }

    @Test
    void testExecute_nullInstructorEmail_throwsInvalidHttpRequestBodyException() throws Exception {
        String instructorToUpdateId = typicalInstructorToUpdate.getGoogleId();
        String instructorToUpdateDisplayName = typicalInstructorToUpdate.getDisplayName();

        loginAsInstructor(instructorToUpdateId);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        String updatedInstructorName = "New Instructor";
        String updatedInstructorRole = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorCreateRequest requestBody = new InstructorCreateRequest(instructorToUpdateId, updatedInstructorName,
                null, updatedInstructorRole,
                instructorToUpdateDisplayName, typicalInstructorToUpdate.isDisplayedToStudents());

        when(mockLogic.updateInstructorCascade(any(String.class), any(InstructorCreateRequest.class)))
                .thenThrow(InvalidParametersException.class);

        verifyHttpRequestBodyFailure(requestBody, params);

        verifyNoTasksAdded();
    }

    @Test
    void testAccessControl_noLogin_cannotAccess() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        logoutUser();
        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_unregisteredUsers_cannotAccess() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        loginAsUnregistered("unregistered user");
        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_students_cannotAccess() {
        Student typicalStudent = getTypicalStudent();
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        loginAsStudent(typicalStudent.getGoogleId());
        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorsFromDifferentCourse_cannotAccess() {
        Course differentCourse = new Course("different id", "different name", Const.DEFAULT_TIME_ZONE,
                "teammates");
        Instructor instructorFromDifferentCourse = getTypicalInstructor();
        instructorFromDifferentCourse.setCourse(differentCourse);
        instructorFromDifferentCourse.setGoogleId("different google id");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        when(mockLogic.getCourse(typicalCourse.getId())).thenReturn(typicalCourse);
        when(mockLogic.getInstructorByGoogleId(differentCourse.getId(), instructorFromDifferentCourse.getGoogleId()))
                .thenReturn(instructorFromDifferentCourse);

        loginAsInstructor(instructorFromDifferentCourse.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorWithoutCorrectCoursePrivilege_cannotAccess() {
        Instructor instructorWithoutCorrectPrivilege = getTypicalInstructor();
        instructorWithoutCorrectPrivilege.setGoogleId("no privilege");
        instructorWithoutCorrectPrivilege.setEmail("helper@teammates.tmt");
        instructorWithoutCorrectPrivilege.setPrivileges(new InstructorPrivileges(INSTRUCTOR_PERMISSION_ROLE_CUSTOM));

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        when(mockLogic.getCourse(typicalCourse.getId())).thenReturn(typicalCourse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutCorrectPrivilege.getGoogleId()))
                .thenReturn(instructorWithoutCorrectPrivilege);

        loginAsInstructor(instructorWithoutCorrectPrivilege.getGoogleId());

        verifyCannotAccess(params);
    }

}
