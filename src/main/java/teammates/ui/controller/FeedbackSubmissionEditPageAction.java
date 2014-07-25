package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

public abstract class FeedbackSubmissionEditPageAction extends Action {
    protected String courseId;
    protected String feedbackSessionName;
    protected FeedbackSubmissionEditPageData data;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(feedbackSessionName);
        
        if(!isSpecificUserJoinedCourse()){
            return createPleaseJoinCourseResponse(courseId);
        }
        
        
        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);
        if(feedbackSession == null) {
            statusToUser.add("The feedback session has been deleted and is no longer accessible.");
                       
            return createSpecificRedirectResult();
        }
        
        verifyAccesibleForSpecificUser();
        
        String userEmailForCourse = getUserEmailForCourse();
        data = new FeedbackSubmissionEditPageData(account, student);
        data.bundle = getDataBundle(userEmailForCourse);
        
        data.isSessionOpenForSubmission = isSessionOpenForSpecificUser(data.bundle.feedbackSession);
        
        setStatusToAdmin();
        
        if (!data.isSessionOpenForSubmission) {
            statusToUser.add(Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN);
        }
        
        return createSpecificShowPageResult();
    }

    protected abstract boolean isSpecificUserJoinedCourse();
    
    protected abstract void verifyAccesibleForSpecificUser();
    
    protected abstract String getUserEmailForCourse();

    protected abstract FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse) throws EntityDoesNotExistException;

    protected abstract boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session);
    
    protected abstract void setStatusToAdmin();
    
    protected abstract ShowPageResult createSpecificShowPageResult();
    
    protected abstract RedirectResult createSpecificRedirectResult();
}
