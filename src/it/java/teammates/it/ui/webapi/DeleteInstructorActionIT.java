package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteInstructorAction;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link DeleteInstructorAction}.
 */
public class DeleteInstructorActionIT extends BaseActionIT<DeleteInstructorAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
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
    protected void testExecute_typicalCaseByGoogleId_shouldPass() {
        loginAsAdmin();

        Instructor instructor = typicalBundle.instructors.get("instructor2OfCourse1");
        String instructorId = instructor.getGoogleId();

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

        assertNull(logic.getInstructorForEmail(instructor.getCourseId(), instructor.getEmail()));
    }

    @Test
    public void testExecute_deleteInstructorByEmail_shouldPass() {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Instructor instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor2OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
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
    protected void testExecute_adminDeletesLastInstructorByGoogleId_shouldFail() {
        loginAsAdmin();

        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse3");
        String instructorId = instructor.getGoogleId();

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        assertEquals(logic.getInstructorsByCourse(instructor.getCourseId()).size(), 1);

        InvalidOperationException ioe = verifyInvalidOperation(submissionParams);
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", ioe.getMessage());

        assertNotNull(logic.getInstructorForEmail(instructor.getCourseId(), instructor.getEmail()));
        assertNotNull(logic.getInstructorByGoogleId(instructor.getCourseId(), instructor.getGoogleId()));
    }

    @Test
    protected void testExecute_instructorDeleteOwnRoleByGoogleId_shouldPass() {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Instructor instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructor2OfCourse1.getGoogleId(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
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
    protected void testExecute_deleteLastInstructorByGoogleId_shouldFail() {
        Instructor instructorToDelete = typicalBundle.instructors.get("instructor1OfCourse3");
        String courseId = instructorToDelete.getCourseId();

        loginAsInstructor(instructorToDelete.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorToDelete.getGoogleId(),
        };

        assertEquals(logic.getInstructorsByCourse(courseId).size(), 1);

        InvalidOperationException ioe = verifyInvalidOperation(submissionParams);
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", ioe.getMessage());

        assertNotNull(logic.getInstructorForEmail(instructorToDelete.getCourseId(), instructorToDelete.getEmail()));
        assertNotNull(logic.getInstructorByGoogleId(instructorToDelete.getCourseId(), instructorToDelete.getGoogleId()));
    }

    @Test
    protected void testExecute_deleteLastInstructorInMasqueradeByGoogleId_shouldFail() {
        Instructor instructorToDelete = typicalBundle.instructors.get("instructor1OfCourse3");
        String courseId = instructorToDelete.getCourseId();

        loginAsAdmin();

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorToDelete.getGoogleId(),
        };

        assertEquals(logic.getInstructorsByCourse(courseId).size(), 1);

        InvalidOperationException ioe = verifyInvalidOperation(
                addUserIdToParams(instructorToDelete.getGoogleId(), submissionParams));
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", ioe.getMessage());

        assertNotNull(logic.getInstructorForEmail(instructorToDelete.getCourseId(), instructorToDelete.getEmail()));
        assertNotNull(logic.getInstructorByGoogleId(instructorToDelete.getCourseId(), instructorToDelete.getGoogleId()));
    }

    @Test
    protected void testExecute_deleteInstructorInMasqueradeByGoogleId_shouldPass() {
        Instructor instructorToDelete = typicalBundle.instructors.get("instructor2OfCourse1");
        String courseId = instructorToDelete.getCourseId();

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorToDelete.getGoogleId(),
        };

        loginAsAdmin();

        assertTrue(logic.getInstructorsByCourse(courseId).size() > 1);

        DeleteInstructorAction deleteInstructorAction =
                getAction(addUserIdToParams(instructorToDelete.getGoogleId(), submissionParams));
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput messageOutput = (MessageOutput) response.getOutput();

        assertEquals("Instructor is successfully deleted.", messageOutput.getMessage());
        assertNull(logic.getInstructorForEmail(courseId, instructorToDelete.getEmail()));
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();

        String[] onlyInstructorParameter = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
        };

        String[] onlyCourseParameter = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        loginAsAdmin();

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(onlyInstructorParameter);
        verifyHttpParameterFailure(onlyCourseParameter);

        loginAsInstructor(instructorId);

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(onlyInstructorParameter);
        verifyHttpParameterFailure(onlyCourseParameter);
    }

    @Test
    protected void testExecute_noSuchInstructor_shouldFail() {
        loginAsAdmin();

        attemptToDeleteFakeInstructorByGoogleId();
        attemptToDeleteFakeInstructorByEmail();

        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        attemptToDeleteFakeInstructorByGoogleId();
        attemptToDeleteFakeInstructorByEmail();
    }

    private void attemptToDeleteFakeInstructorByGoogleId() {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, "fake-googleId",
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        assertNull(logic.getInstructorByGoogleId(instructor1OfCourse1.getCourseId(), "fake-googleId"));

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());
    }

    private void attemptToDeleteFakeInstructorByEmail() {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "fake-instructor@fake-email",
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        assertNull(logic.getInstructorForEmail(instructor1OfCourse1.getCourseId(), "fake-instructor@fake-email"));

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());
    }

    @Test
    protected void testExecute_adminDeletesInstructorInFakeCourse_shouldFail() {
        loginAsAdmin();

        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, "fake-course",
        };

        assertNull(logic.getCourse("fake-course"));

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        Student student = typicalBundle.students.get("student1InCourse1");
        Course course = typicalBundle.courses.get("course1");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyAccessibleForAdmin(params);
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(course,
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, params);
    }

}
