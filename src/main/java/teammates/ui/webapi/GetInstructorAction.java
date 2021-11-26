package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.InstructorData;
import teammates.ui.request.Intent;

/**
 * Get the information of an instructor inside a course.
 */
class GetInstructorAction extends BasicFeedbackSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case INSTRUCTOR_SUBMISSION:
            String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
            String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
            FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(feedbackSession.getCourseId());
            checkAccessControlForInstructorFeedbackSubmission(instructorAttributes, feedbackSession);
            break;
        case INSTRUCTOR_RESULT:
        case FULL_DETAIL:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        String intentString = getNonNullRequestParamValue(Const.ParamsNames.INTENT);
        Intent intent;
        try {
            intent = Intent.valueOf(intentString);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException("Invalid intent: " + intentString, e);
        }

        InstructorAttributes instructorAttributes;
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        switch (intent) {
        case INSTRUCTOR_SUBMISSION:
            instructorAttributes = getInstructorOfCourseFromRequest(courseId);
            break;
        case INSTRUCTOR_RESULT:
        case FULL_DETAIL:
            instructorAttributes = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        if (instructorAttributes == null) {
            throw new EntityNotFoundException("Instructor could not be found for this course");
        }

        InstructorData instructorData = new InstructorData(instructorAttributes);
        if (intent == Intent.FULL_DETAIL) {
            instructorData.setGoogleId(instructorAttributes.getGoogleId());
        }

        return new JsonResult(instructorData);
    }

}
