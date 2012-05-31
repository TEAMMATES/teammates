package teammates;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;

import teammates.exception.InvalidParametersException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Class that stores variables and methods that are widely used across classes
 *
 */
public class Common {
	
	public final static String EOL = System.getProperty("line.separator");
	public static final int UNINITIALIZED_INT = -9999;
	public static final double UNINITIALIZED_DOUBLE = -9999.0;
	public final static String ENCODING = "UTF8";
	public final static String VERSION = "4.17.02";
	public final static int BUFFER_SIZE = 1024*10;
	public final static String TEST_DATA_FOLDER = "src/test/resources/data/";
	public final static String TEST_PAGES_FOLDER = "src/test/resources/pages/";
	
	// Hover messages
	public final static String HOVER_MESSAGE_ENROLL = "Enroll student into the course";
	public final static String HOVER_MESSAGE_VIEW_COURSE = "View, edit and send registration keys to the students in the course";
	public final static String HOVER_MESSAGE_DELETE_COURSE = "Delete the course and its corresponding students and evaluations";
	public final static String HOVER_MESSAGE_ADD_EVALUATION = "Add an evaluation for the course";
	public final static String HOVER_MESSAGE_CLAIMED = "This is student own estimation of his/her contributions to the project";
	public final static String HOVER_MESSAGE_PERCEIVED = "This is the average of what other team members think this student contributed to the project";
	public final static String HOVER_MESSAGE_PERCEIVED_CLAIMED = "Difference between claimed and perceived contribution points";
	public final static String HOVER_MESSAGE_STUDENT_VIEW_COURSE = "View course details";
	
	public final static String HOVER_MESSAGE_EVALUATION_STATUS_AWAITING = "The evaluation is created but has not yet started";
	public final static String HOVER_MESSAGE_EVALUATION_STATUS_OPEN = "The evaluation has started and students can submit feedback until the closing time";
	public final static String HOVER_MESSAGE_EVALUATION_STATUS_CLOSED = "The evaluation has finished but the results have not been made available to the students";
	public final static String HOVER_MESSAGE_EVALUATION_STATUS_PUBLISHED = "The evaluation has finished and the results have been made available to students";
	
	public final static String HOVER_MESSAGE_EVALUATION_VIEW = "View the current results of the evaluation";
	public final static String HOVER_MESSAGE_EVALUATION_EDIT = "Edit evaluation details";
	public final static String HOVER_MESSAGE_EVALUATION_REMIND = "Send e-mails to remind students who have not submitted their evaluations to do so";
	public final static String HOVER_MESSAGE_EVALUATION_DELETE = "Delete the evaluation";
	public final static String HOVER_MESSAGE_EVALUATION_PUBLISH = "Publish evaluation results for students to view";
	public final static String HOVER_MESSAGE_EVALUATION_UNPUBLISH = "Make results not visible to students";
	
	// Evaluation status
	public final static String EVALUATION_STATUS_AWAITING = "AWAITING";
	public final static String EVALUATION_STATUS_OPEN = "OPEN";
	public final static String EVALUATION_STATUS_CLOSED = "CLOSED";
	public final static String EVALUATION_STATUS_PUBLISHED = "PUBLISHED";
	
	// IDs used as div tag "id" attribute
	public final static String COURSE_ID = "courseid";
	public final static String COURSE_NAME = "coursename";
	public final static String COURSE_NUMBEROFTEAMS = "coursenumberofteams";
	public final static String COURSE_TOTALSTUDENTS = "coursetotalstudents";
	public final static String COURSE_UNREGISTERED = "courseunregistered";
	public final static String COURSE_STATUS = "coursestatus";
	
	// JSP Parameter names
	public final static String PARAM_COURSE_ID = "courseid";
	public final static String PARAM_EVALUATION_NAME = "evalname";
	public final static String PARAM_NEXT_URL = "next";
	
