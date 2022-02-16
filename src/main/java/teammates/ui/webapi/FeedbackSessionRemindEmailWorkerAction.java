package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;

/**
 * Task queue worker action: sends feedback session reminder email to a course.
 */
class FeedbackSessionRemindEmailWorkerAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
        String feedbackSessionName = getNonNullRequestParamValue(ParamsNames.FEEDBACK_SESSION_NAME);
        String courseId = getNonNullRequestParamValue(ParamsNames.COURSE_ID);
        String instructorId = getNonNullRequestParamValue(ParamsNames.INSTRUCTOR_ID);

        try {
            FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
            List<StudentAttributes> studentList = logic.getStudentsForCourse(courseId);
            List<InstructorAttributes> instructorList = logic.getInstructorsForCourse(courseId);

            InstructorAttributes instructorToNotify = logic.getInstructorForGoogleId(courseId, instructorId);

            List<StudentAttributes> studentsToRemindList = studentList.stream().filter(student ->
                    !logic.isFeedbackSessionAttemptedByStudent(session, student.getEmail(), student.getTeam())
            ).collect(Collectors.toList());

            List<InstructorAttributes> instructorsToRemindList = instructorList.stream().filter(instructor ->
                    !logic.isFeedbackSessionAttemptedByInstructor(session, instructor.getEmail())
            ).collect(Collectors.toList());

            List<EmailWrapper> emails = emailGenerator.generateFeedbackSessionReminderEmails(
                    session, studentsToRemindList, instructorsToRemindList, instructorToNotify);
            taskQueuer.scheduleEmailsForSending(emails);
        } catch (Exception e) {
            log.severe("Unexpected error while sending emails", e);
        }
        return new JsonResult("Successful");
    }

}
