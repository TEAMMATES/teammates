package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackResultsSeeMoreAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull("null course id", courseId);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull("null feedback session name", feedbackSessionName);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean isCreatorOnly = true;
        
        new GateKeeper().verifyAccessible(
                instructor, 
                session,
                !isCreatorOnly);
        
        InstructorFeedbackResultsSortedQuestionPageData data = 
                new InstructorFeedbackResultsSortedQuestionPageData(account);
        
        String lastQuestionNumberString = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);
        Assumption.assertNotNull("null question number", lastQuestionNumberString);
        
        int questionNum = Integer.parseInt(lastQuestionNumberString) + 1;
        FeedbackSessionResultsBundle bundle = logic.getFeedbackSessionResultsForInstructorFromQuestion(feedbackSessionName, courseId, instructor.email, questionNum);
        data.questionResponseMap = bundle.getQuestionResponseMap();
        
        return createAjaxResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION, data);
    }

}
