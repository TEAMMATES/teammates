package teammates;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import teammates.jdo.EvaluationDetailsForCoordinator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Common {
	
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
	
	// IDs
	public final static String COURSE_ID = "courseid";
	public final static String COURSE_NAME = "coursename";
	public final static String COURSE_NUMBEROFTEAMS = "coursenumberofteams";
	public final static String COURSE_TOTALSTUDENTS = "coursetotalstudents";
	public final static String COURSE_UNREGISTERED = "courseunregistered";
	public final static String COURSE_STATUS = "coursestatus";
	
	//status messages
	public final static String MESSAGE_COURSE_ADDED = "The course has been added. Click the 'Enroll' link in the table below to add students to the course.";
	public final static String MESSAGE_COURSE_EXISTS = "The course already exists.";
	public final static String ERROR_COURSE_MISSING_FIELD = "Course ID and Course Name are compulsory fields.";
	public final static String ERROR_COURSE_INVALID_ID = "Please use only alphabets, numbers, dots, hyphens, underscores and dollars in course ID.";

	//data field sizes
	public static int COURSE_NAME_MAX_LENGTH = 38;
	public static int COURSE_ID_MAX_LENGTH = 21;
	
	//TeammatesServlet responses
	public static final String COORD_ADD_COURSE_RESPONSE_ADDED = "<status>course added</status>";
	public static final String COORD_ADD_COURSE_RESPONSE_EXISTS = "<status>course exists</status>";
	public static final String COORD_ADD_COURSE_RESPONSE_INVALID = "<status>course input invalid</status>";
	public static final String COORD_DELETE_COURSE_RESPONSE_DELETED = "<status>course deleted</status>";
	public static final String COORD_DELETE_COURSE_RESPONSE_NOT_DELETED = "<status>course not deleted</status>";
	
	//APIServlet responses
	public static final String BACKEND_STATUS_SUCCESS = "[BACKEND_STATUS_SUCCESS]";
	public static String BACKEND_STATUS_FAILURE = "[BACKEND_STATUS_FAILURE]";
	
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
	
	public static String getStatusForEval(EvaluationDetailsForCoordinator eval){
		if(eval.getStart().after(new Date())) return EVALUATION_STATUS_AWAITING;
		if(eval.getDeadline().after(new Date())) return EVALUATION_STATUS_OPEN;
		if(!eval.isPublished())	return EVALUATION_STATUS_CLOSED;
		return EVALUATION_STATUS_PUBLISHED;
	}
	
	public static String getHoverMessageForEval(EvaluationDetailsForCoordinator eval){
		String status = getStatusForEval(eval);
		if(status.equals(EVALUATION_STATUS_AWAITING)) return HOVER_MESSAGE_EVALUATION_STATUS_AWAITING;
		if(status.equals(EVALUATION_STATUS_OPEN)) return HOVER_MESSAGE_EVALUATION_STATUS_OPEN;
		if(status.equals(EVALUATION_STATUS_CLOSED)) return HOVER_MESSAGE_EVALUATION_STATUS_CLOSED;
		return HOVER_MESSAGE_EVALUATION_STATUS_PUBLISHED;
	}
	
	public static String getEvaluationActions(EvaluationDetailsForCoordinator eval, int position, boolean isHome){
		StringBuffer result = new StringBuffer();
		final String disabled = "style=\"text-decoration:none; color:gray;\" onclick=\"return false\"";
		
		boolean hasView = false;
		boolean hasEdit = false;
		boolean hasRemind = false;
		boolean hasPublish = false;
		boolean hasUnpublish = false;
		
		String status = getStatusForEval(eval);
		
		if(status.equals(EVALUATION_STATUS_AWAITING)){
			hasEdit = true;
		} else if(status.equals(EVALUATION_STATUS_OPEN)){
			hasView = true;
			hasEdit = true;
			hasRemind = true;
		} else if(status.equals(EVALUATION_STATUS_CLOSED)){
			hasView = true;
			hasEdit = true;
			hasPublish = true;
		} else { // EVALUATION_STATUS_PUBLISHED
			hasView = true;
			hasUnpublish = true;
		}
		
		result.append(
			"<a class='t_eval_view' name='viewEvaluation" + position + "' id='viewEvaluation"+ position + "'" +
			"href=\"coordEval.jsp?courseid="+ eval.getCourseID() + "&evalname=" + eval.getName() + "\"" +
			"onmouseover=\"ddrivetip('"+HOVER_MESSAGE_EVALUATION_VIEW+"')\""+
			"onmouseout=\"hideddrivetip()\"" + (hasView ? "" : disabled) + ">View Results</a>"
		);
		result.append(
			"<a class='t_eval_edit' name='editEvaluation" + position + "' id='editEvaluation" + position + "'" +
			"href=\"coordEvalEdit.jsp?courseid=" + eval.getCourseID() + "&evalname=" + eval.getName() + "\"" +
			"onmouseover=\"ddrivetip('"+HOVER_MESSAGE_EVALUATION_EDIT+"')\" onmouseout=\"hideddrivetip()\"" +
			(hasEdit ? "" : disabled) + ">Edit</a>"
		);
		result.append(
			"<a class='t_eval_delete' name='deleteEvaluation" + position + "' id='deleteEvaluation" + position + "'" +
			"href=\"coordDeleteEvaluation.jsp?courseid=" + eval.getCourseID() + "&evalname=" + eval.getName() + "\"" +
			"onclick=\"hideddrivetip(); return toggleDeleteEvaluationConfirmation('" + eval.getCourseID() + "','" +
			eval.getName() + "');\"" +
			"onmouseover=\"ddrivetip('"+HOVER_MESSAGE_EVALUATION_DELETE+"')\" onmouseout=\"hideddrivetip()\">Delete</a>"
		);
		result.append(
			"<a class='t_eval_remind' name='remindEvaluation" + position + "' id='remindEvaluation" + position + "'" +
			"href=\"javascript: hideddrivetip(); toggleRemindStudents('" + eval.getCourseID() + "','" + eval.getName() + "');\"" +
			"onmouseover=\"ddrivetip('"+HOVER_MESSAGE_EVALUATION_REMIND+"')\"" +
			"onmouseout=\"hideddrivetip()\"" + (hasRemind ? "" : disabled) + ">Remind</a>"
		);
		if (hasUnpublish) {
			result.append(
				"<a class='t_eval_unpublish' name='publishEvaluation" + position + "' id='publishEvaluation" + position + "'" +
				"href=\"javascript: hideddrivetip(); togglePublishEvaluation('" + eval.getCourseID() + "','" +
				eval.getName() + "'," + false + "," + (isHome ? "'coordHome.jsp'" : "'coordEval.jsp'") + ");\"" +
				"onmouseover=\"ddrivetip('"+HOVER_MESSAGE_EVALUATION_UNPUBLISH+"')\" onmouseout=\"hideddrivetip()\">" +
				"Unpublish</a>"
			);
		} else {
			result.append(
				"<a class='t_eval_publish' name='unpublishEvaluation" + position + "' id='publishEvaluation" + position + "'" +
				"href=\"javascript: hideddrivetip(); togglePublishEvaluation('" + eval.getCourseID() + "','" +
				eval.getName() + "'," + true + "," + (isHome ? "'coordHome.jsp'" : "'coordEval.jsp'") + ");\"" +
				"onmouseover=\"ddrivetip('"+HOVER_MESSAGE_EVALUATION_PUBLISH+"')\"" +
				"onmouseout=\"hideddrivetip()\"" + (hasPublish ? "" : disabled) + ">Publish</a>"
			);
		}
		return result.toString();
	}
}
