package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

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
        UUID studentId = getUuidRequestParamValue(Const.ParamsNames.STUDENT_SQL_ID);
        Student student = sqlLogic.getStudent(studentId);
        if (student == null) {
            throw new EntityNotFoundException("Student does not exist");
        }

        EmailType emailType;
        try {
            emailType = EmailType.valueOf(getNonNullRequestParamValue(Const.ParamsNames.EMAIL_TYPE));
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException("Inavalid email type "
                    + getNonNullRequestParamValue(Const.ParamsNames.EMAIL_TYPE), e);
        }

        EmailWrapper email = switch (emailType) {
        case STUDENT_COURSE_JOIN -> {
            Course course = student.getCourse();
            yield sqlEmailGenerator.generateStudentCourseJoinEmail(course, student);
        }
        case FEEDBACK_SESSION_REMINDER -> {
            UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
            FeedbackSession feedbackSession = sqlLogic.getFeedbackSession(feedbackSessionId);
            if (feedbackSession == null) {
                throw new EntityNotFoundException(
                        "Feedback session with ID " + feedbackSessionId + " does not exist");
            }

            if (!Objects.equals(feedbackSession.getCourseId(), student.getCourseId())) {
                throw new InvalidHttpParameterException(
                        "Feedback session does not belong to the same course as the student");
            }

            yield sqlEmailGenerator.generateFeedbackSessionReminderEmails(
                    feedbackSession, Collections.singletonList(student), new ArrayList<>(), null).get(0);
        }
        default -> throw new InvalidHttpParameterException("Inavalid Email type for this action: " + emailType);
        };

        return new JsonResult(new EmailData(email));
    }
}
