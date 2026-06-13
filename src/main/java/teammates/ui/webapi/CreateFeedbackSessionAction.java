package teammates.ui.webapi;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.request.FeedbackSessionCreateRequest;

/**
 * Create a feedback session.
 */
public class CreateFeedbackSessionAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        gateKeeper.verifyInstructorHasPrivilege(requestContext, courseId,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        FeedbackSessionCreateRequest createRequest =
                    getAndValidateRequestBody(FeedbackSessionCreateRequest.class);
        Instructor instructor = getInstructorFromRequest(courseId);
        if (instructor == null) {
            throw new EntityNotFoundException("Failed to find instructor with the given courseId and googleId.");
        }

        try {
            FeedbackSession feedbackSession = logic.createFeedbackSession(courseId, instructor, createRequest);
            return new JsonResult(new FeedbackSessionData(feedbackSession));
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }
}
