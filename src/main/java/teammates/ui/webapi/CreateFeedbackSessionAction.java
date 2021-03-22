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
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.request.FeedbackSessionCreateRequest;

/**
 * Create a feedback session.
 */
class CreateFeedbackSessionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        CourseAttributes course = logic.getCourse(courseId);

        gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    JsonResult execute() {
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
        FeedbackSessionData output = new FeedbackSessionData(fs);
        InstructorPrivilegeData privilege = constructInstructorPrivileges(instructor, feedbackSessionName);
        output.setPrivileges(privilege);

        return new JsonResult(output);
    }

}
