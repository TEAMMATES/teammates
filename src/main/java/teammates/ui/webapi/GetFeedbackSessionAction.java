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
        FeedbackSessionData response = getFeedbackSessionDataWithFilteredDeadlines(intent);

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

    private FeedbackSessionData getFeedbackSessionDataWithFilteredDeadlines(Intent intent) {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSessionAttributes = getNonNullFeedbackSession(feedbackSessionName, courseId);

        FeedbackSessionData response;
        switch (intent) {
        case STUDENT_SUBMISSION:
            // fall-through
        case STUDENT_RESULT:
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(courseId);
            String studentEmailAddress = studentAttributes.getEmail();
            feedbackSessionAttributes = feedbackSessionAttributes.getCopyForStudent(studentEmailAddress);
            response = new FeedbackSessionData(feedbackSessionAttributes);
            response.filterDeadlinesForStudent(studentEmailAddress);
            break;
        case INSTRUCTOR_SUBMISSION:
            // fall-through
        case INSTRUCTOR_RESULT:
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(courseId);
            String instructorEmailAddress = instructorAttributes.getEmail();
            feedbackSessionAttributes = feedbackSessionAttributes.getCopyForInstructor(instructorEmailAddress);
            response = new FeedbackSessionData(feedbackSessionAttributes);
            response.filterDeadlinesForInstructor(instructorEmailAddress);
            break;
        case FULL_DETAIL:
            response = new FeedbackSessionData(feedbackSessionAttributes);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        return response;
    }
}
