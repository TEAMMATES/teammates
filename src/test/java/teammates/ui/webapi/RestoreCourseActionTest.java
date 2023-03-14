package teammates.ui.webapi;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link RestoreCourseAction}.
 */
@Ignore
public class RestoreCourseActionTest
        extends BaseActionTest<RestoreCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.BIN_COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test (enabled = false)
    public void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();
        String courseId = instructor1OfCourse1.getCourseId();

        loginAsInstructor(instructorId);

        ______TS("Not in recycle bin but valid course");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        RestoreCourseAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput message = (MessageOutput) result.getOutput();

        assertEquals("The course " + courseId + " has been restored.", message.getMessage());
        assertNull(logic.getCourse(instructor1OfCourse1.getCourseId()).getDeletedAt());

        ______TS("Typical case, restore a deleted course from Recycle Bin");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        logic.moveCourseToRecycleBin(courseId);
        CourseAttributes deletedCourse = logic.getCourse(courseId);
        assertNotNull(deletedCourse);
        assertTrue(deletedCourse.isCourseDeleted());

        action = getAction(submissionParams);
        result = getJsonResult(action);
        message = (MessageOutput) result.getOutput();

        assertEquals("The course " + courseId + " has been restored.", message.getMessage());
        assertNull(logic.getCourse(instructor1OfCourse1.getCourseId()).getDeletedAt());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Non-Existent Course");

        String[] nonExistentCourse = new String[] {
                Const.ParamsNames.COURSE_ID, "123C",
        };
        verifyEntityNotFound(nonExistentCourse);
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
