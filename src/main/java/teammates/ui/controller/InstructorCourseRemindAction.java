package teammates.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

public class InstructorCourseRemindAction extends Action {
	protected static final Logger log = Utils.getLogger();
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException{
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));
		
		List<MimeMessage> emailsSent = new ArrayList<MimeMessage>();
		try {
			if(studentEmail != null){
				MimeMessage emailSent = logic.sendRegistrationInviteToStudent(courseId, studentEmail);
				emailsSent.add(emailSent);
				statusToUser.add(Const.StatusMessages.COURSE_REMINDER_SENT_TO+studentEmail);
				
			} else {
				emailsSent = logic.sendRegistrationInviteForCourse(courseId);
				statusToUser.add(Const.StatusMessages.COURSE_REMINDERS_SENT);
				
			}
			
			statusToAdmin = generateStatusToAdmin(emailsSent, courseId);
		} catch (InvalidParametersException e) {
			Assumption.fail("InvalidParametersException not expected at this point");
		}
		
		RedirectResult response = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE);
		response.addResponseParam(Const.ParamsNames.COURSE_ID,courseId); 
		return response;

	}
	
	private String generateStatusToAdmin(List<MimeMessage> emailsSent, String courseId) {
		String statusToAdmin = "Registration Key sent to the following students "
				+ "in Course <span class=\"bold\">[" + courseId + "]</span>:<br/>";
		
		Iterator<Entry<String, JoinEmailData>> extractedEmailIterator = 
				extractEmailDataForLogging(emailsSent).entrySet().iterator();
		
		while (extractedEmailIterator.hasNext()) {
			Entry<String, JoinEmailData> extractedEmail = extractedEmailIterator.next();
			
			String studentEmail = extractedEmail.getKey();
			JoinEmailData joinEmailData = extractedEmail.getValue();
			
			statusToAdmin += joinEmailData.studentName + "<span class=\"bold\"> (" + studentEmail + ")"
					+ "</span>.<br/>" + joinEmailData.regKey + "<br/>";
		}
		
		return statusToAdmin;
	}

	private Map<String, JoinEmailData> extractEmailDataForLogging(List<MimeMessage> emails) {
		Map<String, JoinEmailData> logData = new TreeMap<String, JoinEmailData>();
		
		for (MimeMessage email : emails) {
			try {
				String recipient = email.getAllRecipients()[0].toString();
				String studentName = extractStudentName((String) email.getContent());
				String regKey = extractRegistrationKey((String) email.getContent());
				logData.put(recipient, new JoinEmailData(studentName, regKey));
			} catch (MessagingException e) {
				Assumption.fail("Join email corrupted");
			} catch (IOException e) {
				Assumption.fail("Join email corrupted");
			}
		}
		
		return logData;
	}
	
	private String extractStudentName(String emailContent) {
		int startIndex = emailContent.indexOf("Hello ") + "Hello ".length();
		int endIndex = emailContent.indexOf(",");
		return emailContent.substring(startIndex, endIndex);
	}
	
	private String extractRegistrationKey(String emailContent) {
		int startIndex = emailContent.indexOf("regkey=") + "regkey=".length();
		int endIndex = emailContent.indexOf("\">http://");
		return emailContent.substring(startIndex, endIndex);
	}
	
	private class JoinEmailData {
		String studentName;
		String regKey;
		
		public JoinEmailData(String studentName, String regKey) {
			this.studentName = studentName;
			this.regKey = regKey;
		}
	}
}
