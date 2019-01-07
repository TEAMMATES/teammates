package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorFeedbackQuestionCopyPageData;

public class InstructorFeedbackQuestionCopyPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);

        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);
        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                feedbackSession, false,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        List<FeedbackQuestionAttributes> copiableQuestions = null;

        copiableQuestions = logic.getCopiableFeedbackQuestionsForInstructor(account.googleId);

        InstructorFeedbackQuestionCopyPageData data =
                new InstructorFeedbackQuestionCopyPageData(account, sessionToken, copiableQuestions);
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_COPY_MODAL, data);
    }
}
