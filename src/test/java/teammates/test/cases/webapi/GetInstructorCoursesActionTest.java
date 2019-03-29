package teammates.test.cases.webapi;

import java.time.ZoneId;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.ui.webapi.action.GetInstructorCoursesAction;
import teammates.ui.webapi.action.GetInstructorCoursesAction.InstructorCourses;
import teammates.ui.webapi.action.JsonResult;

/**
 * SUT: {@link GetInstructorCoursesAction}.
 */
public class GetInstructorCoursesActionTest extends BaseActionTest<GetInstructorCoursesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_COURSES;
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

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
        };

        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("Typical case, 2 courses");

        if (CoursesLogic.inst().isCoursePresent("new-course")) {
            CoursesLogic.inst().deleteCourseCascade("new-course");
        }

        CoursesLogic.inst().createCourseAndInstructor(instructorId,
                CourseAttributes.builder("new-course")
                        .withName("New course")
                        .withTimezone(ZoneId.of("UTC"))
                        .build());
        loginAsInstructor(instructorId);
        GetInstructorCoursesAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        InstructorCourses output = (InstructorCourses) result.getOutput();
        assertEquals(2, output.getInstructorList().size());
        assertEquals(2, output.getActiveCourses().size());
        assertEquals(0, output.getArchivedCourses().size());
        assertEquals(0, output.getSoftDeletedCourses().size());
        assertEquals("idOfInstructor1OfCourse1", output.getInstructorList().get(0).googleId);
        assertEquals("idOfTypicalCourse1", output.getActiveCourses().get(0).getId());

        ______TS("Masquerade mode, 0 courses");

        loginAsAdmin();

        CoursesLogic.inst().deleteCourseCascade(instructor1ofCourse1.courseId);
        CoursesLogic.inst().deleteCourseCascade("new-course");

        action = getAction(addUserIdToParams(instructorId, submissionParams));
        result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        output = (InstructorCourses) result.getOutput();
        assertEquals(0, output.getInstructorList().size());
        assertEquals(0, output.getActiveCourses().size());
        assertEquals(0, output.getArchivedCourses().size());
        assertEquals(0, output.getSoftDeletedCourses().size());

    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};

        verifyOnlyInstructorsCanAccess(submissionParams);
    }
}
