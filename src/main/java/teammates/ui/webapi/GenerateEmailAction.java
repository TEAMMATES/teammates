package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Collections;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.EmailData;

/**
 * Generate email content.
 */
public class GenerateEmailAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (!isCourseMigrated(courseId)) {
            CourseAttributes course = logic.getCourse(courseId);
            if (course == null) {
                throw new EntityNotFoundException("Course with ID " + courseId + " does not exist!");
            }

            String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
            StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
            if (student == null) {
                throw new EntityNotFoundException("Student does not exist.");
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
                email = emailGenerator.generateFeedbackSessionReminderEmails(
                        feedbackSession, Collections.singletonList(student), new ArrayList<>(), null).get(0);
            } else {
                throw new InvalidHttpParameterException("Email type " + emailType + " not accepted");
            }

            return new JsonResult(new EmailData(email));
        }

        Course course = sqlLogic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException("Course with ID " + courseId + " does not exist!");
        }

        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Student student = sqlLogic.getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            throw new EntityNotFoundException("Student does not exist.");
        }

        String emailType = getNonNullRequestParamValue(Const.ParamsNames.EMAIL_TYPE);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        EmailWrapper email;

        if (emailType.equals(EmailType.STUDENT_COURSE_JOIN.name())) {
            email = sqlEmailGenerator.generateStudentCourseJoinEmail(course, student);
        } else if (emailType.equals(EmailType.FEEDBACK_SESSION_REMINDER.name())) {
            if (feedbackSessionName == null) {
                throw new InvalidHttpParameterException("Feedback session name not specified");
            }
            FeedbackSession feedbackSession = getNonNullSqlFeedbackSession(feedbackSessionName, courseId);
            email = sqlEmailGenerator.generateFeedbackSessionReminderEmails(
                    feedbackSession, Collections.singletonList(student), new ArrayList<>(), null).get(0);
        } else {
            throw new InvalidHttpParameterException("Email type " + emailType + " not accepted");
        }

        return new JsonResult(new EmailData(email));
    }
}