	// JSP pages (NOT to be used in the JSP, but as reference for other classes)
	public final static String JSP_COORD_HOME = "coordHome.jsp";
	public final static String JSP_COORD_COURSE = "coordCourse.jsp";
	public final static String JSP_COORD_COURSE_DELETE = "coordCourseDelete.jsp";
	public final static String JSP_COORD_COURSE_DETAILS = "coordCourseDetails.jsp";
	public final static String JSP_COORD_COURSE_STUDENT_DETAILS = "coordCourseStudentDetails.jsp";
	public final static String JSP_COORD_COURSE_STUDENT_EDIT = "coordCourseStudentEdit.jsp";
	public final static String JSP_COORD_COURSE_ENROLL = "coordCourseEnroll.jsp";
	public final static String JSP_COORD_TFS = "coordTFS.jsp";
	public final static String JSP_COORD_TFS_MANAGE = "coordTFSManage.jsp";
	public final static String JSP_COORD_TFS_CHANGE_TEAM = "coordTFSChangeTeam.jsp";
	public final static String JSP_COORD_TFS_LOGS = "coordTFSLogs.jsp";
	public final static String JSP_COORD_EVAL = "coordEval.jsp";
	public final static String JSP_COORD_EVAL_EDIT = "coordEvalEdit.jsp";
	public final static String JSP_COORD_EVAL_RESULTS = "coordEvalResults.jsp";
	public final static String JSP_COORD_EVAL_SUBMISSION_EDIT = "coordEvalSubmissionEdit.jsp";
	
	public final static String JSP_STUDENT_HOME = "studentHome.jsp";
	public final static String JSP_STUDENT_COURSE = "studentHome.jsp";
	public final static String JSP_STUDENT_COURSE_DETAILS = "studentCourseDetails.jsp";
	public final static String JSP_STUDENT_TFS_MANAGE = "studentTFSManage.jsp";
	public final static String JSP_STUDENT_EVAL = "studentEval.jsp";
	public final static String JSP_STUDENT_EVAL_EDIT = "studentEvalEdit.jsp";
	public final static String JSP_STUDENT_EVAL_RESULTS = "studentEvalResults.jsp";
	
	//status messages
	public final static String MESSAGE_COURSE_ADDED = "The course has been added. Click the 'Enroll' link in the table below to add students to the course.";
	public final static String MESSAGE_COURSE_EXISTS = "The course already exists.";
	public final static String MESSAGE_COURSE_MISSING_FIELD = "Course ID and Course Name are compulsory fields.";
	public final static String MESSAGE_COURSE_INVALID_ID = "Please use only alphabets, numbers, dots, hyphens, underscores and dollars in course ID.";
	
	// DIV tags for HTML testing
	public final static String HEADER_TAG = "<div id=\"frameTop\">";
	public final static String FOOTER_TAG = "<div id=\"frameBottom\">";

	//data field sizes
	public static int COURSE_NAME_MAX_LENGTH = 38;
	public static int COURSE_ID_MAX_LENGTH = 21;
	public static final int STUDENT_NAME_MAX_LENGTH = 40;
	public static final int TEAM_NAME_MAX_LENGTH = 25;
	public static final int COMMENT_MAX_LENGTH = 500;
	
	//TeammatesServlet responses
	public static final String COORD_ADD_COURSE_RESPONSE_ADDED = "<status>course added</status>";
	public static final String COORD_ADD_COURSE_RESPONSE_EXISTS = "<status>course exists</status>";
	public static final String COORD_ADD_COURSE_RESPONSE_INVALID = "<status>course input invalid</status>";
	public static final String COORD_DELETE_COURSE_RESPONSE_DELETED = "<status>course deleted</status>";
	public static final String COORD_DELETE_COURSE_RESPONSE_NOT_DELETED = "<status>course not deleted</status>";
	
	//APIServlet responses
	public static final String BACKEND_STATUS_SUCCESS = "[BACKEND_STATUS_SUCCESS]";
	public static String BACKEND_STATUS_FAILURE = "[BACKEND_STATUS_FAILURE]";
	
	//General Error codes
	public static final String ERRORCODE_EMPTY_STRING = "ERRORCODE_EMPTY_STRING";
	public static final String ERRORCODE_NULL_PARAMETER = "ERRORCODE_NULL_PARAMETER";
	public static final String ERRORCODE_INCORRECTLY_FORMATTED_STRING = "ERRORCODE_INCORRECTLY_FORMATTED_STRING";
	public static final String ERRORCODE_IVALID_CHARS = "ERRORCODE_IVALID_CHARS";
	public static final String ERRORCODE_INVALID_EMAIL = "ERRORCODE_INVALID_EMAIL";
	public static final String ERRORCODE_STRING_TOO_LONG = "ERRORCODE_STRING_TOO_LONG";

