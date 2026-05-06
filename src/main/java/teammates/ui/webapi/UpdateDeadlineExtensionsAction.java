package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.UpdateExtensionsResult;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.DeadlineExtensionsData;
import teammates.ui.request.DeadlineExtensionsUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates the deadline extensions for a feedback session.
 */
public class UpdateDeadlineExtensionsAction extends Action {

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

        gateKeeper.verifyAccessible(
                logic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId()),
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        boolean notifyAboutDeadlines = getBooleanRequestParamValue(Const.ParamsNames.NOTIFY_ABOUT_DEADLINES);
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        DeadlineExtensionsUpdateRequest updateRequest =
                getAndValidateRequestBody(DeadlineExtensionsUpdateRequest.class);

        String courseId = feedbackSession.getCourseId();

        List<UpdateExtensionsResult> updateResults;
        try {
            updateResults = logic.updateDeadlineExtensions(feedbackSession, updateRequest.getUserDeadlines());
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        DeadlineExtensionsData responseData = new DeadlineExtensionsData(feedbackSession.getDeadlineExtensions());
        if (notifyAboutDeadlines) {
            sendEmails(updateResults, courseId, feedbackSession);
        }

        return new JsonResult(responseData);
    }

    private void sendEmails(List<UpdateExtensionsResult> updateResults, String courseId, FeedbackSession feedbackSession) {
        List<EmailWrapper> emailsToSend = new ArrayList<>();
        Course course = logic.getCourse(courseId);
        for (UpdateExtensionsResult result : updateResults) {
            EmailWrapper email = switch (result.updateType()) {
            case CREATED -> emailGenerator.generateDeadlineGrantedEmails(
                    course, feedbackSession, result.oldEndTime(), result.newEndTime(), result.user());
            case UPDATED -> emailGenerator.generateDeadlineUpdatedEmails(
                    course, feedbackSession, result.oldEndTime(), result.newEndTime(), result.user());
            case DELETED -> emailGenerator.generateDeadlineRevokedEmails(
                    course, feedbackSession, result.oldEndTime(), result.newEndTime(), result.user());
            };
            emailsToSend.add(email);
        }

        taskQueuer.scheduleEmailsForSending(emailsToSend);
    }
}
