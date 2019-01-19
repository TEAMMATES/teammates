package teammates.test.cases.newaction;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.newcontroller.DeleteInstructorAction;
import teammates.ui.newcontroller.JsonResult;
import teammates.ui.newcontroller.JsonResult.MessageOutput;

/**
 * SUT: {@link DeleteInstructorAction}.
 */
public class DeleteInstructorActionTest extends BaseActionTest<DeleteInstructorAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTORS;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    protected void testExecute() {

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case: instructor deletes an instructor by google id");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        loginAsInstructor(instructorId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId
        };

        DeleteInstructorAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        MessageOutput msg = (MessageOutput) r.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

        ______TS("Typical case: instructor deletes an instructor by email");

        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        InstructorAttributes instructor3OfCourse1 = typicalBundle.instructors.get("instructor3OfCourse1");

        System.out.println(instructor2OfCourse1 == null);
        System.out.println(instructor3OfCourse1 == null);

        instructorId = instructor2OfCourse1.googleId;
        loginAsInstructor(instructorId);

        submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, instructor2OfCourse1.courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor3OfCourse1.email
        };

        a = getAction(submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

        ______TS("Typical case: admin deletes an instructor by google id");

        loginAsAdmin();

        InstructorAttributes instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");
        instructorId = instructor1OfCourse2.googleId;

        submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, instructor1OfCourse2.courseId
        };

        a = getAction(submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

    }

    @Override
    @Test
    protected void testAccessControl() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        verifyAccessibleForAdmin(submissionParams);
        verifyInaccessibleWithoutModifyInstructorPrivilege(submissionParams);
    }

}