	/**
	 * This creates a Gson object that can handle the Date format we use in the Json file
	 * technique found in http://code.google.com/p/google-gson/source/browse/trunk/gson/src/test/java/com/google/gson/functional/DefaultTypeAdaptersTest.java?spec=svn327&r=327
	 */
	public static Gson getTeammatesGson(){
		return new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat("yyyy-MM-dd h:mm a").setPrettyPrinting().create();
	}
	
	public static void println(String message) {
		System.out.println(String.format("[%d - %s] %s", Thread.currentThread()
				.getId(), Thread.currentThread().getName(), message));
	}

	public static Date convertToDate(String date, int time) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();

		Date newDate = new Date();

		// Perform date manipulation
		try {
			newDate = sdf.parse(date);
			calendar.setTime(newDate);

			if (time == 24) {
				calendar.set(Calendar.HOUR, 23);
				calendar.set(Calendar.MINUTE, 59);
			} else {
				calendar.set(Calendar.HOUR, time);
			}

			return calendar.getTime();
		} catch (Exception e) {
			return null;
		}

	}
	
	public static Date convertToExactDateTime(String date, int time) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();

		Date newDate = new Date();

		// Perform date manipulation
		try {
			newDate = sdf.parse(date);
			calendar.setTime(newDate);

			if (time == 24) {
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

	//TODO: write unit tests
	public static void validateTeamName(String teamName) throws InvalidParametersException {
		if(teamName.length()>TEAM_NAME_MAX_LENGTH){
			throw new InvalidParametersException(ERRORCODE_STRING_TOO_LONG, "Team name cannot be longer than "+TEAM_NAME_MAX_LENGTH);
		}
	}

	//TODO: write unit tests
	public static void validateStudentName(String studentName) throws InvalidParametersException {
		if(!studentName.trim().equals(studentName)) {
			throw new InvalidParametersException(ERRORCODE_INCORRECTLY_FORMATTED_STRING, "Student name should not have leading or trailing spaces");
		}
		if(studentName.equals("")){
			throw new InvalidParametersException(ERRORCODE_EMPTY_STRING, "Student name should not be empty");
		}
		if(studentName.length()>STUDENT_NAME_MAX_LENGTH){
			throw new InvalidParametersException(ERRORCODE_STRING_TOO_LONG, "Student name cannot be longer than "+STUDENT_NAME_MAX_LENGTH);
		}
		
	}

	//TODO: write unit tests
	public static void validateEmail(String email) throws InvalidParametersException {
		verifyNoLeadingAndTrailingSpaces(email, "email");
		verifyNotAnEmptyString(email, "email");
		if(!email.contains("@")){
			throw new InvalidParametersException(ERRORCODE_INVALID_EMAIL, "Email address should contain '@'");
		}
		
	}
	
	//TODO: write unit tests
	public static void validateGoogleId(String googleId) throws InvalidParametersException {
		verifyNoLeadingAndTrailingSpaces(googleId, "Google ID");
		verifyNotAnEmptyString(googleId, "Google ID");
		verifyContainsNoSpaces(googleId, "Google ID");
		
	}

	
	private static void verifyContainsNoSpaces(String stringToCheck, String nameOfString)
			throws InvalidParametersException {
		if(stringToCheck.split(" ").length>1){
			throw new InvalidParametersException(ERRORCODE_IVALID_CHARS, nameOfString+" cannot contain spaces");
		}
	}
	
	//TODO: write unit tests
	public static void validateCoordName(String coordName) throws InvalidParametersException {
		verifyNoLeadingAndTrailingSpaces(coordName, "Coordinator Name");
		verifyNotAnEmptyString(coordName, "Coordinator Name");
	}
	
	private static void verifyNotAnEmptyString(String stringToCheck, String nameOfString)
			throws InvalidParametersException {
		if(stringToCheck.equals("")){
			throw new InvalidParametersException(ERRORCODE_EMPTY_STRING, nameOfString+" should not be empty");
		}
	}

	private static void verifyNoLeadingAndTrailingSpaces(String stringToCheck, String nameOfString)
			throws InvalidParametersException {
		if(!stringToCheck.trim().equals(stringToCheck)) {
			throw new InvalidParametersException(ERRORCODE_INCORRECTLY_FORMATTED_STRING, nameOfString+" should not have leading or trailing spaces");
		}
	}

	//TODO: write unit tests
	public static String generateStringOfLength(int length) {
		assert(length>=0);
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<length; i++){
			sb.append('a');
		}
		return sb.toString();
	}

	public static void validateCourseId(String courseId) throws InvalidParametersException {
		if(courseId.contains(" ")){
			throw new InvalidParametersException(ERRORCODE_INCORRECTLY_FORMATTED_STRING, "Course ID should not contain spaces");
		}
		
	}

	public static void validateComment(String comment) throws InvalidParametersException {
		if(comment.length()>COMMENT_MAX_LENGTH){
			throw new InvalidParametersException(ERRORCODE_STRING_TOO_LONG, "Comment cannot be longer than "+STUDENT_NAME_MAX_LENGTH);
		}
		
	}
	
	/**
	 * Asserts that the superstringActual contains the exact occurence of substringExpected.
	 * Display the difference between the two on failure (in Eclipse).
	 * @param message
	 * @param substringExpected
	 * @param superstringActual
	 */
	public static void assertContains(String substringExpected, String superstringActual){
		if(!superstringActual.contains(substringExpected)){
			assertEquals(substringExpected, superstringActual);
		}
	}
	
	/**
	 * Asserts that the superstringActual contains the exact occurence of substringExpected.
	 * Display the difference between the two on failure (in Eclipse) with the specified message.
	 * @param message
	 * @param substringExpected
	 * @param superstringActual
	 */
	public static void assertContains(String message, String substringExpected, String superstringActual){
		if(!superstringActual.contains(substringExpected)){
			assertEquals(message, substringExpected, superstringActual);
		}
	}
	
	/**
	 * Asserts that the stringActual contains the occurence regexExpected.
	 * Replaces occurences of {*} at regexExpected to match anything in stringActual.
	 * Tries to display the difference between the two on failure (in Eclipse).
	 * @param message
	 * @param regexExpected
	 * @param stringActual
	 */
	public static void assertContainsRegex(String regexExpected, String stringActual){
		String processedRegex = regexExpected.replaceAll("([()\\[.\\+?|^$])",Matcher.quoteReplacement("\\")+"$1").replaceAll("\\{\\*}", ".*");
		if(!stringActual.matches("(?s)(?m).*"+processedRegex+".*")){
			assertEquals(regexExpected,stringActual);
		}
	}
	
	/**
	 * Asserts that the stringActual contains the occurence regexExpected.
	 * Replaces occurences of {*} at regexExpected to match anything in stringActual.
	 * Tries to display the difference between the two on failure (in Eclipse) with the specified message.
	 * @param message
	 * @param regexExpected
	 * @param stringActual
	 */
	public static void assertContainsRegex(String message, String regexExpected, String stringActual){
		String processedRegex = regexExpected.replaceAll("([()\\[.\\+?|^$])",Matcher.quoteReplacement("\\")+"$1").replaceAll("\\{\\*}", ".*");
		if(!stringActual.matches("(?s)(?m).*"+processedRegex+".*")){
			assertEquals(message,regexExpected,stringActual);
		}
	}
	
	/**
	 * Reads from the reader and write it using the writer object.
	 * To get a string from the file, pass a {@link java.io.StringWriter} object as the writer.<br />
	 * Example:
	 * <pre>
	 * StringWriter wr = new StringWriter();
	 * FileReader rd = new FileReader("README.txt");
	 * readFile(rd,wr);
	 * String result = wr.toString();
	 * </pre>
	 * @param reader
	 * @param writer
	 */
	public static void readAndWrite(Reader reader, Writer writer){
		try{
			char[] tmp = new char[Common.BUFFER_SIZE];
			int read = 0;
			while((read = reader.read(tmp))>0) writer.write(tmp,0,read);
			reader.close();
		} catch (IOException e){
			
		}
	}

	public static boolean isWhiteSpace(String string) {
		return string.trim().isEmpty();
	}
	
	public static Date getDateOffsetToCurrentTime(int offsetDays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(cal.getTime());
		cal.add(Calendar.DATE, +offsetDays);
		return cal.getTime();
	}
}
