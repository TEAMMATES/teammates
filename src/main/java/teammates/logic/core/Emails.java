package teammates.logic.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.SystemParams;
import teammates.common.util.EmailTemplates;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.common.util.Utils;

/**
 * Handles operations related to sending e-mails.
 */
public class Emails {
	//TODO: methods in this class throw too many exceptions. Reduce using a wrapper exception?
	private static Logger log = Utils.getLogger();

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
			
	public static enum EmailType {
		EVAL_CLOSING,
		EVAL_OPENING,
		FEEDBACK_CLOSING,
		FEEDBACK_OPENING,
		FEEDBACK_PUBLISHED
	};
	
	private String senderEmail;
	private String senderName;
	private String replyTo;

	public Emails() {
		senderEmail = "noreply@" + Config.inst().getAppId() + ".appspotmail.com";
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

	public void addEvaluationReminderToEmailsQueue(EvaluationAttributes evaluation,
			EmailType typeOfEmail) {
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ParamsNames.EMAIL_EVAL, evaluation.name);
		paramMap.put(ParamsNames.EMAIL_COURSE, evaluation.courseId);
		paramMap.put(ParamsNames.EMAIL_TYPE, typeOfEmail.toString());
		
		TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
		taskQueueLogic.createAndAddTask(SystemParams.EMAIL_TASK_QUEUE,
				Const.ActionURIs.EMAIL_WORKER, paramMap);
	}
	
	public void addFeedbackSessionReminderToEmailsQueue(FeedbackSessionAttributes feedback,
			EmailType typeOfEmail) {
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ParamsNames.EMAIL_FEEDBACK, feedback.feedbackSessionName);
		paramMap.put(ParamsNames.EMAIL_COURSE, feedback.courseId);
		paramMap.put(ParamsNames.EMAIL_TYPE, typeOfEmail.toString());
		
		TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
		taskQueueLogic.createAndAddTask(SystemParams.EMAIL_TASK_QUEUE,
				Const.ActionURIs.EMAIL_WORKER, paramMap);
	}
	public List<MimeMessage> generateEvaluationOpeningEmails(
			CourseAttributes course,
			EvaluationAttributes evaluation, 
			List<StudentAttributes> students,
			List<InstructorAttributes> instructors)
					throws MessagingException, IOException {

		String template = EmailTemplates.USER_EVALUATION_;
		List<MimeMessage> emails = generateEvaluationEmailBases(course,
				evaluation, students, instructors, template);
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace("${subjectPrefix}",
					SUBJECT_PREFIX_STUDENT_EVALUATION_OPENING));
			email.setContent(
					email.getContent().toString()
							.replace("${status}", "is now open"), "text/html");
		}
		return emails;
	}
	public List<MimeMessage> generateEvaluationOpeningEmailsForEval(
			EvaluationAttributes evaluation) 
					throws MessagingException, IOException, EntityDoesNotExistException {
		StudentsLogic studentsLogic = StudentsLogic.inst();
		CoursesLogic coursesLogic = CoursesLogic.inst();
		InstructorsLogic instructorsLogic = InstructorsLogic.inst();

		CourseAttributes course = coursesLogic.getCourse(evaluation.courseId);
		List<StudentAttributes> students = studentsLogic.getStudentsForCourse(evaluation.courseId);
		List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(evaluation.courseId);
		
		List<MimeMessage> emails = null;
		emails = generateEvaluationOpeningEmails(course, evaluation,
													students, instructors);
		return emails;
	}

	public List<MimeMessage> generateEvaluationReminderEmails(
			CourseAttributes course, 
			EvaluationAttributes evaluation,
			List<StudentAttributes> students,
			List<InstructorAttributes> instructors) 
					throws MessagingException, IOException {

		String template = EmailTemplates.USER_EVALUATION_;
		List<MimeMessage> emails = generateEvaluationEmailBases(course,
				evaluation, students, instructors, template);
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
			List<StudentAttributes> students, List<InstructorAttributes> instructors)
					throws MessagingException, IOException {

		String template = EmailTemplates.USER_EVALUATION_;
		List<MimeMessage> emails = generateEvaluationEmailBases(c, e, students,
				instructors, template);
		
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
	
	public List<MimeMessage> generateEvaluationClosingEmailsForEval(EvaluationAttributes ed)
					throws MessagingException, IOException, EntityDoesNotExistException {

		StudentsLogic studentsLogic = StudentsLogic.inst();
		CoursesLogic coursesLogic = CoursesLogic.inst();
		InstructorsLogic instructorsLogic = InstructorsLogic.inst();
		EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
		
		List<StudentAttributes> studentDataList = studentsLogic.getStudentsForCourse(ed.courseId);
		List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForCourse(ed.courseId);
		List<StudentAttributes> studentToRemindList = new ArrayList<StudentAttributes>();
		
		for (StudentAttributes sd : studentDataList) {
			if (!evaluationsLogic.isEvaluationCompletedByStudent(ed, sd.email)) {
				studentToRemindList.add(sd);
			}
		}
	
		CourseAttributes c = coursesLogic.getCourse(ed.courseId);

		List<MimeMessage> emails = null;
		emails = generateEvaluationClosingEmails(c, ed, 
				studentToRemindList, instructorList);
		
		return emails;
	}

	public List<MimeMessage> generateEvaluationPublishedEmails(
			CourseAttributes c,
			EvaluationAttributes e, 
			List<StudentAttributes> students,
			List<InstructorAttributes> instructors)
					throws MessagingException, IOException {

		String template = EmailTemplates.USER_EVALUATION_PUBLISHED;
		List<MimeMessage> emails = generateEvaluationEmailBases(c, e, students,
				instructors, template);
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
			List<InstructorAttributes> instructors,
			String template) 
					throws MessagingException, UnsupportedEncodingException {
		
		ArrayList<MimeMessage> emails = new ArrayList<MimeMessage>();
		for (StudentAttributes s : students) {
			emails.add(generateEvaluationEmailBaseForStudent(course, evaluation, s,
					template));
		}
		for (InstructorAttributes i : instructors) {
			emails.add(generateEvaluationEmailBaseForInstructor(course, evaluation, i,
					template));
		}
		return emails;
	}

	public MimeMessage generateEvaluationEmailBaseForStudent(
			CourseAttributes c,
			EvaluationAttributes e, 
			StudentAttributes s, 
			String template)
					throws MessagingException, UnsupportedEncodingException {

		MimeMessage message = getEmptyEmailAddressedToEmail(s.email);

		message.setSubject(String
				.format("${subjectPrefix} [Course: %s][Evaluation: %s]",
						c.name, e.name));

		String emailBody = template;

		if (isYetToJoinCourse(s)) {
			emailBody = fillUpJoinFragment(s, emailBody);
		} else {
			emailBody = emailBody.replace("${joinFragment}", "");
		}

		emailBody = emailBody.replace("${userName}", s.name);
		emailBody = emailBody.replace("${instructorFragment}", "");
		emailBody = emailBody.replace("${courseName}", c.name);
		emailBody = emailBody.replace("${courseId}", c.id);
		emailBody = emailBody.replace("${evaluationName}", e.name);
		emailBody = emailBody.replace("${deadline}",
				TimeHelper.formatTime(e.endTime));

		String submitUrl = Config.APP_URL
				+ Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_PAGE;
		submitUrl = Url.addParamToUrl(submitUrl, Const.ParamsNames.COURSE_ID,
				c.id);
		submitUrl = Url.addParamToUrl(submitUrl,
				Const.ParamsNames.EVALUATION_NAME, e.name);
		emailBody = emailBody.replace("${submitUrl}", submitUrl);

		String reportUrl = Config.APP_URL
				+ Const.ActionURIs.STUDENT_EVAL_RESULTS_PAGE;
		reportUrl = Url.addParamToUrl(reportUrl, Const.ParamsNames.COURSE_ID,
				c.id);
		reportUrl = Url.addParamToUrl(reportUrl,
				Const.ParamsNames.EVALUATION_NAME, e.name);
		emailBody = emailBody.replace("${reportUrl}", reportUrl);

		message.setContent(emailBody, "text/html");

		return message;
	}
	
	public MimeMessage generateEvaluationEmailBaseForInstructor(
			CourseAttributes c,
			EvaluationAttributes e, 
			InstructorAttributes i, 
			String template)
					throws MessagingException, UnsupportedEncodingException {

		MimeMessage message = getEmptyEmailAddressedToEmail(i.email);

		message.setSubject(String
				.format("${subjectPrefix} [Course: %s][Evaluation: %s]",
						c.name, e.name));

		String emailBody = template;

		emailBody = emailBody.replace("${userName}", i.name);
		emailBody = emailBody.replace("${joinFragment}", "");
		emailBody = emailBody.replace("${instructorFragment}",
					"The email below has been sent to students of course: "+c.id+".<br/>");
		
		emailBody = emailBody.replace("${courseName}", c.name);
		emailBody = emailBody.replace("${courseId}", c.id);
		emailBody = emailBody.replace("${evaluationName}", e.name);
		emailBody = emailBody.replace("${deadline}",
				TimeHelper.formatTime(e.endTime));

		String submitUrl = Config.APP_URL
				+ Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_PAGE;
		submitUrl = Url.addParamToUrl(submitUrl, Const.ParamsNames.COURSE_ID,
				c.id);
		submitUrl = Url.addParamToUrl(submitUrl,
				Const.ParamsNames.EVALUATION_NAME, e.name);
		emailBody = emailBody.replace("${submitUrl}", submitUrl);

		String reportUrl = Config.APP_URL
				+ Const.ActionURIs.STUDENT_EVAL_RESULTS_PAGE;
		reportUrl = Url.addParamToUrl(reportUrl, Const.ParamsNames.COURSE_ID,
				c.id);
		reportUrl = Url.addParamToUrl(reportUrl,
				Const.ParamsNames.EVALUATION_NAME, e.name);
		emailBody = emailBody.replace("${reportUrl}", reportUrl);

		message.setContent(emailBody, "text/html");

		return message;
	}

	public List<MimeMessage> generateFeedbackSessionOpeningEmails(FeedbackSessionAttributes session) 
					throws EntityDoesNotExistException, MessagingException, IOException {
		
		String template = EmailTemplates.USER_FEEDBACK_SESSION;
		StudentsLogic studentsLogic = StudentsLogic.inst();
		CoursesLogic coursesLogic = CoursesLogic.inst();
		InstructorsLogic instructorsLogic = InstructorsLogic.inst();
		FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
		
		CourseAttributes course = coursesLogic
				.getCourse(session.courseId);
		List<InstructorAttributes> instructors = instructorsLogic
				.getInstructorsForCourse(session.courseId);
		List<StudentAttributes> students;
		
		if (fsLogic.isFeedbackSessionViewableToStudents(session)) {
			if (session.isOpeningEmailEnabled) {
				students = studentsLogic.getStudentsForCourse(session.courseId);
			} else {
				students = studentsLogic.getUnregisteredStudentsForCourse(session.courseId);
			}
		} else {
			students = new ArrayList<StudentAttributes>();
		}
		
		List<MimeMessage> emails = null;
		if (session.isOpeningEmailEnabled) {
			emails = generateFeedbackSessionEmailBases(course,
					session, students, instructors, template, false);
			
			for (MimeMessage email : emails) {
				email.setSubject(email.getSubject().replace("${subjectPrefix}",
						SUBJECT_PREFIX_FEEDBACK_SESSION_OPENING));
				email.setContent(
						email.getContent().toString()
								.replace("${status}", "is now open"), "text/html");
			}
		} else {
			emails = generateFeedbackSessionEmailBases(course,
					session, students, instructors, template, true);
			
			for (MimeMessage email : emails) {
				email.setSubject(email.getSubject().replace("${subjectPrefix}",
						SUBJECT_PREFIX_STUDENT_COURSE_JOIN));
				email.setContent(
						email.getContent().toString().replaceFirst(
										"(?s)The following feedback session(.*)</a>",
										"Please join the course to participate in future sessions held by the course.")
						, "text/html");
			}
		}
		
		return emails;
	}
	
	public List<MimeMessage> generateFeedbackSessionReminderEmails(
			CourseAttributes course, 
			FeedbackSessionAttributes session,
			List<StudentAttributes> students,
			List<InstructorAttributes> instructorsToRemind,
			List<InstructorAttributes> instructorsToNotify) 
					throws MessagingException, IOException {

		String template = EmailTemplates.USER_FEEDBACK_SESSION;
		List<MimeMessage> emails = generateFeedbackSessionEmailBasesForInstructorReminders(
				course, session, instructorsToRemind, template);
		emails.addAll(generateFeedbackSessionEmailBases(course,
				session, students, instructorsToNotify, template, false));
		
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace("${subjectPrefix}",
					SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER));
			email.setContent(
					email.getContent()
							.toString()
							.replace("${status}",
									"is still open for submissions"),
					"text/html");
		}
		return emails;
	}
	
	public List<MimeMessage> generateFeedbackSessionClosingEmails(
			FeedbackSessionAttributes session)
					throws MessagingException, IOException, EntityDoesNotExistException {
		
		StudentsLogic studentsLogic = StudentsLogic.inst();
		CoursesLogic coursesLogic = CoursesLogic.inst();
		InstructorsLogic instructorsLogic = InstructorsLogic.inst();
		FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
		String template = EmailTemplates.USER_FEEDBACK_SESSION;
		List<MimeMessage> emails = null;
		
		CourseAttributes course = coursesLogic
				.getCourse(session.courseId);
		List<InstructorAttributes> instructors = instructorsLogic
				.getInstructorsForCourse(session.courseId);
		List<StudentAttributes> students = new ArrayList<StudentAttributes>();

		if (fsLogic.isFeedbackSessionViewableToStudents(session) == true) {
			List<StudentAttributes> allStudents = studentsLogic.
					getStudentsForCourse(session.courseId);

			for (StudentAttributes student : allStudents) {
				if (!fsLogic.isFeedbackSessionFullyCompletedByStudent(
						session.feedbackSessionName, session.courseId,
						student.email)) {
					students.add(student);
				}
			}
		}
		emails = generateFeedbackSessionEmailBases(
				course, session, students, instructors, template, false);
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
			FeedbackSessionAttributes session)
					throws MessagingException, IOException, EntityDoesNotExistException {
		String template = EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED;
		List<MimeMessage> emails = null;

		CoursesLogic coursesLogic = CoursesLogic.inst();
		InstructorsLogic instructorsLogic = InstructorsLogic.inst();
		StudentsLogic studentsLogic = StudentsLogic.inst();
		
		CourseAttributes course = coursesLogic.getCourse(session.courseId);
		List<StudentAttributes> students = studentsLogic
				.getStudentsForCourse(session.courseId);
		List<InstructorAttributes> instructors = instructorsLogic
				.getInstructorsForCourse(session.courseId);
		
		Set<String> studentsWithSomethingNewToSee = 
				getStudentsWithSomethingNewToSee(students, session);
		
		emails = generateFeedbackSessionEmailBases(course,
				session, students, instructors, template, false);
		
		for (MimeMessage email : emails) {
			String recipientAddress = email.getRecipients(Message.RecipientType.TO)[0].toString();
			String emailBody = (String) email.getContent();
			
			if (isCopyToBeSentToInstructor(emailBody)
					|| studentsWithSomethingNewToSee.contains(recipientAddress)) {
				emailBody = emailBody.replace("${mayIgnoreFragment}", "");
			} else {
				emailBody = emailBody
						.replace(
								"${mayIgnoreFragment}",
								"<strong>You did not receive any new responses and may ignore this email. "
								+ "However you can still see your own submissions by following the link below.</strong><br>");
			}
			
			email.setContent(emailBody, "text/html");
		}
		
		for (MimeMessage email : emails) {
			email.setSubject(email.getSubject().replace("${subjectPrefix}",
					SUBJECT_PREFIX_FEEDBACK_SESSION_PUBLISHED));
		}
		return emails;
	}

	private boolean isCopyToBeSentToInstructor(String emailBody) {
		return emailBody.contains("The email below has been sent to students of course");
	}
	
	private Set<String> getStudentsWithSomethingNewToSee(
			List<StudentAttributes> students,
			FeedbackSessionAttributes session)
			throws EntityDoesNotExistException {
		FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
		Set<String> studentsWithSomethingNew = new HashSet<String>();

		for (StudentAttributes student : students) {
			FeedbackSessionResultsBundle resultsForStudent = fsLogic
					.getFeedbackSessionResultsForStudent(
							session.feedbackSessionName, session.courseId,
							student.email);

			if(isStudentHasSomethingNewToSee(student, resultsForStudent)) {
				studentsWithSomethingNew.add(student.email);
			}
		}

		return studentsWithSomethingNew;
	}

	private boolean isStudentHasSomethingNewToSee(StudentAttributes student,
			FeedbackSessionResultsBundle results) {
		for (FeedbackResponseAttributes response : results.responses) {
			if (!response.giverEmail.equals(student.email)) {
				return true;
			}
		}
		return false;
	}

	public List<MimeMessage> generateFeedbackSessionEmailBases(
			CourseAttributes course,
			FeedbackSessionAttributes session, 
			List<StudentAttributes> students,
			List<InstructorAttributes> instructors,
			String template,
			boolean insertInstructorPlaceholderJoinLink) 
					throws MessagingException, UnsupportedEncodingException {
		
		ArrayList<MimeMessage> emails = new ArrayList<MimeMessage>();
		for (StudentAttributes s : students) {
			emails.add(generateFeedbackSessionEmailBaseForStudents(course, session, s,
					template));
		}
		for (InstructorAttributes i : instructors) {
			emails.add(generateFeedbackSessionEmailBaseForInstructors(course,
					session, i, template, insertInstructorPlaceholderJoinLink));
		}
		return emails;
	}
	
	public List<MimeMessage> generateFeedbackSessionEmailBasesForInstructorReminders(
			CourseAttributes course,
			FeedbackSessionAttributes session, 
			List<InstructorAttributes> instructors,
			String template) 
					throws MessagingException, UnsupportedEncodingException {
		
		ArrayList<MimeMessage> emails = new ArrayList<MimeMessage>();
		for (InstructorAttributes i : instructors) {
			emails.add(generateFeedbackSessionEmailBaseForInstructorReminders(course, session, i,
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
		
		String submitUrl = Config.APP_URL
				+ Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE;
		submitUrl = Url.addParamToUrl(submitUrl, Const.ParamsNames.COURSE_ID,
				c.id);
		submitUrl = Url.addParamToUrl(submitUrl,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName);
		emailBody = emailBody.replace("${submitUrl}", submitUrl);

		String reportUrl = Config.APP_URL
				+ Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE;
		reportUrl = Url.addParamToUrl(reportUrl, Const.ParamsNames.COURSE_ID,
				c.id);
		reportUrl = Url.addParamToUrl(reportUrl,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName);
		emailBody = emailBody.replace("${reportUrl}", reportUrl);

		message.setContent(emailBody, "text/html");

		return message;
	}
	
	public MimeMessage generateFeedbackSessionEmailBaseForInstructors(
			CourseAttributes c,
			FeedbackSessionAttributes fs, 
			InstructorAttributes i,
			String template,
			boolean insertPlaceholderJoinFragment)
					throws MessagingException, UnsupportedEncodingException {

		MimeMessage message = getEmptyEmailAddressedToEmail(i.email);

		message.setSubject(String
				.format("${subjectPrefix} [Course: %s][Feedback Session: %s]",
						c.name, fs.feedbackSessionName));

		String emailBody = template;

		if (insertPlaceholderJoinFragment) {
			emailBody = fillUpJoinFragment(null, emailBody);
		} else {
			emailBody = emailBody.replace("${joinFragment}", "");
		}
		emailBody = emailBody.replace("${userName}", i.name);
		emailBody = emailBody.replace("${courseName}", c.name);
		emailBody = emailBody.replace("${courseId}", c.id);
		emailBody = emailBody.replace("${feedbackSessionName}", fs.feedbackSessionName);
		emailBody = emailBody.replace("${deadline}",
				TimeHelper.formatTime(fs.endTime));
		emailBody = emailBody.replace("${instructorFragment}", "The email below has been sent to students of course: "+c.id+".<p/><br/>");
		
		String submitUrl = Config.APP_URL
				+ Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE;
		submitUrl = Url.addParamToUrl(submitUrl, Const.ParamsNames.COURSE_ID,
				c.id);
		submitUrl = Url.addParamToUrl(submitUrl,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName);
		emailBody = emailBody.replace("${submitUrl}", submitUrl);

		String reportUrl = Config.APP_URL
				+ Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE;
		reportUrl = Url.addParamToUrl(reportUrl, Const.ParamsNames.COURSE_ID,
				c.id);
		reportUrl = Url.addParamToUrl(reportUrl,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName);
		emailBody = emailBody.replace("${reportUrl}", reportUrl);

		message.setContent(emailBody, "text/html");

		return message;
	}
	
	public MimeMessage generateFeedbackSessionEmailBaseForInstructorReminders(
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
		emailBody = emailBody.replace("${instructorFragment}", "");
		
		String submitUrl = Config.APP_URL
				+ Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE;
		submitUrl = Url.addParamToUrl(submitUrl, Const.ParamsNames.COURSE_ID,
				c.id);
		submitUrl = Url.addParamToUrl(submitUrl,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName);
		emailBody = emailBody.replace("${submitUrl}", submitUrl);

		String reportUrl = Config.APP_URL
				+ Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE;
		reportUrl = Url.addParamToUrl(reportUrl, Const.ParamsNames.COURSE_ID,
				c.id);
		reportUrl = Url.addParamToUrl(reportUrl,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName);
		emailBody = emailBody.replace("${reportUrl}", reportUrl);

		message.setContent(emailBody, "text/html");

		return message;
	}
	
	public MimeMessage generateStudentCourseJoinEmail(
			CourseAttributes c,	StudentAttributes s) 
					throws AddressException, MessagingException, UnsupportedEncodingException {

		MimeMessage message = getEmptyEmailAddressedToEmail(s.email);
		message.setSubject(String.format(SUBJECT_PREFIX_STUDENT_COURSE_JOIN
				+ " [%s][Course ID: %s]", c.name, c.id));

		String emailBody = EmailTemplates.STUDENT_COURSE_JOIN;
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
		String stackTrace = TeammatesException.toStringWithStackTrace(error);
	
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
		String recipient = Config.SUPPORT_EMAIL;
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				recipient));
		message.setFrom(new InternetAddress(senderEmail, senderName));
		message.setSubject(String.format(SUBJECT_PREFIX_ADMIN_SYSTEM_ERROR,
				version, errorMessage));
	
		String emailBody = EmailTemplates.SYSTEM_ERROR;
	
		emailBody = emailBody.replace("${requestPath}", requestPath);
		emailBody = emailBody.replace("${requestParameters}", requestParam);
		emailBody = emailBody.replace("${errorMessage}", errorMessage);
		emailBody = emailBody.replace("${stackTrace}", stackTrace);
		message.setContent(emailBody, "text/html");
	
		return message;
	}

	public MimeMessage generateCompiledLogsEmail(String logs)
			throws AddressException, MessagingException, UnsupportedEncodingException {
		
		MimeMessage message = getEmptyEmailAddressedToEmail(Config.SUPPORT_EMAIL);
		message.setSubject("Severe Error Logs Compilation");

		String emailBody = logs;

		message.setContent(emailBody, "text/html");
		return message;
	}
	
	public void sendEmails(List<MimeMessage> messages) {
		for (MimeMessage m : messages) {
			try {
				sendEmail(m);
			} catch (MessagingException e) {
				/*
				 * TODO: Add mechanism for handling/resending mails which 
				 * 		 encountered errors
				 *       To consider: Try sending mail till a max retry count is reached?
				 */
				log.severe("Error in sending : " + m.toString()
						    + " Cause : " + e.getMessage());
			}
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
					Config.inst().getAppVersion());
			sendEmail(email);
			log.severe("Sent crash report: " + Emails.getEmailInfo(email));
		} catch (Exception e) {
			log.severe("Error in sending crash report: "
					+ (email == null ? "" : email.toString()));
		}
	
		return email;
	}

	public MimeMessage sendLogReport(MimeMessage message) {
		MimeMessage email = null;
		try {
			sendEmail(message);
		} catch (Exception e) {
			log.severe("Error in sending log report: "
					+ (email == null ? "" : email.toString()));
		}
	
		return email;
	}
	
	private String fillUpJoinFragment(StudentAttributes s, String emailBody) {
		emailBody = emailBody.replace("${joinFragment}",
				EmailTemplates.FRAGMENT_STUDENT_COURSE_JOIN);

		String joinUrl;
		if (s != null) {
			String key;
			key = StringHelper.encrypt(s.key);
	
			joinUrl = Config.APP_URL + Const.ActionURIs.STUDENT_COURSE_JOIN;
			joinUrl = Url.addParamToUrl(joinUrl, Const.ParamsNames.REGKEY, key);
		} else {
			joinUrl = "{The join link unique for each student appears here}";
		}

		emailBody = emailBody.replace("${joinUrl}", joinUrl);
		return emailBody;
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

	/**
	 * Generate email recipient list for the automated reminders sent.
	 * Used for AdminActivityLog
	 */
	public static ArrayList<Object> extractRecipientsList(ArrayList<MimeMessage> emails){
	
		ArrayList<Object> data = new ArrayList<Object>();
		
		try{
			for (int i = 0; i < emails.size(); i++){
				Address[] recipients = emails.get(i).getRecipients(Message.RecipientType.TO);
				for (int j = 0; j < recipients.length; j++){
					data.add(recipients[j]);
				}
			}
		} catch (Exception e){
			throw new RuntimeException("Unexpected exception during generation of log messages for automated reminders",e);
		}
		
		return data;
	}
}