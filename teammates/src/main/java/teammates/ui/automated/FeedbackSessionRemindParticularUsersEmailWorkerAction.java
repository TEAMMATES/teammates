package teammates.ui.automated;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.logic.api.EmailGenerator;

/**
 * Task queue worker action: sends feedback session reminder email to particular students of a course.
 */
public class FeedbackSessionRemindParticularUsersEmailWorkerAction extends AutomatedAction {

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
        String feedbackSessionName = getNonNullRequestParamValue(ParamsNames.SUBMISSION_FEEDBACK);
        String courseId = getNonNullRequestParamValue(ParamsNames.SUBMISSION_COURSE);
        String[] usersToRemind = getNonNullRequestParamValues(ParamsNames.SUBMISSION_REMIND_USERLIST);
        String googleIdOfInstructorToNotify = getNonNullRequestParamValue(ParamsNames.USER_ID);

        try {
            FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
            List<StudentAttributes> studentsToRemindList = new ArrayList<>();
            List<InstructorAttributes> instructorsToRemindList = new ArrayList<>();
            InstructorAttributes instructorToNotify =
                    logic.getInstructorForGoogleId(courseId, googleIdOfInstructorToNotify);

            for (String userEmail : usersToRemind) {
                StudentAttributes student = logic.getStudentForEmail(courseId, userEmail);
                if (student != null) {
                    studentsToRemindList.add(student);
                }

                InstructorAttributes instructor = logic.getInstructorForEmail(courseId, userEmail);
                if (instructor != null) {
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
