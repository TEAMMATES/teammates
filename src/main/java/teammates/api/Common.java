package teammates.api;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.logging.Logger;

import teammates.persistent.Evaluation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Class that stores variables and methods that are widely used across classes
 * 
 */

// TODO: create a subclass (e.g., InternalUtil) and move all internal utility
// functions to that sub class. It should be in util package.
public class Common {

	private static Logger log = Logger.getLogger(Common.class.getName());

	public static final String EOL = System.getProperty("line.separator");
	public static final int UNINITIALIZED_INT = -9999;
	public static final double UNINITIALIZED_DOUBLE = -9999.0;
	public static final String ENCODING = "UTF8";
	//TODO: get this value from a config file
	public static final String VERSION = "4.18";
	public static final String TEST_DATA_FOLDER = "src/test/resources/data";
	public static final String TEST_PAGES_FOLDER = "src/test/resources/pages";

	public static final int POINTS_NOT_SURE = -101;
	public static final int POINTS_NOT_SUBMITTED = -999;

	// Hover messages
	public static final String HOVER_MESSAGE_COURSE_ENROLL = "Enroll student into the course";
	public static final String HOVER_MESSAGE_COURSE_DETAILS = "View, edit and send registration keys to the students in the course";
	public static final String HOVER_MESSAGE_COURSE_DELETE = "Delete the course and its corresponding students and evaluations";
	public static final String HOVER_MESSAGE_COURSE_ADD_EVALUATION = "Add an evaluation for the course";
	public static final String HOVER_MESSAGE_CLAIMED = "This is student own estimation of his/her contributions to the project";
	public static final String HOVER_MESSAGE_PERCEIVED = "This is the average of what other team members think this student contributed to the project";
	public static final String HOVER_MESSAGE_PERCEIVED_CLAIMED = "Difference between claimed and perceived contribution points";
	
	public static final String HOVER_MESSAGE_COURSE_STUDENT_DETAILS = "View the details of the student";
	public static final String HOVER_MESSAGE_COURSE_STUDENT_EDIT = "Edit the details of the student";
	public static final String HOVER_MESSAGE_COURSE_STUDENT_REMIND = "E-mail the registration key to the student";
	public static final String HOVER_MESSAGE_COURSE_STUDENT_DELETE = "Delete the student and the corresponding evaluations from the course";
	
	public static final String HOVER_MESSAGE_COURSE_REMIND = "Send a reminder to all students yet to join the class";
	public static final String HOVER_MESSAGE_COURSE_DELETE_ALL_STUDENTS = "Delete all students in this course"; 

	public static final String HOVER_MESSAGE_EVALUATION_STATUS_AWAITING = "The evaluation is created but has not yet started";
	public static final String HOVER_MESSAGE_EVALUATION_STATUS_OPEN = "The evaluation has started and students can submit feedback until the closing time";
	public static final String HOVER_MESSAGE_EVALUATION_STATUS_CLOSED = "The evaluation has finished but the results have not been made available to the students";
	public static final String HOVER_MESSAGE_EVALUATION_STATUS_PUBLISHED = "The evaluation has finished and the results have been made available to students";

	public static final String HOVER_MESSAGE_EVALUATION_RESULTS = "View the current results of the evaluation";
	public static final String HOVER_MESSAGE_EVALUATION_EDIT = "Edit evaluation details";
	public static final String HOVER_MESSAGE_EVALUATION_REMIND = "Send e-mails to remind students who have not submitted their evaluations to do so";
	public static final String HOVER_MESSAGE_EVALUATION_DELETE = "Delete the evaluation";
	public static final String HOVER_MESSAGE_EVALUATION_PUBLISH = "Publish evaluation results for students to view";
	public static final String HOVER_MESSAGE_EVALUATION_UNPUBLISH = "Make results not visible to students";
	
	public static final String HOVER_MESSAGE_EVALUATION_DIFF = "Perceived Contribution - Claimed Contribution";
	public static final String HOVER_MESSAGE_EVALUATION_RESPONSE_RATE = "Number of students submitted / Class size";
	public static final String HOVER_MESSAGE_EVALUATION_POINTS_GIVEN = "The list of points that this student gives to others";
	public static final String HOVER_MESSAGE_EVALUATION_POINTS_RECEIVED = "The list of points that this student received from others";

