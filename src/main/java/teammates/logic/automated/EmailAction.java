package teammates.logic.automated;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Utils;
import teammates.logic.core.Emails;

public abstract class EmailAction {

	protected HttpServletRequest req;
	protected List<MimeMessage> emailsToBeSent;

	protected String actionName = "unspecified";
	protected String actionDescription = "unspecified";
	
	private static Logger log = Utils.getLogger();
	
	public EmailAction() {
		req = null;
		emailsToBeSent = null;
	}
	
	public EmailAction(HttpServletRequest request) {
		
		req = request;
		emailsToBeSent = null;
	}
	
	//For testing only
	public EmailAction(HashMap<String, String> paramMap) {
		req = null;
		emailsToBeSent = null;
	}
	
	public void sendEmails() {
		try {
			emailsToBeSent = prepareMailToBeSent();
			
			//actually send the mail
			Emails emailManager = new Emails();
			emailManager.sendEmails(emailsToBeSent);
			doPostProcessingForSuccesfulSend();
			
			//carry this out if mail is successfully sent
			ArrayList<MimeMessage> emailList = new ArrayList<MimeMessage>();
			emailList.addAll(emailsToBeSent);
			logActivitySuccess(req, emailList);
				
		} catch (Exception e) {
			logActivityFailure(req, e);	
			log.severe("Unexpected error " + TeammatesException.toStringWithStackTrace(e));
		}
	}
	
	/*
	 *  Used for testing
	 */
	public List<MimeMessage> getPreparedEmailsAndPerformSuccessOperations() {
		List<MimeMessage> preparedMail = null;
		
		try {
			preparedMail = prepareMailToBeSent();
			doPostProcessingForSuccesfulSend();
		} catch (Exception e) {
			log.severe("Unexpected error " + TeammatesException.toStringWithStackTrace(e));
		}
		return preparedMail;
	}
	
	protected abstract void doPostProcessingForSuccesfulSend() throws InvalidParametersException, EntityDoesNotExistException;
	
	protected abstract List<MimeMessage> prepareMailToBeSent() throws MessagingException, IOException, EntityDoesNotExistException;
	
	protected void logActivitySuccess(HttpServletRequest req, ArrayList<MimeMessage> emails) {
				
		String url = HttpRequestHelper.getRequestedURL(req);
		
		String message;
		
		try {
			ArrayList<Object> data = Emails.extractRecipientsList(emails);
			message = "<span class=\"bold\">Emails sent to:</span><br>";
			for (int i = 0; i < data.size(); i++){
				message += data.get(i).toString() + "<br>";
			}
		} catch (Exception e) {
			message = "<span class=\"color_red\">Unable to retrieve email targets in "
							+ actionName + ": " + actionDescription + ".</span>";
		}
		
		ActivityLogEntry activityLogEntry = new ActivityLogEntry(actionName, actionDescription, null, message, url);
		log.log(Level.INFO, activityLogEntry.generateLogMessage());
	}

	protected void logActivityFailure(HttpServletRequest req, Throwable e) {
				
		String url = HttpRequestHelper.getRequestedURL(req);
	
		String message = "<span class=\"color_red\">Servlet Action failure in "	+ actionName + "<br>";
		message += e.getMessage() + "</span>";
		ActivityLogEntry activityLogEntry = new ActivityLogEntry(actionName, actionDescription, null, message, url);
		log.log(Level.INFO, activityLogEntry.generateLogMessage());
		log.severe(e.getMessage());
	}
}
