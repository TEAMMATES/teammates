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
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import teammates.Config;
import teammates.api.Common;
import teammates.api.TeammatesException;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.StudentData;
import teammates.jsp.Helper;
import teammates.persistent.Evaluation;
import teammates.persistent.Student;

/**
 * Email handles all operations with regards to sending e-mails.
 * 
 * @author Gerald GOH
 * 
 */
public class Emails {
	private String from;
	private Properties props;
	private static Logger log = Common.getLogger();

	private final String HEADER_REGISTRATION_INVITATION = "TEAMMATES: Registration Invitation: Register in the course %s";
	private final String HEADER_REGISTRATION_REMINDER = "TEAMMATES: Registration Reminder: Register in the course %s";
	private final String SUBJECT_EVALUATION_OPEN = "TEAMMATES: Peer evaluation now open [Course: %s][Evaluation: %s]";
	private final String HEADER_TEAMFORMING_OPEN = "TEAMMATES: Team Forming Session Opening: %s %s";
	private final String HEADER_EVALUATION_CHANGE = "TEAMMATES: Evaluation Changed: %s %s";
	private final String HEADER_TEAMFORMING_CHANGE = "TEAMMATES: Team Forming Changed: %s %s";
	private final String HEADER_EVALUATION_REMINDER = "TEAMMATES: Evaluation Reminder: %s %s";
	private final String HEADER_TEAMFORMING_REMINDER = "TEAMMATES: Team Forming Reminder: %s %s";
	private final String HEADER_EVALUATION_PUBLISH = "TEAMMATES: Evaluation Published: %s %s";
	private final String HEADER_TEAMFORMING_PUBLISH = "TEAMMATES: Team Forming Published: %s %s";
	private final String TEAMMATES_APP_SIGNATURE = "\n\nIf you encounter any problems using the system, email TEAMMATES support team at teammates@comp.nus.edu.sg"
			+ "\n\nRegards, \nTEAMMATES System";

	private String EMAIL_EVALUATION_OPENING = "";

	/**
	 * Constructs an Email object. Sets the sender's e-mail address and
	 * instantiate a new Properties object.
	 * 
	 */
	public Emails() {
		from = Config.inst().TEAMMATES_APP_ACCOUNT;
		props = new Properties();
	}

	/**
	 * Sends an email to a Student informing him of new Evaluation details.
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
	 * @param instructions
	 *            the evaluation instructions (Precondition: Must not be null)
	 * 
	 * @param deadline
	 *            the evaluation deadline (Precondition: Must not be null)
	 */
	public void informStudentsOfEvaluationChanges(String email,
			String studentName, String courseID, String evaluationName,
			String instructions, String start, String deadline) {
		try {
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(session);

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email));

			message.setFrom(new InternetAddress(from));
			message.setSubject(String.format(HEADER_EVALUATION_CHANGE,
					courseID, evaluationName));
			message.setText("Dear "
					+ studentName
					+ ",\n\n"
					+ "There are changes to the evaluation: \n\n"
					+ courseID
					+ " "
					+ evaluationName
					+ "\n\n"
					+ "made by your coordinator. The start, deadline and instructions of the evaluation are as follow, \n\n"
					+ "Start: " + start + "H. \n\n" + "Deadline: " + deadline
					+ "H. \n\n" + "Instructions : " + instructions
					+ "\n You can access the evaluation here: "
					+ Config.inst().TEAMMATES_APP_URL + TEAMMATES_APP_SIGNATURE);

