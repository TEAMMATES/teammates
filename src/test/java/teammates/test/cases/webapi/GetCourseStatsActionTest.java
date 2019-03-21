package teammates.test.cases.webapi;

import java.time.ZoneId;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.ui.webapi.action.GetCourseStatsAction;
import teammates.ui.webapi.action.GetCourseStatsAction.CourseStats;
import teammates.ui.webapi.action.JsonResult;

/**
 * SUT: {@link GetCourseStatsAction}.
 */
public class GetCourseStatsActionTest extends BaseActionTest<GetCourseStatsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_STATS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() throws Exception {

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId,
        };

        ______TS("Typical case with existing course id");

        if (CoursesLogic.inst().isCoursePresent("new-course")) {
            CoursesLogic.inst().deleteCourseCascade("new-course");
        }

        CoursesLogic.inst().createCourseAndInstructor(instructorId,
                CourseAttributes.builder("new-course")
                        .withName("New course")
                        .withTimezone(ZoneId.of("UTC"))
                        .build());
        loginAsInstructor(instructorId);
        GetCourseStatsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CourseStats output = (CourseStats) result.getOutput();
        assertEquals(2, output.getSectionsTotal());
        assertEquals(5, output.getStudentsTotal());
        assertEquals(2, output.getTeamsTotal());
        assertEquals(0, output.getUnregisteredTotal());

        ______TS("Typical case with non-existing course id");

        submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, "idOfUnknownCourse",
        };

        action = getAction(submissionParams);
        result = getJsonResult(action);

        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());

    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};

        verifyOnlyInstructorsCanAccess(submissionParams);
    }
}
