package teammates.test.cases.action;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.logic.core.CoursesLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseAddAction;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorCoursesPageData;

/**
 * SUT: {@link InstructorCourseAddAction}.
 */
public class InstructorCourseAddActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_ADD;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;

        String adminUserId = "admin.user";

        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Not enough parameters");
        verifyAssumptionFailure();
        verifyAssumptionFailure(Const.ParamsNames.COURSE_NAME, "ticac tac name");

        ______TS("Error: Invalid parameter for Course ID");

        String invalidCourseId = "ticac,tpa1,id";
        InstructorCourseAddAction addAction = getAction(Const.ParamsNames.COURSE_ID, invalidCourseId,
                                                        Const.ParamsNames.COURSE_NAME, "ticac tpa1 name",
                                                        Const.ParamsNames.COURSE_TIME_ZONE, "UTC");
        ShowPageResult pageResult = getShowPageResult(addAction);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_COURSES, true, "idOfInstructor1OfCourse1"),
                pageResult.getDestinationWithParams());

        assertTrue(pageResult.isError);
        assertEquals(getPopulatedErrorMessage(
                         FieldValidator.COURSE_ID_ERROR_MESSAGE, invalidCourseId,
                         FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                         FieldValidator.COURSE_ID_MAX_LENGTH),
                     pageResult.getStatusMessage());

        InstructorCoursesPageData pageData = (InstructorCoursesPageData) pageResult.data;
        assertEquals(1, pageData.getActiveCourses().getRows().size() + pageData.getArchivedCourses().getRows().size());

        String expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd|||instructorCourseAdd|||true|||Instructor|||"
                                    + "Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + getPopulatedErrorMessage(
                                          FieldValidator.COURSE_ID_ERROR_MESSAGE, invalidCourseId,
                                          FieldValidator.COURSE_ID_FIELD_NAME,
                                          FieldValidator.REASON_INCORRECT_FORMAT,
                                          FieldValidator.COURSE_ID_MAX_LENGTH)
                                    + "|||/page/instructorCourseAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, addAction.getLogMessage());

        ______TS("Typical case, 1 existing course");

        addAction = getAction(Const.ParamsNames.COURSE_ID, "ticac.tpa1.id",
                              Const.ParamsNames.COURSE_NAME, "ticac tpa1 name",
                              Const.ParamsNames.COURSE_TIME_ZONE, "UTC");
        RedirectResult redirectResult = getRedirectResult(addAction);

        List<CourseAttributes> courseList = CoursesLogic.inst().getCoursesForInstructor(instructorId);
        assertEquals(2, courseList.size());

        expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd|||instructorCourseAdd|||true|||Instructor|||"
                             + "Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Course added : ticac.tpa1.id<br>Total courses: 2|||/page/instructorCourseAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, addAction.getLogMessage());

        String expected = Const.StatusMessages.COURSE_ADDED
                  .replace("${courseEnrollLink}",
                          getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE, "ticac.tpa1.id",
                                  "idOfInstructor1OfCourse1"))
                  .replace("${courseEditLink}",
                          getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, "ticac.tpa1.id",
                                  "idOfInstructor1OfCourse1"));
        assertEquals(expected, redirectResult.getStatusMessage());

        ______TS("Error: Try to add the same course again");

        addAction = getAction(Const.ParamsNames.COURSE_ID, "ticac.tpa1.id",
                              Const.ParamsNames.COURSE_NAME, "ticac tpa1 name",
                              Const.ParamsNames.COURSE_TIME_ZONE, "UTC");
        pageResult = getShowPageResult(addAction);

        assertEquals(getPageResultDestination(Const.ViewURIs.INSTRUCTOR_COURSES, true, "idOfInstructor1OfCourse1"),
                     pageResult.getDestinationWithParams());
        assertTrue(pageResult.isError);
        assertEquals(Const.StatusMessages.COURSE_EXISTS, pageResult.getStatusMessage());

        pageData = (InstructorCoursesPageData) pageResult.data;
        assertEquals(2, pageData.getActiveCourses().getRows().size() + pageData.getArchivedCourses().getRows().size());

        expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd|||instructorCourseAdd|||true|||Instructor|||"
                             + "Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "A course by the same ID already exists in the system, possibly created by another "
                             + "user. Please choose a different course ID|||/page/instructorCourseAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, addAction.getLogMessage());

        ______TS("Masquerade mode, 0 courses");

        CoursesLogic.inst().deleteCourseCascade(instructor1OfCourse1.courseId);
        CoursesLogic.inst().deleteCourseCascade("ticac.tpa1.id");
        gaeSimulation.loginAsAdmin(adminUserId);
        addAction = getAction(Const.ParamsNames.USER_ID, instructorId,
                              Const.ParamsNames.COURSE_ID, "ticac.tpa2.id",
                              Const.ParamsNames.COURSE_NAME, "ticac tpa2 name",
                              Const.ParamsNames.COURSE_TIME_ZONE, "UTC");
        redirectResult = getRedirectResult(addAction);

        String expectedDestination = getPageResultDestination(
                Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, false, "idOfInstructor1OfCourse1");
        assertEquals(expectedDestination, redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        String expectedStatus = "The course has been added. Click <a href=\"/page/instructorCourseEnrollPage?"
                                + "courseid=ticac.tpa2.id&user=idOfInstructor1OfCourse1\">here</a> to add students "
                                + "to the course or click <a href=\"/page/instructorCourseEditPage?"
                                + "courseid=ticac.tpa2.id&user=idOfInstructor1OfCourse1\">here</a> to add other "
                                + "instructors.<br>If you don't see the course in the list below, please refresh "
                                + "the page after a few moments.";
        assertEquals(expectedStatus, redirectResult.getStatusMessage());

        courseList = CoursesLogic.inst().getCoursesForInstructor(instructorId);
        assertEquals(1, courseList.size());
        expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd|||instructorCourseAdd|||true|||Instructor(M)|||"
                             + "Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Course added : ticac.tpa2.id<br>Total courses: 1|||/page/instructorCourseAdd";
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(expectedLogMessage, addAction.getLogMessage(), adminUserId);

        // delete the new course
        CoursesLogic.inst().deleteCourseCascade("ticac.tpa2.id");

        ______TS("Test archived Courses");
        InstructorAttributes instructorOfArchivedCourse = typicalBundle.instructors.get("instructorOfArchivedCourse");
        instructorId = instructorOfArchivedCourse.googleId;

        gaeSimulation.loginAsInstructor(instructorId);

        addAction = getAction(Const.ParamsNames.COURSE_ID, "ticac.tpa2.id",
                              Const.ParamsNames.COURSE_NAME, "ticac tpa2 name",
                              Const.ParamsNames.COURSE_TIME_ZONE, "UTC");
        redirectResult = getRedirectResult(addAction);

        courseList = CoursesLogic.inst().getCoursesForInstructor(instructorId);
        assertEquals(2, courseList.size());
        expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd|||instructorCourseAdd|||true|||Instructor|||"
                             + "InstructorOfArchiveCourse name|||idOfInstructorOfArchivedCourse|||"
                             + "instructorOfArchiveCourse@archiveCourse.tmt|||Course added : ticac.tpa2.id<br>"
                             + "Total courses: 2|||/page/instructorCourseAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, addAction.getLogMessage());

        expected = Const.StatusMessages.COURSE_ADDED
                .replace("${courseEnrollLink}",
                        getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE, "ticac.tpa2.id",
                                "idOfInstructorOfArchivedCourse"))
                .replace("${courseEditLink}",
                        getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, "ticac.tpa2.id",
                                "idOfInstructorOfArchivedCourse"));
        assertEquals(expected, redirectResult.getStatusMessage());
    }

    @Override
    protected InstructorCourseAddAction getAction(String... params) {
        return (InstructorCourseAddAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    protected String getPageResultDestination(String parentUri, String courseId, String userId) {
        String pageDestination = parentUri;
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.COURSE_ID, courseId);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.USER_ID, userId);
        return pageDestination;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "ticac.tac.id",
                Const.ParamsNames.COURSE_NAME, "ticac tac name",
                Const.ParamsNames.COURSE_TIME_ZONE, "UTC"
        };

        verifyOnlyInstructorsCanAccess(submissionParams);

        // remove course that was created
        CoursesLogic.inst().deleteCourseCascade("ticac.tac.id");
    }
}
