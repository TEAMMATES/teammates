package teammates.logic.automated;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.core.Emails;
import teammates.logic.core.FeedbackSessionsLogic;

public class FeedbackSessionPublishedMailAction extends EmailAction {

    private String feedbackSessionName;
    private String courseId;
    
    public FeedbackSessionPublishedMailAction(HttpServletRequest request) {
        super(request);
        initializeNameAndDescription();
        
        feedbackSessionName = HttpRequestHelper
                .getValueFromRequestParameterMap(request, ParamsNames.EMAIL_FEEDBACK);
        Assumption.assertNotNull(feedbackSessionName);
        
        courseId = HttpRequestHelper
                .getValueFromRequestParameterMap(request, ParamsNames.EMAIL_COURSE);
        Assumption.assertNotNull(courseId);
    }
    
    public FeedbackSessionPublishedMailAction(HashMap<String, String> paramMap) {
        super(paramMap);
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
        feedbackObject.sentPublishedEmail = true;
        FeedbackSessionsLogic.inst().updateFeedbackSession(feedbackObject);
    }

    @Override
    protected List<MimeMessage> prepareMailToBeSent() throws MessagingException, IOException, EntityDoesNotExistException {
        Emails emailManager = new Emails();
        List<MimeMessage> preparedEmails = null;
        
        FeedbackSessionAttributes feedbackObject = FeedbackSessionsLogic.inst()
                .getFeedbackSession(feedbackSessionName, courseId);
        log.info("Fetching feedback session object for feedback session name : "
                + feedbackSessionName + " and course : " + courseId);
        
        if(feedbackObject != null) {
             /*
              * Check if feedback session was deleted between scheduling
              * and the actual sending of emails
              */
            preparedEmails = emailManager
                            .generateFeedbackSessionPublishedEmails(feedbackObject);
        } else {
            log.severe("Feedback session object for feedback session name : " + feedbackSessionName +
                       " for course : " + courseId +" could not be fetched" );
        }
        return preparedEmails;
        
    }
    
    private void initializeNameAndDescription() {
        actionName = "feedbackSessionPublishedMailAction";
        actionDescription = "send published alert";
    }
}
