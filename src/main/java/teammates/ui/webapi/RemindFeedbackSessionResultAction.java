package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.request.FeedbackSessionRespondentRemindRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Remind the student about the published result of a feedback session.
 */
public class RemindFeedbackSessionResultAction extends Action {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        Instructor instructor = sqlLogic.getInstructorByAccountId(courseId, userInfo.getId());
        gateKeeper.verifyAccessible(instructor, feedbackSession, Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        if (!feedbackSession.isPublished()) {
            throw new InvalidOperationException("Published email could not be resent "
                    + "as the feedback session is not published.");
        }

        FeedbackSessionRespondentRemindRequest remindRequest =
                getAndValidateRequestBody(FeedbackSessionRespondentRemindRequest.class);
        String[] usersToEmail = remindRequest.getUsersToRemind();

        // Generate reminder emails for specified users
        List<Student> studentsToEmailList = new ArrayList<>();
        List<Instructor> instructorsToEmailList = new ArrayList<>();
        Instructor instructorToNotify = sqlLogic.getInstructorByAccountId(courseId, userInfo.getId());

        for (String userEmail : usersToEmail) {
            Student student = sqlLogic.getStudentForEmail(courseId, userEmail);
            if (student != null) {
                studentsToEmailList.add(student);
            }

            Instructor instructor = sqlLogic.getInstructorForEmail(courseId, userEmail);
            if (instructor != null) {
                instructorsToEmailList.add(instructor);
            }
        }
        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionPublishedEmails(
                feedbackSession, studentsToEmailList, instructorsToEmailList,
                Collections.singletonList(instructorToNotify));

        // Queue to priority queue for immediate sending (user-triggered)
        taskQueuer.scheduleEmailsForPrioritySending(emails);
        return new JsonResult("Reminders sent");
    }
}
