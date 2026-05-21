package teammates.it.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteInstructorAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link DeleteInstructorAction}.
 */
public class DeleteInstructorActionIT extends BaseActionIT<DeleteInstructorAction> {
    private DataBundle typicalBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    protected void testExecute() {
        // see test cases below
    }

    @Test
    protected void testExecute_typicalCaseByUserId_shouldPass() {
        loginAsAdmin();

        Instructor instructor = typicalBundle.instructors.get("instructor2OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructor.getId().toString(),
        };

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

        assertNull(logic.getInstructorForEmail(instructor.getCourseId(), instructor.getEmail()));
    }

    @Test
    public void testExecute_deleteInstructorByUserId_shouldPass() {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Instructor instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructor2OfCourse1.getId().toString(),
        };

        assertTrue(logic.getInstructorsByCourse(instructor1OfCourse1.getCourseId()).size() > 1);

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

        assertNull(logic.getInstructorForEmail(instructor2OfCourse1.getCourseId(), instructor2OfCourse1.getEmail()));
        assertNotNull(logic.getInstructorForEmail(instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getEmail()));
    }

    @Test
    protected void testExecute_adminDeletesLastInstructorByUserId_shouldFail() {
        loginAsAdmin();

        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse3");

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructor.getId().toString(),
        };

        assertEquals(1, logic.getInstructorsByCourse(instructor.getCourseId()).size());

        InvalidOperationException ioe = verifyInvalidOperation(submissionParams);
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", ioe.getMessage());

        assertNotNull(logic.getInstructorForEmail(instructor.getCourseId(), instructor.getEmail()));
        assertNotNull(logic.getInstructorByGoogleId(instructor.getCourseId(), instructor.getGoogleId()));
    }

    @Test
    protected void testExecute_instructorDeleteOwnRoleByUserId_shouldPass() {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Instructor instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructor2OfCourse1.getId().toString(),
        };

        assertTrue(logic.getInstructorsByCourse(instructor1OfCourse1.getCourseId()).size() > 1);

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

        assertNull(logic.getInstructorForEmail(instructor2OfCourse1.getCourseId(), instructor2OfCourse1.getEmail()));
        assertNotNull(logic.getInstructorForEmail(instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getEmail()));
    }

    @Test
    protected void testExecute_deleteLastInstructorByUserId_shouldFail() {
        Instructor instructorToDelete = typicalBundle.instructors.get("instructor1OfCourse3");
        String courseId = instructorToDelete.getCourseId();

        loginAsInstructor(instructorToDelete.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructorToDelete.getId().toString(),
        };

        assertEquals(1, logic.getInstructorsByCourse(courseId).size());

        InvalidOperationException ioe = verifyInvalidOperation(submissionParams);
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", ioe.getMessage());

        assertNotNull(logic.getInstructorForEmail(instructorToDelete.getCourseId(), instructorToDelete.getEmail()));
        assertNotNull(logic.getInstructorByGoogleId(instructorToDelete.getCourseId(), instructorToDelete.getGoogleId()));
    }

    @Test
    protected void testExecute_deleteLastInstructorInMasqueradeByUserId_shouldFail() {
        Instructor instructorToDelete = typicalBundle.instructors.get("instructor1OfCourse3");
        String courseId = instructorToDelete.getCourseId();

        loginAsAdmin();

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructorToDelete.getId().toString(),
        };

        assertEquals(1, logic.getInstructorsByCourse(courseId).size());

        InvalidOperationException ioe = verifyInvalidOperation(
                addUserToParams(instructorToDelete.getGoogleId(), submissionParams));
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", ioe.getMessage());

        assertNotNull(logic.getInstructorForEmail(instructorToDelete.getCourseId(), instructorToDelete.getEmail()));
        assertNotNull(logic.getInstructorByGoogleId(instructorToDelete.getCourseId(), instructorToDelete.getGoogleId()));
    }

    @Test
    protected void testExecute_deleteInstructorInMasqueradeByUserId_shouldPass() {
        Instructor instructorToDelete = typicalBundle.instructors.get("instructor2OfCourse1");
        String courseId = instructorToDelete.getCourseId();

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructorToDelete.getId().toString(),
        };

        loginAsAdmin();

        assertTrue(logic.getInstructorsByCourse(courseId).size() > 1);

        DeleteInstructorAction deleteInstructorAction =
                getAction(addUserToParams(instructorToDelete.getGoogleId(), submissionParams));
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput messageOutput = (MessageOutput) response.getOutput();

        assertEquals("Instructor is successfully deleted.", messageOutput.getMessage());
        assertNull(logic.getInstructorForEmail(courseId, instructorToDelete.getEmail()));
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();

        String[] onlyCourseParameter = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        loginAsAdmin();

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(onlyCourseParameter);

        loginAsInstructor(instructorId);

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(onlyCourseParameter);
    }

    @Test
    protected void testExecute_noSuchInstructor_shouldFail() {
        loginAsAdmin();

        attemptToDeleteFakeInstructorByUserId();

        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        attemptToDeleteFakeInstructorByUserId();
    }

    private void attemptToDeleteFakeInstructorByUserId() {
        UUID nonExistentInstructorId = UUID.randomUUID();

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, nonExistentInstructorId.toString(),
        };

        assertNull(logic.getInstructor(nonExistentInstructorId));

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        Course course = typicalBundle.courses.get("course1");

        String[] params = new String[] {
                Const.ParamsNames.USER_ID, instructor.getId().toString(),
        };

        verifyAccessibleForAdmin(params);
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(course,
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, params);
    }

}
