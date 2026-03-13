package teammates.ui.webapi;

import java.util.Map;
import java.util.UUID;

import teammates.common.datatransfer.FeedbackQuestionRecipient;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackQuestionRecipientsData;
import teammates.ui.request.Intent;

/**
 * Get the recipients of a feedback question.
 *
 * @see FeedbackQuestionRecipientsData for output format
 */
public class GetFeedbackQuestionRecipientsAction extends BasicFeedbackSubmissionAction {

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

        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        FeedbackSession feedbackSession =
                getNonNullFeedbackSession(feedbackQuestion.getFeedbackSession().getName(),
                                                feedbackQuestion.getCourseId());
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
        String courseId;

        UUID feedbackQuestionId;

        feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        feedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion != null) {
            courseId = feedbackQuestion.getCourseId();
        } else {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        Map<String, FeedbackQuestionRecipient> recipient;
        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getSqlStudentOfCourseFromRequest(courseId);

            recipient = sqlLogic.getRecipientsOfQuestion(feedbackQuestion, null, student);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getSqlInstructorOfCourseFromRequest(courseId);

            recipient = sqlLogic.getRecipientsOfQuestion(feedbackQuestion, instructor, null);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
        return new JsonResult(new FeedbackQuestionRecipientsData(recipient));
    }
}
