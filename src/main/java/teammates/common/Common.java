package teammates.common;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import teammates.ui.controller.Helper;

import com.google.appengine.api.utils.SystemProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Stores variables and methods that are widely used across classes
 */

// TODO: create a subclass (e.g., InternalUtil) and move all internal utility
// functions to that sub class. It should be in util package.
public class Common {
	
	private static Logger log = Logger.getLogger(Common.class.getName());

	public static final String EOL = System.getProperty("line.separator");
	public static final int UNINITIALIZED_INT = -9999;
	public static final double UNINITIALIZED_DOUBLE = -9999.0;
	public static final String ENCODING = "UTF8";
	// TODO: get this value from a config file
	public static final String TEST_DATA_FOLDER = "src/test/resources/data";
	public static final String TEST_PAGES_FOLDER = "src/test/resources/pages";

	public static final int MAX_POSSIBLE_RECIPIENTS = -100;
	public static final int POINTS_NOT_SURE = -101;
	public static final int POINTS_NOT_SUBMITTED = -999;

	public static final int NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT = 24;

	// Number to trigger the header file to truncate the user googleId and show hover message
	public static final int USER_ID_MAX_DISPLAY_LENGTH = 23;
	
	public static Date TIME_REPRESENTS_FOLLOW_OPENING;
	public static Date TIME_REPRESENTS_FOLLOW_VISIBLE;
	public static Date TIME_REPRESENTS_NEVER;
	public static Date TIME_REPRESENTS_LATER;

	static {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(1970, 11, 31);
		TIME_REPRESENTS_FOLLOW_OPENING = calendar.getTime();
		calendar.set(1970, 05, 22);
		TIME_REPRESENTS_FOLLOW_VISIBLE = calendar.getTime();
		calendar.set(1970, 10, 27);
		TIME_REPRESENTS_NEVER = calendar.getTime();
		calendar.set(1970, 00, 01);
		TIME_REPRESENTS_LATER = calendar.getTime();
	}
	
	public static final String TEAM_OF_EMAIL_OWNER = "'s Team";
	
	// Hover messages
	
	public static final String HOVER_MESSAGE_COURSE_ENROLL = "Enroll student into the course";
	public static final String HOVER_MESSAGE_COURSE_ENROLL_SAMPLE_SPREADSHEET = "Download a sample team data spreadsheet";
	public static final String HOVER_MESSAGE_COURSE_DETAILS = "View, edit and send registration keys to the students in the course";
	public static final String HOVER_MESSAGE_COURSE_EDIT = "Edit Course information and instructor list";
	public static final String HOVER_MESSAGE_COURSE_DELETE = "Delete the course and its corresponding students and evaluations";
	public static final String HOVER_MESSAGE_COURSE_ADD_EVALUATION = "Add an evaluation for the course";
	public static final String HOVER_MESSAGE_CLAIMED = "This is student own estimation of his/her contributions to the project";
	public static final String HOVER_MESSAGE_PERCEIVED = "This is the average of what other team members think this student contributed to the project";
	public static final String HOVER_MESSAGE_PERCEIVED_CLAIMED = "Difference between claimed and perceived contribution points";

	public static final String HOVER_MESSAGE_COURSE_STUDENT_DETAILS = "View the details of the student";
	public static final String HOVER_MESSAGE_COURSE_STUDENT_EDIT = "Use this to edit the details of this student. <br>To edit multiple students in one go, you can use the enroll page: <br>Simply enroll students using the updated data and existing data will be updated accordingly";
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
	public static final String HOVER_MESSAGE_EVALUATION_POINTS_RECEIVED = "The list of points that this student received from others";

	public static final String HOVER_MESSAGE_EVALUATION_INPUT_COURSE = "Please select the course for which the evaluation is to be created.";
	public static final String HOVER_MESSAGE_EVALUATION_INPUT_START = "Please enter the start date for the evaluation.";
	public static final String HOVER_MESSAGE_EVALUATION_INPUT_NAME = "Enter the name of the evaluation e.g. Mid-term Evaluation 1.";
	public static final String HOVER_MESSAGE_EVALUATION_INPUT_DEADLINE = "Please enter deadline for the evaluation.";
	public static final String HOVER_MESSAGE_EVALUATION_INPUT_COMMENTSSTATUS = "Enable this if you want students to give anonymous feedback to team members.<br />"
			+ "You can moderate those peer feedback before publishing it to the team.";
	public static final String HOVER_MESSAGE_EVALUATION_INPUT_TIMEZONE = "Daylight saving is not taken into account i.e. if you are in UTC -8:00 and there is<br />"
			+ "daylight saving, you should choose UTC -7:00 and its corresponding timings.";
	public static final String HOVER_MESSAGE_EVALUATION_INPUT_GRACEPERIOD = "Please select the amount of time that the system will continue accepting <br />"
			+ "submissions after the specified deadline.";
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
	public static final String HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_CLOSED = "The evaluation has finished but the instructor has not published the results yet";
	public static final String HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_PUBLISHED = "The evaluation has finished and you can check the results";
	public static final String HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_ERROR = "There were some errors in retrieving this evaluation.";

	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_COURSE = "Please select the course for which the feedback session is to be created.";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_INPUT_NAME = "Enter the name of the feedback session e.g. Feedback Session 1.";	
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_STARTDATE = "Please select the starting date and time for the feedback session. Users can only start responding after this time.";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_ENDDATE = "Please select the closing date and time for the feedback session.";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_VISIBLEDATE = "Please select the date and time for which the feedback session will become visible to users who need to participate in the session.";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_PUBLISHDATE = "Please select the date and time for which the results of the feedback session will become visible.";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_SESSIONVISIBLECUSTOM = "Select this option to use a custom time for when the session will be visible to users. You can make a session visible before it is open for users to start responding.";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_SESSIONVISIBLEATOPEN = "Select this option to have the session become visible at the opening time.";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_SESSIONVISIBLENEVER = "Select this option if you want the feedback session to never be visible. Use this option you want to use this as a private feedback session.";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_RESULTSVISIBLECUSTOM = "Select this option to use a custom time for when the responses of the session will be visible to users.";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_RESULTSVISIBLEATVISIBLE = "Select this option to have the feedback responses be immediately visible when the session is open.";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_RESULTSVISIBLELATER = "Select this option if you intend to make the results visible at a later time.";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_RESULTSVISIBLENEVER = "Select this option if you intend to never publish the results.";
	
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_STATUS_VISIBLE = ", is visible";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_STATUS_AWAITING = ", and is waiting to open";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_STATUS_OPEN = ", and is open for submissions";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_STATUS_CLOSED = ", and has ended";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_STATUS_PUBLISHED = ". The responses for this session are visible";
	
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_RESULTS = "View the current submitted responses of the feedback session";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_EDIT = "Edit feedback session details and questions";
	public static final String HOVER_MESSAGE_FEEDBACK_SESSION_DELETE = "Delete the feedback session";
	
	public static final String HOVER_MESSAGE_FEEDBACK_QUESTION_INPUT_INSTRUCTIONS = "Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?";
	
