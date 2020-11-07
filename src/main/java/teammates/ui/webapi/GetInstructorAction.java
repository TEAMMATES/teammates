package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
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
    void checkSpecificAccessControl() {
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case INSTRUCTOR_SUBMISSION:
            String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
            String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
            FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

            if (feedbackSession == null) {
                throw new EntityNotFoundException(new EntityDoesNotExistException("Feedback Session could not be "
                        + "found"));
            }

            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(feedbackSession.getCourseId());
            checkAccessControlForInstructorFeedbackSubmission(instructorAttributes, feedbackSession);
            break;
        case FULL_DETAIL:
            gateKeeper.verifyLoggedInUserPrivileges();
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    JsonResult execute() {
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        InstructorAttributes instructorAttributes;
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        switch (intent) {
        case INSTRUCTOR_SUBMISSION:
            instructorAttributes = getInstructorOfCourseFromRequest(courseId);
            break;
        case FULL_DETAIL:
            instructorAttributes = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        if (instructorAttributes == null) {
            return new JsonResult("Instructor could not be found for this course", HttpStatus.SC_NOT_FOUND);
        }

        InstructorData instructorData = new InstructorData(instructorAttributes);
        if (intent == Intent.FULL_DETAIL) {
            instructorData.setGoogleId(instructorAttributes.googleId);
        }

        return new JsonResult(instructorData);
    }

}
