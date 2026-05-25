package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static teammates.common.datatransfer.InstructorPermissionRole.getEnum;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorCreateRequest;

/**
 * SUT: {@link CreateInstructorAction}.
 */
public class CreateInstructorActionTest extends BaseActionTest<CreateInstructorAction> {

    private Instructor typicalInstructor;
    private Course typicalCourse;

    @Override
    String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUpMethod() {
        Mockito.reset(mockLogic, mockEmailGenerator);

        typicalInstructor = getTypicalInstructor();
        typicalCourse = getTypicalCourse();
    }

    @Test
    void testExecute_typicalCase_success() throws Exception {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        String newInstructorName = "New Instructor";
        String newInstructorEmail = "newinstructor@teammates.tmt";
        String newInstructorRole = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        Instructor newInstructor = new Instructor(typicalCourse, newInstructorName, newInstructorEmail,
                false, null, getEnum(newInstructorRole),
                new InstructorPrivileges(newInstructorRole));

        InstructorCreateRequest requestBody = new InstructorCreateRequest(typicalInstructor.getGoogleId(),
                newInstructorName, newInstructorEmail, newInstructorRole,
                null, false);

        when(mockLogic.getCourse(typicalCourse.getId())).thenReturn(typicalCourse);
        when(mockLogic.createInstructor(any(Instructor.class))).thenReturn(newInstructor);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        EmailWrapper mockEmail = mock(EmailWrapper.class);
        when(mockEmailGenerator.generateInstructorCourseJoinEmail(typicalInstructor, newInstructor, typicalCourse))
                .thenReturn(mockEmail);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateInstructorAction action = getAction(requestBody, params);
        JsonResult r = getJsonResult(action);
        InstructorData response = (InstructorData) r.getOutput();

        verify(mockLogic, times(1)).getCourse(typicalCourse.getId());
        verify(mockLogic, times(1)).createInstructor(any(Instructor.class));

        verifySpecifiedTasksAdded(Const.TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, 1);

        assertEquals(newInstructor.getName(), response.getName());
        assertEquals(newInstructor.getEmail(), response.getEmail());
        logoutUser();
    }

    @Test
    void testExecute_existingInstructor_throwsInvalidOperationException() throws Exception {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        String existingInstructorName = "instructor-name";
        String existingInstructorEmail = "valid@teammates.tmt";
        String existingInstructorRole = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;

        InstructorCreateRequest requestBody = new InstructorCreateRequest(typicalInstructor.getGoogleId(),
                existingInstructorName, existingInstructorEmail, existingInstructorRole,
                null, false);

        when(mockLogic.getCourse(typicalCourse.getId())).thenReturn(typicalCourse);
        when(mockLogic.createInstructor(any(Instructor.class))).thenThrow(EntityAlreadyExistsException.class);

        loginAsInstructor(typicalInstructor.getGoogleId());

        InvalidOperationException ioe = verifyInvalidOperation(requestBody, params);

        assertEquals("An instructor with the same email address already exists in the course.",
                ioe.getMessage());

        verifyNoTasksAdded();

        verify(mockLogic, times(1)).getCourse(typicalCourse.getId());
        verify(mockLogic, times(1)).createInstructor(any(Instructor.class));
        logoutUser();
    }

    @Test
    void testExecute_invalidInstructorEmail_throwsInvalidHttpRequestBodyException() throws Exception {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        String newInstructorName = "New Instructor";
        String invalidInstructorEmail = "newInvalidInstructor.email.tmt";
        String newInstructorRole = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;

        InstructorCreateRequest requestBody = new InstructorCreateRequest(typicalInstructor.getGoogleId(),
                newInstructorName, invalidInstructorEmail, newInstructorRole,
                null, false);

        when(mockLogic.getCourse(typicalCourse.getId())).thenReturn(typicalCourse);
        when(mockLogic.createInstructor(any(Instructor.class))).thenThrow(InvalidParametersException.class);

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyHttpRequestBodyFailure(requestBody, params);

        verifyNoTasksAdded();

        verify(mockLogic, times(1)).getCourse(typicalCourse.getId());
        verify(mockLogic, times(1)).createInstructor(any(Instructor.class));
        logoutUser();
    }

