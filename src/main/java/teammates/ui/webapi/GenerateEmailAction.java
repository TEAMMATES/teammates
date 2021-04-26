package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.EmailData;

/**
 * Generate email content.
 */
class GenerateEmailAction extends AdminOnlyAction {

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException(
                    new EntityDoesNotExistException("Course with ID " + courseId + " does not exist!"));
        }

        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("Student does not exist."));
        }

        String emailType = getNonNullRequestParamValue(Const.ParamsNames.EMAIL_TYPE);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        EmailWrapper email;

        if (emailType.equals(EmailType.STUDENT_COURSE_JOIN.name())) {
            email = emailGenerator.generateStudentCourseJoinEmail(course, student);
        } else if (emailType.equals(EmailType.FEEDBACK_SESSION_REMINDER.name())) {
            if (feedbackSessionName == null) {
                throw new InvalidHttpParameterException("Feedback session name not specified");
            }
            FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
            email = emailGenerator.generateFeedbackSessionStudentReminderEmail(feedbackSession, student);
        } else {
            throw new InvalidHttpParameterException("Email type " + emailType + " not accepted");
        }

        return new JsonResult(new EmailData(email));
    }
}
