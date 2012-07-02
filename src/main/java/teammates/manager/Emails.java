package teammates.manager;

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

import teammates.Config;
import teammates.api.Common;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.StudentData;
import teammates.jsp.Helper;

/**
 * Email handles all operations with regards to sending e-mails.
 */
public class Emails {
	private String from;
	private Properties props;
	private static Logger log = Common.getLogger();

	private final String HEADER_REGISTRATION_INVITATION = "TEAMMATES: Registration Invitation: Register in the course %s";
	public static final String SUBJECT_PREFIX_STUDENT_EVALUATION_OPENING = "TEAMMATES: Peer evaluation now open";
	public static final String SUBJECT_PREFIX_STUDENT_EVALUATION_REMINDER = "TEAMMATES: Peer evaluation reminder";
	public static final String SUBJECT_PREFIX_STUDENT_EVALUATION_CLOSING = "TEAMMATES: Peer evaluation closing soon";
	public static final String SUBJECT_PREFIX_STUDENT_EVALUATION_PUBLISHED = "TEAMMATES: Peer evaluation published";
	public static final String SUBJECT_PREFIX_STUDENT_COURSE_JOIN = "TEAMMATES: invitation to join course";
	private final String HEADER_EVALUATION_REMINDER = "TEAMMATES: Evaluation Reminder: %s %s";
	private final String TEAMMATES_APP_SIGNATURE = "\n\nIf you encounter any problems using the system, email TEAMMATES support team at teammates@comp.nus.edu.sg"
			+ "\n\nRegards, \nTEAMMATES System";


	public Emails() {
		from = Config.inst().TEAMMATES_APP_ACCOUNT;
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


	/**
	 * Sends an email reminding the Student of the Evaluation deadline.
	 * 
	 * @param email
	 *            the email of the student (Precondition: Must not be null)
	 * 
	 * @param studentName
	 *            the name of the student (Precondition: Must not be null)
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Precondition: Must not be null)
	 * 
	 * @param deadline
	 *            the evaluation deadline (Precondition: Must not be null)
	 */
	public void remindStudent(String email, String studentName,
			String courseID, String evaluationName, String deadline) {
		try {
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(session);

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email));

			message.setFrom(new InternetAddress(from));
			message.setSubject(String.format(HEADER_EVALUATION_REMINDER,
					courseID, evaluationName));
			message.setText("Dear " + studentName + ",\n\n"
					+ "You are reminded to submit the evaluation: \n\n"
					+ courseID + " " + evaluationName + "\n\n" + "by "
					+ deadline + "H.\n"
					+ "You can access the evaluation here: "
					+ Config.inst().TEAMMATES_APP_URL + TEAMMATES_APP_SIGNATURE);

			sendEmail(message);

		}

		catch (MessagingException e) {
			log.severe("remindStudent: fail to send message");
		}
	}


	/**
	 * Sends a registration key to an e-mail address.
	 * 
	 * Pre-conditions: email, registrationKey, studentName, courseID, courseName
	 * and coordinatorName must not be null. Post-condition: The specified
	 * registrationKey is sent to the specified email.
	 * 
	 * Subject line: [Coordinator name] sent you an invitation to register in
	 * Teammates System.
	 * 
	 * Dear [Name], The course [course name] will be using Teammates
	 * Peer-Evaluation System for peer-evaluations. [Coordinator name] has
	 * invited you to use the system to evaluate your team members. These are
	 * the steps to follow. Login to the system: Go to URL {provide the correct
	 * url here} Login as a ‘Student’ using your Google ID. If you do not
	 * have a Google ID, please create one. Join the course: Enter this key :
	 * Key
	 * 
	 * 
	 * Now, [course] should appear in the course list and the names of your
	 * teammates will appear when you click the ‘view’ link corresponding to
	 * the course. Submit pending evaluations: Click ‘Evaluations’ button at
	 * the top to check if there are any pending peer-evaluations you have to
	 * submit.
	 * 
	 * Please inform [coordinator email] if your encounter any problems or if
	 * your team details are not correct.
	 * 
	 * @param email
	 * @param registrationKey
	 */
	public void sendRegistrationKey(String email, String registrationKey,
			String studentName, String courseID, String courseName,
			String coordinatorName, String coordinatorEmail) {
		try {
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(session);

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email));

			message.setFrom(new InternetAddress(from));
			message.setSubject(String.format(HEADER_REGISTRATION_INVITATION,
					courseID));
			message.setText("Dear "
					+ studentName
					+ ",\n\n"
					+ "The course "
					+ courseName
					+ " will be using Teammates Teammates System."
					+ " The system will help you to form teams (if not yet formed) and submit peer-evaluations to your teammates. To use the system, follow these steps:\n\n"
					+ "Login to the system:\n"
					+ "* Go to URL "
					+ Config.inst().TEAMMATES_APP_URL
					+ "\n"
					+ "* Login as \"Student\" using your Google ID. If you do not have a Google ID, please create one.\n\n"
					+ "Join the course: \n"
					+ "* Enter this key to join "
					+ courseID
					+ ": "
					+ registrationKey
					+ "\n"
					+ "* Now, "
					+ courseID
					+ " should appear in the course list\n\n"
					+ "Forming Teams\n"
					+ "If the course requires you to form teams using TEAMMATES system, go the the \"View Teams\" link for the corresponding course."
					+ "You can then create your profile and add students to your team/join a team/leave a team.\n\n"
					+ "Submitting peer evaluations\n"
					+ "If the course requires you to submit peer evaluations, click \"Evaluations\" tab at the top "
					+ "to check if there are any pending peer-evaluations.\n\n"
					+ "In case of problems:\n"
					+ "If any of your details in the system are incorrect, please contact the coordinator of "
					+ courseID + ".\n" + TEAMMATES_APP_SIGNATURE);

			sendEmail(message);
		}

		catch (MessagingException e) {
			log.severe("sendRegistrationKey: fail to send email.");
		}
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
		
		String template = Config.inst().STUDENT_EMAIL_TEMPLATE_EVALUATION_;
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
		
		String template = Config.inst().STUDENT_EMAIL_TEMPLATE_EVALUATION_;
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
		
		String template = Config.inst().STUDENT_EMAIL_TEMPLATE_EVALUATION_;
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
		
		String template = Config.inst().STUDENT_EMAIL_TEMPLATE_EVALUATION_PUBLISHED;
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

		String submitUrl = Config.inst().TEAMMATES_APP_URL
				+ Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		submitUrl = Helper.addParam(submitUrl, Common.PARAM_COURSE_ID, c.id);
		submitUrl = Helper.addParam(submitUrl, Common.PARAM_EVALUATION_NAME,
				e.name);
		emailBody = emailBody.replace("${submitUrl}", submitUrl);
		
		String reportUrl = Config.inst().TEAMMATES_APP_URL
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
		
		String emailBody = Config.inst().STUDENT_EMAIL_TEMPLATE_COURSE_JOIN;
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
				Config.inst().STUDENT_EMAIL_FRAGMENT_COURSE_JOIN);
	
		emailBody = emailBody.replace("${key}", s.key);
	
		String joinUrl = Config.inst().TEAMMATES_APP_URL
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