	public static final String HOVER_MESSAGE_EVALUATION_INPUT_COURSE = "Please select the course for which the evaluation is to be created.";
	public static final String HOVER_MESSAGE_EVALUATION_INPUT_START = "Please enter the start date for the evaluation.";
	public static final String HOVER_MESSAGE_EVALUATION_INPUT_NAME = "Enter the name of the evaluation e.g. Mid-term.";
	public static final String HOVER_MESSAGE_EVALUATION_INPUT_DEADLINE = "Please enter deadline for the evaluation.";
	public static final String HOVER_MESSAGE_EVALUATION_INPUT_COMMENTSSTATUS = "Enable this if you want students to give anonymous feedback to team members.<br />" +
																				"You can moderate those peer feedback before publishing it to the team.";
	public static final String HOVER_MESSAGE_EVALUATION_INPUT_TIMEZONE = "Daylight saving is not taken into account i.e. if you are in UTC -8:00 and there is<br />" +
																			"daylight saving, you should choose UTC -7:00 and its corresponding timings.";
	public static final String HOVER_MESSAGE_EVALUATION_INPUT_GRACEPERIOD = "Please select the amount of time that the system will continue accepting <br />" +
																			"submissions after the specified deadline.";
	public static final String HOVER_MESSAGE_EVALUATION_INPUT_INSTRUCTIONS = "Please enter instructions for your students, e.g. Avoid comments which are too critical.";
	
	public static final String HOVER_MESSAGE_EVALUATION_SUBMISSION_VIEW_REVIEWER = "View feedback from the student for his team<br />This opens in a new window";
	public static final String HOVER_MESSAGE_EVALUATION_SUBMISSION_VIEW_REVIEWEE = "View feedback from the team for the student<br />This opens in a new window";
	public static final String HOVER_MESSAGE_EVALUATION_SUBMISSION_EDIT = "Edit feedback from the student for his team<br />This opens in a new window";
	
	public static final String HOVER_MESSAGE_EVALUATION_SUBMISSION_NOT_AVAILABLE = "Not Available: There is no data for this<br />or the data is not enough";
	public static final String HOVER_MESSAGE_EVALUATION_SUBMISSION_NOT_SURE = "Not sure about the contribution";

	public static final String HOVER_MESSAGE_JOIN_COURSE = "Enter your registration key for the course.";
	public static final String HOVER_MESSAGE_EVALUATION_EDIT_SUBMISSION = "Edit submitted evaluation";
	
	public static final String HOVER_MESSAGE_STUDENT_COURSE_PROFILE = "Your profile in this course";
	public static final String HOVER_MESSAGE_STUDENT_COURSE_DETAILS = "View and edit information regarding your team";
	public static final String HOVER_MESSAGE_STUDENT_EVALUATION_SUBMIT = "Start evaluation";
	
	public static final String HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_PENDING = "The evaluation is yet to be completed by you";
	public static final String HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_SUBMITTED = "You have submitted your feedback for this evaluation";
	public static final String HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_CLOSED = "The evaluation has finished but the coordinator has not published the results yet";
	public static final String HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_PUBLISHED = "The evaluation has finished and you can check the results";
	public static final String HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_ERROR = "There were some errors in retrieving this evaluation.";

	// Evaluation status
	public static final String EVALUATION_STATUS_AWAITING = "Awaiting";
	public static final String EVALUATION_STATUS_OPEN = "Open";
	public static final String EVALUATION_STATUS_CLOSED = "Closed";
	public static final String EVALUATION_STATUS_PUBLISHED = "Published";
	
	public static final String STUDENT_EVALUATION_STATUS_PENDING = "Pending";
	public static final String STUDENT_EVALUATION_STATUS_SUBMITTED = "Submitted";
	public static final String STUDENT_EVALUATION_STATUS_CLOSED = "Closed";
	public static final String STUDENT_EVALUATION_STATUS_PUBLISHED = "Published";
	public static final String STUDENT_EVALUATION_STATUS_ERROR = "Error";
	
	// Student status
	public static final String STUDENT_STATUS_YET_TO_JOIN = "Yet to join";
	public static final String STUDENT_STATUS_JOINED = "Joined";

	// JSP Parameter names
	public static final String PARAM_COURSE_ID = "courseid";
	public static final String PARAM_COURSE_NAME = "coursename";
	public static final String PARAM_COORD_ID = "coordid";
	public static final String PARAM_COORD_EMAIL = "coordemail";
	public static final String PARAM_COORD_NAME = "coordname";
	public static final String PARAM_STUDENTS_ENROLLMENT_INFO = "enrollstudents";

	public static final String PARAM_EVALUATION_NAME = "evaluationname";

