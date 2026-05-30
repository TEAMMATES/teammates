package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.SessionResultsData;
import teammates.ui.request.Intent;

/**
 * Gets user-scoped feedback session results for instructor/student result views.
 */
public class GetUserSessionResultsAction extends BasicFeedbackSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.REG_KEY;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);
        boolean isPreviewResults = !StringHelper.isEmpty(previewAsPerson);

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        String courseId = feedbackSession.getCourseId();

        switch (intent) {
        case INSTRUCTOR_RESULT:
            if (!isPreviewResults && !feedbackSession.isPublished()) {
                throw new UnauthorizedAccessException("This feedback session is not yet published.", true);
            }
            Instructor instructor = getInstructorOfCourseForResult(courseId);
            checkAccessControlForInstructorFeedbackResult(instructor, feedbackSession);
            break;
        case STUDENT_RESULT:
            if (!isPreviewResults && !feedbackSession.isPublished()) {
                throw new UnauthorizedAccessException("This feedback session is not yet published.", true);
            }
            Student student = getStudentOfCourseForResult(courseId);
            checkAccessControlForStudentFeedbackResult(student, feedbackSession);
            break;
        case FULL_DETAIL, INSTRUCTOR_SUBMISSION, STUDENT_SUBMISSION:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        UUID questionId = getNullableUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);
        boolean isPreviewResults = !StringHelper.isEmpty(previewAsPerson);

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        String courseId = feedbackSession.getCourseId();
        SessionResultsBundle bundle;

        switch (intent) {
        case INSTRUCTOR_RESULT:
            Instructor instructor = getInstructorOfCourseForResult(courseId);
            bundle = logic.getSessionResultsForUser(feedbackSession, instructor, questionId, isPreviewResults);
            return new JsonResult(SessionResultsData.initForUser(bundle, instructor));
        case STUDENT_RESULT:
            Student student = getStudentOfCourseForResult(courseId);
            bundle = logic.getSessionResultsForUser(feedbackSession, student, questionId, isPreviewResults);
            return new JsonResult(SessionResultsData.initForUser(bundle, student));
        case FULL_DETAIL, INSTRUCTOR_SUBMISSION, STUDENT_SUBMISSION:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }
}
