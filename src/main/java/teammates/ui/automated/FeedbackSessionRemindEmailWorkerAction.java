package teammates.ui.automated;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.logic.api.EmailGenerator;

/**
 * Task queue worker action: sends feedback session reminder email to a course.
 */
public class FeedbackSessionRemindEmailWorkerAction extends AutomatedAction {

    private static final Logger log = Logger.getLogger();

    @Override
    protected String getActionDescription() {
        return null;
    }

    @Override
    protected String getActionMessage() {
        return null;
    }

    @Override
    public void execute() {
        String feedbackSessionName = getRequestParamValue(ParamsNames.SUBMISSION_FEEDBACK);
        Assumption.assertPostParamNotNull(ParamsNames.SUBMISSION_FEEDBACK, feedbackSessionName);

        String courseId = getRequestParamValue(ParamsNames.SUBMISSION_COURSE);
        Assumption.assertPostParamNotNull(ParamsNames.SUBMISSION_COURSE, courseId);

        String instructorId = getRequestParamValue(ParamsNames.USER_ID);
        Assumption.assertPostParamNotNull(ParamsNames.USER_ID, instructorId);

        try {
            FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
            List<StudentAttributes> studentList = logic.getStudentsForCourse(courseId);
            List<InstructorAttributes> instructorList = logic.getInstructorsForCourse(courseId);

            InstructorAttributes instructorToNotify = logic.getInstructorForGoogleId(courseId, instructorId);

            List<StudentAttributes> studentsToRemindList = new ArrayList<>();
            for (StudentAttributes student : studentList) {
                if (!logic.isFeedbackSessionCompletedByStudent(session, student.email)) {
                    studentsToRemindList.add(student);
                }
            }

            // Filter out instructors who have submitted the feedback session
            List<InstructorAttributes> instructorsToRemindList = new ArrayList<>();
            for (InstructorAttributes instructor : instructorList) {
                if (!logic.isFeedbackSessionCompletedByInstructor(session, instructor.email)) {
                    instructorsToRemindList.add(instructor);
                }
            }

            List<EmailWrapper> emails = new EmailGenerator().generateFeedbackSessionReminderEmails(
                    session, studentsToRemindList, instructorsToRemindList, instructorToNotify);
            taskQueuer.scheduleEmailsForSending(emails);
        } catch (Exception e) {
            log.severe("Unexpected error while sending emails: " + TeammatesException.toStringWithStackTrace(e));
        }
    }

}
