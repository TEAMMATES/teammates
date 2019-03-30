package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
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

    private static final String SUCCESSFUL_UPDATE = "Student's links for this course have been regenerated,";
    private static final String SUCCESSFUL_UPDATE_WITH_EMAIL = SUCCESSFUL_UPDATE + " and the email has been sent.";
    private static final String SUCCESSFUL_UPDATE_BUT_EMAIL_FAILED = SUCCESSFUL_UPDATE + " but the email failed to send.";

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
            return new JsonResult(new RegenerateStudentCourseLinksData("The student with the email " + studentEmailAddress
                    + " could not be found in the course " + courseId + ".", null));
        }

        try {
            StudentAttributes updatedStudent = logic.regenerateStudentCourseLinks(student);

            boolean emailSent = sendEmail(updatedStudent);
            String statusMessage = emailSent ? SUCCESSFUL_UPDATE_WITH_EMAIL : SUCCESSFUL_UPDATE_BUT_EMAIL_FAILED;

            return new JsonResult(
                    new RegenerateStudentCourseLinksData(statusMessage, StringHelper.encrypt(updatedStudent.key)));
        } catch (EntityDoesNotExistException ex) {
            return new JsonResult(ex.getMessage(), HttpStatus.SC_NOT_FOUND);
        }
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
