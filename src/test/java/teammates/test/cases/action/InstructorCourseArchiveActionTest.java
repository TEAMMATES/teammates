package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseArchiveAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorCourseArchiveAction}.
 */
public class InstructorCourseArchiveActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_ARCHIVE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        String[] submissionParams = new String[] {};

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;
        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Not enough parameters");

        verifyAssumptionFailure();
        verifyAssumptionFailure(Const.ParamsNames.COURSE_ID, courseId);
        verifyAssumptionFailure(Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true");

        ______TS("Typical case: archive a course, redirect to homepage");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_HOME_PAGE
        };

        InstructorCourseArchiveAction archiveAction = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(archiveAction);

        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_HOME_PAGE, false, "idOfInstructor1OfCourse1"),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_ARCHIVED_FROM_HOMEPAGE, courseId),
                     redirectResult.getStatusMessage());

        String expectedLogSegment = "Course archived: " + courseId;
        AssertHelper.assertContains(expectedLogSegment, archiveAction.getLogMessage());

        ______TS("Rare case: archive an already archived course, redirect to homepage");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_HOME_PAGE
        };

        archiveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(archiveAction);

        assertEquals(getPageResultDestination(Const.ActionURIs.INSTRUCTOR_HOME_PAGE, false, "idOfInstructor1OfCourse1"),
                     redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_ARCHIVED_FROM_HOMEPAGE, courseId),
                     redirectResult.getStatusMessage());

        expectedLogSegment = "Course archived: " + courseId;
        AssertHelper.assertContains(expectedLogSegment, archiveAction.getLogMessage());

        ______TS("Typical case: unarchive a course, redirect to Courses page");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "false",
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE
        };

        InstructorCourseArchiveAction unarchiveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(unarchiveAction);

        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, false, "idOfInstructor1OfCourse1"),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_UNARCHIVED, courseId),
                     redirectResult.getStatusMessage());

        expectedLogSegment = "Course unarchived: " + courseId;
        AssertHelper.assertContains(expectedLogSegment, unarchiveAction.getLogMessage());

        ______TS("Rare case: unarchive an active course, redirect to Courses page");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "false",
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE
        };

        unarchiveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(unarchiveAction);

        assertEquals(getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, false, "idOfInstructor1OfCourse1"),
                     redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_UNARCHIVED, courseId),
                     redirectResult.getStatusMessage());

        expectedLogSegment = "Course unarchived: " + courseId;
        AssertHelper.assertContains(expectedLogSegment, unarchiveAction.getLogMessage());

        ______TS("Rare case: unarchive an active course, no next URL, redirect to Courses page");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "false",
        };

        unarchiveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(unarchiveAction);

        assertEquals(getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, false, "idOfInstructor1OfCourse1"),
                     redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_UNARCHIVED, courseId),
                     redirectResult.getStatusMessage());

        expectedLogSegment = "Course unarchived: " + courseId;
        AssertHelper.assertContains(expectedLogSegment, unarchiveAction.getLogMessage());

        ______TS("Masquerade mode: archive course, redirect to Courses page");

        gaeSimulation.loginAsAdmin("admin.user");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE
        };
        archiveAction = getAction(addUserIdToParams(instructorId, submissionParams));
        redirectResult = getRedirectResult(archiveAction);

        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, false, "idOfInstructor1OfCourse1"),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_ARCHIVED, courseId),
                     redirectResult.getStatusMessage());

        expectedLogSegment = "Course archived: " + courseId;
        AssertHelper.assertContains(expectedLogSegment, archiveAction.getLogMessage());

        ______TS("Rare case: empty course ID");

        gaeSimulation.loginAsAdmin("admin.user");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "",
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE
        };
        archiveAction = getAction(addUserIdToParams(instructorId, submissionParams));

        try {
            redirectResult = getRedirectResult(archiveAction);
            signalFailureToDetectException(" - IllegalArgumentException");
        } catch (Exception e) {
            AssertHelper.assertContains("name cannot be null or empty", e.getMessage());
        }

        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, false, "idOfInstructor1OfCourse1"),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_ARCHIVED, courseId), redirectResult.getStatusMessage());

        expectedLogSegment = "TEAMMATESLOG|||instructorCourseArchive|||instructorCourseArchive|||true|||"
                             + "Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                             + "instr1@course1.tmt|||Unknown|||/page/instructorCourseArchive";
        AssertHelper.assertContains(expectedLogSegment, archiveAction.getLogMessage());

    }

    @Override
    protected InstructorCourseArchiveAction getAction(String... params) {
        return (InstructorCourseArchiveAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true"
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
