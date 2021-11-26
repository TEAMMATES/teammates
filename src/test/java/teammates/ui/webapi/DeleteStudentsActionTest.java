package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link DeleteStudentsAction}.
 */
public class DeleteStudentsActionTest extends BaseActionTest<DeleteStudentsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    protected void testExecute() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("success: delete all students");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        DeleteStudentsAction action = getAction(submissionParams);
        getJsonResult(action);

        ______TS("fails silently if random course given");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "RANDOM_ID",
        };

        action = getAction(submissionParams);
        getJsonResult(action);

        ______TS("failure: invalid params");

        verifyHttpParameterFailure();
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT, submissionParams);
    }

}
