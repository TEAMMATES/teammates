package teammates.ui.webapi.action;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.FeedbackResponsesData;
import teammates.ui.webapi.request.Intent;

/**
 * Get all responses given by the user for a question.
 */
public class GetFeedbackResponsesAction extends BasicFeedbackSubmissionAction {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestionAttributes feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);
        if (feedbackQuestion == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("The feedback question does not exist."));
        }
        FeedbackSessionAttributes feedbackSession =
                getFeedbackSession(feedbackQuestion.getFeedbackSessionName(), feedbackQuestion.getCourseId());

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
    }

    @Override
    public ActionResult execute() {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        FeedbackQuestionAttributes questionAttributes = logic.getFeedbackQuestion(feedbackQuestionId);

        FeedbackResponsesData result;
        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(questionAttributes.getCourseId());
            result = new FeedbackResponsesData(
                    logic.getFeedbackResponsesFromStudentOrTeamForQuestion(questionAttributes, studentAttributes));
            break;
        case INSTRUCTOR_SUBMISSION:
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(questionAttributes.getCourseId());
            result = new FeedbackResponsesData(
                    logic.getFeedbackResponsesFromInstructorForQuestion(questionAttributes, instructorAttributes));
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        return new JsonResult(result);
    }

}
