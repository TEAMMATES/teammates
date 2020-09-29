package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;

/**
 * Task queue worker action: sends feedback session reminder email to a course.
 */
class FeedbackSessionRemindEmailWorkerAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    JsonResult execute() {
        String feedbackSessionName = getNonNullRequestParamValue(ParamsNames.FEEDBACK_SESSION_NAME);
        String courseId = getNonNullRequestParamValue(ParamsNames.COURSE_ID);
        String instructorId = getNonNullRequestParamValue(ParamsNames.INSTRUCTOR_ID);

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

            List<EmailWrapper> emails = emailGenerator.generateFeedbackSessionReminderEmails(
                    session, studentsToRemindList, instructorsToRemindList, instructorToNotify);
            taskQueuer.scheduleEmailsForSending(emails);
        } catch (Exception e) {
            log.severe("Unexpected error while sending emails: " + TeammatesException.toStringWithStackTrace(e));
        }
        return new JsonResult("Successful");
    }

}
