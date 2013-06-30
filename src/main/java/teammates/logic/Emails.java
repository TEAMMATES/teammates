package teammates.logic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import teammates.common.BuildProperties;
import teammates.common.Common;
import teammates.common.StringHelper;
import teammates.common.TimeHelper;
import teammates.common.Url;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;

/**
 * Handles operations related to sending e-mails.
 */
public class Emails {
	//TODO: methods in this class throw too many exceptions. Reduce using a wrapper exception?
	private static Logger log = Common.getLogger();

	public static final String SUBJECT_PREFIX_STUDENT_EVALUATION_OPENING = "TEAMMATES: Peer evaluation now open";
	public static final String SUBJECT_PREFIX_STUDENT_EVALUATION_REMINDER = "TEAMMATES: Peer evaluation reminder";
	public static final String SUBJECT_PREFIX_STUDENT_EVALUATION_CLOSING = "TEAMMATES: Peer evaluation closing soon";
	public static final String SUBJECT_PREFIX_STUDENT_EVALUATION_PUBLISHED = "TEAMMATES: Peer evaluation published";
	public static final String SUBJECT_PREFIX_FEEDBACK_SESSION_OPENING = "TEAMMATES: Feedback session now open";
	public static final String SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER = "TEAMMATES: Feedback session reminder";
	public static final String SUBJECT_PREFIX_FEEDBACK_SESSION_CLOSING = "TEAMMATES: Feedback session closing soon";
	public static final String SUBJECT_PREFIX_FEEDBACK_SESSION_PUBLISHED = "TEAMMATES: Feedback session results published";
	public static final String SUBJECT_PREFIX_STUDENT_COURSE_JOIN = "TEAMMATES: Invitation to join course";
	public static final String SUBJECT_PREFIX_ADMIN_SYSTEM_ERROR = "TEAMMATES (%s): New System Exception: %s";

	private String senderEmail;
	private String senderName;
	private String replyTo;

	public Emails() {
		senderEmail = "noreply@" + Common.APP_ID + ".appspotmail.com";
		senderName = "TEAMMATES Admin (noreply)";
		replyTo = "teammates@comp.nus.edu.sg";
	}

	public static String getEmailInfo(MimeMessage message)
			throws MessagingException {
		StringBuffer messageInfo = new StringBuffer();
		messageInfo.append("[Email sent]");
		messageInfo
				.append("to="
						+ message.getRecipients(Message.RecipientType.TO)[0]
								.toString());
		messageInfo.append("|from=" + message.getFrom()[0].toString());
		messageInfo.append("|subject=" + message.getSubject());
		return messageInfo.toString();
	}

