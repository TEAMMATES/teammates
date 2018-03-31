package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.FieldValidator;
import teammates.common.util.Url;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.StringHelperExtension;
import teammates.ui.controller.InstructorCourseStudentDetailsEditSaveAction;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;

/**
 * SUT: {@link InstructorCourseStudentDetailsEditSaveAction}.
 */
public class InstructorCourseStudentDetailsEditSaveActionTest extends BaseActionTest {

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_SAVE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        String instructorId = instructor1OfCourse1.googleId;
        String newStudentEmail = "newemail@gmail.tmt";
        String newStudentTeam = "new student's team";
        String newStudentComments = "this is new comment after editing";
        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Invalid parameters");

        //no parameters
        verifyAssumptionFailure();

        //null student email
        String[] invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId
        };
        verifyAssumptionFailure(invalidParams);

        //null course id
        invalidParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email
        };
        verifyAssumptionFailure(invalidParams);

        ______TS("Typical case, successful edit and save student detail");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
                Const.ParamsNames.STUDENT_NAME, student1InCourse1.name,
                Const.ParamsNames.NEW_STUDENT_EMAIL, newStudentEmail,
                Const.ParamsNames.COMMENTS, newStudentComments,
                Const.ParamsNames.TEAM_NAME, newStudentTeam,
                Const.ParamsNames.SESSION_SUMMARY_EMAIL_SEND_CHECK, "true"
        };

        InstructorCourseStudentDetailsEditSaveAction a = getAction(submissionParams);
        RedirectResult r = getRedirectResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE,
                        false,
                        "idOfInstructor1OfCourse1",
                        "idOfTypicalCourse1"),
                r.getDestinationWithParams());

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.STUDENT_EDITED_AND_EMAIL_SENT, r.getStatusMessage());

        verifyNumberOfEmailsSent(a, 1);

        EmailWrapper email = getEmailsSent(a).get(0);
        String courseName = coursesLogic.getCourse(instructor1OfCourse1.courseId).getName();
        assertEquals(String.format(EmailType.STUDENT_EMAIL_CHANGED.getSubject(), courseName, instructor1OfCourse1.courseId),
                     email.getSubject());
        assertEquals(newStudentEmail, email.getRecipient());

        String expectedLogMessage =
                "TEAMMATESLOG|||instructorCourseStudentDetailsEditSave|||instructorCourseStudentDetailsEditSave"
                + "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "Student <span class=\"bold\">" + student1InCourse1.email
                + "'s</span> details in Course <span class=\"bold\">[idOfTypicalCourse1]</span> edited.<br>"
                + "New Email: " + newStudentEmail
                + "<br>New Team: " + newStudentTeam
                + "<br>Comments: " + newStudentComments
                + "|||/page/instructorCourseStudentDetailsEditSave";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

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
                Const.ParamsNames.SESSION_SUMMARY_EMAIL_SEND_CHECK, "true"
        };

        InstructorCourseStudentDetailsEditSaveAction aToBeTrimmed = getAction(submissionParamsToBeTrimmed);
        RedirectResult rToBeTrimmed = getRedirectResult(aToBeTrimmed);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE,
                        false,
                        "idOfInstructor1OfCourse1",
                        "idOfTypicalCourse1"),
                rToBeTrimmed.getDestinationWithParams());

        assertFalse(rToBeTrimmed.isError);
        assertEquals(Const.StatusMessages.STUDENT_EDITED, rToBeTrimmed.getStatusMessage());

        verifyNoEmailsSent(aToBeTrimmed);

        String expectedLogMessageToBeTrimmed =
                "TEAMMATESLOG|||instructorCourseStudentDetailsEditSave|||instructorCourseStudentDetailsEditSave"
                + "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "Student <span class=\"bold\">" + newStudentEmail
                + "'s</span> details in Course <span class=\"bold\">[idOfTypicalCourse1]</span> edited.<br>"
                + "New Email: " + newStudentEmailToBeTrimmed.trim()
                + "<br>New Team: " + newStudentTeamToBeTrimmed.trim()
                + "<br>Comments: " + newStudentCommentsToBeTrimmed.trim()
                + "|||/page/instructorCourseStudentDetailsEditSave";
        AssertHelper.assertLogMessageEquals(expectedLogMessageToBeTrimmed, aToBeTrimmed.getLogMessage());

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
                Const.ParamsNames.TEAM_NAME, student1InCourse1.team
        };

        gaeSimulation.loginAsInstructor(instructorId);
        a = getAction(submissionParams);
        ShowPageResult result = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_COURSE_STUDENT_EDIT,
                        true,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());

        assertTrue(result.isError);
        assertEquals(getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, invalidStudentEmail,
                         FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                         FieldValidator.EMAIL_MAX_LENGTH),
                     result.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorCourseStudentDetailsEditSave|||instructorCourseStudentDetailsEditSave"
                + "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "Servlet Action Failure : "
                + getPopulatedErrorMessage(
                      FieldValidator.EMAIL_ERROR_MESSAGE, invalidStudentEmail,
                      FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                      FieldValidator.EMAIL_MAX_LENGTH)
                + "|||/page/instructorCourseStudentDetailsEditSave";

        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("Error case, invalid email parameter (email already taken by others)");

        StudentAttributes student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        String takenStudentEmail = student2InCourse1.email;

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, newStudentEmail, //Use the new email as the previous email have been changed
                Const.ParamsNames.STUDENT_NAME, student1InCourse1.name,
                Const.ParamsNames.NEW_STUDENT_EMAIL, takenStudentEmail,
                Const.ParamsNames.COMMENTS, student1InCourse1.comments,
                Const.ParamsNames.TEAM_NAME, student1InCourse1.team
        };

        gaeSimulation.loginAsInstructor(instructorId);
        a = getAction(submissionParams);
        result = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_COURSE_STUDENT_EDIT,
                        true,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());

        assertTrue(result.isError);
        assertEquals(String.format(Const.StatusMessages.STUDENT_EMAIL_TAKEN_MESSAGE, student2InCourse1.name,
                                   takenStudentEmail),
                result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorCourseStudentDetailsEditSave|||instructorCourseStudentDetailsEditSave"
                + "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "Servlet Action Failure : "
                + String.format(Const.StatusMessages.STUDENT_EMAIL_TAKEN_MESSAGE, student2InCourse1.name,
                                takenStudentEmail)
                + "|||/page/instructorCourseStudentDetailsEditSave";

        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

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
                Const.ParamsNames.TEAM_NAME, student1InCourse1.team
        };

        gaeSimulation.loginAsInstructor(instructorId);
        a = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE,
                        true,
                        instructorId,
                        instructor1OfCourse1.courseId),
                redirectResult.getDestinationWithParams());

        assertTrue(redirectResult.isError);
        assertEquals(Const.StatusMessages.STUDENT_NOT_FOUND_FOR_EDIT, redirectResult.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorCourseStudentDetailsEditSave|||instructorCourseStudentDetailsEditSave"
                + "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "Student <span class=\"bold\">" + nonExistentEmailForStudent + "</span> in "
                + "Course <span class=\"bold\">[" + instructor1OfCourse1.courseId + "]</span> not found."
                + "|||/page/instructorCourseStudentDetailsEditSave";

        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("Unsuccessful case: test null student email parameter");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId
        };

        try {
            a = getAction(submissionParams);
            r = getRedirectResult(a);
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                    Const.ParamsNames.STUDENT_EMAIL), e.getMessage());
        }

        ______TS("Unsuccessful case: test null course id parameter");
        submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, newStudentEmail
        };

        try {
            a = getAction(submissionParams);
            r = getRedirectResult(a);
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                    Const.ParamsNames.COURSE_ID), e.getMessage());
        }
    }

    @Override
    protected InstructorCourseStudentDetailsEditSaveAction getAction(String... params) {
        return (InstructorCourseStudentDetailsEditSaveAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    protected String getPageResultDestination(String parentUri, boolean isError, String userId, String courseId) {
        String pageDestination = parentUri;
        pageDestination = Url.addParamToUrl(pageDestination, Const.ParamsNames.ERROR, Boolean.toString(isError));
        pageDestination = Url.addParamToUrl(pageDestination, Const.ParamsNames.COURSE_ID, courseId);
        pageDestination = Url.addParamToUrl(pageDestination, Const.ParamsNames.USER_ID, userId);
        return pageDestination;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student3InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifyStudentPrivilege(submissionParams);
    }

}
