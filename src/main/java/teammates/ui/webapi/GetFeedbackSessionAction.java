package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.request.Intent;

/**
 * Get a feedback session.
 */
class GetFeedbackSessionAction extends BasicFeedbackSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case STUDENT_SUBMISSION:
        case STUDENT_RESULT:
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(courseId);
            checkAccessControlForStudentFeedbackSubmission(studentAttributes, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
        case INSTRUCTOR_RESULT:
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(courseId);
            checkAccessControlForInstructorFeedbackSubmission(instructorAttributes, feedbackSession);
            break;
        case FULL_DETAIL:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                    feedbackSession, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        FeedbackSessionAttributes feedbackSession = getFeedbackSessionAttributesWithCorrectDeadlines(intent);
        FeedbackSessionData response = new FeedbackSessionData(feedbackSession);

        switch (intent) {
        case STUDENT_SUBMISSION:
            // fall-through
        case STUDENT_RESULT:
            // fall-through
        case INSTRUCTOR_SUBMISSION:
            response.hideInformationForStudent();
            break;
        case INSTRUCTOR_RESULT:
            response.hideInformationForInstructor();
            break;
        case FULL_DETAIL:
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        return new JsonResult(response);
    }

    private FeedbackSessionAttributes getFeedbackSessionAttributesWithCorrectDeadlines(Intent intent) {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSessionAttributes = getNonNullFeedbackSession(feedbackSessionName, courseId);

        switch (intent) {
        case STUDENT_SUBMISSION:
            // fall-through
        case STUDENT_RESULT:
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(courseId);
            String studentEmailAddress = studentAttributes.getEmail();
            feedbackSessionAttributes = feedbackSessionAttributes.getCopyForStudent(studentEmailAddress);
            break;
        case INSTRUCTOR_SUBMISSION:
            // fall-through
        case INSTRUCTOR_RESULT:
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(courseId);
            String instructorEmailAddress = instructorAttributes.getEmail();
            feedbackSessionAttributes = feedbackSessionAttributes.getCopyForInstructor(instructorEmailAddress);
            break;
        case FULL_DETAIL:
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        return feedbackSessionAttributes;
    }
}
