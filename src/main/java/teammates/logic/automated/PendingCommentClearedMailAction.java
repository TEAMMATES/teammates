package teammates.logic.automated;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.api.Logic;
import teammates.logic.core.CommentsLogic;
import teammates.logic.core.Emails;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackSessionsLogic;

public class PendingCommentClearedMailAction extends EmailAction {

    private CommentsLogic commentsLogic = CommentsLogic.inst();
    private FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private String courseId;
    
    public PendingCommentClearedMailAction(HttpServletRequest req) {
        super(req);
        initializeNameAndDescription();
        
        courseId = HttpRequestHelper
                .getValueFromRequestParameterMap(req, ParamsNames.EMAIL_COURSE);
        Assumption.assertNotNull(courseId);
    }

    public PendingCommentClearedMailAction(HashMap<String, String> paramMap) {
        super(paramMap);
        initializeNameAndDescription();
        
        courseId = paramMap.get(ParamsNames.EMAIL_COURSE);
        Assumption.assertNotNull(courseId);
    }

    @Override
    protected void doPostProcessingForSuccesfulSend()
            throws InvalidParametersException, EntityDoesNotExistException {
        commentsLogic.clearPendingComments(courseId);
        frcLogic.clearPendingFeedbackResponseComments(courseId);
    }

    @Override
    protected List<MimeMessage> prepareMailToBeSent()
            throws MessagingException, IOException, EntityDoesNotExistException {
        Emails emailManager = new Emails();
        List<MimeMessage> preparedEmails = null;
        
        Set<String> recipients = commentsLogic.getRecipientEmailsForPendingComments(courseId);
        log.info("Fetching recipient emails for pending comments in course : "
                + courseId);
        
        if(recipients != null) {
            preparedEmails = emailManager
                            .generatePendingCommentsClearedEmails(recipients);
        } else {
            log.severe("Recipient emails for pending comments in course : " + courseId +
                       " could not be fetched" );
        }
        return preparedEmails;
    }

    private void initializeNameAndDescription() {
        actionName = "pendingCommentClearedMailAction";
        actionDescription = "clear pending comments";
    }
}