	public static final String HOVER_MESSAGE_FEEDBACK_SUBMIT_CLOSED = "You can view the questions for this feedback session but cannot submit responses yet as the session is not yet opened.";
	
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
	public static final String PARAM_COURSE_INSTRUCTOR_LIST = "instructorlist";
	public static final String PARAM_INSTRUCTOR_ID = "instructorid";
	public static final String PARAM_INSTRUCTOR_EMAIL = "instructoremail";
	public static final String PARAM_INSTRUCTOR_INSTITUTION = "instructorinstitution";
	public static final String PARAM_INSTRUCTOR_NAME = "instructorname";
	public static final String PARAM_STUDENTS_ENROLLMENT_INFO = "enrollstudents";
	public static final String PARAM_INSTRUCTOR_IMPORT_SAMPLE = "importsample";

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
	
	public static final String PARAM_FEEDBACK_SESSION_NAME = "fsname";
	public static final String PARAM_FEEDBACK_SESSION_CREATOR = "fscreator";
	public static final String PARAM_FEEDBACK_SESSION_CREATEDATE = "createdate";
	public static final String PARAM_FEEDBACK_SESSION_CREATETIME = "createtime";
	public static final String PARAM_FEEDBACK_SESSION_STARTDATE = "startdate";
	public static final String PARAM_FEEDBACK_SESSION_STARTTIME = "starttime";
	public static final String PARAM_FEEDBACK_SESSION_ENDDATE = "enddate";
	public static final String PARAM_FEEDBACK_SESSION_ENDTIME = "endtime";
	public static final String PARAM_FEEDBACK_SESSION_VISIBLEDATE = "visibledate";
	public static final String PARAM_FEEDBACK_SESSION_VISIBLETIME = "visibletime";
	public static final String PARAM_FEEDBACK_SESSION_PUBLISHDATE = "publishdate";
	public static final String PARAM_FEEDBACK_SESSION_PUBLISHTIME = "publishtime";
	public static final String PARAM_FEEDBACK_SESSION_TIMEZONE = "timezone";
	public static final String PARAM_FEEDBACK_SESSION_GRACEPERIOD = "graceperiod";
	public static final String PARAM_FEEDBACK_SESSION_TYPE = "fstype";
	public static final String PARAM_FEEDBACK_SESSION_OPENEMAILSENT = "fsopenemailsent";
	public static final String PARAM_FEEDBACK_SESSION_PUBLISHEDEMAILSENT = "fspublishedemailsent";
	public static final String PARAM_FEEDBACK_SESSION_SESSIONVISIBLEBUTTON = "sessionVisibleFromButton";
	public static final String PARAM_FEEDBACK_SESSION_RESULTSVISIBLEBUTTON = "resultsVisibleFromButton";
	public static final String PARAM_FEEDBACK_SESSION_INSTRUCTIONS = "instructions";
	
	public static final String PARAM_FEEDBACK_QUESTION_ID = "questionid";
	public static final String PARAM_FEEDBACK_QUESTION_NUMBER = "questionnum";
	public static final String PARAM_FEEDBACK_QUESTION_TEXT = "questiontext";
	public static final String PARAM_FEEDBACK_QUESTION_TYPE = "questiontype";
	public static final String PARAM_FEEDBACK_QUESTION_GIVERTYPE = "givertype";
	public static final String PARAM_FEEDBACK_QUESTION_RECIPIENTTYPE = "recipienttype";
	public static final String PARAM_FEEDBACK_QUESTION_NUMBEROFENTITIES = "numofrecipients";
	public static final String PARAM_FEEDBACK_QUESTION_EDITTEXT = "questionedittext";
	public static final String PARAM_FEEDBACK_QUESTION_EDITTYPE = "questionedittype";
	public static final String PARAM_FEEDBACK_QUESTION_SAVECHANGESTEXT = "questionsavechangestext";
	public static final String PARAM_FEEDBACK_QUESTION_SHOWRESPONSESTO = "showresponsesto";
	public static final String PARAM_FEEDBACK_QUESTION_SHOWGIVERTO = "showgiverto";
	public static final String PARAM_FEEDBACK_QUESTION_SHOWRECIPIENTTO = "showrecipientto";

	public static final String PARAM_FEEDBACK_RESPONSE_ID = "responseid";
	public static final String PARAM_FEEDBACK_RESPONSE_RECIPIENT = "responserecipient";
	public static final String PARAM_FEEDBACK_RESPONSE_TEXT = "responsetext";
	public static final String PARAM_FEEDBACK_RESPONSE_TOTAL = "responsestotal";
	
	public static final String PARAM_FEEDBACK_RESULTS_UPLOADDOWNLOADBUTTON = "fruploaddownloadbtn";
	public static final String PARAM_FEEDBACK_RESULTS_SORTTYPE = "frsorttype";
	
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

	public static final String PARAM_LOGIN_ADMIN = "admin";
	public static final String PARAM_LOGIN_INSTRUCTOR = "instructor";
	public static final String PARAM_LOGIN_STUDENT = "student";

	/*
	 * Logical pages links. These are the links which should be accessed as the
	 * URL to get the real pages. The JSP pages in the next section are the
	 * pages to display, which should not be accessed directly. In order to
	 * cater with masquerade mode, it is highly encourage to use the helper
	 * methods get*Link which automatically includes masqueraded userId.
	 * Otherwise, you need to call helper.processMasquerade(link) yourself In
	 * forms, you need to include a hidden input field with parameter
	 * PARAM_USER_ID and value helper.requestedUser to support masquerade mode.
	 * It should only be shown if the session is in masquerade mode. Below is
	 * the code to be put at the end of the form to include masquerade mode in
	 * forms. It is put at the end of the form so that it will be at the end of
	 * the URL. Just a convention.
	 * 
	 * <% if(helper.isMasqueradeMode()){ %> <input type="hidden"
	 * name="<%= Common.PARAM_USER_ID %>" value="<%= helper.requestedUser %>" />
	 * <% } %>
	 */