	public static final String PARAM_EVALUATION_START = "start";
	public static final String PARAM_EVALUATION_STARTTIME = "starttime";
	public static final String PARAM_EVALUATION_DEADLINE = "deadline";
	public static final String PARAM_EVALUATION_DEADLINETIME = "deadlinetime";
	public static final String PARAM_EVALUATION_TIMEZONE = "timezone";

	public static final String PARAM_EVALUATION_COMMENTSENABLED = "commentsstatus";
	public static final String PARAM_EVALUATION_GRACEPERIOD = "graceperiod";
	public static final String PARAM_EVALUATION_INSTRUCTIONS = "instr";
	public static final String PARAM_EVALUATION_NUMBEROFCOMPLETEDEVALUATIONS = "numberofevaluations";
	public static final String PARAM_EVALUATION_NUMBEROFEVALUATIONS = "numberofcompletedevaluations";
	public static final String PARAM_EVALUATION_PUBLISHED = "published";
	public static final String PARAM_EVALUATION_TYPE = "evaluationtype";
	
	public static final String PARAM_STUDENT_ID = "googleid";
	
	public static final String PARAM_REGKEY = "regkey";
	public static final String PARAM_STUDENT_EMAIL = "studentemail";
	public static final String PARAM_NEW_STUDENT_EMAIL = "newstudentemail";

	public static final String PARAM_STUDENT_NAME = "studentname";
	public static final String PARAM_FROM_EMAIL = "fromemail";
	public static final String PARAM_TO_EMAIL = "toemail";
	public static final String PARAM_TEAM_NAME = "teamname";
	public static final String PARAM_POINTS = "points";
	public static final String PARAM_JUSTIFICATION = "justification";
	public static final String PARAM_COMMENTS = "comments";
	public static final String PARAM_TEAMMATES = "teammates";

	public static final String PARAM_STATUS_MESSAGE = "message";
	public static final String PARAM_ERROR = "error";
	public static final String PARAM_NEXT_URL = "next";
	public static final String PARAM_USER_ID = "user";

	public static final String PARAM_LOGIN_ADMINISTRATOR = "administrator";
	public static final String PARAM_LOGIN_COORDINATOR = "coordinator";
	public static final String PARAM_LOGIN_STUDENT = "student";

	/*
	 * Logical pages links. These are the links which should be accessed as
	 * the URL to get the real pages. The JSP pages in the next section are
	 * the pages to display, which should not be accessed directly.
	 * In order to cater with masquerade mode, it is highly encourage to use
	 * the helper methods get*Link which automatically includes masqueraded
	 * userId.
	 * Otherwise, you need to call helper.processMasquerade(link) yourself
	 * In forms, you need to include a hidden input field with parameter
	 * PARAM_USER_ID and value helper.requestedUser to support masquerade mode.
	 * It should only be shown if the session is in masquerade mode.
	 * Below is the code to be put at the end of the form to include masquerade
	 * mode in forms. It is put at the end of the form so that it will be at
	 * the end of the URL. Just a convention.
		
		<% if(helper.isMasqueradeMode()){ %>
			<input type="hidden" name="<%= Common.PARAM_USER_ID %>" value="<%= helper.requestedUser %>" />
		<% } %>
		
	 */
	public static final String PAGE_COORD_HOME = "/page/coordHome"; 
	public static final String PAGE_COORD_COURSE = "/page/coordCourse"; 
	public static final String PAGE_COORD_COURSE_DELETE = "/page/coordCourseDelete"; 
	public static final String PAGE_COORD_COURSE_DETAILS = "/page/coordCourseDetails"; 
	public static final String PAGE_COORD_COURSE_STUDENT_DETAILS = "/page/coordCourseStudentDetails"; 
	public static final String PAGE_COORD_COURSE_STUDENT_EDIT = "/page/coordCourseStudentEdit"; 
	public static final String PAGE_COORD_COURSE_STUDENT_DELETE = "/page/coordCourseStudentDelete"; 
	public static final String PAGE_COORD_COURSE_ENROLL = "/page/coordCourseEnroll"; 
	public static final String PAGE_COORD_COURSE_REMIND = "/page/coordCourseRemind"; 
	public static final String PAGE_COORD_EVAL = "/page/coordEval"; 
	public static final String PAGE_COORD_EVAL_DELETE = "/page/coordEvalDelete"; 
	public static final String PAGE_COORD_EVAL_EDIT = "/page/coordEvalEdit"; 
	public static final String PAGE_COORD_EVAL_RESULTS = "/page/coordEvalResults"; 
	public static final String PAGE_COORD_EVAL_SUBMISSION_VIEW = "/page/coordEvalSubmissionView"; 
	public static final String PAGE_COORD_EVAL_SUBMISSION_EDIT = "/page/coordEvalSubmissionEdit"; 
	public static final String PAGE_COORD_EVAL_SUBMISSION_EDIT_HANDLER = "/page/coordEvalSubmissionEditHandler"; 
	public static final String PAGE_COORD_EVAL_REMIND = "/page/coordEvalRemind"; 
	public static final String PAGE_COORD_EVAL_PUBLISH = "/page/coordEvalPublish"; 
	public static final String PAGE_COORD_EVAL_UNPUBLISH = "/page/coordEvalUnpublish"; 

