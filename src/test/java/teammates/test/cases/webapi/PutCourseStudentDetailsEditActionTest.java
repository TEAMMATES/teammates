package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.FieldValidator;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.test.driver.StringHelperExtension;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.PutCourseStudentDetailsEditAction;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link PutCourseStudentDetailsEditAction}.
 */
public class PutCourseStudentDetailsEditActionTest extends BaseActionTest<PutCourseStudentDetailsEditAction> {

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_STUDENT_DETAILS_EDIT;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        String instructorId = instructor1OfCourse1.googleId;
        String newStudentEmail = "newemail@gmail.tmt";
        String newStudentTeam = "new student's team";
        String newStudentComments = "this is new comment after editing";
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

        ______TS("Typical case, successful edit and save student detail");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
                Const.ParamsNames.STUDENT_NAME, student1InCourse1.name,
                Const.ParamsNames.NEW_STUDENT_EMAIL, newStudentEmail,
                Const.ParamsNames.COMMENTS, newStudentComments,
                Const.ParamsNames.TEAM_NAME, newStudentTeam,
                Const.ParamsNames.SESSION_SUMMARY_EMAIL_SEND_CHECK, "true",
        };

        PutCourseStudentDetailsEditAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        MessageOutput msgOutput = (MessageOutput) r.getOutput();
        assertEquals(Const.StatusMessages.STUDENT_EDITED_AND_EMAIL_SENT,
                msgOutput.getMessage());
        verifyNumberOfEmailsSent(a, 1);

        EmailWrapper email = getEmailsSent(a).get(0);
        String courseName = coursesLogic.getCourse(instructor1OfCourse1.courseId).getName();
        assertEquals(String.format(EmailType.STUDENT_EMAIL_CHANGED.getSubject(), courseName, instructor1OfCourse1.courseId),
                email.getSubject());
        assertEquals(newStudentEmail, email.getRecipient());

        ______TS("Typical case, successful edit and save student detail with spaces to be trimmed");

        String newStudentEmailToBeTrimmed = "  newemail@gmail.tmt   "; // after trim, this is equal to newStudentEmail
        String newStudentTeamToBeTrimmed = "  New team   ";
        String newStudentCommentsToBeTrimmed = "  this is new comment after editing   ";

        String[] submissionParamsToBeTrimmed = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, newStudentEmail,
                Const.ParamsNames.STUDENT_NAME, student1InCourse1.name,
                Const.ParamsNames.NEW_STUDENT_EMAIL, newStudentEmailToBeTrimmed,
                Const.ParamsNames.COMMENTS, newStudentCommentsToBeTrimmed,
                Const.ParamsNames.TEAM_NAME, newStudentTeamToBeTrimmed,
                Const.ParamsNames.SESSION_SUMMARY_EMAIL_SEND_CHECK, "true",
        };

        PutCourseStudentDetailsEditAction aToBeTrimmed = getAction(submissionParamsToBeTrimmed);
        JsonResult rToBeTrimmed = getJsonResult(aToBeTrimmed);

        assertEquals(HttpStatus.SC_OK, rToBeTrimmed.getStatusCode());
        MessageOutput msgTrimmedOutput = (MessageOutput) rToBeTrimmed.getOutput();
        assertEquals(Const.StatusMessages.STUDENT_EDITED,
                msgTrimmedOutput.getMessage());
        verifyNoEmailsSent(aToBeTrimmed);

        ______TS("Error case, invalid email parameter (email has too many characters)");

        String invalidStudentEmail = StringHelperExtension.generateStringOfLength(255 - "@gmail.tmt".length())
                + "@gmail.tmt";
        assertEquals(FieldValidator.EMAIL_MAX_LENGTH + 1, invalidStudentEmail.length());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, newStudentEmail, //Use the new email as the previous email have been changed
                Const.ParamsNames.STUDENT_NAME, student1InCourse1.name,
                Const.ParamsNames.NEW_STUDENT_EMAIL, invalidStudentEmail,
                Const.ParamsNames.COMMENTS, student1InCourse1.comments,
                Const.ParamsNames.TEAM_NAME, student1InCourse1.team,
        };

        loginAsInstructor(instructorId);
        a = getAction(submissionParams);
        JsonResult result = getJsonResult(a);

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());
        MessageOutput invalidParamsOutput = (MessageOutput) result.getOutput();

        assertEquals(getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, invalidStudentEmail,
                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.EMAIL_MAX_LENGTH),
                invalidParamsOutput.getMessage());

        ______TS("Error case, invalid email parameter (email already taken by others)");

        StudentAttributes student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        String takenStudentEmail = student2InCourse1.email;

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, newStudentEmail, //Use the new email as the previous email have been changed
                Const.ParamsNames.STUDENT_NAME, student1InCourse1.name,
                Const.ParamsNames.NEW_STUDENT_EMAIL, takenStudentEmail,
                Const.ParamsNames.COMMENTS, student1InCourse1.comments,
                Const.ParamsNames.TEAM_NAME, student1InCourse1.team,
        };

        loginAsInstructor(instructorId);
        a = getAction(submissionParams);
        result = getJsonResult(a);

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());
        invalidParamsOutput = (MessageOutput) result.getOutput();

        assertEquals("Trying to update to an email that is already used", invalidParamsOutput.getMessage());

        // deleting edited student
        AccountsLogic.inst().deleteAccountCascade(student2InCourse1.googleId);
        AccountsLogic.inst().deleteAccountCascade(student1InCourse1.googleId);

        ______TS("Error case, student does not exist");

        String nonExistentEmailForStudent = "notinuseemail@gmail.tmt";

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, nonExistentEmailForStudent,
                Const.ParamsNames.STUDENT_NAME, student1InCourse1.name,
                Const.ParamsNames.NEW_STUDENT_EMAIL, student1InCourse1.email,
                Const.ParamsNames.COMMENTS, student1InCourse1.comments,
                Const.ParamsNames.TEAM_NAME, student1InCourse1.team,
        };

        loginAsInstructor(instructorId);
        a = getAction(submissionParams);
        result = getJsonResult(a);

        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());
        invalidParamsOutput = (MessageOutput) result.getOutput();

        assertEquals(Const.StatusMessages.STUDENT_NOT_FOUND_FOR_EDIT,
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

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyInaccessibleWithoutModifyStudentPrivilege(submissionParams);
    }
}
