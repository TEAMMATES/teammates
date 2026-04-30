package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.User;
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
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        FeedbackSession feedbackSession = sqlLogic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        Instructor instructor = sqlLogic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId());
        gateKeeper.verifyAccessible(instructor, feedbackSession, Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        FeedbackSession feedbackSession = sqlLogic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        if (!feedbackSession.isPublished()) {
            throw new InvalidOperationException("Published email could not be resent "
                    + "as the feedback session is not published.");
        }

        FeedbackSessionRespondentRemindRequest remindRequest =
                getAndValidateRequestBody(FeedbackSessionRespondentRemindRequest.class);
        UUID[] usersToRemind = remindRequest.getUsersToRemind();

        // Generate reminder emails for specified users
        List<Student> studentsToRemindList = new ArrayList<>();
        List<Instructor> instructorsToRemindList = new ArrayList<>();
        Instructor instructorToNotify = sqlLogic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId());

        for (UUID userId : usersToRemind) {
            User user = sqlLogic.getUser(userId);
            if (user == null) {
                throw new EntityNotFoundException("User with ID " + userId + " not found");
            }

            if (!Objects.equals(user.getCourseId(), feedbackSession.getCourseId())) {
                throw new InvalidOperationException("User with ID "
                    + userId + " does not belong to the same course as the feedback session");
            }

            if (user instanceof Student student) {
                studentsToRemindList.add(student);
            } else if (user instanceof Instructor instructor) {
                instructorsToRemindList.add(instructor);
            }
        }

        List<EmailWrapper> emails = emailGenerator.generateFeedbackSessionPublishedEmails(
                feedbackSession, studentsToRemindList, instructorsToRemindList,
                Collections.singletonList(instructorToNotify));

        taskQueuer.scheduleEmailsForPrioritySending(emails);

        return new JsonResult("Reminders sent");
    }
}