	public static final String PAGE_STUDENT_HOME = "/page/studentHome"; 
	public static final String PAGE_STUDENT_JOIN_COURSE = "/page/studentCourseJoin"; 
	public static final String PAGE_STUDENT_COURSE_DETAILS = "/page/studentCourseDetails"; 
	/** To submit evaluation and also to edit */
	public static final String PAGE_STUDENT_EVAL_SUBMISSION_EDIT = "/page/studentEvalEdit"; 
	public static final String PAGE_STUDENT_EVAL_SUBMISSION_EDIT_HANDLER = "/page/studentEvalEditHandler"; 
	public static final String PAGE_STUDENT_EVAL_RESULTS = "/page/studentEvalResults"; 
	
	public static final String PAGE_ADMINISTRATOR_HOME = "/page/administrator"; 
	public static final String PAGE_LOGIN = "/login"; 

	/*
	 * JSP pages links. These links are here to provide ease of 
	 * moving the JSP folder or renaming.
	 */
	public static final String JSP_COORD_HOME = "/jsp/coordHome.jsp"; // Done
	public static final String JSP_COORD_COURSE = "/jsp/coordCourse.jsp"; // Done
	public static final String JSP_COORD_COURSE_DETAILS = "/jsp/coordCourseDetails.jsp"; // Done
	public static final String JSP_COORD_COURSE_STUDENT_DETAILS = "/jsp/coordCourseStudentDetails.jsp"; // Done
	public static final String JSP_COORD_COURSE_STUDENT_EDIT = "/jsp/coordCourseStudentEdit.jsp"; // Done
	public static final String JSP_COORD_COURSE_ENROLL = "/jsp/coordCourseEnroll.jsp"; // Done
	public static final String JSP_COORD_TFS = "/jsp/coordTFS.jsp"; // Pending
	public static final String JSP_COORD_TFS_MANAGE = "/jsp/coordTFSManage.jsp"; // Pending
	public static final String JSP_COORD_TFS_CHANGE_TEAM = "/jsp/coordTFSChangeTeam.jsp"; // Pending
	public static final String JSP_COORD_TFS_LOGS = "/jsp/coordTFSLogs.jsp"; // Pending
	public static final String JSP_COORD_EVAL = "/jsp/coordEval.jsp"; // Done
	public static final String JSP_COORD_EVAL_EDIT = "/jsp/coordEvalEdit.jsp"; // Done
	public static final String JSP_COORD_EVAL_RESULTS = "/jsp/coordEvalResults.jsp"; // Done
	public static final String JSP_COORD_EVAL_SUBMISSION_VIEW = "/jsp/coordEvalSubmissionView.jsp"; // Done
	public static final String JSP_COORD_EVAL_SUBMISSION_EDIT = "/jsp/coordEvalSubmissionEdit.jsp"; // Done

	public static final String JSP_STUDENT_HOME = "/jsp/studentHome.jsp"; // Done
	public static final String JSP_STUDENT_COURSE_PROFILE = "/jsp/studentCourseProfile.jsp"; // Pending
	public static final String JSP_STUDENT_COURSE_DETAILS = "/jsp/studentCourseDetails.jsp"; // Done
	/** To submit evaluation and also to edit */
	public static final String JSP_STUDENT_EVAL_SUBMISSION_EDIT = "/jsp/studentEvalEdit.jsp"; // Done
	public static final String JSP_STUDENT_EVAL_RESULTS = "/jsp/studentEvalResults.jsp"; // Done
	
	public static final String JSP_COORD_HEADER = "/jsp/coordHeader.jsp"; // Done
	public static final String JSP_STUDENT_HEADER = "/jsp/studentHeader.jsp"; // Done
	public static final String JSP_FOOTER = "/jsp/footer.jsp"; // Done
	public static final String JSP_STATUS_MESSAGE = "/jsp/statusMessage.jsp"; // Done
	public static final String JSP_EVAL_SUBMISSION_EDIT = "/jsp/evalSubmissionEdit.jsp"; // Done

