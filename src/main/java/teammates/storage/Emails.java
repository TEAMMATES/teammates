package teammates.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import teammates.BuildProperties;
import teammates.api.Common;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.StudentData;
import teammates.ui.Helper;

/**
 * Email handles all operations with regards to sending e-mails.
 */
public class Emails {
	private String from;
	private Properties props;
	private static Logger log = Common.getLogger();

	public static final String SUBJECT_PREFIX_STUDENT_EVALUATION_OPENING = "TEAMMATES: Peer evaluation now open";
	public static final String SUBJECT_PREFIX_STUDENT_EVALUATION_REMINDER = "TEAMMATES: Peer evaluation reminder";
	public static final String SUBJECT_PREFIX_STUDENT_EVALUATION_CLOSING = "TEAMMATES: Peer evaluation closing soon";
	public static final String SUBJECT_PREFIX_STUDENT_EVALUATION_PUBLISHED = "TEAMMATES: Peer evaluation published";
	public static final String SUBJECT_PREFIX_STUDENT_COURSE_JOIN = "TEAMMATES: Invitation to join course";


	public Emails() {
		from = BuildProperties.inst().TEAMMATES_APP_ADMIN_EMAIL;
		props = new Properties();
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

	@Deprecated
	public void sendEmail() throws MessagingException {
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);

		String to = "damith@gmail.com";
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

		message.setFrom(new InternetAddress(from));
		String subject = "Teammates Testing";
		message.setSubject(subject);
		message.setText("This is a testing email");

		log.fine("Sending email to " + to + "[" + subject + "]");

		sendEmail(message);
	}

