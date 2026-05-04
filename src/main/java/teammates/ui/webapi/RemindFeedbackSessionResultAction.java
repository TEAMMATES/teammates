package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Student;
import teammates.logic.entity.User;
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

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        Instructor instructor = logic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId());
        gateKeeper.verifyAccessible(instructor, feedbackSession, Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
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
        Instructor instructorToNotify = logic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId());

        for (UUID userId : usersToRemind) {
            User user = logic.getUser(userId);
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
