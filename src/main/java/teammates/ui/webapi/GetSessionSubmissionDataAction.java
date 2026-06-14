package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.SessionSubmissionBundle;
import teammates.common.datatransfer.SessionSubmissionBundle.QuestionSubmissionBundle;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.FeedbackQuestionRecipientsData;
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.output.SessionSubmissionData;
import teammates.ui.output.SessionSubmissionData.SessionSubmissionQuestionData;
import teammates.ui.request.Intent;

/**
 * Get all feedback session submission data needed by the submission page.
 */
public class GetSessionSubmissionDataAction extends BasicFeedbackSubmissionAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseForSubmission(feedbackSession.getCourseId(), true);
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getInstructorOfCourseForSubmission(feedbackSession.getCourseId(), true);
            checkAccessControlForInstructorFeedbackSubmission(instructor, feedbackSession);
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

        Student student = null;
        Instructor instructor = null;
        switch (intent) {
        case STUDENT_SUBMISSION:
            student = getStudentOfCourseForSubmission(feedbackSession.getCourseId(), true);
            break;
        case INSTRUCTOR_SUBMISSION:
            instructor = getInstructorOfCourseForSubmission(feedbackSession.getCourseId(), true);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        boolean isPreview = !StringHelper.isEmpty(getRequestParamValue(Const.ParamsNames.PREVIEWAS));
        boolean isModeration = !StringHelper.isEmpty(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON));
        SessionSubmissionBundle bundle;
        switch (intent) {
        case STUDENT_SUBMISSION:
            bundle = logic.getSessionSubmissionBundleForStudent(feedbackSession, student, isPreview, isModeration);
            break;
        case INSTRUCTOR_SUBMISSION:
            bundle = logic.getSessionSubmissionBundleForInstructor(feedbackSession, instructor, isPreview,
                    isModeration);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
        List<SessionSubmissionQuestionData> questionData = bundle.getQuestionSubmissionBundles().stream()
                .map(this::buildQuestionData)
                .toList();

        return new JsonResult(new SessionSubmissionData(questionData));
    }

    private SessionSubmissionQuestionData buildQuestionData(QuestionSubmissionBundle questionSubmissionBundle) {
        FeedbackQuestionData questionData = new FeedbackQuestionData(questionSubmissionBundle.getQuestion(),
                questionSubmissionBundle.getDynamicallyGeneratedOptions());
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        if (intent == Intent.STUDENT_SUBMISSION) {
            questionData.hideInformationForStudent();
        }

        FeedbackQuestionRecipientsData recipientsData =
                new FeedbackQuestionRecipientsData(questionSubmissionBundle.getRecipients());
        List<FeedbackResponseData> responseData = questionSubmissionBundle.getResponses()
                .stream()
                .map(FeedbackResponseData::new)
                .toList();

        return new SessionSubmissionQuestionData(questionData, recipientsData.getRecipients(), responseData);
    }
}