	public List<MimeMessage> generateEvaluationOpeningEmails(
			CourseData course, EvaluationData evaluation,
			List<StudentData> students) throws MessagingException, IOException {
		
		String template = BuildProperties.inst().STUDENT_EMAIL_TEMPLATE_EVALUATION_;
		List<MimeMessage> emails = generateEvaluationEmailBases(course,
				evaluation, students, template);
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace(
					"${subjectPrefix}",
					SUBJECT_PREFIX_STUDENT_EVALUATION_OPENING));
			email.setContent(
					email.getContent()
							.toString()
							.replace("${status}",
									"is now open"), "text/html");
		}
		return emails;
	}

	public List<MimeMessage> generateEvaluationReminderEmails(
			CourseData course, EvaluationData evaluation,
			List<StudentData> students) throws MessagingException, IOException {
		
		String template = BuildProperties.inst().STUDENT_EMAIL_TEMPLATE_EVALUATION_;
		List<MimeMessage> emails = generateEvaluationEmailBases(course,
				evaluation, students, template);
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace(
					"${subjectPrefix}",
					SUBJECT_PREFIX_STUDENT_EVALUATION_REMINDER));
			email.setContent(
					email.getContent()
							.toString()
							.replace("${status}",
									"is still open for submissions"), "text/html");
		}
		return emails;
	}

	public List<MimeMessage> generateEvaluationClosingEmails(CourseData c,
			EvaluationData e, List<StudentData> students)
			throws MessagingException, IOException {
		
		String template = BuildProperties.inst().STUDENT_EMAIL_TEMPLATE_EVALUATION_;
		List<MimeMessage> emails = generateEvaluationEmailBases(c, e,
				students, template);
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace(
					"${subjectPrefix}",
					SUBJECT_PREFIX_STUDENT_EVALUATION_CLOSING));
			email.setContent(
					email.getContent()
							.toString()
							.replace("${status}",
									"is closing soon"), "text/html");
		}
		return emails;
	}
	
	public List<MimeMessage> generateEvaluationPublishedEmails(CourseData c,
			EvaluationData e, List<StudentData> students)
			throws MessagingException, IOException {
		
		String template = BuildProperties.inst().STUDENT_EMAIL_TEMPLATE_EVALUATION_PUBLISHED;
		List<MimeMessage> emails = generateEvaluationEmailBases(c, e,
				students, template);
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace(
					"${subjectPrefix}",
					SUBJECT_PREFIX_STUDENT_EVALUATION_PUBLISHED));
		}
		return emails;
	}


	public List<MimeMessage> generateEvaluationEmailBases(CourseData course,
			EvaluationData evaluation, List<StudentData> students, String template)
			throws MessagingException {
		ArrayList<MimeMessage> emails = new ArrayList<MimeMessage>();
		for (StudentData s : students) {
			
			emails.add(generateEvaluationEmailBase(course, evaluation, s, template));
		}
		return emails;
	}

	public MimeMessage generateEvaluationEmailBase(CourseData c,
			EvaluationData e, StudentData s, String template) throws MessagingException {

		MimeMessage message = getEmptyEmailAddressedToStudent(s);

		message.setSubject(String.format("${subjectPrefix} [Course: %s][Evaluation: %s]", c.name, e.name));

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
				Common.formatTime(e.endTime));

		String submitUrl = BuildProperties.inst().TEAMMATES_APP_URL
				+ Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		submitUrl = Helper.addParam(submitUrl, Common.PARAM_COURSE_ID, c.id);
		submitUrl = Helper.addParam(submitUrl, Common.PARAM_EVALUATION_NAME,
				e.name);
		emailBody = emailBody.replace("${submitUrl}", submitUrl);
		
		String reportUrl = BuildProperties.inst().TEAMMATES_APP_URL
				+ Common.PAGE_STUDENT_EVAL_RESULTS;
		reportUrl = Helper.addParam(reportUrl, Common.PARAM_COURSE_ID, c.id);
		reportUrl = Helper.addParam(reportUrl, Common.PARAM_EVALUATION_NAME,
				e.name);
		emailBody = emailBody.replace("${reportUrl}", reportUrl);
		
		message.setContent(emailBody, "text/html");

		return message;
	}


	public MimeMessage generateStudentCourseJoinEmail(CourseData c,
			StudentData s) throws AddressException, MessagingException {
		
		MimeMessage message = getEmptyEmailAddressedToStudent(s);
		message.setSubject(String.format(SUBJECT_PREFIX_STUDENT_COURSE_JOIN+" [%s][Course ID: %s]", c.name, c.id));
		
		String emailBody = BuildProperties.inst().STUDENT_EMAIL_TEMPLATE_COURSE_JOIN;
		emailBody = fillUpJoinFragment(s, emailBody);
		emailBody = emailBody.replace("${studentName}", s.name);
		emailBody = emailBody.replace("${courseName}", c.name);
		
		message.setContent(emailBody, "text/html");
		return message;
	}


	public void sendEmails(List<MimeMessage> messages)
			throws MessagingException {
		for (MimeMessage m : messages) {
			sendEmail(m);
		}
	}

	public void sendEmail(MimeMessage message) throws MessagingException {
		log.info(getEmailInfo(message));
		Transport.send(message);
	}

	private String fillUpJoinFragment(StudentData s, String emailBody) {
		emailBody = emailBody.replace("${joinFragment}",
				BuildProperties.inst().STUDENT_EMAIL_FRAGMENT_COURSE_JOIN);
	
		emailBody = emailBody.replace("${key}", s.key);
	
		String joinUrl = BuildProperties.inst().TEAMMATES_APP_URL
				+ Common.PAGE_STUDENT_JOIN_COURSE;
		joinUrl = Helper.addParam(joinUrl, Common.PARAM_REGKEY, s.key);
	
		emailBody = emailBody.replace("${joinUrl}", joinUrl);
		return emailBody;
	}


	private MimeMessage getEmptyEmailAddressedToStudent(StudentData s)
			throws MessagingException, AddressException {
		Session session = Session.getDefaultInstance(new Properties(), null);
		MimeMessage message = new MimeMessage(session);
	
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				s.email));
	
		message.setFrom(new InternetAddress(from));
		return message;
	}


	private boolean isYetToJoinCourse(StudentData s) {
		return s.id == null || s.id.isEmpty();
	}
}