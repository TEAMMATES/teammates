package teammates.ui.webapi;

import java.time.Instant;

import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
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
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        switch (intent) {
        case STUDENT_SUBMISSION:
        case STUDENT_RESULT:
            Student student = getSqlStudentOfCourseFromRequest(courseId);
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
        case INSTRUCTOR_RESULT:
            Instructor instructor = getSqlInstructorOfCourseFromRequest(courseId);
            checkAccessControlForInstructorFeedbackSubmission(instructor, feedbackSession);
            break;
        case FULL_DETAIL:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            gateKeeper.verifyAccessible(sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()),
                    feedbackSession, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        FeedbackSessionData response;

        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        switch (intent) {
        case STUDENT_SUBMISSION:
        case STUDENT_RESULT:
            Student student = getSqlStudentOfCourseFromRequest(courseId);
            Instant studentDeadline = sqlLogic.getDeadlineForUser(feedbackSession, student);
            response = new FeedbackSessionData(feedbackSession, studentDeadline);
            response.hideInformationForStudent(student.getEmail());
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructorSubmission = getSqlInstructorOfCourseFromRequest(courseId);
            response = new FeedbackSessionData(feedbackSession,
                    sqlLogic.getDeadlineForUser(feedbackSession,
                    instructorSubmission));
            response.hideInformationForInstructorSubmission(instructorSubmission.getEmail());
            break;
        case INSTRUCTOR_RESULT:
            Instructor instructorResult = getSqlInstructorOfCourseFromRequest(courseId);
            response = new FeedbackSessionData(feedbackSession,
                    sqlLogic.getDeadlineForUser(feedbackSession,
                    instructorResult));
            response.hideInformationForInstructor(instructorResult.getEmail());
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
