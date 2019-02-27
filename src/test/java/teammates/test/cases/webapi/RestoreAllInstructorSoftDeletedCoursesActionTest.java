package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.RestoreAllInstructorSoftDeletedCoursesAction;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link RestoreAllInstructorSoftDeletedCoursesAction}.
 */
public class RestoreAllInstructorSoftDeletedCoursesActionTest
        extends BaseActionTest<RestoreAllInstructorSoftDeletedCoursesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_COURSES_RESTORE_ALL;
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

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
        };

        ______TS("Typical case, restore all soft-deleted courses from Recycle Bin");

        if (CoursesLogic.inst().isCoursePresent("new-course")) {
            CoursesLogic.inst().deleteCourseCascade("new-course");
        }

        CoursesLogic.inst().createCourseAndInstructor(instructorId, "new-course", "New course", "UTC");
        loginAsInstructor(instructorId);
        assertEquals(2, CoursesLogic.inst().getCoursesForInstructor(instructorId).size());
        CoursesLogic.inst().moveCourseToRecycleBin(courseId);
        CoursesLogic.inst().moveCourseToRecycleBin("new-course");
        assertEquals(0, CoursesLogic.inst().getCoursesForInstructor(instructorId).size());

        RestoreAllInstructorSoftDeletedCoursesAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput message = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertEquals("All courses have been restored.", message.getMessage());

    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};

        verifyOnlyInstructorsCanAccess(submissionParams);
    }
}
