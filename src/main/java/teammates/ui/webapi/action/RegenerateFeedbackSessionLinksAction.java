package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;

/**
 * Regenerates the submission link.
 */
public class RegenerateFeedbackSessionLinksAction extends Action {

    private static final String SUCCESSFUL_UPDATE = "Session links for this course have been regenerated,";
    private static final String SUCCESSFUL_UPDATE_WITH_EMAIL = SUCCESSFUL_UPDATE + " and email sent";
    private static final String SUCCESSFUL_UPDATE_BUT_EMAIL_FAILED = SUCCESSFUL_UPDATE + " but email failed to send";

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!Config.isDevServer()) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentEmailAddress = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmailAddress);
        if (student == null) {
            return new JsonResult(
                    "The student with the email " + studentEmailAddress + " could not be found in course " + courseId + ".");
        }

        boolean emailSent;

        try {
            logic.regenerateStudentSessionLinks(student);

            emailSent = sendEmail(student);
            String statusMessage = emailSent ? SUCCESSFUL_UPDATE_WITH_EMAIL : SUCCESSFUL_UPDATE_BUT_EMAIL_FAILED;

            return new JsonResult(statusMessage);
        } catch (EntityDoesNotExistException ex) {
            return new JsonResult(ex.getMessage(), HttpStatus.SC_NOT_FOUND);
        }
    }

    /**
     * Sends the regenerated course registration and feedback session links to the student.
     *
     * @return The true if email was sent successfully or false otherwise.
     */
    private boolean sendEmail(StudentAttributes student) {
        EmailWrapper email = emailGenerator.regenerateFeedbackSessionLinks(student);
        EmailSendingStatus status = emailSender.sendEmail(email);
        return status.isSuccess();
    }

}
