package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.RegenerateStudentException;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.ui.output.RegenerateStudentCourseLinksData;

/**
 * Regenerates the course join and feedback session links for a given student in a course.
 */
class RegenerateStudentCourseLinksAction extends AdminOnlyAction {

    /** Message indicating that the email parameter value is not a valid email address. */
    static final String STUDENT_NOT_FOUND =
            "The student with the email %s could not be found for the course with ID [%s].";

    private static final String SUCCESSFUL_REGENERATION =
            "Student's links for this course have been successfully regenerated,";

    /** Message indicating that the links regeneration was successful, and corresponding email was sent. */
    static final String SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT =
            SUCCESSFUL_REGENERATION + " and the email has been sent.";

    private static final String UNSUCCESSFUL_REGENERATION =
            "Regeneration of the student's links for this course was unsuccessful.";

    /** Message indicating that the links regeneration was successful, but corresponding email could not be sent. */
    private static final String SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED =
            SUCCESSFUL_REGENERATION + " but the email failed to send.";

    @Override
    JsonResult execute() {
        String studentEmailAddress = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        StudentAttributes updatedStudent;
        try {
            updatedStudent = logic.regenerateStudentRegistrationKey(courseId, studentEmailAddress);
        } catch (EntityDoesNotExistException ex) {
            return new JsonResult(
                    String.format(STUDENT_NOT_FOUND, studentEmailAddress, courseId), HttpStatus.SC_NOT_FOUND);
        } catch (RegenerateStudentException ex) {
            return new JsonResult(UNSUCCESSFUL_REGENERATION, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        boolean emailSent = sendEmail(updatedStudent);
        String statusMessage = emailSent
                                ? SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT
                                : SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED;

        return new JsonResult(
                new RegenerateStudentCourseLinksData(statusMessage, StringHelper.encrypt(updatedStudent.key)));
    }

    /**
     * Sends the regenerated course join and feedback session links to the student.
     * @return true if the email was sent successfully, and false otherwise.
     */
    private boolean sendEmail(StudentAttributes student) {
        EmailWrapper email = emailGenerator.generateFeedbackSessionSummaryOfCourse(student.getCourse(), student.getEmail(),
                                            Templates.EmailTemplates.USER_REGKEY_REGENERATION_RESEND_ALL_COURSE_LINKS);
        EmailSendingStatus status = emailSender.sendEmail(email);
        return status.isSuccess();
    }

}