	public static final String JSP_ADMINISTRATOR_HOME = "/jsp/administratorHome.jsp";
	public static final String JSP_LOGOUT = "/logout.jsp"; // Done
	public static final String JSP_SHOW_MESSAGE = "/showMessage.jsp"; // Done
	public static final String JSP_UNAUTHORIZED = "/unauthorized.jsp"; // Done
	public static final String JSP_ERROR_PAGE = "/errorPage.jsp"; // Done
	public static final String JSP_ENTITY_NOT_FOUND_PAGE = "/entityNotFoundPage.jsp"; // Done
	public static final String JSP_PAGE_NOT_FOUND_PAGE = "/pageNotFound.jsp"; // Done

	// data field sizes
	public static final int COURSE_NAME_MAX_LENGTH = 38;
	public static final int COURSE_ID_MAX_LENGTH = 21;
	public static final int EVALUATION_NAME_MAX_LENGTH = 38;
	public static final int STUDENT_NAME_MAX_LENGTH = 40;
	public static final int TEAM_NAME_MAX_LENGTH = 25;
	public static final int COMMENT_MAX_LENGTH = 500;
	
	// status messages
	public static final String MESSAGE_LOADING = "<img src=\"/images/ajax-loader.gif\" /><br />";
	public static final String MESSAGE_STUDENT_FIRST_TIME = "Welcome stranger :-) " +
			"<br/><br/>It seems you are not a registered user of TEAMMATES. To use TEAMMATES, a course coordinator has to add you to a course first. " +
			"After that, TEAMMATES will send you an email containing the link to 'join' that course. " +
			"<br/><br/>If you already clicked on such a link and ended up here, it is likely that your email software messed up the link. Please retry to join by filling in the above box the registration key given in that same e-mail." +
			"If you still cannot join, feel free to <a href='http://www.comp.nus.edu.sg/~teams/contact.html'>contact us</a> for help. ";
	
	public static final String MESSAGE_COURSE_ADDED = "The course has been added. Click the 'Enroll' link in the table below to add students to the course.";
	public static final String MESSAGE_COURSE_EXISTS = "A course by the same ID already exists in the system, possibly created by another user. Please choose a different course ID";
	public static final String MESSAGE_COURSE_DELETED = "The course has been deleted.";
	public static final String MESSAGE_COURSE_EMPTY = "You have not created any courses yet. Use the form above to create a course.";
	public static final String MESSAGE_COURSE_EMPTY_IN_EVALUATION = "You have not created any courses yet. Go <a href=\""+PAGE_COORD_COURSE+"${user}\">here</a> to create one.";
	public static final String MESSAGE_COURSE_REMINDER_SENT_TO = "Registration key has been sent to ";
	public static final String MESSAGE_COURSE_REMINDERS_SENT = "Emails have been sent to unregistered students.";
	
	public static final String MESSAGE_STUDENT_EDITED = "The student has been edited successfully";
	public static final String MESSAGE_STUDENT_DELETED = "The student has been removed from the course";

	public static final String MESSAGE_EVALUATION_ADDED = "The evaluation has been added.";
	public static final String MESSAGE_EVALUATION_DELETED = "The evaluation has been deleted.";
	public static final String MESSAGE_EVALUATION_EDITED = "The evaluation has been edited.";
	public static final String MESSAGE_EVALUATION_INFORMEDSTUDENTSOFCHANGES = "E-mails have been sent out to inform the students of the changes to the evaluation.";
	public static final String MESSAGE_EVALUATION_PUBLISHED = "The evaluation has been published.";
	public static final String MESSAGE_EVALUATION_UNPUBLISHED = "The evaluation has been unpublished.";
	public static final String MESSAGE_EVALUATION_REMINDERSSENT = "Reminder e-mails have been sent out to those students.";
	public static final String MESSAGE_EVALUATION_RESULTSEDITED = "The particular evaluation results have been edited.";
	public static final String MESSAGE_EVALUATION_EMPTY = "You have not created any evaluations yet. Use the form above to create a new evaluation.";

