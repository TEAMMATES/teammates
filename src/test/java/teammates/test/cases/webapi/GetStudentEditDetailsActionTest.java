package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetStudentEditDetailsAction;
import teammates.ui.webapi.action.GetStudentEditDetailsAction.StudentEditDetails;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link GetStudentEditDetailsAction}.
 */
public class GetStudentEditDetailsActionTest extends BaseActionTest<GetStudentEditDetailsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_EDIT_DETAILS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        loginAsInstructor(instructorId);

        ______TS("Invalid parameters");

        //no parameters
        verifyHttpParameterFailure();

        //null student email
        String[] invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };
        verifyHttpParameterFailure(invalidParams);

        //null course id
        invalidParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
        };
        verifyHttpParameterFailure(invalidParams);

        ______TS("Typical case, view student edit details");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
        };

        GetStudentEditDetailsAction a = getAction(submissionParams);
        JsonResult result = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        StudentEditDetails output = (StudentEditDetails) result.getOutput();

        /**
         * Checks that the same student is being requested and the value of boolean
         * 'isOpenOrPublishedEmailSentForTheCourse' is correct
         */
        assertEquals(student1InCourse1.toString(), output.getStudent().toString());
        assertEquals(logic.isOpenOrPublishedEmailSentForTheCourse(instructor1OfCourse1.courseId),
                output.isOpenOrPublishedEmailSentForTheCourse());

        ______TS("Error case, student does not exist");

        String nonExistentEmailForStudent = "notinuseemail@gmail.tmt";

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, nonExistentEmailForStudent,
        };

        a = getAction(submissionParams);
        result = getJsonResult(a);

        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());
        MessageOutput invalidParamsOutput = (MessageOutput) result.getOutput();

        assertEquals("No student with given email in given course.",
                invalidParamsOutput.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student3InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
        };

        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
    }
}
