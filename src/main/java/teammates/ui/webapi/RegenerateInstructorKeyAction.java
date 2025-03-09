package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.RegenerateKeyData;

/**
 * Regenerates the key for a given instructor in a course. This will also resend the course registration
 * and feedback session links to the affected instructor, as any previously sent links will no longer work.
 */
public class RegenerateInstructorKeyAction extends AdminOnlyAction {

    /** Message indicating that the key regeneration was successful. */
    public static final String SUCCESSFUL_REGENERATION =
            "Instructor's key for this course has been successfully regenerated,";

    /** Message indicating that the key regeneration was successful, and corresponding email was sent. */
    public static final String SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT =
            SUCCESSFUL_REGENERATION + " and the email has been sent.";

    /** Message indicating that the key regeneration was unsuccessful. */
    public static final String UNSUCCESSFUL_REGENERATION =
            "Regeneration of the instructor's key was unsuccessful.";

    /** Message indicating that the key regeneration was successful, but corresponding email could not be sent. */
    public static final String SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED =
            SUCCESSFUL_REGENERATION + " but the email failed to send.";

    @Override
    public JsonResult execute() {
        String instructorEmailAddress = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (!isCourseMigrated(courseId)) {
            InstructorAttributes updatedInstructor;
            try {
                updatedInstructor = logic.regenerateInstructorRegistrationKey(courseId, instructorEmailAddress);
            } catch (EntityDoesNotExistException ex) {
                throw new EntityNotFoundException(ex);
            } catch (EntityAlreadyExistsException ex) {
                // No logging here as severe logging is done at the origin of the error
                return new JsonResult(UNSUCCESSFUL_REGENERATION, HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }

            boolean emailSent = sendEmail(updatedInstructor);
            String statusMessage = emailSent
                    ? SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT
                    : SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED;

            return new JsonResult(new RegenerateKeyData(statusMessage, updatedInstructor.getKey()));
        }

        Instructor updatedInstructor;
        try {
            updatedInstructor = sqlLogic.regenerateInstructorRegistrationKey(courseId, instructorEmailAddress);
        } catch (EntityDoesNotExistException ex) {
            throw new EntityNotFoundException(ex);
        } catch (InstructorUpdateException ex) {
            return new JsonResult(UNSUCCESSFUL_REGENERATION, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        boolean emailSent = sendEmail(updatedInstructor);
        String statusMessage = emailSent
                ? SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT
                : SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED;

        return new JsonResult(new RegenerateKeyData(statusMessage, updatedInstructor.getRegKey()));
    }

    /**
     * Sends the regenerated course join and feedback session links to the instructor.
     * @return true if the email was sent successfully, and false otherwise.
     */
    private boolean sendEmail(Instructor instructor) {
        EmailWrapper email = sqlEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                instructor.getCourseId(), instructor.getEmail(), EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED);
        EmailSendingStatus status = emailSender.sendEmail(email);
        return status.isSuccess();
    }

    /**
     * Sends the regenerated course join and feedback session links to the instructor.
     * @return true if the email was sent successfully, and false otherwise.
     */
    private boolean sendEmail(InstructorAttributes instructor) {
        EmailWrapper email = emailGenerator.generateFeedbackSessionSummaryOfCourse(
                instructor.getCourseId(), instructor.getEmail(), EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED);
        EmailSendingStatus status = emailSender.sendEmail(email);
        return status.isSuccess();
    }
}
