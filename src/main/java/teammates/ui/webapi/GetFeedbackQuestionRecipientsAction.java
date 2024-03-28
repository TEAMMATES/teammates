package teammates.ui.webapi;

import java.util.Map;
import java.util.UUID;

import teammates.common.datatransfer.FeedbackQuestionRecipient;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
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
            verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);

            Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
            FeedbackSessionAttributes feedbackSession =
                    getNonNullFeedbackSession(feedbackQuestion.getFeedbackSessionName(), feedbackQuestion.getCourseId());
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

        verifyInstructorCanSeeQuestionIfInModeration(sqlFeedbackQuestion);

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        FeedbackSession feedbackSession =
                getNonNullSqlFeedbackSession(sqlFeedbackQuestion.getFeedbackSession().getName(),
                                                sqlFeedbackQuestion.getCourseId());
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
        FeedbackQuestionAttributes question = null;
        FeedbackQuestion sqlFeedbackQuestion = null;
        String courseId;

        UUID feedbackQuestionSqlId;

        try {
            feedbackQuestionSqlId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
            sqlFeedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionSqlId);
        } catch (InvalidHttpParameterException verifyHttpParameterFailure) {
            // if the question id cannot be converted to UUID, we check the datastore for the question
            question = logic.getFeedbackQuestion(feedbackQuestionId);
        }

        if (question != null) {
            courseId = question.getCourseId();
        } else if (sqlFeedbackQuestion != null) {
            courseId = sqlFeedbackQuestion.getCourseId();
        } else {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        if (!isCourseMigrated(courseId)) {
            Map<String, FeedbackQuestionRecipient> recipient;
            switch (intent) {
            case STUDENT_SUBMISSION:
                StudentAttributes studentAttributes = getStudentOfCourseFromRequest(question.getCourseId());
                recipient = logic.getRecipientsOfQuestion(question, null, studentAttributes);
                break;
            case INSTRUCTOR_SUBMISSION:
                InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(question.getCourseId());
                recipient = logic.getRecipientsOfQuestion(question, instructorAttributes, null);
                break;
            default:
                throw new InvalidHttpParameterException("Unknown intent " + intent);
            }
            return new JsonResult(new FeedbackQuestionRecipientsData(recipient));
        }

        Map<String, FeedbackQuestionRecipient> recipient;
        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getSqlStudentOfCourseFromRequest(courseId);

            recipient = sqlLogic.getRecipientsOfQuestion(sqlFeedbackQuestion, null, student);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getSqlInstructorOfCourseFromRequest(courseId);

            recipient = sqlLogic.getRecipientsOfQuestion(sqlFeedbackQuestion, instructor, null);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
        return new JsonResult(new FeedbackQuestionRecipientsData(recipient));
    }
}
