package teammates.ui.webapi;

import java.util.UUID;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UserUpdateException;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.User;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.RegenerateKeyData;

/**
 * Regenerates the key for a given user in a course. This will also resend the course registration
 * and feedback session links to the affected user, as any previously sent links will no longer work.
 */
public class RegenerateUserKeyAction extends AdminOnlyAction {

    /** Message indicating that the key regeneration was successful. */
    public static final String SUCCESSFUL_REGENERATION =
            "User's key for this course has been successfully regenerated,";

    /** Message indicating that the key regeneration was successful, and corresponding email was sent. */
    public static final String SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT =
            SUCCESSFUL_REGENERATION + " and the email has been sent.";

    /** Message indicating that the key regeneration was unsuccessful. */
    public static final String UNSUCCESSFUL_REGENERATION =
            "Regeneration of the user's key was unsuccessful.";

    /** Message indicating that the key regeneration was successful, but corresponding email could not be sent. */
    public static final String SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED =
            SUCCESSFUL_REGENERATION + " but the email failed to send.";

    @Override
    public JsonResult execute() {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);

        User updatedUser;
        try {
            updatedUser = logic.regenerateUserRegistrationKey(userId);
        } catch (EntityDoesNotExistException ex) {
            throw new EntityNotFoundException(ex);
        } catch (UserUpdateException ex) {
            return new JsonResult(UNSUCCESSFUL_REGENERATION, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        boolean emailSent = sendEmail(updatedUser);
        String statusMessage = emailSent
                ? SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT
                : SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED;

        return new JsonResult(new RegenerateKeyData(statusMessage, updatedUser.getRegKey()));
    }

    /**
     * Sends the regenerated course join and feedback session links to the user.
     * @return true if the email was sent successfully, and false otherwise.
     */
    private boolean sendEmail(User user) {
        EmailType emailType = user.getUserType() == UserType.STUDENT
                ? EmailType.STUDENT_COURSE_LINKS_REGENERATED
                : EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED;
        EmailWrapper email = emailGenerator.generateFeedbackSessionSummaryOfCourse(
                user.getCourseId(), user.getEmail(), emailType);
        EmailSendingStatus status = emailSender.sendEmail(email);
        return status.isSuccess();
    }
}
