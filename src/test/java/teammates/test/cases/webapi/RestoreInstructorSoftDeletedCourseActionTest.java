package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.RestoreInstructorSoftDeletedCourseAction;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link RestoreInstructorSoftDeletedCourseAction}.
 */
public class RestoreInstructorSoftDeletedCourseActionTest
        extends BaseActionTest<RestoreInstructorSoftDeletedCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_COURSES_RESTORE;
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

        ______TS("Typical case, restore a soft-deleted course from Recycle Bin");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId,
        };

        loginAsInstructor(instructorId);
        CoursesLogic.inst().moveCourseToRecycleBin(courseId);
        assertEquals(courseId, CoursesLogic.inst().getSoftDeletedCourseForInstructor(instructor1OfCourse1).getId());

        RestoreInstructorSoftDeletedCourseAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput message = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertEquals("The course " + courseId + " has been restored.", message.getMessage());

    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
        };

        verifyOnlyInstructorsCanAccess(submissionParams);
        verifyInaccessibleWithoutModifyCoursePrivilege(submissionParams);
    }
}
