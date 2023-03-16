package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
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
        FeedbackSessionAttributes feedbackSessionAttributes = getNonNullFeedbackSession(feedbackSessionName, courseId);
        FeedbackSession feedbackSession = getNonNullSqlFeedbackSession(feedbackSessionName, courseId);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case STUDENT_SUBMISSION:
        case STUDENT_RESULT:
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(courseId);
            checkAccessControlForStudentFeedbackSubmission(studentAttributes, feedbackSessionAttributes);
            break;
        case INSTRUCTOR_SUBMISSION:
        case INSTRUCTOR_RESULT:
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(courseId);
            checkAccessControlForInstructorFeedbackSubmission(instructorAttributes, feedbackSessionAttributes);
            break;
        case FULL_DETAIL:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            if (!isCourseMigrated(courseId)) {
                gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                    feedbackSessionAttributes, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
            } else {
                gateKeeper.verifyAccessible(sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()),
                    feedbackSession, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
            }
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        FeedbackSessionData response;
        switch (intent) {
        case STUDENT_SUBMISSION:
        case STUDENT_RESULT:
            response = getStudentFeedbackSessionData(feedbackSession);
            response.hideInformationForStudent();
            break;
        case INSTRUCTOR_SUBMISSION:
            response = getInstructorFeedbackSessionData(feedbackSession);
            response.hideInformationForInstructorSubmission();
            break;
        case INSTRUCTOR_RESULT:
            response = getInstructorFeedbackSessionData(feedbackSession);
            response.hideInformationForInstructor();
            break;
        case FULL_DETAIL:
            response = new FeedbackSessionData(feedbackSession);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
        return new JsonResult(response);
    }

    private FeedbackSessionData getStudentFeedbackSessionData(FeedbackSessionAttributes session) {
        StudentAttributes student = getStudentOfCourseFromRequest(session.getCourseId());
        String email = student.getEmail();
        return new FeedbackSessionData(session.getCopyForStudent(email));
    }

    private FeedbackSessionData getInstructorFeedbackSessionData(FeedbackSessionAttributes session) {
        InstructorAttributes instructor = getInstructorOfCourseFromRequest(session.getCourseId());
        String email = instructor.getEmail();
        return new FeedbackSessionData(session.getCopyForInstructor(email));
    }
}
