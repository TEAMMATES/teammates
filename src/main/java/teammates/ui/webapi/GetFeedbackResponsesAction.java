package teammates.ui.webapi;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
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
class GetFeedbackResponsesAction extends BasicFeedbackSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestionAttributes feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion != null) {
            FeedbackSessionAttributes feedbackSession =
                    getNonNullFeedbackSession(feedbackQuestion.getFeedbackSessionName(), feedbackQuestion.getCourseId());

            verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
            verifyNotPreview();

            Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
            switch (intent) {
            case STUDENT_SUBMISSION:
                gateKeeper.verifyAnswerableForStudent(feedbackQuestion);
                StudentAttributes studentAttributes = getStudentOfCourseFromRequest(feedbackSession.getCourseId());
                checkAccessControlForStudentFeedbackSubmission(studentAttributes, feedbackSession);
                break;
            case INSTRUCTOR_SUBMISSION:
                gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
                InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(feedbackSession.getCourseId());
                checkAccessControlForInstructorFeedbackSubmission(instructorAttributes, feedbackSession);
                break;
            default:
                throw new InvalidHttpParameterException("Unknown intent " + intent);
            }
            return;
        }

        UUID feedbackQuestionSqlId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestion sqlFeedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionSqlId);
        if (sqlFeedbackQuestion == null) {
            throw new EntityNotFoundException("The feedback question does not exist.");
        }

        FeedbackSession feedbackSession =
                getNonNullSqlFeedbackSession(sqlFeedbackQuestion.getFeedbackSession().getName(),
                                                sqlFeedbackQuestion.getCourseId());

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
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        FeedbackQuestionAttributes questionAttributes = logic.getFeedbackQuestion(feedbackQuestionId);

        if (questionAttributes != null) {
            List<FeedbackResponseAttributes> responses;
            switch (intent) {
            case STUDENT_SUBMISSION:
                StudentAttributes studentAttributes = getStudentOfCourseFromRequest(questionAttributes.getCourseId());
                responses = logic.getFeedbackResponsesFromStudentOrTeamForQuestion(questionAttributes, studentAttributes);
                break;
            case INSTRUCTOR_SUBMISSION:
                InstructorAttributes instructorAttributes =
                        getInstructorOfCourseFromRequest(questionAttributes.getCourseId());
                responses = logic.getFeedbackResponsesFromInstructorForQuestion(questionAttributes, instructorAttributes);
                break;
            default:
                throw new InvalidHttpParameterException("Unknown intent " + intent);
            }

            List<FeedbackResponseData> responsesData = new LinkedList<>();
            responses.forEach(response -> {
                FeedbackResponseData data = new FeedbackResponseData(response);
                if (questionAttributes.getQuestionType() == FeedbackQuestionType.MCQ
                        || questionAttributes.getQuestionType() == FeedbackQuestionType.MSQ) {
                    // Only MCQ and MSQ questions can have participant comment
                    FeedbackResponseCommentAttributes comment =
                            logic.getFeedbackResponseCommentForResponseFromParticipant(response.getId());
                    if (comment != null) {
                        data.setGiverComment(new FeedbackResponseCommentData(comment));
                    }
                }
                responsesData.add(data);
            });
            FeedbackResponsesData result = new FeedbackResponsesData();
            if (!responsesData.isEmpty()) {
                result.setResponses(responsesData);
            }

            return new JsonResult(result);
        }

        UUID feedbackQuestionSqlId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestion sqlFeedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionSqlId);

        if (sqlFeedbackQuestion == null) {
            throw new EntityNotFoundException("The feedback question does not exist.");
        }

        List<FeedbackResponse> responses;
        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getSqlStudentOfCourseFromRequest(sqlFeedbackQuestion.getCourseId());
            responses = sqlLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(sqlFeedbackQuestion, student);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getSqlInstructorOfCourseFromRequest(sqlFeedbackQuestion.getCourseId());
            responses = sqlLogic.getFeedbackResponsesFromInstructorForQuestion(sqlFeedbackQuestion, instructor);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        List<FeedbackResponseData> responsesData = new LinkedList<>();
        responses.forEach(response -> {
            FeedbackResponseData data = new FeedbackResponseData(response);
            if (sqlFeedbackQuestion.getQuestionDetailsCopy().getQuestionType() == FeedbackQuestionType.MCQ
                    || sqlFeedbackQuestion.getQuestionDetailsCopy().getQuestionType() == FeedbackQuestionType.MSQ) {
                // Only MCQ and MSQ questions can have participant comment
                FeedbackResponseComment comment =
                        sqlLogic.getFeedbackResponseCommentForResponseFromParticipant(response.getId());
                if (comment != null) {
                    data.setGiverComment(new FeedbackResponseCommentData(comment));
                }
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