	public static final String PAGE_BACKDOOR = "/backdoor";
	public static final String PAGE_MASHUP = "/dev/mashup.jsp";
	public static final String PAGE_INSTRUCTOR_HOME = "/page/instructorHome";
	public static final String PAGE_INSTRUCTOR_COURSE = "/page/instructorCourse";
	public static final String PAGE_INSTRUCTOR_COURSE_ADD = "/page/instructorCourseAdd";
	public static final String PAGE_INSTRUCTOR_COURSE_DELETE = "/page/instructorCourseDelete";
	public static final String PAGE_INSTRUCTOR_COURSE_DETAILS = "/page/instructorCourseDetails";
	public static final String PAGE_INSTRUCTOR_COURSE_EDIT = "/page/instructorCourseEdit";
	public static final String PAGE_INSTRUCTOR_COURSE_EDIT_SAVE = "/page/instructorCourseEditSave";
	public static final String PAGE_INSTRUCTOR_COURSE_STUDENT_DETAILS = "/page/instructorCourseStudentDetails";
	public static final String PAGE_INSTRUCTOR_COURSE_STUDENT_EDIT = "/page/instructorCourseStudentEdit";
	public static final String PAGE_INSTRUCTOR_COURSE_STUDENT_DELETE = "/page/instructorCourseStudentDelete";
	public static final String PAGE_INSTRUCTOR_COURSE_ENROLL = "/page/instructorCourseEnroll";
	public static final String PAGE_INSTRUCTOR_COURSE_ENROLL_SAVE = "/page/instructorCourseEnrollSave";
	public static final String PAGE_INSTRUCTOR_COURSE_REMIND = "/page/instructorCourseRemind";
	public static final String PAGE_INSTRUCTOR_EVAL = "/page/instructorEval";
	public static final String PAGE_INSTRUCTOR_EVAL_ADD = "/page/instructorEvalAdd";
	public static final String PAGE_INSTRUCTOR_EVAL_DELETE = "/page/instructorEvalDelete";
	public static final String PAGE_INSTRUCTOR_EVAL_EDIT = "/page/instructorEvalEdit";
	public static final String PAGE_INSTRUCTOR_EVAL_RESULTS = "/page/instructorEvalResults";
	public static final String PAGE_INSTRUCTOR_EVAL_SUBMISSION_VIEW = "/page/instructorEvalSubmissionView";
	public static final String PAGE_INSTRUCTOR_EVAL_SUBMISSION_EDIT = "/page/instructorEvalSubmissionEdit";
	public static final String PAGE_INSTRUCTOR_EVAL_SUBMISSION_EDIT_HANDLER = "/page/instructorEvalSubmissionEditHandler";
	public static final String PAGE_INSTRUCTOR_EVAL_REMIND = "/page/instructorEvalRemind";
	public static final String PAGE_INSTRUCTOR_EVAL_PUBLISH = "/page/instructorEvalPublish";
	public static final String PAGE_INSTRUCTOR_EVAL_UNPUBLISH = "/page/instructorEvalUnpublish";
	public static final String PAGE_INSTRUCTOR_EVAL_EXPORT = "/page/instructorEvalExport";
	
	public static final String PAGE_INSTRUCTOR_FEEDBACK = "/page/instructorFeedback";
	public static final String PAGE_INSTRUCTOR_FEEDBACK_ADD = "/page/instructorFeedbackAdd";
	public static final String PAGE_INSTRUCTOR_FEEDBACK_CHANGE_TYPE = "/page/instructorFeedbackChangeType"; 
	public static final String PAGE_INSTRUCTOR_FEEDBACK_DELETE = "/page/instructorFeedbackDelete";
	public static final String PAGE_INSTRUCTOR_FEEDBACK_EDIT = "/page/instructorFeedbackEdit";
	public static final String PAGE_INSTRUCTOR_FEEDBACK_EDIT_SAVE = "/page/instructorFeedbackEditSave";
	public static final String PAGE_INSTRUCTOR_FEEDBACK_RESULTS = "/page/instructorFeedbackResults";
	public static final String PAGE_INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD = "/page/instructorFeedbackResultsDownload";
	
	public static final String PAGE_INSTRUCTOR_FEEDBACK_QUESTION_ADD = "/page/instructorFeedbackQuestionAdd";
	public static final String PAGE_INSTRUCTOR_FEEDBACK_QUESTION_EDIT = "/page/instructorFeedbackQuestionEdit";

	
	public static final String PAGE_STUDENT_HOME = "/page/studentHome";
	public static final String PAGE_STUDENT_JOIN_COURSE = "/page/studentCourseJoin";
	public static final String PAGE_STUDENT_COURSE_DETAILS = "/page/studentCourseDetails";
	/** To submit evaluation and also to edit */
	public static final String PAGE_STUDENT_EVAL_SUBMISSION_EDIT = "/page/studentEvalEdit";
	public static final String PAGE_STUDENT_EVAL_SUBMISSION_EDIT_HANDLER = "/page/studentEvalEditHandler";
	public static final String PAGE_STUDENT_EVAL_RESULTS = "/page/studentEvalResults";
	public static final String PAGE_STUDENT_FEEDBACK_SUBMIT = "/page/studentFeedbackSubmit";
	public static final String PAGE_STUDENT_FEEDBACK_SUBMIT_SAVE = "/page/studentFeedbackSubmitSave";

	public static final String PAGE_ADMIN_HOME = "/admin/adminHome";
	public static final String PAGE_ADMIN_ACCOUNT_MANAGEMENT = "/admin/adminAccountManagement";
	public static final String PAGE_ADMIN_ACCOUNT_DETAILS = "/admin/adminAccountDetails";
	public static final String PAGE_ADMIN_ACCOUNT_DELETE = "/admin/adminAccountDelete";
	public static final String PAGE_ADMIN_EXCEPTION_TEST = "/admin/adminExceptionTest";
	public static final String PAGE_ADMIN_ACTIVITY_LOG = "/admin/adminActivityLog";
	public static final String PAGE_ADMIN_SEARCH = "/admin/adminSearch";
	public static final String PAGE_LOGIN = "/login";

	/*
	 * JSP pages links. These links are here to provide ease of moving the JSP
	 * folder or renaming.
	 */
	public static final String JSP_INSTRUCTOR_HOME = "/jsp/instructorHome.jsp"; 
	public static final String JSP_INSTRUCTOR_COURSE = "/jsp/instructorCourse.jsp"; 
	public static final String JSP_INSTRUCTOR_COURSE_EDIT = "/jsp/instructorCourseEdit.jsp"; 
	public static final String JSP_INSTRUCTOR_COURSE_DETAILS = "/jsp/instructorCourseDetails.jsp"; 
	public static final String JSP_INSTRUCTOR_COURSE_STUDENT_DETAILS = "/jsp/instructorCourseStudentDetails.jsp"; 
	public static final String JSP_INSTRUCTOR_COURSE_STUDENT_EDIT = "/jsp/instructorCourseStudentEdit.jsp"; 
	public static final String JSP_INSTRUCTOR_COURSE_ENROLL = "/jsp/instructorCourseEnroll.jsp"; 
	public static final String JSP_INSTRUCTOR_COURSE_ENROLL_RESULT = "/jsp/instructorCourseEnrollResult.jsp"; 
	public static final String JSP_INSTRUCTOR_EVAL = "/jsp/instructorEval.jsp"; 
	public static final String JSP_INSTRUCTOR_EVAL_EDIT = "/jsp/instructorEvalEdit.jsp"; 
	public static final String JSP_INSTRUCTOR_EVAL_RESULTS = "/jsp/instructorEvalResults.jsp"; 
	public static final String JSP_INSTRUCTOR_EVAL_SUBMISSION_VIEW = "/jsp/instructorEvalSubmissionView.jsp"; 
	public static final String JSP_INSTRUCTOR_EVAL_SUBMISSION_EDIT = "/jsp/instructorEvalSubmissionEdit.jsp"; 
	public static final String JSP_INSTRUCTOR_FEEDBACK = "/jsp/instructorFeedback.jsp";
	public static final String JSP_INSTRUCTOR_FEEDBACK_EDIT = "/jsp/instructorFeedbackEdit.jsp";
	public static final String JSP_INSTRUCTOR_FEEDBACK_RESULTS_TOP = "/jsp/instructorFeedbackResultsTop.jsp";
	public static final String JSP_INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER = "/jsp/instructorFeedbackResultsByGiver.jsp";
	public static final String JSP_INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT = "/jsp/instructorFeedbackResultsByRecipient.jsp"; 
	public static final String JSP_INSTRUCTOR_FEEDBACK_RESULTS_BY_TABLE = "/jsp/instructorFeedbackResultsByTable.jsp"; 