    @Test
    void testExecute_adminToMasqueradeAsInstructor_success() throws Exception {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        String newInstructorName = "New Instructor";
        String newInstructorEmail = "newinstructor@teammates.tmt";
        String newInstructorRole = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        Instructor newInstructor = new Instructor(typicalCourse, newInstructorName, newInstructorEmail,
                false, null, getEnum(newInstructorRole),
                new InstructorPrivileges(newInstructorRole));

        InstructorCreateRequest requestBody = new InstructorCreateRequest(typicalInstructor.getGoogleId(),
                newInstructorName, newInstructorEmail, newInstructorRole,
                null, false);

        when(mockLogic.getCourse(typicalCourse.getId())).thenReturn(typicalCourse);
        when(mockLogic.createInstructor(any(Instructor.class))).thenReturn(newInstructor);
        when(mockLogic.getInstructorByGoogleId(Mockito.eq(typicalCourse.getId()), Mockito.anyString()))
                .thenReturn(typicalInstructor);

        EmailWrapper mockEmail = mock(EmailWrapper.class);
        when(mockEmailGenerator.generateInstructorCourseJoinEmail(
                any(Instructor.class), any(Instructor.class), any(Course.class)))
                .thenReturn(mockEmail);

        loginAsAdmin();

        CreateInstructorAction action = getAction(requestBody, params);
        JsonResult r = getJsonResult(action);
        InstructorData response = (InstructorData) r.getOutput();

        verify(mockLogic, times(1)).getCourse(typicalCourse.getId());
        verify(mockLogic, times(1)).createInstructor(any(Instructor.class));

        verifySpecifiedTasksAdded(Const.TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, 1);

        assertEquals(newInstructor.getName(), response.getName());
        assertEquals(newInstructor.getEmail(), response.getEmail());
        logoutUser();
    }

    @Test
    void testAccessControl_sameCourseInstructorWithModifyInstructorPrivilege_canAccess() {
        typicalInstructor.setCourse(typicalCourse);
        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(typicalInstructor);
        when(mockLogic.getCourse(typicalCourse.getId())).thenReturn(typicalCourse);
        loginAsInstructor(typicalInstructor.getId().toString());
        verifyCanAccess(Const.ParamsNames.COURSE_ID, typicalCourse.getId());
        logoutUser();
    }

    @Test
    void testAccessControl_sameCourseInstructorWithoutModifyInstructorPrivilege_cannotAccess() {
        typicalInstructor.setCourse(typicalCourse);
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, false);
        typicalInstructor.setPrivileges(privileges);
        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(typicalInstructor);
        when(mockLogic.getCourse(typicalCourse.getId())).thenReturn(typicalCourse);
        loginAsInstructor(typicalInstructor.getId().toString());
        verifyCannotAccess(Const.ParamsNames.COURSE_ID, typicalCourse.getId());
        logoutUser();
    }

    @Test
    void testAccessControl_differentCourseInstructor_cannotAccess() {
        Course otherCourse = new Course("other-course-id", "other-course-name", Const.DEFAULT_TIME_ZONE, "teammates");
        typicalInstructor.setCourse(otherCourse);
        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(typicalInstructor);
        when(mockLogic.getCourse(typicalCourse.getId())).thenReturn(typicalCourse);
        loginAsInstructor(typicalInstructor.getId().toString());
        verifyCannotAccess(Const.ParamsNames.COURSE_ID, typicalCourse.getId());
        logoutUser();
    }

    @Test
    void testAccessControl_student_cannotAccess() {
        loginAsStudent("student-googleId");
        verifyCannotAccess(Const.ParamsNames.COURSE_ID, typicalCourse.getId());
        logoutUser();
    }

    @Test
    void testAccessControl_unregistered_cannotAccess() {
        loginAsUnregistered("unregistered-googleId");
        verifyCannotAccess(Const.ParamsNames.COURSE_ID, typicalCourse.getId());
        logoutUser();
    }

    @Test
    void testAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        verifyCannotAccess(Const.ParamsNames.COURSE_ID, typicalCourse.getId());
    }
}
