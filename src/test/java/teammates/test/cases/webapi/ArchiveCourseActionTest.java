package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.ArchiveCourseAction;
import teammates.ui.webapi.action.JsonResult;

/**
 * SUT: {@link ArchiveCourseAction}.
 */
public class ArchiveCourseActionTest extends BaseActionTest<ArchiveCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        loginAsInstructor(instructorId);

        ______TS("Not enough parameters");
        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, courseId);
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true");

        ______TS("Typical case: archive a course");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
        };

        ArchiveCourseAction archiveAction = getAction(submissionParams);
        JsonResult result = getJsonResult(archiveAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        ______TS("Rare case: archive an already archived course");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
        };

        archiveAction = getAction(submissionParams);
        result = getJsonResult(archiveAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        ______TS("Typical case: unarchive a course, redirect to Courses page");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "false",
        };

        ArchiveCourseAction unarchiveAction = getAction(submissionParams);
        result = getJsonResult(unarchiveAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        ______TS("Rare case: unarchive an active course, redirect to Courses page");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "false",
        };

        unarchiveAction = getAction(submissionParams);
        result = getJsonResult(unarchiveAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        ______TS("Masquerade mode: archive course, redirect to Courses page");

        loginAsAdmin();
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
        };
        archiveAction = getAction(addUserIdToParams(instructorId, submissionParams));
        result = getJsonResult(archiveAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
    }

    @Override
    @Test
    protected void testAccessControl() {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