			sendEmail(message);

		}

		catch (MessagingException e) {

		}

	}

	/**
	 * Sends an email to a Student informing him of the opening of an
	 * evaluation.
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
	 */
	public void informStudentsOfEvaluationOpening(String email,
			String studentName, String courseID, String evaluationName,
			String deadline) {
		try {
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(session);

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email));

			message.setFrom(new InternetAddress(from));
			message.setSubject(String.format(SUBJECT_EVALUATION_OPEN, courseID,
					evaluationName));
			message.setText("Dear " + studentName + ",\n\n"
					+ "The following evaluation: \n\n" + courseID + " "
					+ evaluationName + "\n\n"
					+ "is open from now until the deadline " + deadline
					+ "H.\n" + "You can access the evaluation here: "
					+ Config.inst().TEAMMATES_APP_URL + TEAMMATES_APP_SIGNATURE);

			sendEmail(message);

		}

		catch (MessagingException e) {

		}

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

	public void informStudentsOfTeamFormingChanges(String email,
			String studentName, String courseID, String instructions,
			String start, String deadline, String profileTemplate) {
		try {
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(session);

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email));

			message.setFrom(new InternetAddress(from));
			message.setSubject(String.format(HEADER_TEAMFORMING_CHANGE,
					courseID, " "));
			message.setText("Dear "
					+ studentName
					+ ",\n\n"
					+ "There are changes to the team forming session of: \n\n"
					+ courseID
					+ " \n\n"
					+ "made by your coordinator. The start, deadline, instructions and "
					+ "profile template of the team forming are as follow, \n\n"
					+ "Start: " + start + "H. \n\n" + "Deadline: " + deadline
					+ "H. \n\n" + "Instructions : " + instructions
					+ "Profile Template: " + profileTemplate
					+ "\n You can access the team forming session here: "
					+ Config.inst().TEAMMATES_APP_URL + TEAMMATES_APP_SIGNATURE);

			sendEmail(message);
		}

		catch (MessagingException e) {
			log.fine("teamFormingSessionChanges: fail to send email.");
		}
	}

	public void informStudentsOfTeamFormingOpening(String email,
			String studentName, String courseID, String deadline) {
		try {
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(session);

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email));

			message.setFrom(new InternetAddress(from));
			message.setSubject(String.format(HEADER_TEAMFORMING_OPEN, courseID,
					" "));
			message.setText("Dear " + studentName + ",\n\n"
					+ "The following team forming session for: " + courseID
					+ " " + "is now open.\n\nThe deadline is: " + deadline
					+ "\n" + "You can access the list of students here: "
					+ Config.inst().TEAMMATES_APP_URL + TEAMMATES_APP_SIGNATURE);

			sendEmail(message);

		}

		catch (MessagingException e) {
			log.severe("teamFormingSessionOpening: fail to send email.");
		}
	}

	/**
	 * Sends an email to a Student informing him of the publishing of results
	 * for a particular evaluation.
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
	 */
	public void informStudentsOfPublishedEvaluation(String email,
			String studentName, String courseID, String evaluationName) {
		try {
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(session);

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email));

			message.setFrom(new InternetAddress(from));
			message.setSubject(String.format(HEADER_EVALUATION_PUBLISH,
					courseID, evaluationName));
			message.setText("Dear " + studentName + ",\n\n"
					+ "The results of the evaluation: \n\n" + courseID + " "
					+ evaluationName + "\n\n" + "have been published.\n"
					+ "You can view the result here: "
					+ Config.inst().TEAMMATES_APP_URL + TEAMMATES_APP_SIGNATURE);

			sendEmail(message);

		}

		catch (MessagingException e) {

		}

	}

	public void informStudentsOfPublishedTeamForming(String email,
			String studentName, String courseID) {
		try {
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(session);

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email));

			message.setFrom(new InternetAddress(from));
			message.setSubject(String.format(HEADER_TEAMFORMING_PUBLISH,
					courseID, " "));
			message.setText("Dear " + studentName + ",\n\n"
					+ "The results of the team forming session: \n\n"
					+ courseID + " \n\n" + "have been published.\n"
					+ "You can view the result here: "
					+ Config.inst().TEAMMATES_APP_URL + TEAMMATES_APP_SIGNATURE);

			sendEmail(message);
		}

		catch (MessagingException e) {
			log.severe("teamFormingPublished: fail to send email.");
		}
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
	 * Sends an email reminding the Student of the Team Forming deadline.
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
	 * @param deadline
	 *            the evaluation deadline (Precondition: Must not be null)
	 */
	public void remindStudentOfTeamForming(String email, String studentName,
			String courseID, String deadline) {
		try {
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(session);

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email));

			message.setFrom(new InternetAddress(from));
			message.setSubject(String.format(HEADER_TEAMFORMING_REMINDER,
					courseID, " "));
			message.setText("Dear " + studentName + ",\n\n"
					+ "You are reminded to make a team for: \n\n" + courseID
					+ " \n\n" + "by " + deadline + "H.\n"
					+ "You can access the team forming session here: "
					+ Config.inst().TEAMMATES_APP_URL + TEAMMATES_APP_SIGNATURE);

			sendEmail(message);

		}

		catch (MessagingException e) {
			log.severe("remindStudentOfTeamForming: fail to send message");
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

	/**
	 * Stress testing of mail account
	 * 
	 * @param email
	 * @param size
	 * @author wangsha
	 */
	public void mailStressTesting(String email, int size) {
		try {
			Session session = Session.getDefaultInstance(props, null);

			for (int i = 0; i < size; i++) {
				MimeMessage message = new MimeMessage(session);

				message.addRecipient(Message.RecipientType.TO,
						new InternetAddress(email));

				message.setFrom(new InternetAddress(from));
				message.setSubject("Teammates Mail Stree Testing [" + i + "|"
						+ size + "]");
				message.setText("This is a testing email");

				sendEmail(message);
				log.fine("send email " + i + "|" + size);
			}

		}

		catch (MessagingException e) {

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

	public List<MimeMessage> generateEvaluationOpeningEmails(CourseData course,
			EvaluationData evaluation, List<StudentData> students)
			throws MessagingException {
		ArrayList<MimeMessage> emails = new ArrayList<MimeMessage>();
		for (StudentData s : students) {
			emails.add(generateEvaluationOpeningEmail(course, evaluation, s));
		}
		return emails;
	}

	public MimeMessage generateEvaluationOpeningEmail(CourseData c,
			EvaluationData e, StudentData s) throws MessagingException {

		Session session = Session.getDefaultInstance(new Properties(), null);
		MimeMessage message = new MimeMessage(session);

		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				s.email));

		message.setFrom(new InternetAddress(from));

		// TODO: specify subject line in the email template itself
		message.setSubject(String.format(SUBJECT_EVALUATION_OPEN, c.name,
				e.name));

		String emailBody = Config.inst().STUDENT_EMAIL_TEMPLATE_EVALUATION_OPENING;

		if (isYetToJoinCourse(s)) {
			emailBody = emailBody.replace("${joinFragment}",
					Config.inst().STUDENT_EMAIL_FRAGMENT_JOIN_COURSE);

			emailBody = emailBody.replace("${key}", s.key);

			String joinUrl = Config.inst().TEAMMATES_APP_URL
					+ Common.PAGE_STUDENT_JOIN_COURSE;
			joinUrl = Helper.addParam(joinUrl, Common.PARAM_REGKEY, s.key);

			emailBody = emailBody.replace("${joinUrl}", joinUrl);
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

		message.setContent(emailBody, "text/html");

		return message;
	}

	private boolean isYetToJoinCourse(StudentData s) {
		return s.id == null || s.id.isEmpty();
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

	public List<MimeMessage> sendEvaluationReminders(CourseData course,
			EvaluationData evaluation, List<StudentData> students)
			throws MessagingException {
		List<MimeMessage> emails = generateEvaluationReminderEmails(course,
				evaluation, students);
		sendEmails(emails);
		return emails;
	}

	public List<MimeMessage> generateEvaluationReminderEmails(
			CourseData course, EvaluationData evaluation,
			List<StudentData> students) throws MessagingException {
		List<MimeMessage> emails = generateEvaluationOpeningEmails(course,
				evaluation, students);
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace(
					"TEAMMATES: Peer evaluation now open",
					"TEAMMATES: Peer evaluation reminder"));
		}
		return emails;
	}

	public List<MimeMessage> generateEvaluationClosingEmails(CourseData c,
			EvaluationData e, List<StudentData> students)
			throws MessagingException, IOException {
		List<MimeMessage> emails = generateEvaluationOpeningEmails(c, e,
				students);
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace(
					"TEAMMATES: Peer evaluation now open",
					"TEAMMATES: Peer evaluation closing soon"));
			email.setContent(
					email.getContent()
							.toString()
							.replace("is now open for submission",
									"is closing soon"), "text/html");
		}
		return emails;
	}
}