package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static teammates.common.datatransfer.InstructorPermissionRole.getEnum;
import static teammates.common.util.Const.InstructorPermissionRoleNames.CUSTOM;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorUpdateRequest;

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
        String instructorToUpdateDisplayName = typicalInstructorToUpdate.getDisplayName();

        loginAsInstructor(typicalInstructorToUpdate.getGoogleId());

        String updatedInstructorName = "New Instructor";
        String updatedInstructorEmail = "newinstructor@teammates.tmt";
        String updatedInstructorRole = Const.InstructorPermissionRoleNames.COOWNER;
        Instructor updatedInstructor = new Instructor(typicalCourse, updatedInstructorName, updatedInstructorEmail,
                false, null, getEnum(updatedInstructorRole),
                new InstructorPrivileges(updatedInstructorRole));

        InstructorUpdateRequest requestBody = new InstructorUpdateRequest(typicalInstructorToUpdate.getId(),
                updatedInstructorName,
                updatedInstructorEmail, updatedInstructorRole,
                instructorToUpdateDisplayName, typicalInstructorToUpdate.isDisplayedToStudents());

        when(mockLogic.updateInstructorCascade(any(InstructorUpdateRequest.class)))
                .thenReturn(updatedInstructor);

        UpdateInstructorAction updateInstructorAction = getAction(requestBody);
        JsonResult r = getJsonResult(updateInstructorAction);

        InstructorData response = (InstructorData) r.getOutput();

        assertEquals(updatedInstructor.getName(), response.getName());
        assertEquals(updatedInstructor.getEmail(), response.getEmail());

        verify(mockLogic, times(1))
                .updateToEnsureValidityOfInstructorsForTheCourse(updatedInstructor);
    }

    @Test
    void testExecute_invalidEmail_throwsInvalidHttpRequestBodyException() throws Exception {
        String instructorToUpdateDisplayName = typicalInstructorToUpdate.getDisplayName();

        loginAsInstructor(typicalInstructorToUpdate.getGoogleId());

        String updatedInstructorName = "New Instructor";
        String invalidEmail = "invalidemail.com";
        String updatedInstructorRole = Const.InstructorPermissionRoleNames.COOWNER;
        InstructorUpdateRequest requestBody = new InstructorUpdateRequest(typicalInstructorToUpdate.getId(),
                updatedInstructorName,
                invalidEmail, updatedInstructorRole,
                instructorToUpdateDisplayName, typicalInstructorToUpdate.isDisplayedToStudents());

        when(mockLogic.updateInstructorCascade(any(InstructorUpdateRequest.class)))
                .thenThrow(InvalidParametersException.class);

        verifyHttpRequestBodyFailure(requestBody);

        verifyNoTasksAdded();
    }

    @Test
    void testExecute_noInstructorDisplayed_throwsInvalidOperationException() throws Exception {
        loginAsInstructor(typicalInstructorToUpdate.getGoogleId());

        String updatedInstructorName = "New Instructor";
        String updatedInstructorEmail = "newinstructor@teammates.tmt";
        String updatedInstructorRole = Const.InstructorPermissionRoleNames.COOWNER;
        InstructorUpdateRequest requestBody = new InstructorUpdateRequest(typicalInstructorToUpdate.getId(),
                updatedInstructorName,
                updatedInstructorEmail, updatedInstructorRole,
                null, false);

        when(mockLogic.updateInstructorCascade(any(InstructorUpdateRequest.class)))
                .thenThrow(InstructorUpdateException.class);

        verifyInvalidOperation(requestBody);

        verifyNoTasksAdded();
    }

    @Test
    void testExecute_adminToMasqueradeAsInstructor_success() throws Exception {
        String instructorToUpdateDisplayName = typicalInstructorToUpdate.getDisplayName();

        loginAsAdmin();

        String updatedInstructorName = "New Instructor";
        String updatedInstructorEmail = "newinstructor@teammates.tmt";
        String updatedInstructorRole = Const.InstructorPermissionRoleNames.COOWNER;
        Instructor updatedInstructor = new Instructor(typicalCourse, updatedInstructorName, updatedInstructorEmail,
                false, null, getEnum(updatedInstructorRole),
                new InstructorPrivileges(updatedInstructorRole));

        InstructorUpdateRequest requestBody = new InstructorUpdateRequest(typicalInstructorToUpdate.getId(),
                updatedInstructorName,
                updatedInstructorEmail, updatedInstructorRole,
                instructorToUpdateDisplayName, true);

        when(mockLogic.updateInstructorCascade(any(InstructorUpdateRequest.class)))
                .thenReturn(updatedInstructor);

        UpdateInstructorAction updateInstructorAction = getAction(requestBody);
        JsonResult r = getJsonResult(updateInstructorAction);

        InstructorData response = (InstructorData) r.getOutput();

        assertEquals(updatedInstructor.getName(), response.getName());
        assertEquals(updatedInstructor.getEmail(), response.getEmail());

        verify(mockLogic, times(1))
                .updateToEnsureValidityOfInstructorsForTheCourse(updatedInstructor);
    }

    @Test
        void testExecute_nullInstructorId_throwsInvalidHttpRequestBodyException() throws Exception {
        InstructorUpdateRequest requestBody = new InstructorUpdateRequest(null, typicalInstructorToUpdate.getName(),
                typicalInstructorToUpdate.getEmail(), Const.InstructorPermissionRoleNames.COOWNER,
                typicalInstructorToUpdate.getDisplayName(), typicalInstructorToUpdate.isDisplayedToStudents());

        when(mockLogic.updateInstructorCascade(any(InstructorUpdateRequest.class)))
                .thenThrow(InvalidParametersException.class);

                verifyHttpRequestBodyFailure(requestBody);

        verifyNoTasksAdded();
    }

    @Test
    void testExecute_nullInstructorEmail_throwsInvalidHttpRequestBodyException() throws Exception {
        String instructorToUpdateDisplayName = typicalInstructorToUpdate.getDisplayName();

        loginAsInstructor(typicalInstructorToUpdate.getGoogleId());

        String updatedInstructorName = "New Instructor";
        String updatedInstructorRole = Const.InstructorPermissionRoleNames.COOWNER;
        InstructorUpdateRequest requestBody = new InstructorUpdateRequest(typicalInstructorToUpdate.getId(),
                updatedInstructorName,
                null, updatedInstructorRole,
                instructorToUpdateDisplayName, typicalInstructorToUpdate.isDisplayedToStudents());

        when(mockLogic.updateInstructorCascade(any(InstructorUpdateRequest.class)))
                .thenThrow(InvalidParametersException.class);

        verifyHttpRequestBodyFailure(requestBody);

        verifyNoTasksAdded();
    }

    @Test
    void testAccessControl_noLogin_cannotAccess() {
        InstructorUpdateRequest requestBody = new InstructorUpdateRequest(typicalInstructorToUpdate.getId(),
                typicalInstructorToUpdate.getName(), typicalInstructorToUpdate.getEmail(),
                typicalInstructorToUpdate.getRole().getRoleName(), typicalInstructorToUpdate.getDisplayName(),
                typicalInstructorToUpdate.isDisplayedToStudents());

        logoutUser();
        assertThrows(UnauthorizedAccessException.class, () -> getAction(requestBody).checkAccessControl());
    }

    @Test
    void testAccessControl_unregisteredUsers_cannotAccess() {
        InstructorUpdateRequest requestBody = new InstructorUpdateRequest(typicalInstructorToUpdate.getId(),
                typicalInstructorToUpdate.getName(), typicalInstructorToUpdate.getEmail(),
                typicalInstructorToUpdate.getRole().getRoleName(), typicalInstructorToUpdate.getDisplayName(),
                typicalInstructorToUpdate.isDisplayedToStudents());

        loginAsUnregistered("unregistered user");
        assertThrows(UnauthorizedAccessException.class, () -> getAction(requestBody).checkAccessControl());
    }

    @Test
    void testAccessControl_students_cannotAccess() {
        Student typicalStudent = getTypicalStudent();
        InstructorUpdateRequest requestBody = new InstructorUpdateRequest(typicalInstructorToUpdate.getId(),
                typicalInstructorToUpdate.getName(), typicalInstructorToUpdate.getEmail(),
                typicalInstructorToUpdate.getRole().getRoleName(), typicalInstructorToUpdate.getDisplayName(),
                typicalInstructorToUpdate.isDisplayedToStudents());

        loginAsStudent(typicalStudent.getGoogleId());
        assertThrows(UnauthorizedAccessException.class, () -> getAction(requestBody).checkAccessControl());
    }

    @Test
    void testAccessControl_instructorsFromDifferentCourse_cannotAccess() {
        Course differentCourse = new Course("different id", "different name", Const.DEFAULT_TIME_ZONE,
                "teammates");
        Instructor instructorFromDifferentCourse = getTypicalInstructor();
        instructorFromDifferentCourse.setCourse(differentCourse);
        instructorFromDifferentCourse.setGoogleId("different google id");

        InstructorUpdateRequest requestBody = new InstructorUpdateRequest(typicalInstructorToUpdate.getId(),
                typicalInstructorToUpdate.getName(), typicalInstructorToUpdate.getEmail(),
                typicalInstructorToUpdate.getRole().getRoleName(), typicalInstructorToUpdate.getDisplayName(),
                typicalInstructorToUpdate.isDisplayedToStudents());

        when(mockLogic.getInstructor(typicalInstructorToUpdate.getId())).thenReturn(typicalInstructorToUpdate);

        loginAsInstructor(instructorFromDifferentCourse.getGoogleId());

        assertThrows(UnauthorizedAccessException.class, () -> getAction(requestBody).checkAccessControl());
    }

    @Test
    void testAccessControl_instructorWithoutCorrectCoursePrivilege_cannotAccess() {
        Instructor instructorWithoutCorrectPrivilege = getTypicalInstructor();
        instructorWithoutCorrectPrivilege.setGoogleId("no privilege");
        instructorWithoutCorrectPrivilege.setEmail("helper@teammates.tmt");
        instructorWithoutCorrectPrivilege.setRole(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        instructorWithoutCorrectPrivilege.setPrivileges(new InstructorPrivileges(CUSTOM));

        InstructorUpdateRequest requestBody = new InstructorUpdateRequest(typicalInstructorToUpdate.getId(),
                typicalInstructorToUpdate.getName(), typicalInstructorToUpdate.getEmail(),
                typicalInstructorToUpdate.getRole().getRoleName(), typicalInstructorToUpdate.getDisplayName(),
                typicalInstructorToUpdate.isDisplayedToStudents());

        when(mockLogic.getInstructor(typicalInstructorToUpdate.getId())).thenReturn(typicalInstructorToUpdate);

        loginAsInstructor(instructorWithoutCorrectPrivilege.getGoogleId());

        assertThrows(UnauthorizedAccessException.class, () -> getAction(requestBody).checkAccessControl());
    }

}
