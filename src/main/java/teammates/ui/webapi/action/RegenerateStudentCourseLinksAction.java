package teammates.ui.webapi.action;

import static teammates.common.util.FieldValidator.REGEX_EMAIL;

import org.apache.http.HttpStatus;

import com.google.api.client.http.HttpStatusCodes;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.ui.webapi.output.RegenerateStudentCourseLinksData;

/**
 * Regenerates the course join and feedback session links for a given student in a course.
 */
public class RegenerateStudentCourseLinksAction extends Action {

    /** Message indicating that the email parameter value is not a valid email address. */
    public static final String INVALID_EMAIL_ADDRESS = "Invalid email address: %s";

    /** Message indicating that the email parameter value is not a valid email address. */
    public static final String STUDENT_NOT_FOUND = "The student with the email %s could not be found for"
                                                + "the course with ID [%s].";

    private static final String SUCCESSFUL_REGENERATION = "Student's links for this course have been regenerated,";

    /** Message indicating that the links regeneration was successful, and corresponding email was sent. */
    public static final String SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT =
                                                        SUCCESSFUL_REGENERATION + " and the email has been sent.";

    /** Message indicating that the links regeneration was successful, but corresponding email could not be sent. */
    public static final String SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED =
                                                        SUCCESSFUL_REGENERATION + " but the email failed to send.";

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {
        String studentEmailAddress = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        if (!StringHelper.isMatching(studentEmailAddress, REGEX_EMAIL)) {
            return new JsonResult(String.format(INVALID_EMAIL_ADDRESS, studentEmailAddress),
                    HttpStatusCodes.STATUS_CODE_BAD_REQUEST);
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmailAddress);
        if (student == null) {
            return new JsonResult(String.format(STUDENT_NOT_FOUND, studentEmailAddress, courseId), HttpStatus.SC_NOT_FOUND);
        }

        StudentAttributes updatedStudent = logic.regenerateStudentRegistrationKey(student);

        boolean emailSent = sendEmail(updatedStudent);
        String statusMessage = emailSent
                                ? SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT
                                : SUCCESSFUL_REGENERATION_BUT_EMAIL_FAILED;

        return new JsonResult(
                new RegenerateStudentCourseLinksData(statusMessage, StringHelper.encrypt(updatedStudent.key)));

    }

    /**
     * Sends the regenerated course join and feedback session links to the student.
     *
     * @return true if the email was sent successfully, and false otherwise.
     */
    private boolean sendEmail(StudentAttributes student) {
        EmailWrapper email = emailGenerator.generateFeedbackSessionSummaryOfCourse(student.getCourse(), student.getEmail(),
                                                    Templates.EmailTemplates.REGENERATE_STUDENT_KEY_RESEND_ALL_COURSE_LINKS);
        EmailSendingStatus status = emailSender.sendEmail(email);
        return status.isSuccess();
    }

}
