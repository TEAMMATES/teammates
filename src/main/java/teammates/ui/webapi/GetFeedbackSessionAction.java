package teammates.ui.webapi;

import java.time.Instant;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.request.Intent;

/**
 * Get a feedback session.
 */
public class GetFeedbackSessionAction extends BasicFeedbackSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.REG_KEY;
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
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseForSubmission(courseId, true);
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case STUDENT_RESULT:
            student = getStudentOfCourseForResult(courseId);
            checkAccessControlForStudentFeedbackResult(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getInstructorOfCourseForSubmission(courseId, true);
            checkAccessControlForInstructorFeedbackSubmission(instructor, feedbackSession);
            break;
        case INSTRUCTOR_RESULT:
            instructor = getInstructorOfCourseForResult(courseId);
            checkAccessControlForInstructorFeedbackResult(instructor, feedbackSession);
            break;
        case FULL_DETAIL:
            gateKeeper.verifyLoggedInUserPrivileges(requestContext);
            Instructor fullDetailInstructor = getInstructorFromRequest(courseId);
            gateKeeper.verifyInstructorInCourse(requestContext, courseId);
            gateKeeper.verifyInstructorHasPrivilege(fullDetailInstructor,
                    Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
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
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseForSubmission(courseId, true);
            Instant studentDeadline = logic.getDeadlineForUser(feedbackSession, student);
            response = new FeedbackSessionData(feedbackSession, studentDeadline);
            response.hideInformation();
            break;
        case STUDENT_RESULT:
            student = getStudentOfCourseForResult(courseId);
            studentDeadline = logic.getDeadlineForUser(feedbackSession, student);
            response = new FeedbackSessionData(feedbackSession, studentDeadline);
            response.hideInformation();
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructorSubmission = getInstructorOfCourseForSubmission(courseId, true);
            response = new FeedbackSessionData(feedbackSession,
                    logic.getDeadlineForUser(feedbackSession,
                    instructorSubmission));
            response.hideInformation();
            break;
        case INSTRUCTOR_RESULT:
            Instructor instructorResult = getInstructorOfCourseForResult(courseId);
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
