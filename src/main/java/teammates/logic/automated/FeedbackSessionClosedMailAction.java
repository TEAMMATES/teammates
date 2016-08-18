package teammates.logic.automated;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.core.EmailGenerator;
import teammates.logic.core.FeedbackSessionsLogic;

public class FeedbackSessionClosedMailAction extends EmailAction {

    private String feedbackSessionName;
    private String courseId;
    
    public FeedbackSessionClosedMailAction(HttpServletRequest request) {
        super(request);
        initializeNameAndDescription();
        
        feedbackSessionName = HttpRequestHelper.getValueFromRequestParameterMap(request, ParamsNames.EMAIL_FEEDBACK);
        Assumption.assertNotNull(feedbackSessionName);
        
        courseId = HttpRequestHelper.getValueFromRequestParameterMap(request, ParamsNames.EMAIL_COURSE);
        Assumption.assertNotNull(courseId);
    }
    
    public FeedbackSessionClosedMailAction(HashMap<String, String> paramMap) {
        super();
        initializeNameAndDescription();
        
        feedbackSessionName = paramMap.get(ParamsNames.EMAIL_FEEDBACK);
        Assumption.assertNotNull(feedbackSessionName);
        
        courseId = paramMap.get(ParamsNames.EMAIL_COURSE);
        Assumption.assertNotNull(courseId);
    }
    
    @Override
    protected void doPostProcessingForSuccesfulSend() throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSessionAttributes feedbackObject = FeedbackSessionsLogic.inst()
                .getFeedbackSession(feedbackSessionName, courseId);
        feedbackObject.setSentClosedEmail(true);
        FeedbackSessionsLogic.inst().updateFeedbackSession(feedbackObject);
    }

    @Override
    protected List<EmailWrapper> prepareMailToBeSent() {
        
        FeedbackSessionAttributes feedbackSession = FeedbackSessionsLogic.inst()
                .getFeedbackSession(feedbackSessionName, courseId);
        
        /*
         * Check if feedback session was deleted between scheduling
         * and the actual sending of emails
         */
        if (feedbackSession == null) {
            log.severe("Feedback session object for feedback session name : " + feedbackSessionName
                       + " for course : " + courseId + " could not be fetched");
            return null;
        }
        
        return new EmailGenerator().generateFeedbackSessionClosedEmails(feedbackSession);

    }
    
    private void initializeNameAndDescription() {
        actionName = Const.AutomatedActionNames.AUTOMATED_FEEDBACKSESSION_CLOSED_MAIL_ACTION;
        actionDescription = "send closed reminders";
    }

    @Override
    protected void doPostProcessingForUnsuccesfulSend() {
        // TODO
    }
}
