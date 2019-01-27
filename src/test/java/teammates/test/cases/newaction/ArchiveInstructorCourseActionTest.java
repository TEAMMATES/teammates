package teammates.test.cases.newaction;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.newcontroller.ArchiveInstructorCourseAction;
import teammates.ui.newcontroller.JsonResult;
import teammates.ui.newcontroller.JsonResult.MessageOutput;

/**
 * SUT: {@link ArchiveInstructorCourseAction}.
 */
public class ArchiveInstructorCourseActionTest extends BaseActionTest<ArchiveInstructorCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_COURSES_ARCHIVE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    public void testExecute() throws Exception {

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        ______TS("Typical case, archive an active course from home page");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
                Const.ParamsNames.NEXT_URL, Const.ResourceURIs.INSTRUCTOR_HOME
        };

        loginAsInstructor(instructorId);
        ArchiveInstructorCourseAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput message = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertEquals("The course " + courseId + " has been archived. It will not appear in the home page "
                + "any more. You can access archived courses from the 'Courses' tab.<br>Go there to undo the archiving "
                + "and bring the course back to the home page.", message.getMessage());

        ______TS("Typical case, unarchive an archived course");

        submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "false"
        };

        action = getAction(submissionParams);
        result = getJsonResult(action);
        message = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertEquals("The course " + courseId + " has been unarchived.", message.getMessage());

        ______TS("Typical case, archive an active course from courses page");

        submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
                Const.ParamsNames.NEXT_URL, Const.ResourceURIs.INSTRUCTOR_COURSES
        };

        action = getAction(submissionParams);
        result = getJsonResult(action);
        message = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertEquals("The course " + courseId + " has been archived. It will not appear "
                + "in the home page any more.", message.getMessage());

    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};

        verifyOnlyInstructorsCanAccess(submissionParams);
    }
}
