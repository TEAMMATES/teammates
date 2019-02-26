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
import teammates.ui.webapi.action.UpdateStudentAction;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.StudentUpdateRequest;

/**
 * SUT: {@link UpdateStudentAction}.
 */
public class UpdateStudentActionTest extends BaseActionTest<UpdateStudentAction> {

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT;
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
        String newStudentEmail = "newemail@gmail.tmt";
        String newStudentTeam = "new student's team";
        String newStudentComments = "this is new comment after editing";
        StudentUpdateRequest updateRequest = new StudentUpdateRequest(student1InCourse1.name, newStudentEmail,
                newStudentTeam, student1InCourse1.section, newStudentComments);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
                Const.ParamsNames.SESSION_SUMMARY_EMAIL_SEND_CHECK, "true",
        };

        UpdateStudentAction updateAction = getAction(updateRequest, submissionParams);
        JsonResult actionOutput = getJsonResult(updateAction);

        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());
        MessageOutput msgOutput = (MessageOutput) actionOutput.getOutput();
        assertEquals("Student has been updated and email sent", msgOutput.getMessage());
        verifyNumberOfEmailsSent(updateAction, 1);

        EmailWrapper email = getEmailsSent(updateAction).get(0);
        String courseName = coursesLogic.getCourse(instructor1OfCourse1.courseId).getName();
        assertEquals(String.format(EmailType.STUDENT_EMAIL_CHANGED.getSubject(), courseName,
                instructor1OfCourse1.courseId), email.getSubject());
        assertEquals(newStudentEmail, email.getRecipient());

        ______TS("Typical case, successful edit and save student detail with spaces to be trimmed");
        String newStudentEmailToBeTrimmed = "  newemail@gmail.tmt   "; // after trim, this is equal to newStudentEmail
        String newStudentTeamToBeTrimmed = "  New team   ";
        String newStudentCommentsToBeTrimmed = "  this is new comment after editing   ";
        updateRequest = new StudentUpdateRequest(student1InCourse1.name, newStudentEmailToBeTrimmed,
                newStudentTeamToBeTrimmed, student1InCourse1.section, newStudentCommentsToBeTrimmed);

        String[] submissionParamsToBeTrimmed = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, newStudentEmail,
                Const.ParamsNames.SESSION_SUMMARY_EMAIL_SEND_CHECK, "true",
        };

        UpdateStudentAction actionToBeTrimmed = getAction(updateRequest, submissionParamsToBeTrimmed);
        JsonResult outputToBeTrimmed = getJsonResult(actionToBeTrimmed);

        assertEquals(HttpStatus.SC_OK, outputToBeTrimmed.getStatusCode());
        MessageOutput msgTrimmedOutput = (MessageOutput) outputToBeTrimmed.getOutput();
        assertEquals("Student has been updated", msgTrimmedOutput.getMessage());
        verifyNoEmailsSent(actionToBeTrimmed);

        ______TS("Error case, invalid email parameter (email has too many characters)");

        String invalidStudentEmail = StringHelperExtension.generateStringOfLength(255 - "@gmail.tmt".length())
                + "@gmail.tmt";
        assertEquals(FieldValidator.EMAIL_MAX_LENGTH + 1, invalidStudentEmail.length());

        updateRequest = new StudentUpdateRequest(student1InCourse1.name, invalidStudentEmail,
                student1InCourse1.team, student1InCourse1.section, student1InCourse1.comments);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, newStudentEmail,
                Const.ParamsNames.SESSION_SUMMARY_EMAIL_SEND_CHECK, "false",
        };

        UpdateStudentAction invalidEmailAction = getAction(updateRequest, submissionParams);
        JsonResult invalidEmailOutput = getJsonResult(invalidEmailAction);

        assertEquals(HttpStatus.SC_BAD_REQUEST, invalidEmailOutput.getStatusCode());
        MessageOutput invalidParamsOutput = (MessageOutput) invalidEmailOutput.getOutput();

        assertEquals(getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, invalidStudentEmail,
                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.EMAIL_MAX_LENGTH),
                invalidParamsOutput.getMessage());

        ______TS("Error case, invalid email parameter (email already taken by others)");

        StudentAttributes student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        String takenStudentEmail = student2InCourse1.email;

        updateRequest = new StudentUpdateRequest(student1InCourse1.name, takenStudentEmail,
                student1InCourse1.team, student1InCourse1.section, student1InCourse1.comments);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, newStudentEmail,
                Const.ParamsNames.SESSION_SUMMARY_EMAIL_SEND_CHECK, "false",
        };

        UpdateStudentAction takenEmailAction = getAction(updateRequest, submissionParams);
        JsonResult takenEmailOutput = getJsonResult(takenEmailAction);

        assertEquals(HttpStatus.SC_CONFLICT, takenEmailOutput.getStatusCode());
        invalidParamsOutput = (MessageOutput) takenEmailOutput.getOutput();

        assertEquals("Trying to update to an email that is already used", invalidParamsOutput.getMessage());

        // deleting edited student
        AccountsLogic.inst().deleteAccountCascade(student2InCourse1.googleId);
        AccountsLogic.inst().deleteAccountCascade(student1InCourse1.googleId);

        ______TS("Error case, student does not exist");

        String nonExistentEmailForStudent = "notinuseemail@gmail.tmt";
        updateRequest = new StudentUpdateRequest(student1InCourse1.name, student1InCourse1.email,
                student1InCourse1.team, student1InCourse1.section, student1InCourse1.comments);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, nonExistentEmailForStudent,
                Const.ParamsNames.SESSION_SUMMARY_EMAIL_SEND_CHECK, "false",
        };

        UpdateStudentAction nonExistentStudentAction = getAction(updateRequest, submissionParams);
        JsonResult nonExistentStudentOuput = getJsonResult(nonExistentStudentAction);

        assertEquals(HttpStatus.SC_NOT_FOUND, nonExistentStudentOuput.getStatusCode());
        invalidParamsOutput = (MessageOutput) nonExistentStudentOuput.getOutput();

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

        ______TS("Only instructors of same course can access");

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyInaccessibleWithoutModifyStudentPrivilege(submissionParams);

        ______TS("Instructors of other courses cannot access");

        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
    }
}
