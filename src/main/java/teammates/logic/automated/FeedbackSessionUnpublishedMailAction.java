package teammates.logic.automated;

import java.util.List;
import java.util.Map;

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

public class FeedbackSessionUnpublishedMailAction extends EmailAction {

    private String feedbackSessionName;
    private String courseId;
    
    public FeedbackSessionUnpublishedMailAction(HttpServletRequest request) {
        super(request);
        initializeNameAndDescription();
        
        feedbackSessionName = HttpRequestHelper
                .getValueFromRequestParameterMap(request, ParamsNames.EMAIL_FEEDBACK);
        Assumption.assertNotNull(feedbackSessionName);
        
        courseId = HttpRequestHelper
                .getValueFromRequestParameterMap(request, ParamsNames.EMAIL_COURSE);
        Assumption.assertNotNull(courseId);
    }
    
    public FeedbackSessionUnpublishedMailAction(Map<String, String> paramMap) {
        super();
        initializeNameAndDescription();
        
        feedbackSessionName = paramMap.get(ParamsNames.EMAIL_FEEDBACK);
        Assumption.assertNotNull(feedbackSessionName);
        
        courseId = paramMap.get(ParamsNames.EMAIL_COURSE);
        Assumption.assertNotNull(courseId);
    }
    
    @Override
    protected void doPostProcessingForSuccesfulSend() throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSessionAttributes feedbackSession = FeedbackSessionsLogic.inst()
                .getFeedbackSession(feedbackSessionName, courseId);
        feedbackSession.setSentPublishedEmail(false);
        FeedbackSessionsLogic.inst().updateFeedbackSession(feedbackSession);
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
                       + " for course : " + courseId + " could not be fetched while sending unpublished emails");
            return null;
        }
        return new EmailGenerator().generateFeedbackSessionUnpublishedEmails(feedbackSession);
    }
    
    private void initializeNameAndDescription() {
        actionName = Const.AutomatedActionNames.AUTOMATED_FEEDBACKSESSION_UNPUBLISHED_MAIL_ACTION;
        actionDescription = "send unpublished alert";
    }

    @Override
    protected void doPostProcessingForUnsuccesfulSend() {
        // TODO fix this
    }
}