	public static final String JSP_STUDENT_HOME = "/jsp/studentHome.jsp"; 
	public static final String JSP_STUDENT_COURSE_DETAILS = "/jsp/studentCourseDetails.jsp"; 
	/** To submit evaluation and also to edit */
	public static final String JSP_STUDENT_EVAL_SUBMISSION_EDIT = "/jsp/studentEvalEdit.jsp"; 
	public static final String JSP_STUDENT_EVAL_RESULTS = "/jsp/studentEvalResults.jsp"; 
	public static final String JSP_STUDENT_FEEDBACK_SUBMIT = "/jsp/studentFeedbackSubmit.jsp"; 
	public static final String JSP_STUDENT_FEEDBACK_RESULTS = "/jsp/studentFeedbackResults.jsp"; 

	public static final String JSP_INSTRUCTOR_HEADER = "/jsp/instructorHeader.jsp"; 
	public static final String JSP_INSTRUCTOR_HEADER_NEW = "/jsp/instructorHeaderNew.jsp"; //TODO: rename this after all pages are migrated to new header
	public static final String JSP_STUDENT_HEADER = "/jsp/studentHeader.jsp";
	public static final String JSP_STUDENT_HEADER_NEW = "/jsp/studentHeaderNew.jsp"; 
	public static final String JSP_ADMIN_HEADER = "/jsp/adminHeader.jsp"; 
	public static final String JSP_FOOTER = "/jsp/footer.jsp"; 
	public static final String JSP_FOOTER_NEW = "/jsp/footerNew.jsp"; //TODO: rename this after all pages are migrated to new footer
	public static final String JSP_STATUS_MESSAGE = "/jsp/statusMessage.jsp"; 
	public static final String JSP_STATUS_MESSAGE_NEW = "/jsp/statusMessageNew.jsp"; //TODO: rename this after all pages are migrated to new status message
	public static final String JSP_EVAL_SUBMISSION_EDIT = "/jsp/evalSubmissionEdit.jsp"; 

	public static final String JSP_ADMIN_HOME = "/jsp/adminHome.jsp";
	public static final String JSP_ADMIN_ACCOUNT_MANAGEMENT = "/jsp/adminAccountManagement.jsp";
	public static final String JSP_ADMIN_SEARCH = "/jsp/adminSearch.jsp";
	public static final String JSP_ADMIN_ACTIVITY_LOG = "/jsp/adminActivityLog.jsp";
	public static final String JSP_ADMIN_ACCOUNT_DETAILS = "/jsp/adminAccountDetails.jsp";
	public static final String JSP_LOGOUT = "/logout.jsp"; 
	public static final String JSP_SHOW_MESSAGE = "/showMessage.jsp"; 
	public static final String JSP_UNAUTHORIZED = "/unauthorized.jsp"; 
	public static final String JSP_ERROR_PAGE = "/errorPage.jsp"; 
	public static final String JSP_DEADLINE_EXCEEDED_ERROR_PAGE = "/deadlineExceededErrorPage.jsp"; 
	public static final String JSP_ENTITY_NOT_FOUND_PAGE = "/entityNotFoundPage.jsp"; 
	public static final String JSP_PAGE_NOT_FOUND_PAGE = "/pageNotFound.jsp"; 

	// status messages
	public static final String MESSAGE_LOADING = "<img src=\"/images/ajax-loader.gif\" /><br />";
	public static final String MESSAGE_STUDENT_FIRST_TIME = "Welcome stranger :-) "
			+ "<br/><br/>It seems you are not a registered user of TEAMMATES. To use TEAMMATES, a course instructor has to add you to a course first. "
			+ "After that, TEAMMATES will send you an email containing the link to 'join' that course. "
			+ "<br/><br/>If you already clicked on such a link and ended up here, it is likely that your email software messed up the link. Please retry to join by filling in the above box the registration key given in that same e-mail."
			+ "If you still cannot join, feel free to <a href='http://www.comp.nus.edu.sg/%7Eteams/contact.html'>contact us</a> for help. "
			+ "<br/><br/>Not a stranger to TEAMMATES? Could log in before, but not any more? That can happen if you changed the primary email from a non-Gmail address to a Gmail address recently. " 
			+ "<br/>In that case, <a href='http://www.comp.nus.edu.sg/%7Eteams/contact.html'>email us</a> so that we can reconfigure your account to use the new Gmail address. ";
	
	public static final String MESSAGE_COURSE_ADDED = "The course has been added. Click the 'Enroll' link in the table below to add students to the course. If you don't see the course in the list below, please refresh the page after a few moments.";
	public static final String MESSAGE_COURSE_EXISTS = "A course by the same ID already exists in the system, possibly created by another user. Please choose a different course ID";
	public static final String MESSAGE_COURSE_EDITED = "The course has been edited.";
	public static final String MESSAGE_COURSE_DELETED = "The course has been deleted.";
	public static final String MESSAGE_COURSE_EMPTY = "You have not created any courses yet. Use the form above to create a course.";
	public static final String MESSAGE_COURSE_EMPTY_IN_EVALUATION = "You have not created any courses yet. Go <a href=\""
			+ PAGE_INSTRUCTOR_COURSE + "${user}\">here</a> to create one.";
	public static final String MESSAGE_COURSE_REMINDER_SENT_TO = "Registration key has been sent to ";
	public static final String MESSAGE_COURSE_REMINDERS_SENT = "Emails have been sent to unregistered students.";

	public static final String MESSAGE_STUDENT_EDITED = "The student has been edited successfully";
	public static final String MESSAGE_STUDENT_DELETED = "The student has been removed from the course";

	public static final String MESSAGE_EVALUATION_ADDED = "The evaluation has been added. If you don't see that evaluation in the list below, please refresh the page after a few moments.";
	public static final String MESSAGE_EVALUATION_DELETED = "The evaluation has been deleted.";
	public static final String MESSAGE_EVALUATION_EDITED = "The evaluation has been edited.";
	public static final String MESSAGE_EVALUATION_INFORMEDSTUDENTSOFCHANGES = "E-mails have been sent out to inform the students of the changes to the evaluation.";
	public static final String MESSAGE_EVALUATION_PUBLISHED = "The evaluation has been published.";
	public static final String MESSAGE_EVALUATION_UNPUBLISHED = "The evaluation has been unpublished.";
	public static final String MESSAGE_EVALUATION_REMINDERSSENT = "Reminder e-mails have been sent out to those students.";
	public static final String MESSAGE_EVALUATION_RESULTSEDITED = "The particular evaluation results have been edited.";
	public static final String MESSAGE_EVALUATION_EMPTY = "You have not created any evaluations yet. Use the form above to create a new evaluation.";
	public static final String MESSAGE_EVALUATION_NOT_OPEN = "This evaluation is not open at this time. You are not allowed to edit your submission.";
	public static final String MESSAGE_EVALUATION_EXISTS = "An evaluation by this name already exists under this course";
	