	public static final String MESSAGE_EVALUATION_EXISTS = "An evaluation by this name already exists under this course";
	// Status messages from Javascript
	public static final String MESSAGE_COURSE_MISSING_FIELD = "Course ID and Course Name are compulsory fields.";
	public static final String MESSAGE_COURSE_LONG_ID = "Course ID should not exceed " + COURSE_ID_MAX_LENGTH + " characters.";
	public static final String MESSAGE_COURSE_LONG_NAME = "Course name should not exceed " + COURSE_NAME_MAX_LENGTH + " characters.";
	public static final String MESSAGE_COURSE_INVALID_ID = "Please use only alphabets, numbers, dots, hyphens, underscores and dollars in course ID.";
	public static final String MESSAGE_EVALUATION_NAMEINVALID = "Please use only alphabets, numbers and whitespace in evaluation name.";
	public static final String MESSAGE_EVALUATION_NAME_LENGTHINVALID = "Evaluation name should not exceed 38 characters.";
	public static final String MESSAGE_EVALUATION_SCHEDULEINVALID = "The evaluation schedule (start/deadline) is not valid.<br />" +
											 "The start time should be in the future, and the deadline should be after start time.";
	public static final String MESSAGE_FIELDS_EMPTY = "Please fill in all the relevant fields.";

	// Messages that are templates only
	/** Template String. Parameters: Student's name, Evaluation name, Course ID */
	public static final String MESSAGE_COORD_EVALUATION_SUBMISSION_RECEIVED = "You have edited %s's submission for evaluation %s in course %s successfully<br />" +
																			"The change will not be reflected here until you refresh the page.";
	/** Template String. Parameters: Evaluation name, Course ID */
	public static final String MESSAGE_STUDENT_EVALUATION_SUBMISSION_RECEIVED = "Your submission for %s in course %s has been saved successfully";

	// DIV tags for HTML testing
	public static final String HEADER_TAG = "<div id=\"frameTop\">";
	public static final String FOOTER_TAG = "<div id=\"frameBottom\">";

	// TeammatesServlet responses
	public static final String COORD_ADD_COURSE_RESPONSE_ADDED = "<status>course added</status>";
	public static final String COORD_ADD_COURSE_RESPONSE_EXISTS = "<status>course exists</status>";
	public static final String COORD_ADD_COURSE_RESPONSE_INVALID = "<status>course input invalid</status>";
	public static final String COORD_DELETE_COURSE_RESPONSE_DELETED = "<status>course deleted</status>";
	public static final String COORD_DELETE_COURSE_RESPONSE_NOT_DELETED = "<status>course not deleted</status>";

	// APIServlet responses
	public static final String BACKEND_STATUS_SUCCESS = "[BACKEND_STATUS_SUCCESS]";
	public static String BACKEND_STATUS_FAILURE = "[BACKEND_STATUS_FAILURE]";

	// General Error codes
	public static final String ERRORCODE_ACTIVATED_BEFORE_START = "ERRORCODE_ACTIVATED_BEFORE_START";
	public static final String ERRORCODE_ALREADY_JOINED = "ERRORCODE_ALREADY_JOINED";
	public static final String ERRORCODE_EMPTY_STRING = "ERRORCODE_EMPTY_STRING";
	public static final String ERRORCODE_END_BEFORE_START = "ERRORCODE_END_BEFORE_START";
	public static final String ERRORCODE_NULL_PARAMETER = "ERRORCODE_NULL_PARAMETER";
	public static final String ERRORCODE_INCORRECTLY_FORMATTED_STRING = "ERRORCODE_INCORRECTLY_FORMATTED_STRING";
	public static final String ERRORCODE_INVALID_CHARS = "ERRORCODE_IVALID_CHARS";
	public static final String ERRORCODE_INVALID_EMAIL = "ERRORCODE_INVALID_EMAIL";
	public static final String ERRORCODE_INVALID_KEY = "ERRORCODE_INVALID_KEY";
	public static final String ERRORCODE_KEY_BELONGS_TO_DIFFERENT_USER = "ERRORCODE_KEY_BELONGS_TO_DIFFERENT_USER";
	public static final String ERRORCODE_LEADING_OR_TRAILING_SPACES = "ERRORCODE_LEADING_OR_TRAILING_SPACES";
	public static final String ERRORCODE_PUBLISHED_BEFORE_CLOSING = "ERRORCODE_PUBLISHED_BEFORE_CLOSING";
	public static final String ERRORCODE_STRING_TOO_LONG = "ERRORCODE_STRING_TOO_LONG";


	@SuppressWarnings("unused")
	private void ____VALIDATE_parameters___________________________________() {
	}

	// TODO: add more checks and write unit tests
	public static void validateTeamName(String teamName)
			throws InvalidParametersException {
		if (teamName == null) {
			return;
		}
		if (teamName.length() > TEAM_NAME_MAX_LENGTH) {
			throw new InvalidParametersException(ERRORCODE_STRING_TOO_LONG,
					"Team name cannot be longer than " + TEAM_NAME_MAX_LENGTH);
		}
	}

