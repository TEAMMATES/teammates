package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
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
        Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
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

        java.util.List<teammates.storage.sqlentity.Student> studentsToEmailList = new java.util.ArrayList<>();
        java.util.List<Instructor> instructorsToEmailList = new java.util.ArrayList<>();

        Instructor instructorToNotify = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());

        for (String userEmail : usersToEmail) {
            teammates.storage.sqlentity.Student student = sqlLogic.getStudentForEmail(courseId, userEmail);
            if (student != null) {
                studentsToEmailList.add(student);
            }

            Instructor userInstructor = sqlLogic.getInstructorForEmail(courseId, userEmail);
            if (userInstructor != null) {
                instructorsToEmailList.add(userInstructor);
            }
        }
        java.util.List<teammates.common.util.EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionPublishedEmails(
                feedbackSession, studentsToEmailList, instructorsToEmailList, java.util.Collections.singletonList(instructorToNotify));
        taskQueuer.scheduleEmailsForSending(emails);

        return new JsonResult("Reminders sent");
    }
}
