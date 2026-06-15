package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionViewData;
import teammates.ui.output.InstructorFeedbackSessionPermissionsData;
import teammates.ui.request.Intent;

/**
 * Get a feedback session.
 */
public class GetFeedbackSessionAction extends BasicFeedbackSubmissionAction {
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
            gateKeeper.verifyInstructorHasPrivilege(requestContext, courseId,
                    Const.InstructorPermissions.CAN_VIEW_SESSION);
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
        FeedbackSessionViewData response;

        switch (intent) {
        case STUDENT_SUBMISSION, STUDENT_RESULT:
            response = new FeedbackSessionViewData(new FeedbackSessionData(feedbackSession));
            response.getFeedbackSession().hideInformation();
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructorSubmission = getInstructorOfCourseForSubmission(courseId, true);
            response = new FeedbackSessionViewData(new FeedbackSessionData(feedbackSession));
            response.getFeedbackSession().hideInformation();
            response.setInstructorPermissions(getPermissions(feedbackSession, instructorSubmission));
            break;
        case INSTRUCTOR_RESULT:
            Instructor instructorResult = getInstructorOfCourseForResult(courseId);
            response = new FeedbackSessionViewData(new FeedbackSessionData(feedbackSession));
            response.getFeedbackSession().hideInformation();
            response.setInstructorPermissions(getPermissions(feedbackSession, instructorResult));
            break;
        case FULL_DETAIL:
            Instructor instructor = getInstructorFromRequest(courseId);
            response = new FeedbackSessionViewData(new FeedbackSessionData(feedbackSession));
            if (instructor != null) {
                response.setInstructorPermissions(getPermissions(feedbackSession, instructor));
            }
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
        return new JsonResult(response);
    }

    private InstructorFeedbackSessionPermissionsData getPermissions(FeedbackSession feedbackSession,
            Instructor instructorSubmission) {
        boolean canModifySession =
                logic.hasInstructorPermissions(instructorSubmission, Const.InstructorPermissions.CAN_MODIFY_SESSION);
        boolean canSubmitSession = logic.hasInstructorPermissions(instructorSubmission,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION)
                || logic.hasInstructorPermissionsForSectionInAnySection(instructorSubmission, feedbackSession.getId(),
                Const.InstructorPermissions.CAN_SUBMIT_SESSION);
        boolean canViewSession = logic.hasInstructorPermissions(instructorSubmission,
                Const.InstructorPermissions.CAN_VIEW_SESSION)
                || logic.hasInstructorPermissionsForSectionInAnySection(instructorSubmission, feedbackSession.getId(),
                Const.InstructorPermissions.CAN_VIEW_SESSION);
        return new InstructorFeedbackSessionPermissionsData(
                canModifySession,
                canSubmitSession,
                canViewSession);
    }
}
