package teammates.sqlui.webapi;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteInstructorAction;
import teammates.ui.webapi.InvalidOperationException;

/**
 * SUT: {@link DeleteInstructorAction}.
 */
public class DeleteInstructorActionTest extends BaseActionTest<DeleteInstructorAction> {

    private Course course;
    private Instructor instructor;
    private Instructor instructor2;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @BeforeMethod
    void setUp() {
        course = new Course("course-id", "Course Name", Const.DEFAULT_TIME_ZONE, "institute");
        instructor = setupInstructor("instructor-googleId", "name", "instructoremail@tm.tmt");
        instructor2 = setupInstructor("instructor2-googleId", "name2", "instructor2email@tm.tmt");

        setupMockLogic();
    }

    private Instructor setupInstructor(String googleId, String name, String email) {
        Account account = new Account(googleId, name, email);
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, true);

        Instructor instructor = new Instructor(course, name, email,
                true, "", null, instructorPrivileges);
        instructor.setAccount(account);
        return instructor;
    }

    private void setupMockLogic() {
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor2.getGoogleId())).thenReturn(instructor2);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor.getEmail())).thenReturn(instructor);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor2.getEmail())).thenReturn(instructor2);
        when(mockLogic.getInstructorsByCourse(course.getId())).thenReturn(List.of(instructor, instructor2));
    }

    private void simulateInstructorDeletion(Instructor targetInstructor) {
        AtomicReference<Instructor> instructorRef = new AtomicReference<>(targetInstructor);

        when(mockLogic.getInstructorByGoogleId(course.getId(), targetInstructor.getGoogleId()))
                .thenAnswer(invocation -> instructorRef.get());
        when(mockLogic.getInstructorForEmail(course.getId(), targetInstructor.getEmail()))
                .thenAnswer(invocation -> instructorRef.get());

        doAnswer(invocation -> {
            instructorRef.set(null);
            return null;
        }).when(mockLogic).deleteInstructorCascade(course.getId(), targetInstructor.getEmail());
    }

    @Test
    void testExecute_deleteInstructorByGoogleId_success() {
        simulateInstructorDeletion(instructor2);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor2.getGoogleId(),
        };

        DeleteInstructorAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Instructor is successfully deleted.", actionOutput.getMessage());
        assertNull(mockLogic.getInstructorByGoogleId(course.getId(), instructor2.getGoogleId()));
    }

    @Test
    void testExecute_deleteInstructorByEmail_success() {
        simulateInstructorDeletion(instructor2);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor2.getEmail(),
        };

        DeleteInstructorAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Instructor is successfully deleted.", actionOutput.getMessage());
        assertNull(mockLogic.getInstructorForEmail(course.getId(), instructor2.getEmail()));
    }

    @Test
    void testExecute_deleteLastInstructorByGoogleId_fail() {
        // Override the mock logic for the course to have only one instructor
        when(mockLogic.getInstructorsByCourse(course.getId())).thenReturn(List.of(instructor));

        simulateInstructorDeletion(instructor);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };

        assertEquals(mockLogic.getInstructorsByCourse(course.getId()).size(), 1);

        InvalidOperationException ioe = verifyInvalidOperation(params);
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", ioe.getMessage());

        assertNotNull(mockLogic.getInstructorForEmail(course.getId(), instructor.getEmail()));
        assertNotNull(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId()));
    }

    @Test
    void testExecute_instructorDeleteOwnRoleByGoogleId_success() {
        loginAsInstructor(instructor.getGoogleId());

        simulateInstructorDeletion(instructor);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };

        DeleteInstructorAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Instructor is successfully deleted.", actionOutput.getMessage());
        assertNull(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId()));
        assertNotNull(mockLogic.getInstructorByGoogleId(course.getId(), instructor2.getGoogleId()));
    }

    @Test
    void testExecute_deleteNonExistentInstructorByGoogleId_failSilently() {
        when(mockLogic.getInstructorByGoogleId(course.getId(), "fake-googleId")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, "fake-googleId",
        };

        assertNull(mockLogic.getInstructorByGoogleId(course.getId(), "fake-googleId"));

        DeleteInstructorAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Instructor is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_deleteNonExistentInstructorByEmail_failSilently() {
        when(mockLogic.getInstructorForEmail(course.getId(), "fake-instructoremail@tm.tmt")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, "fake-instructoremail@tm.tmt",
        };

        assertNull(mockLogic.getInstructorForEmail(course.getId(), "fake-instructoremail@tm.tmt"));

        DeleteInstructorAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Instructor is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_courseDoesNotExist_failSilently() {
        when(mockLogic.getCourse("non-existent-course-id")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "non-existent-course-id",
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };

        DeleteInstructorAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Instructor is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_missingCourseIdWithInstructorId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingCourseIdWithInstructorEmail_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_onlyCourseId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testSpecificAccessControl_admin_canAccess() {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithPermission_canAccess() {
        loginAsInstructor(instructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor2.getGoogleId(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithInvalidPermission_cannotAccess() {
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, false);
        instructor.setPrivileges(instructorPrivileges);

        loginAsInstructor(instructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor2.getGoogleId(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorInDifferentCourse_cannotAccess() {
        loginAsInstructor("instructor3-googleId");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };

        loginAsStudent("student-googleId");
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }
}
