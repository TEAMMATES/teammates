package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.NullHttpParameterException;
import teammates.common.util.Const;
import teammates.ui.output.CourseArchiveData;
import teammates.ui.request.CourseArchiveRequest;

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

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        CourseArchiveRequest courseArchiveRequest = new CourseArchiveRequest();
        courseArchiveRequest.setArchiveStatus(true);

        ______TS("Not enough parameters");
        verifyHttpParameterFailure();
        verifyHttpParameterFailure(submissionParams);
        ArchiveCourseAction archiveCourseActionWithoutParam = getAction(courseArchiveRequest);
        assertThrows(NullHttpParameterException.class, () -> getJsonResult(archiveCourseActionWithoutParam));

        ______TS("Typical case: archive a course");

        ArchiveCourseAction archiveCourseAction = getAction(courseArchiveRequest, submissionParams);
        JsonResult result = getJsonResult(archiveCourseAction);
        CourseArchiveData courseArchiveData = (CourseArchiveData) result.getOutput();

        InstructorAttributes theInstructor = logic.getInstructorForGoogleId(
                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getGoogleId());

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertTrue(theInstructor.isArchived);
        verifyCourseArchive(courseArchiveData, instructor1OfCourse1.courseId, true);

        ______TS("Rare case: archive an already archived course");

        courseArchiveRequest.setArchiveStatus(true);

        archiveCourseAction = getAction(courseArchiveRequest, submissionParams);
        result = getJsonResult(archiveCourseAction);
        courseArchiveData = (CourseArchiveData) result.getOutput();

        theInstructor = logic.getInstructorForGoogleId(
                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getGoogleId());

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertTrue(theInstructor.isArchived);
        verifyCourseArchive(courseArchiveData, instructor1OfCourse1.courseId, true);

        ______TS("Typical case: unarchive a course");

        courseArchiveRequest.setArchiveStatus(false);

        ArchiveCourseAction unarchiveAction = getAction(courseArchiveRequest, submissionParams);
        result = getJsonResult(unarchiveAction);
        courseArchiveData = (CourseArchiveData) result.getOutput();

        theInstructor = logic.getInstructorForGoogleId(instructor1OfCourse1.getCourseId(),
                instructor1OfCourse1.getGoogleId());

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertFalse(theInstructor.isArchived);
        verifyCourseArchive(courseArchiveData, instructor1OfCourse1.courseId, false);

        ______TS("Rare case: unarchive an active course");

        courseArchiveRequest.setArchiveStatus(false);

        unarchiveAction = getAction(courseArchiveRequest, submissionParams);
        result = getJsonResult(unarchiveAction);
        courseArchiveData = (CourseArchiveData) result.getOutput();

        theInstructor = logic.getInstructorForGoogleId(
                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getGoogleId());

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertFalse(theInstructor.isArchived);
        verifyCourseArchive(courseArchiveData, instructor1OfCourse1.courseId, false);

        ______TS("Masquerade mode: archive course");

        loginAsAdmin();
        courseArchiveRequest.setArchiveStatus(true);

        archiveCourseAction = getAction(courseArchiveRequest, addUserIdToParams(instructorId, submissionParams));
        result = getJsonResult(archiveCourseAction);
        courseArchiveData = (CourseArchiveData) result.getOutput();

        theInstructor = logic.getInstructorForGoogleId(
                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getGoogleId());

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertTrue(theInstructor.isArchived);
        verifyCourseArchive(courseArchiveData, instructor1OfCourse1.courseId, true);
    }

    private void verifyCourseArchive(CourseArchiveData courseArchiveData, String courseId, boolean isArchived) {
        assertEquals(courseArchiveData.getCourseId(), courseId);
        assertEquals(courseArchiveData.getIsArchived(), isArchived);
    }

    @Override
    @Test
    protected void testAccessControl() {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").courseId,
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
