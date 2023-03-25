package teammates.ui.webapi;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link DeleteCourseAction}.
 */
@Ignore
public class DeleteCourseActionTest
        extends BaseActionTest<DeleteCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    public void testExecute() throws Exception {

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();
        String courseId = instructor1OfCourse1.getCourseId();

        ______TS("Typical case, delete a soft-deleted course in Recycle Bin");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        CourseAttributes courseToBeDeleted = logic.getCourse(courseId);
        loginAsInstructor(instructorId);
        logic.moveCourseToRecycleBin(courseToBeDeleted.getId());
        CourseAttributes deletedCourse = logic.getCourse(courseId);
        assertNotNull(deletedCourse);
        assertTrue(deletedCourse.isCourseDeleted());

        DeleteCourseAction deleteCourseAction = getAction(submissionParams);
        JsonResult result = getJsonResult(deleteCourseAction);
        MessageOutput messageOutput = (MessageOutput) result.getOutput();

        assertEquals("OK", messageOutput.getMessage());
        assertNull(logic.getCourse(instructor1OfCourse1.getCourseId()));
    }

    @Test
    public void testExecute_notInRecycleBin_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();
        String courseId = instructor1OfCourse1.getCourseId();

        ______TS("delete a course not in Recycle Bin");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        CourseAttributes courseToBeDeleted = logic.getCourse(instructor1OfCourse1.getCourseId());
        assertNull(courseToBeDeleted.getDeletedAt());
        loginAsInstructor(instructorId);

        DeleteCourseAction deleteCourseAction = getAction(submissionParams);
        JsonResult result = getJsonResult(deleteCourseAction);
        MessageOutput messageOutput = (MessageOutput) result.getOutput();

        assertEquals("OK", messageOutput.getMessage());
        assertNull(logic.getCourse(instructor1OfCourse1.getCourseId()));
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_COURSE, submissionParams);
    }
}
