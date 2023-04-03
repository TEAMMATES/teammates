package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.CourseArchiveData;
import teammates.ui.request.CourseArchiveRequest;
import teammates.ui.webapi.ArchiveCourseAction;
import teammates.ui.webapi.JsonResult;

public class ArchiveCourseActionIT extends BaseActionIT<ArchiveCourseAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    String getActionUri() {
        return Const.ResourceURIs.COURSE_ARCHIVE;
    }

    @Override
    String getRequestMethod() {
        return PUT;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();

        loginAsInstructor(instructorId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        CourseArchiveRequest courseArchiveRequest = new CourseArchiveRequest();
        courseArchiveRequest.setArchiveStatus(true);

        ______TS("Not enough parameters");
        verifyHttpParameterFailure();
        verifyHttpRequestBodyFailure(null, submissionParams);
        verifyHttpParameterFailure(courseArchiveRequest);

        ______TS("Typical Success Case: archive a course");

        ArchiveCourseAction archiveCourseAction = getAction(courseArchiveRequest, submissionParams);
        JsonResult result = getJsonResult(archiveCourseAction);
        CourseArchiveData courseArchiveData = (CourseArchiveData) result.getOutput();

        Instructor theInstructor = logic.getInstructorByGoogleId(
                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getGoogleId());

        assertTrue(theInstructor.getIsArchived());
        verifyCourseArchive(courseArchiveData, instructor1OfCourse1.getCourseId(), true);

        ______TS("Rare case: archive an already archived course");

        courseArchiveRequest.setArchiveStatus(true);

        archiveCourseAction = getAction(courseArchiveRequest, submissionParams);
        result = getJsonResult(archiveCourseAction);
        courseArchiveData = (CourseArchiveData) result.getOutput();

        theInstructor = logic.getInstructorByGoogleId(
                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getGoogleId());

        assertTrue(theInstructor.getIsArchived());
        verifyCourseArchive(courseArchiveData, instructor1OfCourse1.getCourseId(), true);

        ______TS("Typical case: unarchive a course");

        courseArchiveRequest.setArchiveStatus(false);

        ArchiveCourseAction unarchiveAction = getAction(courseArchiveRequest, submissionParams);
        result = getJsonResult(unarchiveAction);
        courseArchiveData = (CourseArchiveData) result.getOutput();

        theInstructor = logic.getInstructorByGoogleId(instructor1OfCourse1.getCourseId(),
                instructor1OfCourse1.getGoogleId());

        assertFalse(theInstructor.getIsArchived());
        verifyCourseArchive(courseArchiveData, instructor1OfCourse1.getCourseId(), false);

        ______TS("Rare case: unarchive an active course");

        courseArchiveRequest.setArchiveStatus(false);

        unarchiveAction = getAction(courseArchiveRequest, submissionParams);
        result = getJsonResult(unarchiveAction);
        courseArchiveData = (CourseArchiveData) result.getOutput();

        theInstructor = logic.getInstructorByGoogleId(
                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getGoogleId());

        assertFalse(theInstructor.getIsArchived());
        verifyCourseArchive(courseArchiveData, instructor1OfCourse1.getCourseId(), false);

        ______TS("Masquerade mode: archive course");

        loginAsAdmin();
        courseArchiveRequest.setArchiveStatus(true);

        archiveCourseAction = getAction(courseArchiveRequest, addUserIdToParams(instructorId, submissionParams));
        result = getJsonResult(archiveCourseAction);
        courseArchiveData = (CourseArchiveData) result.getOutput();

        theInstructor = logic.getInstructorByGoogleId(
                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getGoogleId());

        assertTrue(theInstructor.getIsArchived());
        verifyCourseArchive(courseArchiveData, instructor1OfCourse1.getCourseId(), true);
    }

    private void verifyCourseArchive(CourseArchiveData courseArchiveData, String courseId, boolean isArchived) {
        assertEquals(courseArchiveData.getCourseId(), courseId);
        assertEquals(courseArchiveData.getIsArchived(), isArchived);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(instructor.getCourse(), submissionParams);
    }
    
}