	public List<MimeMessage> generateEvaluationOpeningEmails(
			CourseAttributes course,
			EvaluationAttributes evaluation, 
			List<StudentAttributes> students)
					throws MessagingException, IOException {

		String template = Common.STUDENT_EMAIL_TEMPLATE_EVALUATION_;
		List<MimeMessage> emails = generateEvaluationEmailBases(course,
				evaluation, students, template);
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace("${subjectPrefix}",
					SUBJECT_PREFIX_STUDENT_EVALUATION_OPENING));
			email.setContent(
					email.getContent().toString()
							.replace("${status}", "is now open"), "text/html");
		}
		return emails;
	}

	public List<MimeMessage> generateEvaluationReminderEmails(
			CourseAttributes course, 
			EvaluationAttributes evaluation,
			List<StudentAttributes> students) 
					throws MessagingException, IOException {

		String template = Common.STUDENT_EMAIL_TEMPLATE_EVALUATION_;
		List<MimeMessage> emails = generateEvaluationEmailBases(course,
				evaluation, students, template);
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace("${subjectPrefix}",
					SUBJECT_PREFIX_STUDENT_EVALUATION_REMINDER));
			email.setContent(
					email.getContent()
							.toString()
							.replace("${status}",
									"is still open for submissions"),
					"text/html");
		}
		return emails;
	}

	public List<MimeMessage> generateEvaluationClosingEmails(
			CourseAttributes c,
			EvaluationAttributes e, 
			List<StudentAttributes> students)
					throws MessagingException, IOException {

		String template = Common.STUDENT_EMAIL_TEMPLATE_EVALUATION_;
		List<MimeMessage> emails = generateEvaluationEmailBases(c, e, students,
				template);
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace("${subjectPrefix}",
					SUBJECT_PREFIX_STUDENT_EVALUATION_CLOSING));
			email.setContent(
					email.getContent().toString()
							.replace("${status}", "is closing soon"),
					"text/html");
		}
		return emails;
	}

	public List<MimeMessage> generateEvaluationPublishedEmails(
			CourseAttributes c,
			EvaluationAttributes e, 
			List<StudentAttributes> students)
					throws MessagingException, IOException {

		String template = Common.STUDENT_EMAIL_TEMPLATE_EVALUATION_PUBLISHED;
		List<MimeMessage> emails = generateEvaluationEmailBases(c, e, students,
				template);
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace("${subjectPrefix}",
					SUBJECT_PREFIX_STUDENT_EVALUATION_PUBLISHED));
		}
		return emails;
	}

	public List<MimeMessage> generateEvaluationEmailBases(
			CourseAttributes course,
			EvaluationAttributes evaluation, 
			List<StudentAttributes> students,
			String template) 
					throws MessagingException, UnsupportedEncodingException {
		
		ArrayList<MimeMessage> emails = new ArrayList<MimeMessage>();
		for (StudentAttributes s : students) {

			emails.add(generateEvaluationEmailBase(course, evaluation, s,
					template));
		}
		return emails;
	}

	public MimeMessage generateEvaluationEmailBase(
			CourseAttributes c,
			EvaluationAttributes e, 
			StudentAttributes s, 
			String template)
					throws MessagingException, UnsupportedEncodingException {

		MimeMessage message = getEmptyEmailAddressedToStudent(s);

		message.setSubject(String
				.format("${subjectPrefix} [Course: %s][Evaluation: %s]",
						c.name, e.name));

		String emailBody = template;

		if (isYetToJoinCourse(s)) {
			emailBody = fillUpJoinFragment(s, emailBody);
		} else {
			emailBody = emailBody.replace("${joinFragment}", "");
		}

		emailBody = emailBody.replace("${studentName}", s.name);
		emailBody = emailBody.replace("${courseName}", c.name);
		emailBody = emailBody.replace("${courseId}", c.id);
		emailBody = emailBody.replace("${evaluationName}", e.name);
		emailBody = emailBody.replace("${deadline}",
				TimeHelper.formatTime(e.endTime));

		String submitUrl = Common.TEAMMATES_APP_URL
				+ Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		submitUrl = Url.addParamToUrl(submitUrl, Common.PARAM_COURSE_ID,
				c.id);
		submitUrl = Url.addParamToUrl(submitUrl,
				Common.PARAM_EVALUATION_NAME, e.name);
		emailBody = emailBody.replace("${submitUrl}", submitUrl);

		String reportUrl = Common.TEAMMATES_APP_URL
				+ Common.PAGE_STUDENT_EVAL_RESULTS;
		reportUrl = Url.addParamToUrl(reportUrl, Common.PARAM_COURSE_ID,
				c.id);
		reportUrl = Url.addParamToUrl(reportUrl,
				Common.PARAM_EVALUATION_NAME, e.name);
		emailBody = emailBody.replace("${reportUrl}", reportUrl);

		message.setContent(emailBody, "text/html");

		return message;
	}

	public List<MimeMessage> generateFeedbackSessionOpeningEmails(
			CourseAttributes course, FeedbackSessionAttributes session,
			List<StudentAttributes> students, List<InstructorAttributes> instructors) 
					throws MessagingException, IOException {
		
		String template = Common.USER_EMAIL_TEMPLATE_FEEDBACK_SESSION;
		List<MimeMessage> emails = generateFeedbackSessionEmailBases(course,
				session, students, instructors, template);
		
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace("${subjectPrefix}",
					SUBJECT_PREFIX_FEEDBACK_SESSION_OPENING));
			email.setContent(
					email.getContent().toString()
							.replace("${status}", "is now open"), "text/html");
		}
		
		return emails;
	}
	
	public List<MimeMessage> generateFeedbackSessionClosingEmails(
			CourseAttributes course,
			FeedbackSessionAttributes session, 
			List<StudentAttributes> students,
			List<InstructorAttributes> instructors)
					throws MessagingException, IOException {

		String template = Common.USER_EMAIL_TEMPLATE_FEEDBACK_SESSION;
		List<MimeMessage> emails = generateFeedbackSessionEmailBases(
				course, session, students, instructors, template);
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace("${subjectPrefix}",
					SUBJECT_PREFIX_FEEDBACK_SESSION_CLOSING));
			email.setContent(
					email.getContent().toString()
							.replace("${status}", "is closing soon"),
					"text/html");
		}
		return emails;
	}
	
	public List<MimeMessage> generateFeedbackSessionPublishedEmails(
			CourseAttributes course,
			FeedbackSessionAttributes session,
			List<StudentAttributes> students,
			List<InstructorAttributes> instructors) 
					throws MessagingException, IOException {
		
		String template = Common.USER_EMAIL_TEMPLATE_FEEDBACK_SESSION_PUBLISHED;
		List<MimeMessage> emails = generateFeedbackSessionEmailBases(course,
				session, students, instructors, template);
		
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace("${subjectPrefix}",
					SUBJECT_PREFIX_FEEDBACK_SESSION_PUBLISHED));
		}
		
		return emails;
	}
	
	public List<MimeMessage> generateFeedbackSessionEmailBases(
			CourseAttributes course,
			FeedbackSessionAttributes session, 
			List<StudentAttributes> students,
			List<InstructorAttributes> instructors,
			String template) 
					throws MessagingException, UnsupportedEncodingException {
		
		ArrayList<MimeMessage> emails = new ArrayList<MimeMessage>();
		for (StudentAttributes s : students) {
			emails.add(generateFeedbackSessionEmailBaseForStudents(course, session, s,
					template));
		}
		for (InstructorAttributes i : instructors) {
			emails.add(generateFeedbackSessionEmailBaseForInstructors(course, session, i,
					template));
		}
		return emails;
	}

	public MimeMessage generateFeedbackSessionEmailBaseForStudents(
			CourseAttributes c,
			FeedbackSessionAttributes fs, 
			StudentAttributes s,
			String template)
					throws MessagingException, UnsupportedEncodingException {

		MimeMessage message = getEmptyEmailAddressedToEmail(s.email);

		message.setSubject(String
				.format("${subjectPrefix} [Course: %s][Feedback Session: %s]",
						c.name, fs.feedbackSessionName));

		String emailBody = template;

		if (isYetToJoinCourse(s)) {
			emailBody = fillUpJoinFragment(s, emailBody);
		} else {
			emailBody = emailBody.replace("${joinFragment}", "");
		}
		
		emailBody = emailBody.replace("${userName}", s.name);
		emailBody = emailBody.replace("${courseName}", c.name);
		emailBody = emailBody.replace("${courseId}", c.id);
		emailBody = emailBody.replace("${feedbackSessionName}", fs.feedbackSessionName);
		emailBody = emailBody.replace("${deadline}",
				TimeHelper.formatTime(fs.endTime));
		emailBody = emailBody.replace("${instructorFragment}", "");
		
		String submitUrl = Common.TEAMMATES_APP_URL
				+ Common.PAGE_STUDENT_FEEDBACK_SUBMIT;
		submitUrl = Url.addParamToUrl(submitUrl, Common.PARAM_COURSE_ID,
				c.id);
		submitUrl = Url.addParamToUrl(submitUrl,
				Common.PARAM_FEEDBACK_SESSION_NAME, fs.feedbackSessionName);
		emailBody = emailBody.replace("${submitUrl}", submitUrl);

		String reportUrl = Common.TEAMMATES_APP_URL
				+ Common.PAGE_STUDENT_FEEDBACK_RESULTS;
		reportUrl = Url.addParamToUrl(reportUrl, Common.PARAM_COURSE_ID,
				c.id);
		reportUrl = Url.addParamToUrl(reportUrl,
				Common.PARAM_FEEDBACK_SESSION_NAME, fs.feedbackSessionName);
		emailBody = emailBody.replace("${reportUrl}", reportUrl);

		message.setContent(emailBody, "text/html");

		return message;
	}
	
	public MimeMessage generateFeedbackSessionEmailBaseForInstructors(
			CourseAttributes c,
			FeedbackSessionAttributes fs, 
			InstructorAttributes i,
			String template)
					throws MessagingException, UnsupportedEncodingException {

		MimeMessage message = getEmptyEmailAddressedToEmail(i.email);

		message.setSubject(String
				.format("${subjectPrefix} [Course: %s][Feedback Session: %s]",
						c.name, fs.feedbackSessionName));

		String emailBody = template;

		emailBody = emailBody.replace("${joinFragment}", "");
		emailBody = emailBody.replace("${userName}", i.name);
		emailBody = emailBody.replace("${courseName}", c.name);
		emailBody = emailBody.replace("${courseId}", c.id);
		emailBody = emailBody.replace("${feedbackSessionName}", fs.feedbackSessionName);
		emailBody = emailBody.replace("${deadline}",
				TimeHelper.formatTime(fs.endTime));
		emailBody = emailBody.replace("${instructorFragment}", "The email below has also been sent to students of course: "+c.id+".<br/>");
		
		String submitUrl = Common.TEAMMATES_APP_URL
				+ Common.PAGE_STUDENT_FEEDBACK_SUBMIT;
		submitUrl = Url.addParamToUrl(submitUrl, Common.PARAM_COURSE_ID,
				c.id);
		submitUrl = Url.addParamToUrl(submitUrl,
				Common.PARAM_FEEDBACK_SESSION_NAME, fs.feedbackSessionName);
		emailBody = emailBody.replace("${submitUrl}", submitUrl);

		String reportUrl = Common.TEAMMATES_APP_URL
				+ Common.PAGE_INSTRUCTOR_FEEDBACK_RESULTS;
		reportUrl = Url.addParamToUrl(reportUrl, Common.PARAM_COURSE_ID,
				c.id);
		reportUrl = Url.addParamToUrl(reportUrl,
				Common.PARAM_FEEDBACK_SESSION_NAME, fs.feedbackSessionName);
		emailBody = emailBody.replace("${reportUrl}", reportUrl);

		message.setContent(emailBody, "text/html");

		return message;
	}
	
	public MimeMessage generateStudentCourseJoinEmail(
			CourseAttributes c,	StudentAttributes s) 
					throws AddressException, MessagingException, UnsupportedEncodingException {

		MimeMessage message = getEmptyEmailAddressedToStudent(s);
		message.setSubject(String.format(SUBJECT_PREFIX_STUDENT_COURSE_JOIN
				+ " [%s][Course ID: %s]", c.name, c.id));

		String emailBody = Common.STUDENT_EMAIL_TEMPLATE_COURSE_JOIN;
		emailBody = fillUpJoinFragment(s, emailBody);
		emailBody = emailBody.replace("${studentName}", s.name);
		emailBody = emailBody.replace("${courseName}", c.name);

		message.setContent(emailBody, "text/html");
		return message;
	}

	
	public MimeMessage generateSystemErrorEmail(
			Throwable error,
			String requestPath, 
			String requestParam, 
			String version)
			throws AddressException, MessagingException, UnsupportedEncodingException {
		
		//TODO: remove version parameter?
		
		Session session = Session.getDefaultInstance(new Properties(), null);
		MimeMessage message = new MimeMessage(session);
		String errorMessage = error.getMessage();
		String stackTrace = Common.stackTraceToString(error);
	
		// if the error doesn't contain a short description,
		// retrieve the first line of stack trace.
		// truncate stack trace at first "at" string
		if (errorMessage == null) {
			int msgTruncateIndex = stackTrace.indexOf("at");
			if (msgTruncateIndex > 0) {
				errorMessage = stackTrace.substring(0, msgTruncateIndex);
			} else {
				errorMessage = "";
			}
		}
		String recipient = BuildProperties.inst().getAppCrashReportEmail();
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				recipient));
		message.setFrom(new InternetAddress(senderEmail, senderName));
		message.setSubject(String.format(SUBJECT_PREFIX_ADMIN_SYSTEM_ERROR,
				version, errorMessage));
	
		String emailBody = Common.SYSTEM_ERROR_EMAIL_TEMPLATE;
	
		emailBody = emailBody.replace("${requestPath}", requestPath);
		emailBody = emailBody.replace("${requestParameters}", requestParam);
		emailBody = emailBody.replace("${errorMessage}", errorMessage);
		emailBody = emailBody.replace("${stackTrace}", stackTrace);
		message.setContent(emailBody, "text/html");
	
		return message;
	}

	public void sendEmails(List<MimeMessage> messages) throws MessagingException {
		for (MimeMessage m : messages) {
			sendEmail(m);
		}
	}

	public void sendEmail(MimeMessage message) throws MessagingException {
		log.info(getEmailInfo(message));
		Transport.send(message);
	}

	public MimeMessage sendErrorReport(String path, String params, Throwable error) {
		MimeMessage email = null;
		try {
			email = generateSystemErrorEmail(error, path, params,
					BuildProperties.getAppVersion());
			sendEmail(email);
			log.severe("Sent crash report: " + Emails.getEmailInfo(email));
		} catch (Exception e) {
			log.severe("Error in sending crash report: "
					+ (email == null ? "" : email.toString()));
		}
	
		return email;
	}

	private String fillUpJoinFragment(StudentAttributes s, String emailBody) {
		emailBody = emailBody.replace("${joinFragment}",
				Common.STUDENT_EMAIL_FRAGMENT_COURSE_JOIN);

		String key;
		key = StringHelper.encrypt(s.key);
		emailBody = emailBody.replace("${key}", key);

		String joinUrl = Common.TEAMMATES_APP_URL
				+ Common.PAGE_STUDENT_JOIN_COURSE;
		joinUrl = Url.addParamToUrl(joinUrl, Common.PARAM_REGKEY, key);

		emailBody = emailBody.replace("${joinUrl}", joinUrl);
		return emailBody;
	}

	private MimeMessage getEmptyEmailAddressedToStudent(StudentAttributes s)
			throws MessagingException, AddressException,
			UnsupportedEncodingException {
		Session session = Session.getDefaultInstance(new Properties(), null);
		MimeMessage message = new MimeMessage(session);

		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				s.email));
		message.setFrom(new InternetAddress(senderEmail, senderName));
		message.setReplyTo(new Address[] { new InternetAddress(replyTo) });
		return message;
	}

	private MimeMessage getEmptyEmailAddressedToEmail(String email)
			throws MessagingException, AddressException,
			UnsupportedEncodingException {
		Session session = Session.getDefaultInstance(new Properties(), null);
		MimeMessage message = new MimeMessage(session);

		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				email));
		message.setFrom(new InternetAddress(senderEmail, senderName));
		message.setReplyTo(new Address[] { new InternetAddress(replyTo) });
		return message;
	}
	
	private boolean isYetToJoinCourse(StudentAttributes s) {
		return s.googleId == null || s.googleId.isEmpty();
	}
}