	public static final String MESSAGE_FEEDBACK_SESSION_ADDED = "The feedback session has been added. Click on Edit next to your feedback session below to start adding questions. If you don't see that feedback session in the list below, please refresh the page after a few moments.";
	public static final String MESSAGE_FEEDBACK_SESSION_EDITED = "The feedback session has been updated.";
	public static final String MESSAGE_FEEDBACK_SESSION_DELETED = "The feedback session has been deleted.";
	public static final String MESSAGE_FEEDBACK_SESSION_EXISTS = "A feedback session by this name already exists under this course";
	public static final String MESSAGE_FEEDBACK_SESSION_EMPTY = "You have not created any feedback sessions yet. Use the form above to create a new feedback session.";

	public static final String MESSAGE_FEEDBACK_QUESTION_ADDED = "The question has been added to this feedback session.";
	public static final String MESSAGE_FEEDBACK_QUESTION_EDITED = "The changes to the question has been updated.";
	public static final String MESSAGE_FEEDBACK_QUESTION_DELETED = "The question has been deleted.";
	public static final String MESSAGE_FEEDBACK_QUESTION_EXISTS = "The requested question has already been created.";
	public static final String MESSAGE_FEEDBACK_QUESTION_EMPTY = "You have not created any questions for this feedback session yet. Click the button below to add a feedback question.";

	// Status messages from Javascript
	
	public static final String MESSAGE_COURSE_INPUT_FIELDS_EXTRA = "There are too many fields.";
	public static final String MESSAGE_COURSE_INPUT_FIELDS_MISSING = "There are missing fields.";
	public static final String MESSAGE_COURSE_GOOGLEID_INVALID = "GoogleID should only consist of alphanumerics, fullstops, dashes or underscores.";
	public static final String MESSAGE_COURSE_EMAIL_INVALID = "The e-mail address is invalid.";
	public static final String MESSAGE_COURSE_INSTRUCTORNAME_INVALID = "Name should only consist of alphanumerics or hyphens, apostrophes, fullstops, commas, slashes, round brackets\nand not more than 40 characters.";
	public static final String MESSAGE_COURSE_COURSE_ID_EMPTY = "Course ID cannot be empty.";
	public static final String MESSAGE_COURSE_COURSE_NAME_EMPTY = "Course name cannot be empty";
	public static final String MESSAGE_COURSE_INSTRUCTOR_LIST_EMPTY = "Instructor list cannot be empty";
	public static final String MESSAGE_COURSE_INVALID_ID = "Please use only alphabets, numbers, dots, hyphens, underscores and dollar signs in course ID.";
	
	public static final String MESSAGE_EVALUATION_NAMEINVALID = "Please use only alphabets, numbers and whitespace in evaluation name.";
	public static final String MESSAGE_EVALUATION_NAME_LENGTHINVALID = "Evaluation name should not exceed 38 characters.";
	public static final String MESSAGE_EVALUATION_SCHEDULEINVALID = "The evaluation schedule (start/deadline) is not valid.<br />"
			+ "The start time should be in the future, and the deadline should be after start time.";
	public static final String MESSAGE_FIELDS_EMPTY = "Please fill in all the relevant fields.";

	public static final String MESSAGE_INSTRUCTOR_STATUS_DELETED = "The Instructor status has been deleted";
	public static final String MESSAGE_INSTRUCTOR_ACCOUNT_DELETED = "The Account has been deleted";
	public static final String MESSAGE_INSTRUCTOR_REMOVED_FROM_COURSE = "The Instructor has been removed from the Course";
	// Messages that are templates only
	/** Template String. Parameters: Student's name, Evaluation name, Course ID */
	public static final String MESSAGE_INSTRUCTOR_EVALUATION_SUBMISSION_RECEIVED = "You have edited %s's submission for evaluation %s in course %s successfully.<br />"
			+ "The change will not be reflected here until you <span class='color_red bold'>REFRESH</span> the page.";
	/** Template String. Parameters: Evaluation name, Course ID */
	public static final String MESSAGE_STUDENT_EVALUATION_SUBMISSION_RECEIVED = "Your submission for %s in course %s has been saved successfully";
	

	// DIV tags for HTML testing
	public static final String HEADER_TAG = "<div id=\"frameTop\">";
	public static final String FOOTER_TAG = "<div id=\"frameBottom\">";

	// TeammatesServlet responses
	public static final String INSTRUCTOR_ADD_COURSE_RESPONSE_ADDED = "<status>course added</status>";
	public static final String INSTRUCTOR_ADD_COURSE_RESPONSE_EXISTS = "<status>course exists</status>";
	public static final String INSTRUCTOR_ADD_COURSE_RESPONSE_INVALID = "<status>course input invalid</status>";
	public static final String INSTRUCTOR_DELETE_COURSE_RESPONSE_DELETED = "<status>course deleted</status>";
	public static final String INSTRUCTOR_DELETE_COURSE_RESPONSE_NOT_DELETED = "<status>course not deleted</status>";

	// APIServlet responses
	public static final String BACKEND_STATUS_SUCCESS = "[BACKEND_STATUS_SUCCESS]";
	public static final String BACKEND_STATUS_FAILURE = "[BACKEND_STATUS_FAILURE]";

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
	public static final String ERRORCODE_UNPUBLISHED_BEFORE_PUBLISHING = "ERRORCODE_UNPUBLISHED_BEFORE_PUBLISHING";
	
	// Error message used across DB level
	public static final String ERROR_DBLEVEL_NULL_INPUT = "Supplied parameter was null\n";
	
	/**
	 * Build Properties Params
	 */
	private static final BuildProperties BUILD_PROPERTIES = BuildProperties.inst();
	public static String APP_ID = SystemProperty.applicationId.get();
	public static String TEAMMATES_APP_URL = BUILD_PROPERTIES.getAppUrl();

	/**
	 * Password used by Test driver to identify itself.
	 */
	public static String BACKDOOR_KEY = BUILD_PROPERTIES.getAppBackdoorKey();

	/**
	 * Generate delay to handle slow writing IO in datastore
	 */
	public static long PERSISTENCE_CHECK_DURATION = BUILD_PROPERTIES.getAppPersistenceCheckduration();
	public static final int WAIT_DURATION = 200;

	
	
	/**
	 * Email templates
	 */
	public static String STUDENT_EMAIL_TEMPLATE_EVALUATION_ = readResourseFile("studentEmailTemplate-evaluation_.html");
	public static String STUDENT_EMAIL_TEMPLATE_EVALUATION_PUBLISHED = readResourseFile("studentEmailTemplate-evaluationPublished.html");
	public static String STUDENT_EMAIL_TEMPLATE_COURSE_JOIN = readResourseFile("studentEmailTemplate-courseJoin.html");
	public static String STUDENT_EMAIL_FRAGMENT_COURSE_JOIN = readResourseFile("studentEmailFragment-courseJoin.html");
	public static String SYSTEM_ERROR_EMAIL_TEMPLATE = readResourseFile("systemErrorEmailTemplate.html");
	
