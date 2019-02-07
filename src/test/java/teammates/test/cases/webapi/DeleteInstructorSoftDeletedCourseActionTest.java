package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.ui.webapi.action.DeleteInstructorSoftDeletedCourseAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link DeleteInstructorSoftDeletedCourseAction}.
 */
public class DeleteInstructorSoftDeletedCourseActionTest
        extends BaseActionTest<DeleteInstructorSoftDeletedCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_COURSES_PERMANENTLY_DELETE;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    public void testExecute() throws Exception {

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        ______TS("Typical case, delete a soft-deleted course in Recycle Bin");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId,
        };

        loginAsInstructor(instructorId);
        CoursesLogic.inst().moveCourseToRecycleBin(courseId);
        assertEquals(courseId, CoursesLogic.inst().getSoftDeletedCourseForInstructor(instructor1OfCourse1).getId());

        DeleteInstructorSoftDeletedCourseAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput message = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertEquals("The course " + courseId + " has been permanently deleted.", message.getMessage());

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
