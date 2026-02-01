package teammates.ui.webapi;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.output.FeedbackResponsesData;
import teammates.ui.request.Intent;

/**
 * Get all responses given by the user for a question.
 */
public class GetFeedbackResponsesAction extends BasicFeedbackSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        FeedbackQuestion feedbackQuestion = null;

        UUID feedbackQuestionId;

        feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        feedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionId);

        FeedbackSession feedbackSession =
                getNonNullFeedbackSession(feedbackQuestion.getFeedbackSession().getName(),
                                                feedbackQuestion.getCourseId());

        verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
        verifyNotPreview();

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            gateKeeper.verifyAnswerableForStudent(feedbackQuestion);
            Student student = getSqlStudentOfCourseFromRequest(feedbackSession.getCourse().getId());
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
            Instructor instructor = getSqlInstructorOfCourseFromRequest(feedbackSession.getCourse().getId());
            checkAccessControlForInstructorFeedbackSubmission(instructor, feedbackSession);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        FeedbackQuestion feedbackQuestion = null;

        UUID feedbackQuestionId;

        feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        feedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionId);

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        List<FeedbackResponse> responses;
        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getSqlStudentOfCourseFromRequest(feedbackQuestion.getCourseId());
            responses = sqlLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(feedbackQuestion, student);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getSqlInstructorOfCourseFromRequest(feedbackQuestion.getCourseId());
            responses = sqlLogic.getFeedbackResponsesFromInstructorForQuestion(feedbackQuestion, instructor);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        List<FeedbackResponseData> responsesData = new LinkedList<>();
        responses.forEach(response -> {
            FeedbackResponseData data = new FeedbackResponseData(response);
            // Only MCQ and MSQ questions can have participant comment
            FeedbackResponseComment comment =
                    sqlLogic.getFeedbackResponseCommentForResponseFromParticipant(response.getId());
            if (comment != null) {
                data.setGiverComment(new FeedbackResponseCommentData(comment));
            }
            responsesData.add(data);
        });
        FeedbackResponsesData result = new FeedbackResponsesData();
        if (!responsesData.isEmpty()) {
            result.setResponses(responsesData);
        }

        return new JsonResult(result);
    }

}
