package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.request.FeedbackSessionRemindRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Task queue worker action: sends feedback session reminder email to particular students of a course.
 */
public class FeedbackSessionRemindParticularUsersEmailWorkerAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        FeedbackSessionRemindRequest remindRequest = getAndValidateRequestBody(FeedbackSessionRemindRequest.class);
        String googleIdOfInstructorToNotify = remindRequest.getRequestingInstructorId();
        if (googleIdOfInstructorToNotify == null) {
            throw new InvalidHttpRequestBodyException("Instructor to notify cannot be null.");
        }
        String feedbackSessionName = remindRequest.getFeedbackSessionName();
        String courseId = remindRequest.getCourseId();
        String[] usersToRemind = remindRequest.getUsersToRemind();
        boolean isSendingCopyToInstructor = remindRequest.getIsSendingCopyToInstructor();

        if (!isCourseMigrated(courseId)) {
            try {
                FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
                List<StudentAttributes> studentsToRemindList = new ArrayList<>();
                List<InstructorAttributes> instructorsToRemindList = new ArrayList<>();
                InstructorAttributes instructorToNotify = isSendingCopyToInstructor
                        ? logic.getInstructorForGoogleId(courseId, googleIdOfInstructorToNotify)
                        : null;

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

                List<EmailWrapper> emails = emailGenerator.generateFeedbackSessionReminderEmails(
                        session, studentsToRemindList, instructorsToRemindList, instructorToNotify);
                taskQueuer.scheduleEmailsForSending(emails);
            } catch (Exception e) {
                log.severe("Unexpected error while sending emails", e);
            }
            return new JsonResult("Successful");
        }

        try {
            FeedbackSession session = sqlLogic.getFeedbackSession(feedbackSessionName, courseId);
            List<Student> studentsToRemindList = new ArrayList<>();
            List<Instructor> instructorsToRemindList = new ArrayList<>();
            Instructor instructorToNotify = isSendingCopyToInstructor
                    ? sqlLogic.getInstructorByGoogleId(courseId, googleIdOfInstructorToNotify)
                    : null;

            for (String userEmail : usersToRemind) {
                Student student = sqlLogic.getStudentForEmail(courseId, userEmail);
                if (student != null) {
                    studentsToRemindList.add(student);
                }

                Instructor instructor = sqlLogic.getInstructorForEmail(courseId, userEmail);
                if (instructor != null) {
                    instructorsToRemindList.add(instructor);
                }
            }

            List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionReminderEmails(
                    session, studentsToRemindList, instructorsToRemindList, instructorToNotify);
            taskQueuer.scheduleEmailsForSending(emails);
        } catch (Exception e) {
            log.severe("Unexpected error while sending emails", e);
        }
        return new JsonResult("Successful");
    }

}