	/**
	 * Instructor Servlets
	 */
	public static String INSTRUCTOR_HOME_SERVLET = "instructorHome";
	public static String INSTRUCTOR_COURSE_SERVLET = "instructorCourse";
	public static String INSTRUCTOR_COURSE_ENROLL_SERVLET = "instructorCourseEnroll";
	public static String INSTRUCTOR_COURSE_ENROLL_SAVE_SERVLET = "instructorCourseEnrollSave";
	public static String INSTRUCTOR_COURSE_EDIT_SERVLET = "instructorCourseEdit";
	public static String INSTRUCTOR_COURSE_DETAILS_SERVLET = "instructorCourseDetails";
	public static String INSTRUCTOR_COURSE_DELETE_SERVLET = "instructorCourseDelete";
	public static String INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET = "instructorCourseStudentEdit";
	public static String INSTRUCTOR_COURSE_STUDENT_DELETE_SERVLET = "instructorCourseStudentDelete";
	public static String INSTRUCTOR_COURSE_STUDENT_DETAILS_SERVLET = "instructorCourseStudentDetails";
	public static String INSTRUCTOR_COURSE_REMIND_SERVLET = "instructorCourseRemind";
	public static String INSTRUCTOR_EVAL_SERVLET = "instructorEval";
	public static String INSTRUCTOR_EVAL_EXPORT_SERVLET = "instructorEvalExport";
	public static String INSTRUCTOR_EVAL_EDIT_SERVLET = "instructorEvalEdit";
	public static String INSTRUCTOR_EVAL_DELETE_SERVLET = "instructorEvalDelete";
	public static String INSTRUCTOR_EVAL_REMIND_SERVLET = "instructorEvalRemind";
	public static String INSTRUCTOR_EVAL_PUBLISH_SERVLET = "instructorEvalPublish";
	public static String INSTRUCTOR_EVAL_UNPUBLISH_SERVLET = "instructorEvalUnpublish";
	public static String INSTRUCTOR_EVAL_RESULTS_SERVLET = "instructorEvalResults";
	public static String INSTRUCTOR_EVAL_SUBMISSION_EDIT_HANDLER_SERVLET = "instructorEvalSubmissionEditHandler";
	public static String INSTRUCTOR_EVAL_SUBMISSION_EDIT_SERVLET = "instructorEvalSubmissionEdit";
	public static String INSTRUCTOR_EVAL_SUBMISSION_VIEW_SERVLET = "instructorEvalSubmissionView";
	
	/**
	 * Instructor Servlet Actions
	 */
	public static String INSTRUCTOR_HOME_SERVLET_PAGE_LOAD = "Pageload";
	public static String INSTRUCTOR_COURSE_SERVLET_ADD_COURSE = "Add New Course";
	public static String INSTRUCTOR_COURSE_SERVLET_PAGE_LOAD = "Pageload";
	public static String INSTRUCTOR_COURSE_ENROLL_SERVLET_PAGE_LOAD = "Pageload";
	public static String INSTRUCTOR_COURSE_ENROLL_SERVLET_ENROLL_STUDENTS = "Enroll Students";
	public static String INSTRUCTOR_COURSE_EDIT_SERVLET_PAGE_LOAD = "Pageload";
	public static String INSTRUCTOR_COURSE_EDIT_SERVLET_EDIT_COURSE_INFO = "Edit Course Info";
	public static String INSTRUCTOR_COURSE_DETAILS_SERVLET_PAGE_LOAD = "Pageload";
	public static String INSTRUCTOR_COURSE_DELETE_SERVLET_DELETE_COURSE = "Delete Course";
	public static String INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET_PAGE_LOAD = "Pageload";
	public static String INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET_EDIT_DETAILS = "Edit Student Details";
	public static String INSTRUCTOR_COURSE_STUDENT_DELETE_SERVLET_DELETE_STUDENT = "Delete Student";
	public static String INSTRUCTOR_COURSE_STUDENT_DETAILS_SERVLET_PAGE_LOAD = "Pageload";
	public static String INSTRUCTOR_COURSE_REMIND_SERVLET_SEND_REGISTRATION = "Send Registration";
	public static String INSTRUCTOR_EVAL_SERVLET_PAGE_LOAD = "Pageload";
	public static String INSTRUCTOR_EVAL_SERVLET_NEW_EVALUATION = "Create New Evaluation";
	public static String INSTRUCTOR_EVAL_EDIT_SERVLET_PAGE_LOAD = "Pageload";
	public static String INSTRUCTOR_EVAL_EDIT_SERVLET_EDIT_EVALUATION = "Edit Evaluation Info";
	public static String INSTRUCTOR_EVAL_DELETE_SERVLET_DELETE_EVALUATION = "Delete Evaluation";
	public static String INSTRUCTOR_EVAL_REMIND_SERVLET_SEND_EVAL_REMINDER = "Remind Students About Evaluation";
	public static String INSTRUCTOR_EVAL_PUBLISH_SERVLET_PUBLISH_EVALUATION = "Publish Evaluation";
	public static String INSTRUCTOR_EVAL_UNPUBLISH_SERVLET_UNPUBLISH_EVALUATION = "Unpublish Evaluation";
	public static String INSTRUCTOR_EVAL_RESULTS_SERVLET_PAGE_LOAD = "Pageload";
	public static String INSTRUCTOR_EVAL_SUBMISSION_EDIT_HANDLER_SERVLET_EDIT_SUBMISSION = "Edit Submission";
	public static String INSTRUCTOR_EVAL_SUBMISSION_EDIT_SERVLET_PAGE_LOAD = "Pageload";
	public static String INSTRUCTOR_EVAL_SUBMISSION_VIEW_SERVLET_PAGE_LOAD = "Pageload";
	
	
	/**
	 * Student Servlets
	 */
	public static String STUDENT_HOME_SERVLET = "studentHome";
	public static String STUDENT_COURSE_DETAILS_SERVLET = "studentCourseDetails";
	public static String STUDENT_COURSE_JOIN_SERVLET = "studentCourseJoin";
	public static String STUDENT_EVAL_EDIT_HANDLER_SERVLET = "studentEvalEditHandler";
	public static String STUDENT_EVAL_EDIT_SERVLET = "studentEvalEdit";
	public static String STUDENT_EVAL_RESULTS_SERVLET = "studentEvalResults";
	
	/**
	 * Student Servlet Actions
	 */
	public static String STUDENT_HOME_SERVLET_PAGE_LOAD = "Pageload";
	public static String STUDENT_COURSE_DETAILS_SERVLET_PAGE_LOAD = "Pageload";
	public static String STUDENT_COURSE_JOIN_SERVLET_JOIN_COURSE = "Student Joining Course";
	public static String STUDENT_EVAL_EDIT_HANDLER_SERVLET_EDIT_SUBMISSION = "Edit Submission";
	public static String STUDENT_EVAL_EDIT_SERVLET_PAGE_LOAD = "Pageload";
	public static String STUDENT_EVAL_RESULTS_SERVLET_PAGE_LOAD = "Pageload";
	
	
	
