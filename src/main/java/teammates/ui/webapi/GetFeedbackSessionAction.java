package teammates.ui.webapi;

import java.time.Instant;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Student;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.request.Intent;

/**
 * Get a feedback session.
 */
public class GetFeedbackSessionAction extends BasicFeedbackSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }
        String courseId = feedbackSession.getCourseId();

        switch (intent) {
        case STUDENT_SUBMISSION, STUDENT_RESULT:
            Student student = getSqlStudentOfCourseFromRequest(courseId);
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION, INSTRUCTOR_RESULT:
            Instructor instructor = getSqlInstructorOfCourseFromRequest(courseId);
            checkAccessControlForInstructorFeedbackSubmission(instructor, feedbackSession);
            break;
        case FULL_DETAIL:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            gateKeeper.verifyAccessible(logic.getInstructorByGoogleId(courseId, userInfo.getId()),
                    feedbackSession, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }
        String courseId = feedbackSession.getCourseId();
        FeedbackSessionData response;

        switch (intent) {
        case STUDENT_SUBMISSION, STUDENT_RESULT:
            Student student = getSqlStudentOfCourseFromRequest(courseId);
            Instant studentDeadline = logic.getDeadlineForUser(feedbackSession, student);
            response = new FeedbackSessionData(feedbackSession, studentDeadline);
            response.hideInformation();
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructorSubmission = getSqlInstructorOfCourseFromRequest(courseId);
            response = new FeedbackSessionData(feedbackSession,
                    logic.getDeadlineForUser(feedbackSession,
                    instructorSubmission));
            response.hideInformation();
            break;
        case INSTRUCTOR_RESULT:
            Instructor instructorResult = getSqlInstructorOfCourseFromRequest(courseId);
            response = new FeedbackSessionData(feedbackSession,
                    logic.getDeadlineForUser(feedbackSession,
                    instructorResult));
            response.hideInformationForStudentAndInstructor();
            break;
        case FULL_DETAIL:
            response = new FeedbackSessionData(feedbackSession);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
        return new JsonResult(response);
    }
}
