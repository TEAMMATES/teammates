package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.InstructorsLogic;
import teammates.ui.webapi.action.DeleteInstructorInCourseAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link DeleteInstructorInCourseAction}.
 */
public class DeleteInstructorInCourseActionTest extends BaseActionTest<DeleteInstructorInCourseAction> {

    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_DELETE_INSTRUCTOR;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    protected void testExecute() {
        InstructorAttributes loginInstructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String loginInstructorId = loginInstructor.googleId;
        String courseId = loginInstructor.courseId;

        loginAsInstructor(loginInstructorId);

        ______TS("Typical case: Delete other instructor successfully");

        InstructorAttributes instructorToDelete = typicalBundle.instructors.get("instructor2OfCourse1");
        String instructorEmailToDelete = instructorToDelete.email;

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmailToDelete,
        };

        DeleteInstructorInCourseAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        MessageOutput msg = (MessageOutput) r.getOutput();
        assertEquals("The instructor has been deleted from the course.", msg.getMessage());

        assertFalse(instructorsLogic.isEmailOfInstructorOfCourse(instructorEmailToDelete, courseId));

        ______TS("Success: delete own instructor role from course");

        instructorEmailToDelete = loginInstructor.email;

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmailToDelete,
        };

        a = getAction(submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        assertEquals("The instructor has been deleted from the course.", msg.getMessage());

        assertFalse(instructorsLogic.isGoogleIdOfInstructorOfCourse(loginInstructor.googleId, courseId));

        ______TS("Masquerade mode: delete instructor failed due to last instructor in course");

        instructorToDelete = typicalBundle.instructors.get("instructor4");
        instructorEmailToDelete = instructorToDelete.email;
        courseId = instructorToDelete.courseId;

        loginAsAdmin();

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmailToDelete,
        };

        a = getAction(addUserIdToParams(instructorToDelete.googleId, submissionParams));
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", msg.getMessage());

        assertTrue(instructorsLogic.isGoogleIdOfInstructorOfCourse(instructorToDelete.googleId, courseId));

        ______TS("Masquerade mode: delete instructor failed due to last instructor being displayed in course");

        instructorToDelete = typicalBundle.instructors.get("instructorNotDisplayedToStudent1");
        instructorEmailToDelete = instructorToDelete.email;
        courseId = instructorToDelete.courseId;

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmailToDelete,
        };

        a = getAction(addUserIdToParams(instructorToDelete.googleId, submissionParams));
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());
        msg = (MessageOutput) r.getOutput();

        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", msg.getMessage());

        assertTrue(instructorsLogic.isGoogleIdOfInstructorOfCourse(instructorToDelete.googleId, courseId));

        ______TS("Masquerade mode: delete instructor success as at least one more instructor there that is being displayed");

        instructorToDelete = typicalBundle.instructors.get("instructorNotDisplayedToStudent2");
        instructorEmailToDelete = instructorToDelete.email;
        courseId = instructorToDelete.courseId;

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmailToDelete,
        };

        a = getAction(addUserIdToParams(instructorToDelete.googleId, submissionParams));
        r = getJsonResult(a);

        instructorsLogic.deleteInstructorCascade(courseId, instructorEmailToDelete);
        verifyAbsentInDatastore(instructorToDelete);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        msg = (MessageOutput) r.getOutput();

        assertEquals("The instructor has been deleted from the course.", msg.getMessage());
        assertFalse(instructorsLogic.isGoogleIdOfInstructorOfCourse(loginInstructor.googleId, courseId));
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor2OfCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.email,
        };

        verifyInaccessibleWithoutModifyInstructorPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