	/**
	 * Admin Servlets
	 */
	public static String ADMIN_HOME_SERVLET = "adminHome";
	public static String ADMIN_ACCOUNT_MANAGEMENT_SERVLET = "adminAccountManagement";
	public static String ADMIN_ACCOUNT_DETAILS_SERVLET = "adminAccountDetails";
	public static String ADMIN_ACCOUNT_DELETE_SERVLET = "adminAccountDelete";
	public static String ADMIN_ACTIVITY_LOG_SERVLET = "adminActivityLog";
	public static String ADMIN_SEARCH_SERVLET = "adminSearch";
	public static String ADMIN_SEARCH_TASK_SERVLET = "adminSearchTask";
	public static String ADMIN_EXCEPTION_TEST_SERVLET = "adminExceptionTest";
	
	
	/**
	 * Admin Servlet Actions
	 */
	public static String ADMIN_HOME_SERVLET_PAGE_LOAD = "Pageload";
	public static String ADMIN_HOME_SERVLET_CREATE_INSTRUCTOR = "Create Instructor";
	public static String ADMIN_ACCOUNT_MANAGEMENT_SERVLET_PAGE_LOAD = "Pageload";
	public static String ADMIN_ACCOUNT_DETAILS_SERVLET_PAGE_LOAD = "Pageload";
	public static String ADMIN_ACCOUNT_DELETE_SERVLET_DELETE_INSTRUCTOR_FROM_COURSE = "Delete Instructor from Course";
	public static String ADMIN_ACCOUNT_DELETE_SERVLET_DELETE_INSTRUCTOR_STATUS = "Delete Instructor Status";
	public static String ADMIN_ACCOUNT_DELETE_SERVLET_DELETE_INSTRUCTOR_ACCOUNT = "Delete Instructor Account";
	public static String ADMIN_ACTIVITY_LOG_SERVLET_PAGE_LOAD = "Pageload";
	public static String ADMIN_SEARCH_SERVLET_PAGE_LOAD = "Pageload";
	public static String ADMIN_EXCEPTION_TEST_SERVLET_PAGE_LOAD = "Pageload";
	public static String ADMIN_HOME_SERVLET_ID_ALREADY_REGISTERED = "This Google ID is already registered as an instructor";
	
	
	/**
	 * Automated Servlets
	 */
	public static String EVALUATION_CLOSING_REMINDERS_SERVLET = "evaluationclosingreminders";
	public static String EVALUATION_OPENING_REMINDERS_SERVLET = "evaluationopeningreminders";
	
	
	/**
	 * Automated Servlet Actions
	 */
	public static String EVALUATION_CLOSING_REMINDERS_SERVLET_EVALUATION_CLOSE_REMINDER = "Send Evaluation Closing reminders";
	public static String EVALUATION_OPENING_REMINDERS_SERVLET_EVALUATION_OPEN_REMINDER = "Send Evaluation Opening reminders";
	
	/**
	 * Other Servlet Actions
	 */
	public static String LOG_SYSTEM_ERROR_REPORT = "System Error Report";
	public static String LOG_SERVLET_ACTION_FAILURE = "Servlet Action Failure";
	
	@SuppressWarnings("unused")
	private void ____MISC_utility_methods___________________________________() {
	}
	
	/**
	 * Concatenates a list of strings to a single string, separated by line breaks.
	 * @return Concatenated string.
	 */
	public static String toString(List<String> strings) {
		return toString(strings, EOL);	
	}
	
	/**
	 * Concatenates a list of strings to a single string, separated by the given delimiter.
	 * @return Concatenated string.
	 */
	public static String toString(List<String> strings, String delimiter) {
		String returnValue = "";
		
		if(strings.size()==0){
			return returnValue;
		}
		
		for(int i=0; i < strings.size()-1; i++){
			String s = strings.get(i);
			returnValue += s + delimiter;
		}
		//append the last item
		returnValue += strings.get(strings.size()-1);
		
		return returnValue;		
	}
	
	/**
	 * 
	 * @param paramMap A parameter map (e.g., the kind found in HttpServletRequests)
	 * @param key
	 * @return the first value for the key. Returns null if key not found.
	 */
	public static String getValueFromParamMap(Map<String, String[]> paramMap, String key) {
		String[] values = paramMap.get(key);
		return values == null ? null : values[0];
	}
	

	/**
	 * This creates a Gson object that can handle the Date format we use in the
	 * Json file and also reformat the Json string in pretty-print format. <br>
	 * Technique found in <a href=
	 * "http://code.google.com/p/google-gson/source/browse/trunk/gson/src/test/java/com/google/gson/functional/DefaultTypeAdaptersTest.java?spec=svn327&r=327"
	 * >here </a>
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
	 * Convert a date string and time string into a Date object. Returns null on error.
	 * 
	 * @param date
	 *            The date in format dd/MM/yyyy
	 * @param time
	 *            The time in format HHMM
	 * @return
	 */
	public static Date combineDateTime(String inputDate, String inputTime) {
		if (inputDate == null || inputTime == null) {
			return null;
		}

		int inputTimeInt = 0;
		if (inputTime != null) {
			inputTimeInt = Integer.parseInt(inputTime) * 100;
		}
		return convertToDate(inputDate, inputTimeInt);
	}

	private static Date convertToDate(String date, int time) {
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
	 * @param dateInStringFormat should be in the format "yyyy-MM-dd h:mm a"
	 * e.g. "2014-04-01 11:59 PM"
	 */
	public static Date convertToDate(String dateInStringFormat) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd h:mm a");
		return df.parse(dateInStringFormat);
	}

