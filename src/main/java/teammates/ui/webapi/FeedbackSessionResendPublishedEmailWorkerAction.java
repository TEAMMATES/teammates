package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.ui.request.FeedbackSessionRemindRequest;

/**
 * Task queue worker action: sends feedback session reminder email to particular students of a course.
 */
class FeedbackSessionResendPublishedEmailWorkerAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    JsonResult execute() {
        FeedbackSessionRemindRequest remindRequest = getAndValidateRequestBody(FeedbackSessionRemindRequest.class);
        String feedbackSessionName = remindRequest.getFeedbackSessionName();
        String courseId = remindRequest.getCourseId();
        String[] usersToRemind = remindRequest.getUsersToRemind();

        try {
            FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
            List<StudentAttributes> studentsToEmailList = new ArrayList<>();
            List<InstructorAttributes> instructorsToEmailList = new ArrayList<>();

            for (String userEmail : usersToRemind) {
                StudentAttributes student = logic.getStudentForEmail(courseId, userEmail);
                if (student != null) {
                    studentsToEmailList.add(student);
                }

                InstructorAttributes instructor = logic.getInstructorForEmail(courseId, userEmail);
                if (instructor != null) {
                    instructorsToEmailList.add(instructor);
                }
            }

            List<EmailWrapper> emails = emailGenerator.generateFeedbackSessionPublishedEmails(
                    session, studentsToEmailList, instructorsToEmailList);
            taskQueuer.scheduleEmailsForSending(emails);
        } catch (Exception e) {
            log.severe("Unexpected error while sending emails: " + TeammatesException.toStringWithStackTrace(e));
        }
        return new JsonResult("Successful");
    }

}
