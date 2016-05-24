package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

public abstract class FeedbackQuestionSubmissionEditSaveAction extends FeedbackSubmissionEditSaveAction {
    protected String feedbackQuestionId;
    protected FeedbackSubmissionEditPageData data;
    
    @Override
    protected void setAdditionalParameters() {
        feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1");
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestionId);
        
    }

    @Override
    protected abstract void verifyAccesibleForSpecificUser();

    @Override
    protected abstract void appendRespondant();

    @Override
    protected abstract void removeRespondant();
    
    @Override
    protected abstract String getUserEmailForCourse();
    
    @Override
    protected abstract String getUserSectionForCourse();
    
    @Override
    protected abstract FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse) throws EntityDoesNotExistException;
    
    @Override
    protected abstract void setStatusToAdmin();
    
    @Override
    protected abstract boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes fs);
}
