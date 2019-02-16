package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.InstructorsLogic;
import teammates.ui.webapi.action.ArchiveCourseAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.request.CourseArchiveRequest;

/**
 * SUT: {@link ArchiveCourseAction}.
 */
public class ArchiveCourseActionTest extends BaseActionTest<ArchiveCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_ARCHIVE;
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

        loginAsInstructor(instructorId);

        ______TS("Not enough parameters");
        verifyHttpParameterFailure();

        ______TS("Typical case: archive a course");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        CourseArchiveRequest courseArchiveRequest = new CourseArchiveRequest();
        courseArchiveRequest.setArchiveStatus(true);

        ArchiveCourseAction archiveAction = getAction(courseArchiveRequest, submissionParams);
        JsonResult result = getJsonResult(archiveAction);

        InstructorAttributes theInstructor = InstructorsLogic.inst().getInstructorForGoogleId(
                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getGoogleId());

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertTrue(theInstructor.isArchived);

        ______TS("Rare case: archive an already archived course");

        courseArchiveRequest.setArchiveStatus(true);

        archiveAction = getAction(courseArchiveRequest, submissionParams);
        result = getJsonResult(archiveAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        ______TS("Typical case: unarchive a course, redirect to Courses page");

        courseArchiveRequest.setArchiveStatus(false);

        ArchiveCourseAction unarchiveAction = getAction(courseArchiveRequest, submissionParams);
        result = getJsonResult(unarchiveAction);

        theInstructor = InstructorsLogic.inst().getInstructorForGoogleId(instructor1OfCourse1.getCourseId(),
                instructor1OfCourse1.getGoogleId());

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertFalse(theInstructor.isArchived);

        ______TS("Rare case: unarchive an active course, redirect to Courses page");

        courseArchiveRequest.setArchiveStatus(false);

        unarchiveAction = getAction(courseArchiveRequest, submissionParams);
        result = getJsonResult(unarchiveAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        ______TS("Masquerade mode: archive course, redirect to Courses page");

        loginAsAdmin();
        courseArchiveRequest.setArchiveStatus(true);

        archiveAction = getAction(courseArchiveRequest, addUserIdToParams(instructorId, submissionParams));
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
