package teammates.ui.webapi;

import java.util.Set;
import java.util.UUID;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
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
        return AuthType.REG_KEY;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {

        FeedbackQuestion feedbackQuestion = null;

        UUID feedbackQuestionId;

        feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

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
            Student student = getStudentOfCourseForSubmission(feedbackSession.getCourseId());
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
            Instructor instructor = getInstructorOfCourseForSubmission(feedbackSession.getCourseId());
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

        feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion != null) {
            courseId = feedbackQuestion.getCourseId();
        } else {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        ResponseGiver responseGiver;
        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseForSubmission(courseId);

            responseGiver = feedbackQuestion.getGiverType() == QuestionGiverType.TEAMS
                    ? new ResponseGiver(student.getTeam())
                    : new ResponseGiver(student);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getInstructorOfCourseForSubmission(courseId);

            responseGiver = new ResponseGiver(instructor);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        Set<ResponseRecipient> recipients = logic.getRecipientsOfQuestion(feedbackQuestion, responseGiver);
        return new JsonResult(new FeedbackQuestionRecipientsData(recipients));
    }
}
