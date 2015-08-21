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
        super.setAdditionalParameters();
        
        feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1");
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestionId);
        
    }
    
    @Override
    protected boolean isUserRespondentOfSession() {
        // Since only the form only contains a single question,
        // we need to use the database for handling the situation where the user has responses to other 
        // questions if there is no response to the single question on the submitted form.
        return isHasResponse 
               || logic.hasGiverRespondedForSession(getUserEmailForCourse(), feedbackSessionName, courseId);
    }

    protected abstract void verifyAccesibleForSpecificUser();

    protected abstract void appendRespondant();

    protected abstract void removeRespondant();
    
    protected abstract String getUserEmailForCourse();
    
    protected abstract String getUserSectionForCourse();
    
    protected abstract FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse) throws EntityDoesNotExistException;
    
    protected abstract void setStatusToAdmin();
    
    protected abstract boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes fs);
}