	// TODO: add more checks and write unit tests
	public static void validateStudentName(String studentName)
			throws InvalidParametersException {
		if (!studentName.trim().equals(studentName)) {
			throw new InvalidParametersException(
					ERRORCODE_INCORRECTLY_FORMATTED_STRING,
					"Student name should not have leading or trailing spaces");
		}
		if (studentName.equals("")) {
			throw new InvalidParametersException(ERRORCODE_EMPTY_STRING,
					"Student name should not be empty");
		}
		if (studentName.length() > STUDENT_NAME_MAX_LENGTH) {
			throw new InvalidParametersException(ERRORCODE_STRING_TOO_LONG,
					"Student name cannot be longer than "
							+ STUDENT_NAME_MAX_LENGTH);
		}
	}

	// TODO: add more checks and write unit tests
	public static void validateEmail(String email)
			throws InvalidParametersException {
		verifyNotNull(email, "email");
		verifyNoLeadingAndTrailingSpaces(email, "email");
		verifyNotAnEmptyString(email, "email");
		if (!email.contains("@")) {
			throw new InvalidParametersException(ERRORCODE_INVALID_EMAIL,
					"Email address should contain '@'");
		}

	}

	// TODO: add more checks and write unit tests
	public static void validateGoogleId(String googleId)
			throws InvalidParametersException {
		verifyNotNull(googleId, "Google ID");
		verifyNoLeadingAndTrailingSpaces(googleId, "Google ID");
		verifyNotAnEmptyString(googleId, "Google ID");
		verifyContainsNoSpaces(googleId, "Google ID");

	}

	// TODO: add more checks and write unit tests
	public static void validateCoordName(String coordName)
			throws InvalidParametersException {
		verifyNoLeadingAndTrailingSpaces(coordName, "Coordinator name");
		verifyNotAnEmptyString(coordName, "Coordinator name");
	}

	// TODO: add more checks and write unit tests
	public static void validateCourseId(String courseId)
			throws InvalidParametersException {
		verifyContainsNoSpaces(courseId, "Course ID");

	}

	// TODO: add more checks and write unit tests
	public static void validateCourseName(String stringToCheck)
			throws InvalidParametersException {
		verifyNoLeadingAndTrailingSpaces(stringToCheck, "Course name");
		verifyNotAnEmptyString(stringToCheck, "Course name");
	}

	// TODO: add more checks and write unit tests
	public static void validateComment(String comment)
			throws InvalidParametersException {
		if (comment == null) {
			return;
		}
		if (comment.length() > COMMENT_MAX_LENGTH) {
			throw new InvalidParametersException(ERRORCODE_STRING_TOO_LONG,
					"Comment cannot be longer than " + STUDENT_NAME_MAX_LENGTH);
		}
	}

	public static void verifyContainsNoSpaces(String stringToCheck,
			String nameOfString) throws InvalidParametersException {
		if (stringToCheck.split(" ").length > 1) {
			throw new InvalidParametersException(ERRORCODE_INVALID_CHARS,
					nameOfString + " cannot contain spaces");
		}
	}

	public static void verifyNotNull(Object objectToCheck, String nameOfObject){
		if (objectToCheck == null) {
			throw new NullPointerException(nameOfObject + " cannot be null");
		}
	}

	public static void verifyNotAnEmptyString(String stringToCheck,
			String nameOfString) throws InvalidParametersException {
		if (stringToCheck.equals("")) {
			throw new InvalidParametersException(ERRORCODE_EMPTY_STRING,
					nameOfString + " should not be empty");
		}
	}

	public static void verifyNoLeadingAndTrailingSpaces(String stringToCheck,
			String nameOfString) throws InvalidParametersException {
		if (!stringToCheck.trim().equals(stringToCheck)) {
			throw new InvalidParametersException(
					ERRORCODE_LEADING_OR_TRAILING_SPACES, nameOfString
							+ " should not have leading or trailing spaces");
		}
	}

	@SuppressWarnings("unused")
	private void ____MISC_utility_methods___________________________________() {
	}

	/**
	 * This creates a Gson object that can handle the Date format we use in the
	 * Json file technique found in
	 * http://code.google.com/p/google-gson/source/browse
	 * /trunk/gson/src/test/java
	 * /com/google/gson/functional/DefaultTypeAdaptersTest
	 * .java?spec=svn327&r=327
	 */
	public static Gson getTeammatesGson() {
		return new GsonBuilder().setDateFormat(DateFormat.FULL)
				.setDateFormat("yyyy-MM-dd h:mm a").setPrettyPrinting()
				.create();
	}

