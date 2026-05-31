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
        UUID feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);
        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        String courseId = feedbackQuestion.getCourseId();
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        ResponseGiver responseGiver;
        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseForSubmission(courseId, true);

            responseGiver = feedbackQuestion.getGiverType() == QuestionGiverType.TEAMS
                    ? new ResponseGiver(student.getTeam())
                    : new ResponseGiver(student);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getInstructorOfCourseForSubmission(courseId, true);

            responseGiver = new ResponseGiver(instructor);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        Set<ResponseRecipient> recipients = logic.getRecipientsOfQuestion(feedbackQuestion, responseGiver);
        return new JsonResult(new FeedbackQuestionRecipientsData(recipients));
    }
}