	/**
	 * Returns the date object with specified offset in number of days from now
	 * 
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
	 * 
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
		return convertToUserTimeZone(c, timeZone).getTime();
	}

	/**
	 * Returns the date object representing the next full hour from now.
	 * Example: If now is 1055, this will return 1100
	 * 
	 * @return
	 */
	public static Date getNextHour() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 0);
		return cal.getTime();
	}

	/**
	 * Formats a date in the corresponding option value in 'Time' dropdowns The
	 * hour just after midnight is converted to option 24 (i.e., 2359 as shown
	 * to the user) 23.59 is also converted to 24. (i.e., 23.59-00.59 ---> 24)
	 * 
	 * @param date
	 * @return
	 */
	public static String convertToOptionValueInTimeDropDown(Date date) { 
		//TODO: see if we can eliminate this method (i.e., merge with convertToDisplayValueInTimeDropDown)
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		hour = (hour == 0 ? 24 : hour);
		hour = ((hour == 23) && (minutes == 59)) ? 24 : hour;
		return hour + "";
	}
	
	/**
	 * @return one of these : 0100H, 0200H, ..., 0900H, 1000H, ... 2300H, 2359H.
	 * Note the last one is different from the others.
	 */
	public static String convertToDisplayValueInTimeDropDown(Date date) {
		String optionValue = convertToOptionValueInTimeDropDown(date);
		if (optionValue.equals("24")) {
			return "2359H";
		}else if (optionValue.length() == 1) {
			return "0" + optionValue + "00H";
		} else if (optionValue.length() == 2) {
			return optionValue + "00H";
		} else {
			throw new RuntimeException("Unrecognized time option: "+optionValue);
		}
	}

	/**
	 * Formats a date in the format dd/MM/yyyy
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		if (date == null)
			return "";
		return new SimpleDateFormat("dd/MM/yyyy").format(date);
	}

	/**
	 * Formats a date in the format dd MMM yyyy, hh:mm. Example: 05 May 2012,
	 * 22:04<br />
	 * This is used in JSP pages to display time information to users
	 * 
	 * @param date
	 * @return
	 */
	public static String formatTime(Date date) {
		if (date == null)
			return "";
		return new SimpleDateFormat("dd MMM yyyy, HH:mm").format(date);
	}

	public static String calendarToString(Calendar c) {
		if (c == null)
			return "";
		return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss SSS").format(c
				.getTime());
	}

	public static Calendar dateToCalendar(Date date) {
		Calendar c = Calendar.getInstance();
		if (date == null)
			return c;
		c.setTime(date);
		return c;
	}
	
	public static Calendar convertToUserTimeZone(Calendar time, double timeZone) {
		time.add(Calendar.MILLISECOND, (int) (60 * 60 * 1000 * timeZone));
		return time; // for chaining
	}
	
	/**
	 * Returns whether the given date is being used as a special representation,
	 * signifying it's face value should not be used without proper processing.
	 * A null date is not a special time.
	 * 
	 * @param date
	 * @return {@code Boolean}
	 */
	public static boolean isSpecialTime(Date date) {
		
		if (date == null) {
			return false;
		}
		
		if (date.equals(Common.TIME_REPRESENTS_FOLLOW_OPENING) ||
			date.equals(Common.TIME_REPRESENTS_FOLLOW_VISIBLE) ||
			date.equals(Common.TIME_REPRESENTS_LATER) ||
			date.equals(Common.TIME_REPRESENTS_NEVER)) {
			return true;
		} else {
			return false;
		}
		
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
	 * Wrapper. Loading jobs are delegated to BuildProperties
	 * 
	 * @param reader
	 * @return
	 */
	public static String readStream(InputStream stream) {
		return BUILD_PROPERTIES.readStream(stream);
	}
	
	public static String readResourseFile(String file) {
		return readStream(BuildProperties.class.getClassLoader()
				.getResourceAsStream(file));
	}

	public static boolean isWhiteSpace(String string) {
		return string.trim().isEmpty();
	}

	public static String trimTrailingSlash(String url) {
		return url.trim().replaceAll("/(?=$)", "");
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

	/**
	 * Returns the URL with the specified key-value pair parameter added.
	 * Unchanged if either the key or value is null, or the key already exists<br />
	 * Example:
	 * <ul>
	 * <li><code>addParam("index.jsp","action","add")</code> returns
	 * <code>index.jsp?action=add</code></li>
	 * <li><code>addParam("index.jsp?action=add","courseid","cs1101")</code>
	 * returns <code>index.jsp?action=add&courseid=cs1101</code></li>
	 * <li><code>addParam("index.jsp","message",null)</code> returns
	 * <code>index.jsp</code></li>
	 * </ul>
	 * 
	 * @param url
	 * @param key
	 * @param value
	 * @return
	 */
	public static String addParamToUrl(String url, String key, String value) {
		if (key == null || value == null)
			return url;
		if (url.contains("?" + key + "=") || url.contains("&" + key + "="))
			return url;
		url += url.indexOf('?') >= 0 ? '&' : '?';
		url += key + "=" + Helper.convertForURL(value);
		return url;
	}
	
	public static String printRequestParameters(HttpServletRequest request) {
		String requestParameters = "{";
		for (Enumeration f = request.getParameterNames(); f.hasMoreElements();) {
			String paramet = new String(f.nextElement().toString());
			requestParameters += paramet + "::";
			String[] parameterValues = request.getParameterValues(paramet);
			for (int j = 0; j < parameterValues.length; j++){
				requestParameters += parameterValues[j] + "//";
			}
			requestParameters = requestParameters.substring(0, requestParameters.length() - 2) + ", ";
		}
		if (requestParameters != "{") {
			requestParameters = requestParameters.substring(0, requestParameters.length() - 2);
		}
		requestParameters += "}";
		return requestParameters;
	}
	
	public static void waitBriefly() {
		try {
			Thread.sleep(WAIT_DURATION);
		} catch (InterruptedException e) {
			log.severe(Common.stackTraceToString(e));
		}
	}
	
	/**
	 * Makes the thread sleep for the specified time. 
	 */
	public static void waitFor(int timeInMilliSeconds) {
		try {
			Thread.sleep(timeInMilliSeconds);
		} catch (InterruptedException e) {
			log.severe(Common.stackTraceToString(e));
		}
	}

	public static String encrypt(String value) {
		try {
			SecretKeySpec sks = new SecretKeySpec(
					hexStringToByteArray(BUILD_PROPERTIES.getEncyptionKey()), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
			byte[] encrypted = cipher.doFinal(value.getBytes());
			return byteArrayToHexString(encrypted);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String decrypt(String message) {
		try {
			SecretKeySpec sks = new SecretKeySpec(
					hexStringToByteArray(BUILD_PROPERTIES.getEncyptionKey()), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, sks);
			byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
			return new String(decrypted);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @return  the URL used for the HTTP request but without the domain.
	 * e.g. "/page/studentHome?user=james" 
	 */
	public static String getRequestedURL(HttpServletRequest req) {
		String link = req.getRequestURI();
		String query = req.getQueryString();
		if (query != null && !query.trim().isEmpty()){
			link += "?" + query;
		}
		return link;
	}


	private static String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

	private static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte) v;
		}
		return b;
	}
	
	@SuppressWarnings("unused")
	private void ____PRIVATE_helper_methods_________________________________() {
	}

	public static String stackTraceToString(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return "\n" + sw.toString();
		
	}
	
	public static String getCurrentThreadStack() {
		StringWriter sw = new StringWriter();
		new Throwable("").printStackTrace(new PrintWriter(sw));
		return "\n" + sw.toString();
	}
	
	/**
	 * 
	 *  This function loads new buildproperties in run-time
	 * 
	 * @param Properties	The properties stream
	 */
	public static void readProperties(Properties p) {
		BUILD_PROPERTIES.readProperties(p);

		APP_ID = SystemProperty.applicationId.get();
		TEAMMATES_APP_URL = BUILD_PROPERTIES.getAppUrl();
		BACKDOOR_KEY = BUILD_PROPERTIES.getAppBackdoorKey();
		PERSISTENCE_CHECK_DURATION = BUILD_PROPERTIES.getAppPersistenceCheckduration();
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
