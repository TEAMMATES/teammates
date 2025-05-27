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
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        FeedbackQuestionAttributes feedbackQuestion = null;
        FeedbackQuestion sqlFeedbackQuestion = null;
        String courseId;

        UUID feedbackQuestionSqlId;

        try {
            feedbackQuestionSqlId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
            sqlFeedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionSqlId);
        } catch (InvalidHttpParameterException verifyHttpParameterFailure) {
            // if the question id cannot be converted to UUID, we check the datastore for the question
            feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);
        }

        if (feedbackQuestion != null) {
            courseId = feedbackQuestion.getCourseId();
        } else if (sqlFeedbackQuestion != null) {
            courseId = sqlFeedbackQuestion.getCourseId();
        } else {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        if (!isCourseMigrated(courseId)) {
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

        FeedbackSession feedbackSession =
                getNonNullSqlFeedbackSession(sqlFeedbackQuestion.getFeedbackSession().getName(),
                                                sqlFeedbackQuestion.getCourseId());

        verifyInstructorCanSeeQuestionIfInModeration(sqlFeedbackQuestion);
        verifyNotPreview();

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            gateKeeper.verifyAnswerableForStudent(sqlFeedbackQuestion);
            Student student = getSqlStudentOfCourseFromRequest(feedbackSession.getCourse().getId());
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            gateKeeper.verifyAnswerableForInstructor(sqlFeedbackQuestion);
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

        FeedbackQuestionAttributes questionAttributes = null;
        FeedbackQuestion sqlFeedbackQuestion = null;
        String courseId;

        UUID feedbackQuestionSqlId;

        try {
            feedbackQuestionSqlId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
            sqlFeedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionSqlId);
        } catch (InvalidHttpParameterException verifyHttpParameterFailure) {
            // if the question id cannot be converted to UUID, we check the datastore for the question
            questionAttributes = logic.getFeedbackQuestion(feedbackQuestionId);
        }

        if (questionAttributes != null) {
            courseId = questionAttributes.getCourseId();
        } else if (sqlFeedbackQuestion != null) {
            courseId = sqlFeedbackQuestion.getCourseId();
        } else {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        if (!isCourseMigrated(courseId)) {
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
                // Only MCQ and MSQ questions can have participant comment
                FeedbackResponseCommentAttributes comment =
                        logic.getFeedbackResponseCommentForResponseFromParticipant(response.getId());
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
