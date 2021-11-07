package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.RegenerateKeyData;

/**
 * Regenerates the key for a given student in a course. This will also resend the course registration
 * and feedback session links to the affected student, as any previously sent links will no longer work.
 */
class RegenerateStudentKeyAction extends AdminOnlyAction {

    private static final String SUCCESSFUL_REGENERATION =
            "Student's key for this course has been successfully regenerated,";

    /** Message indicating that the key regeneration was successful, and corresponding email was sent. */
    static final String SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT =
            SUCCESSFUL_REGENERATION + " and the email has been sent.";

    private static final String UNSUCCESSFUL_REGENERATION =
            "Regeneration of the student's key was unsuccessful.";

    /** Message indicating that the key regeneration was successful, but corresponding email could not be sent. */
    private static final String SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED =
            SUCCESSFUL_REGENERATION + " but the email failed to send.";

    @Override
    public JsonResult execute() {
        String studentEmailAddress = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        StudentAttributes updatedStudent;
        try {
            updatedStudent = logic.regenerateStudentRegistrationKey(courseId, studentEmailAddress);
        } catch (EntityDoesNotExistException ex) {
            throw new EntityNotFoundException(ex);
        } catch (EntityAlreadyExistsException ex) {
            // No logging here as severe logging is done at the origin of the error
            return new JsonResult(UNSUCCESSFUL_REGENERATION, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        boolean emailSent = sendEmail(updatedStudent);
        String statusMessage = emailSent
                                ? SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT
                                : SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED;

        return new JsonResult(new RegenerateKeyData(statusMessage, updatedStudent.getKey()));
    }

    /**
     * Sends the regenerated course join and feedback session links to the student.
     * @return true if the email was sent successfully, and false otherwise.
     */
    private boolean sendEmail(StudentAttributes student) {
        EmailWrapper email = emailGenerator.generateFeedbackSessionSummaryOfCourse(
                student.getCourse(), student.getEmail(), EmailType.STUDENT_COURSE_LINKS_REGENERATED);
        EmailSendingStatus status = emailSender.sendEmail(email);
        return status.isSuccess();
    }

}
