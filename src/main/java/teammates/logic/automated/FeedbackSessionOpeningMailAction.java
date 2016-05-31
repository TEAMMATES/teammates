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
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.core.Emails;
import teammates.logic.core.FeedbackSessionsLogic;

public class FeedbackSessionOpeningMailAction extends EmailAction {

    private String feedbackSessionName;
    private String courseId;
    
    public FeedbackSessionOpeningMailAction(HttpServletRequest request) {
        super(request);
        initializeNameAndDescription();
        
        feedbackSessionName = HttpRequestHelper
                .getValueFromRequestParameterMap(request, ParamsNames.EMAIL_FEEDBACK);
        Assumption.assertNotNull(feedbackSessionName);
        
        courseId = HttpRequestHelper
                .getValueFromRequestParameterMap(request, ParamsNames.EMAIL_COURSE);
        Assumption.assertNotNull(courseId);
    }
    
    public FeedbackSessionOpeningMailAction(HashMap<String, String> paramMap) {
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
        feedbackObject.sentOpenEmail = true;
        FeedbackSessionsLogic.inst().updateFeedbackSession(feedbackObject);
    }

    @Override
    protected List<MimeMessage> prepareMailToBeSent() throws MessagingException, IOException {
        
        FeedbackSessionAttributes feedbackObject = FeedbackSessionsLogic.inst()
                .getFeedbackSession(feedbackSessionName, courseId);
        log.info("Fetching feedback session object for feedback session name : "
                + feedbackSessionName + " and course : " + courseId);
        
        if (feedbackObject == null) {
            log.severe("Feedback session object for feedback session name : " + feedbackSessionName
                       + " for course : " + courseId + " could not be fetched");
            return null;
        }
        /*
         * Check if feedback session was deleted between scheduling
         * and the actual sending of emails
         */
        return new Emails().generateFeedbackSessionOpeningEmails(feedbackObject);
        
    }
    
    private void initializeNameAndDescription() {
        actionName = Const.AutomatedActionNames.AUTOMATED_FEEDBACKSESSION_OPENING_MAIL_ACTION;
        actionDescription = "send opening reminders";
    }

    @Override
    protected void doPostProcessingForUnsuccesfulSend() {
        // TODO implement this
    }
}