	public static void println(String message) {
		log.fine(String.format("[%d - %s] %s", Thread.currentThread().getId(),
				Thread.currentThread().getName(), message));
	}

	/**
	 * Convert a date string and time into a Date object. Returns null on error.
	 * 
	 * @param date
	 *            The date in format dd/MM/yyyy
	 * @param time
	 *            The time in format HHMM
	 * @return
	 */
	public static Date convertToDate(String date, int time) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();

		Date newDate = new Date();

		// Perform date manipulation
		try {
			newDate = sdf.parse(date);
			calendar.setTime(newDate);

			if (time == 2400) {
				calendar.set(Calendar.HOUR, 23);
				calendar.set(Calendar.MINUTE, 59);
			} else {
				calendar.set(Calendar.HOUR, time / 100);
				calendar.set(Calendar.MINUTE, time % 100);
			}

			return calendar.getTime();
		} catch (Exception e) {
			return null;
		}

	}
	/**
	 * Returns the date object with specified offset in number of days from now
	 * @param offsetDays
	 * @return
	 */
	public static Date getDateOffsetToCurrentTime(int offsetDays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(cal.getTime());
		cal.add(Calendar.DATE, +offsetDays);
		return cal.getTime();
	}

	/**
	 * Returns the date object with specified offset in number of ms from now
	 * @param offsetMilliseconds
	 * @return
	 */
	public static Date getMsOffsetToCurrentTime(int offsetMilliseconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(cal.getTime());
		cal.add(Calendar.MILLISECOND, +offsetMilliseconds);
		return cal.getTime();
	}
	
	public static Date getMsOffsetToCurrentTimeInUserTimeZone(int offset, double timeZone) {
		Date d = Common.getMsOffsetToCurrentTime(offset);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return Evaluation.convertToUserTimeZone(c,timeZone).getTime();
	}
	
	/**
	 * Returns the date object representing the next full hour from now.
	 * Example: If now is 1055, this will return 1100
	 * @return
	 */
	public static Date getNextHour() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 0);
		return cal.getTime();
	}
	
	/**
	 * Formats a date in the format HH for evaluation.
	 * This will return "24" for time 2359
	 * @param date
	 * @return
	 */
	public static String formatTimeForEvaluation(Date date) {
		if(new SimpleDateFormat("mm").format(date).equals("59")) return "24";
		return new SimpleDateFormat("HH").format(date);
	}

	/**
	 * Formats a date in the format dd/MM/yyyy
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		if(date==null) return "";
		return new SimpleDateFormat("dd/MM/yyyy").format(date);
	}

	/**
	 * Formats a date in the format dd MMM yyyy, hh:mm.
	 * Example: 05 May 2012, 22:04<br />
	 * This is used in JSP pages to display time information to users
	 * @param date
	 * @return
	 */
	public static String formatTime(Date date){
		if(date==null) return "";
		return new SimpleDateFormat("dd MMM yyyy, hh:mm").format(date);
	}

	public static String calendarToString(Calendar c) {
		if(c==null) return "";
		return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss SSS")
				.format(c.getTime());
	}
	
	public static Calendar dateToCalendar(Date date) {
		Calendar c = Calendar.getInstance();
		if(date==null) return c;
		c.setTime(date);
		return c;
	}

	/**
	 * Read a file content and return a String
	 * 
	 * @param filename
	 * @return
	 */
	public static String readFile(String filename) throws FileNotFoundException {
		String ans = new Scanner(new FileReader(filename)).useDelimiter("\\Z")
				.next();
		return ans;
	}

	/**
	 * Reads from a stream and returns the string
	 * 
	 * @param reader
	 * @return
	 */
	public static String readStream(InputStream stream) {
		return new Scanner(stream).useDelimiter("\\Z").next();
	}

	public static boolean isWhiteSpace(String string) {
		return string.trim().isEmpty();
	}

	public static String generateStringOfLength(int length) {
		return generateStringOfLength(length, 'a');
	}
	
	public static String getIndent(int length) {
		return generateStringOfLength(length, ' ');
	}

	
	public static String generateStringOfLength(int length, char character) {
		assert (length >= 0);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(character);
		}
		return sb.toString();
	}

	public static Logger getLogger() {
		return Logger.getLogger(Thread.currentThread().getStackTrace()[2]
				.getClassName());
	}

	@SuppressWarnings("unused")
	private void ____PRIVATE_helper_methods_________________________________() {
	}

}
