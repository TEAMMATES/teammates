package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.request.FeedbackSessionCreateRequest;

/**
 * Create a feedback session.
 */
public class CreateFeedbackSessionAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        CourseAttributes course = logic.getCourse(courseId);

        gateKeeper.verifyAccessible(instructor, course, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        CourseAttributes course = logic.getCourse(courseId);

        FeedbackSessionCreateRequest createRequest =
                getAndValidateRequestBody(FeedbackSessionCreateRequest.class);

        String feedbackSessionName = SanitizationHelper.sanitizeTitle(createRequest.getFeedbackSessionName());

        FeedbackSessionAttributes fs =
                FeedbackSessionAttributes
                        .builder(feedbackSessionName, course.getId())
                        .withCreatorEmail(instructor.getEmail())
                        .withTimeZone(course.getTimeZone())
                        .withInstructions(createRequest.getInstructions())
                        .withStartTime(createRequest.getSubmissionStartTime())
                        .withEndTime(createRequest.getSubmissionEndTime())
                        .withGracePeriod(createRequest.getGracePeriod())
                        .withSessionVisibleFromTime(createRequest.getSessionVisibleFromTime())
                        .withResultsVisibleFromTime(createRequest.getResultsVisibleFromTime())
                        .withIsClosingEmailEnabled(createRequest.isClosingEmailEnabled())
                        .withIsPublishedEmailEnabled(createRequest.isPublishedEmailEnabled())
                        .build();

        try {
            logic.createFeedbackSession(fs);
        } catch (EntityAlreadyExistsException | InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e.getMessage(), e);
        }

        fs = getNonNullFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId());
        return new JsonResult(new FeedbackSessionData(fs));
    }

}
