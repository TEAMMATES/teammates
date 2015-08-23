package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;

public abstract class FeedbackQuestionSubmissionEditPageAction extends Action {
    protected String courseId;
    protected String feedbackSessionName;
    protected String feedbackQuestionId;
    protected FeedbackSubmissionEditPageData data;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(feedbackSessionName);
        
        feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        Assumption.assertNotNull(feedbackQuestionId);
        

        String regKey = getRequestParamValue(Const.ParamsNames.REGKEY);      
        String email = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        
        if (!isSpecificUserJoinedCourse()) {
            return createPleaseJoinCourseResponse(courseId);
        }
        
        verifyAccesibleForSpecificUser();
        
        String userEmailForCourse = getUserEmailForCourse();        
        data = new FeedbackSubmissionEditPageData(account, student);
        data.setShowRealQuestionNumber(true);
        data.setHeaderHidden(true);
        data.bundle = getDataBundle(userEmailForCourse);
        data.setSessionOpenForSubmission(isSessionOpenForSpecificUser(data.bundle.feedbackSession));
        
        if (!data.isSessionOpenForSubmission()) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN, StatusMessageColor.WARNING));
        }
        
        setStatusToAdmin();
        
        data.init(regKey, email, courseId);
        
        return createSpecificShowPageResult();
    }

    protected abstract boolean isSpecificUserJoinedCourse();
    
    protected abstract void verifyAccesibleForSpecificUser();
    
    protected abstract String getUserEmailForCourse();

    protected abstract FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse) throws EntityDoesNotExistException;
    
    protected abstract boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes fs);
    
    protected abstract void setStatusToAdmin();
    
    protected abstract ShowPageResult createSpecificShowPageResult();
}
