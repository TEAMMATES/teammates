package teammates.ui.controller;

import java.util.Hashtable;
import java.util.Vector;

import teammates.common.Common;

public class AdminActivityLogHelper extends Helper{
	public Vector<String> listOfServlets;
	public String[] servletSearchList;
	public String checkAllServlets;
	public String searchPerson;
	public String searchRole;
	public String offset;
	
	public AdminActivityLogHelper(){
		listOfServlets = new Vector<String>();
		
		//Manually add in all the possible lists of servlets that will appear in the form
		listOfServlets.add(Common.INSTRUCTOR_HOME_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_COURSE_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_COURSE_ENROLL_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_COURSE_EDIT_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_COURSE_DELETE_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_COURSE_DETAILS_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_COURSE_STUDENT_DELETE_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_COURSE_STUDENT_DETAILS_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_COURSE_REMIND_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_EVAL_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_EVAL_EXPORT_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_EVAL_EDIT_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_EVAL_DELETE_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_EVAL_REMIND_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_EVAL_PUBLISH_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_EVAL_RESULTS_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_EVAL_UNPUBLISH_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_EVAL_SUBMISSION_EDIT_HANDLER_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_EVAL_SUBMISSION_EDIT_SERVLET);
		listOfServlets.add(Common.INSTRUCTOR_EVAL_SUBMISSION_VIEW_SERVLET);
		listOfServlets.add(Common.STUDENT_HOME_SERVLET);
		listOfServlets.add(Common.STUDENT_COURSE_JOIN_SERVLET);
		listOfServlets.add(Common.STUDENT_COURSE_DETAILS_SERVLET);
		listOfServlets.add(Common.STUDENT_EVAL_EDIT_HANDLER_SERVLET);
		listOfServlets.add(Common.STUDENT_EVAL_EDIT_SERVLET);
		listOfServlets.add(Common.STUDENT_EVAL_RESULTS_SERVLET);
		listOfServlets.add(Common.EVALUATION_CLOSING_REMINDERS_SERVLET);
		listOfServlets.add(Common.EVALUATION_OPENING_REMINDERS_SERVLET);
	}
	
	/*
	 * To search the listOfServlets for a specific servlet
	 */
	public boolean searchServlets(String servletName){
		if (servletSearchList != null){
			for (int i = 0; i < servletSearchList.length; i++){
				if (servletSearchList[i].equals(servletName)){
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * Filters out the unwanted logs based on the input from the form
	 */
	public boolean performFiltering(String message){
		String[] tokens = message.split("\\|\\|\\|", -1);
		
		//Filter based on Person name, email and google Id
		if (searchPerson != null && !searchPerson.equals("")){
			String searchTerm = searchPerson.toLowerCase();
			if (!tokens[3].toLowerCase().contains(searchTerm) && !tokens[4].toLowerCase().contains(searchTerm) && !tokens[5].toLowerCase().contains(searchTerm)){
				return false;
			}
		}
		//Filter based on All/Instructor/Student/Others Role
		if (searchRole != null && !searchRole.equals("")){
			if((searchRole.equals("Instructor") || searchRole.equals("Student")) && !searchRole.equals(tokens[2])){
				return false;
			} else if (searchRole.equals("Others") && (tokens[2].equals("Instructor") || tokens[2].equals("Student"))){
				return false;
			}
		}
		//Filter based on Servlet
		if(servletSearchList == null || servletSearchList.length == 0){
			return false;
		} else if (!searchServlets(tokens[1])){
			return false;
		}
		
		return true;
	}
	
	
	
	/*
	 * Formats the Log message to a readable format
	 */
	public String parseLogMessage(String time, String message){
		String parsedMessage = "";
		//Log messages are in the format [TEAMMATES_LOG]|||Action|||Role|||Name|||Google Id|||Email|||Request Parameters
		//We use the delimiter |||, which is unlikely to appear in the Log message
		String[] tokens = message.split("\\|\\|\\|", -1);
		
		
		//Format information
		String actionName = servletToAction(tokens[1]);
		String formattedInformation = formatRequestParameters(tokens[1], tokens[6]);
		
		
		parsedMessage += "<td>" + time + "</td>";
		parsedMessage += "<td>" + tokens[2] + "</td>";
		parsedMessage += "<td><span title=\"" + tokens[4] + "\">" + tokens[3] + "<br>" + tokens[5] + "</span></td>";
		
		//For Servlet Actions
		if (!formattedInformation.equals("")){	
			parsedMessage += "<td><span class=\"bold\">" + actionName + "</span></td>";
			parsedMessage += "<td>" + formattedInformation + "</td>";
		} 
		//For System Errors
		else if(tokens[0].equals("[TEAMMATES_ERROR]")){
			parsedMessage += "<td><span class=\"bold color_negative\">" + tokens[1] + "</span></td>";
			parsedMessage += "<td><span class=\"color_negative\">" + tokens[6] + "</span></td>";
		}
		//For Page Loads
		else {
			parsedMessage += "<td><span class=\"bold\">" + tokens[1] + "</span></td>";
			parsedMessage += "<td>Page Load</td>";
		}
		
		return parsedMessage;
	}
	
	
	private static String formatRequestParameters(String servletName, String requestParams){

		requestParams = requestParams.substring(1, requestParams.length() - 1);
		if(requestParams.equals("")){
			return "";
		}
		Hashtable<String, String[]> parameterTable = generateTable(requestParams);
		String output = "";
		
		//Formatting is based on the servlet
		try{
			//Add New Course Action
			if (servletName.equals(Common.INSTRUCTOR_COURSE_SERVLET)){
				output = formatInstructorCourseServletData(parameterTable);
			}
			
			//Enroll Students Action
			else if (servletName.equals(Common.INSTRUCTOR_COURSE_ENROLL_SERVLET)){
				output = formatInstructorCourseEnrollServletData(parameterTable);
			}		
			
			//Edit Existing Course Action
			else if (servletName.equals(Common.INSTRUCTOR_COURSE_EDIT_SERVLET)){
				output = formatInstructorCourseEditServletData(parameterTable);
			}
			
			//Delete Existing Course Action
			else if (servletName.equals(Common.INSTRUCTOR_COURSE_DELETE_SERVLET)){
				output = formatInstructorCourseDeleteServletData(parameterTable);
			}
			
			//Edit Student Details Action
			else if (servletName.equals(Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET)){
				output = formatInstructorCourseStudentEditServletData(parameterTable);
			}
			
			//Delete Student Action
			else if (servletName.equals(Common.INSTRUCTOR_COURSE_STUDENT_DELETE_SERVLET)){
				output = formatInstructorCourseStudentDeleteServletData(parameterTable);
			}
			
			//Send Registration Action
			else if (servletName.equals(Common.INSTRUCTOR_COURSE_REMIND_SERVLET)){
				output = formatInstructorCourseRemindServletData(parameterTable);
			}
			
			//Create New Evaluation Action
			else if (servletName.equals(Common.INSTRUCTOR_EVAL_SERVLET)){
				output = formatInstructorEvalServletData(parameterTable);
			}		
			
			//Edit Evaluation Info Action
			else if (servletName.equals(Common.INSTRUCTOR_EVAL_EDIT_SERVLET)){
				output = formatInstructorEvalEditServletData(parameterTable);
			}
			
			//Delete Evaluation Action
			else if (servletName.equals(Common.INSTRUCTOR_EVAL_DELETE_SERVLET)){
				output = formatInstructorEvalDeleteServletData(parameterTable);
			}
			
			//Remind Students Action
			else if (servletName.equals(Common.INSTRUCTOR_EVAL_REMIND_SERVLET)){
				output = formatInstructorEvalRemindServletData(parameterTable);
			}
			
			//Publish Evaluation Action
			else if (servletName.equals(Common.INSTRUCTOR_EVAL_PUBLISH_SERVLET)){
				output = formatInstructorEvalPublishServletData(parameterTable);
			}
			
			//Unpublish Evaluation Action
			else if (servletName.equals(Common.INSTRUCTOR_EVAL_UNPUBLISH_SERVLET)){
				output = formatInstructorEvalUnpublishServletData(parameterTable);
			}
			
			//Instructor Edit Submission Action
			else if (servletName.equals(Common.INSTRUCTOR_EVAL_SUBMISSION_EDIT_HANDLER_SERVLET)){
				output = formatInstructorEvalSubmissionEditHandlerServletData(parameterTable);
			}
			
			//Student Edit Submission Action
			else if (servletName.equals(Common.STUDENT_EVAL_EDIT_HANDLER_SERVLET)){
				output = formatStudentEvalEditHandlerServletData(parameterTable);
			}
			
			//Evaluation Closing Reminders Action
			else if (servletName.equals(Common.EVALUATION_CLOSING_REMINDERS_SERVLET)){
				output = formatEvaluationClosingRemindersServletData(parameterTable);
			}
			
			//Evaluation Opening Reminders Action
			else if (servletName.equals(Common.EVALUATION_OPENING_REMINDERS_SERVLET)){
				output = formatEvaluationOpeningRemindersServletData(parameterTable);
			}
			
			//Student Course Join Action
			else if (servletName.equals(Common.STUDENT_COURSE_JOIN_SERVLET)){
				output = formatStudentCourseJoinServletData(parameterTable);
			}
		} catch (Exception e){
			output = "Error Processing Parameters<br>" + servletName + ": " + requestParams;
		}
		return output;
	}
	
	
	private static String formatInstructorCourseServletData(Hashtable<String, String[]> parameterTable) {
		String courseId, courseName, courseInstructorList;
		
		try {
			courseId = parameterTable.get(Common.PARAM_COURSE_ID)[0];
			courseName = parameterTable.get(Common.PARAM_COURSE_NAME)[0];
			courseInstructorList = parameterTable.get(Common.PARAM_COURSE_INSTRUCTOR_LIST)[0];
			courseInstructorList = " - " + courseInstructorList;
		} catch (NullPointerException e) {
			return "";
		}
		
		return "A New Course [" + courseId + "] : " + courseName + " has been created.<br><span class=\"bold\">List of Instructors:</span><br>" + courseInstructorList.replace("\n", "<br> - ");
	}
	
	
	private static String formatInstructorCourseEnrollServletData(Hashtable<String, String[]> parameterTable){
		String studentList, courseId;
		
		try {
			courseId = parameterTable.get(Common.PARAM_COURSE_ID)[0];
			studentList = parameterTable.get(Common.PARAM_STUDENTS_ENROLLMENT_INFO)[0];
			studentList = " - " + studentList;
		} catch (NullPointerException e) {
			return "";
		}
		
		return "<span class=\"bold\">Students Enrolled in Course [" + courseId + "]:</span><br>" + studentList.replace("\n", "<br> - ");
	}
	
	
	private static String formatInstructorCourseEditServletData(Hashtable<String, String[]> parameterTable){
		String courseId, courseInstructorList, submit;
		
		try{
			submit = parameterTable.get("submit")[0];
			courseId = parameterTable.get(Common.PARAM_COURSE_ID)[0];
			courseInstructorList = parameterTable.get(Common.PARAM_COURSE_INSTRUCTOR_LIST)[0];
			courseInstructorList = " - " + courseInstructorList;
		} catch (NullPointerException e){
			return "";
		}
		
		return "Course [" + courseId + "] edited.<br><span class=\"bold\">New Instructor List:</span> <br>" + courseInstructorList.replace("\n", "<br> - ");
	}
	
	
	private static String formatInstructorCourseDeleteServletData(Hashtable<String, String[]> parameterTable){
		String courseId;
		
		try{
			courseId = parameterTable.get(Common.PARAM_COURSE_ID)[0];
		} catch (NullPointerException e){
			return "";
		}
		
		return "Course [" + courseId + "] deleted.";
	}
	
	
	private static String formatInstructorCourseStudentEditServletData(Hashtable<String, String[]> parameterTable){
		String submit, courseId, studentName, studentEmail, studentTeam, comments;
		
		try{
			submit = parameterTable.get("submit")[0];
			courseId = parameterTable.get(Common.PARAM_COURSE_ID)[0];
			studentName = parameterTable.get(Common.PARAM_STUDENT_NAME)[0];
			studentEmail = parameterTable.get(Common.PARAM_NEW_STUDENT_EMAIL)[0];
			studentTeam = parameterTable.get(Common.PARAM_TEAM_NAME)[0];
			comments = parameterTable.get(Common.PARAM_COMMENTS)[0];
		} catch (NullPointerException e){
			return "";
		}
		
		return "Student " + studentName + "'s details in Course [" + courseId + "] edited.<br>New Email: " + studentEmail + "<br>New Team: " + studentTeam + "<br>Comments: " + comments; 
	}
	
	
	private static String formatInstructorCourseStudentDeleteServletData(Hashtable<String, String[]> parameterTable){
		String studentEmail, courseId;
		
		try{
			courseId = parameterTable.get(Common.PARAM_COURSE_ID)[0];
			studentEmail = parameterTable.get(Common.PARAM_STUDENT_EMAIL)[0];
		} catch (NullPointerException e){
			return "";
		}
		
		return "Student " + studentEmail + " in Course [" + courseId + "] deleted.";
	}
	
	
	private static String formatInstructorCourseRemindServletData(Hashtable<String, String[]> parameterTable){
		String studentEmail, courseId;
		
		try{
			courseId = parameterTable.get(Common.PARAM_COURSE_ID)[0];
		} catch (NullPointerException e){
			return "";
		}
		
		try{
			studentEmail = parameterTable.get(Common.PARAM_STUDENT_EMAIL)[0];
		} catch (NullPointerException e){
			studentEmail = "";
		}
		
		if (studentEmail.equals("")){
			return "Registration Key sent to all unregistered students in Course [" + courseId + "]"; 
		} else {
			return "Registration Key sent to " + studentEmail + " in Course [" + courseId + "]";
		}
		
	}
	
	
	private static String formatInstructorEvalServletData(Hashtable<String, String[]> parameterTable){
		String courseId, start, deadline, startTime, deadlineTime, gracePeriod, timezone, peerFeedback, evaluationName, instructions;
		
		try{
			courseId = parameterTable.get(Common.PARAM_COURSE_ID)[0];
			evaluationName = parameterTable.get(Common.PARAM_EVALUATION_NAME)[0];
			start = parameterTable.get(Common.PARAM_EVALUATION_START)[0];
			deadline = parameterTable.get(Common.PARAM_EVALUATION_DEADLINE)[0];
			startTime = parameterTable.get(Common.PARAM_EVALUATION_STARTTIME)[0];
			deadlineTime = parameterTable.get(Common.PARAM_EVALUATION_DEADLINETIME)[0];
			gracePeriod = parameterTable.get(Common.PARAM_EVALUATION_GRACEPERIOD)[0];
			timezone = parameterTable.get(Common.PARAM_EVALUATION_TIMEZONE)[0];
			peerFeedback = parameterTable.get(Common.PARAM_EVALUATION_COMMENTSENABLED)[0];
			instructions = parameterTable.get(Common.PARAM_EVALUATION_INSTRUCTIONS)[0];
		} catch (NullPointerException e){
			return "";
		}
		
		return "New Evaluation (" + evaluationName + ") for Course [" + courseId + "] created.<br>" +
				"<span class=\"bold\">From:</span> " + start + " (" + startTime + "00HR) <span class=\"bold\">to</span> " + deadline + " (" + deadlineTime + "00HR)<br>" +
				"<span class=\"bold\">Peer feedback:</span> " + (peerFeedback.equals("true") ? "enabled" : "disabled") + "<br><br>" + 
				"<span class=\"bold\">Instructions:</span> " + instructions;
	}
	
	
	private static String formatInstructorEvalEditServletData(Hashtable<String, String[]> parameterTable){
		String courseId, start, deadline, startTime, deadlineTime, gracePeriod, timezone, peerFeedback, evaluationName, instructions;
		
		try{
			courseId = parameterTable.get(Common.PARAM_COURSE_ID)[0];
			evaluationName = parameterTable.get(Common.PARAM_EVALUATION_NAME)[0];
			start = parameterTable.get(Common.PARAM_EVALUATION_START)[0];
			deadline = parameterTable.get(Common.PARAM_EVALUATION_DEADLINE)[0];
			startTime = parameterTable.get(Common.PARAM_EVALUATION_STARTTIME)[0];
			deadlineTime = parameterTable.get(Common.PARAM_EVALUATION_DEADLINETIME)[0];
			gracePeriod = parameterTable.get(Common.PARAM_EVALUATION_GRACEPERIOD)[0];
			timezone = parameterTable.get(Common.PARAM_EVALUATION_TIMEZONE)[0];
			peerFeedback = parameterTable.get(Common.PARAM_EVALUATION_COMMENTSENABLED)[0];
			instructions = parameterTable.get(Common.PARAM_EVALUATION_INSTRUCTIONS)[0];
		} catch (NullPointerException e){
			return "";
		}
		
		return "Evaluation (" + evaluationName + ") for Course [" + courseId + "] edited.<br>" +
		"<span class=\"bold\">From:</span> " + start + " (" + startTime + "00HR) <span class=\"bold\">to</span> " + deadline + " (" + deadlineTime + "00HR)<br>" +
		"<span class=\"bold\">Peer feedback:</span> " + (peerFeedback.equals("true") ? "enabled" : "disabled") + "<br><br>" + 
		"<span class=\"bold\">Instructions:</span> " + instructions;
	}
	
	
	private static String formatInstructorEvalDeleteServletData(Hashtable<String, String[]> parameterTable){
		String courseId, evaluationName;
		
		try{
			courseId = parameterTable.get(Common.PARAM_COURSE_ID)[0];
			evaluationName = parameterTable.get(Common.PARAM_EVALUATION_NAME)[0];
		} catch (NullPointerException e) {
			return "";
		}
		
		return "Evaluation (" + evaluationName + ") for Course [" + courseId + "] deleted.";
	}
	
	
	private static String formatInstructorEvalRemindServletData(Hashtable<String, String[]> parameterTable){
		String courseId, evaluationName;
		
		try{
			courseId = parameterTable.get(Common.PARAM_COURSE_ID)[0];
			evaluationName = parameterTable.get(Common.PARAM_EVALUATION_NAME)[0];
		} catch (NullPointerException e){
			return "";
		}
		
		return "Email sent out to all students who have not completed Evaluation (" + evaluationName + ") of Course [" + courseId + "]";
	}
	
	
	private static String formatInstructorEvalPublishServletData(Hashtable<String, String[]> parameterTable){
		String courseId, evaluationName;
		
		try{
			courseId = parameterTable.get(Common.PARAM_COURSE_ID)[0];
			evaluationName = parameterTable.get(Common.PARAM_EVALUATION_NAME)[0];
		} catch (NullPointerException e){
			return "";
		}
		
		return "Evaluation (" + evaluationName + ") for Course [" + courseId + "] published.";
	}
	
	
	private static String formatInstructorEvalUnpublishServletData(Hashtable<String, String[]> parameterTable){
		String courseId, evaluationName;
		
		try{
			courseId = parameterTable.get(Common.PARAM_COURSE_ID)[0];
			evaluationName = parameterTable.get(Common.PARAM_EVALUATION_NAME)[0];
		} catch (NullPointerException e){
			return "";
		}
		
		return "Evaluation (" + evaluationName + ") for Course [" + courseId + "] unpublished.";
	}
	
	
	private static String formatInstructorEvalSubmissionEditHandlerServletData(Hashtable<String, String[]> parameterTable){
		String teamName, fromEmail, courseId, evaluationName;
		String[] points, comments, justifications, toEmails;
		
		try{
			teamName = parameterTable.get(Common.PARAM_TEAM_NAME)[0];
			fromEmail = parameterTable.get(Common.PARAM_FROM_EMAIL)[0];
			courseId = parameterTable.get(Common.PARAM_COURSE_ID)[0];
			evaluationName = parameterTable.get(Common.PARAM_EVALUATION_NAME)[0];
			toEmails = parameterTable.get(Common.PARAM_TO_EMAIL);
			points = parameterTable.get(Common.PARAM_POINTS);
			comments = parameterTable.get(Common.PARAM_COMMENTS);
			justifications = parameterTable.get(Common.PARAM_JUSTIFICATION);
		} catch (NullPointerException e){
			return "";
		}
		
		String output = "(" + teamName + ")" + fromEmail + "'s Submission for Evaluation (" + evaluationName + ") for Course [" + courseId + "] edited.<br><br>";
		for (int i = 0; i < toEmails.length; i++){
			output += "<span class=\"bold\">To:</span> " + toEmails[i] + "<br>";
			output += "<span class=\"bold\">Points:</span> " + points[i] + "<br>";
			if (comments == null){	//p2pDisabled
				output += "<span class=\"bold\">Comments: </span>Disabled<br>";
			} else {
				output += "<span class=\"bold\">Comments:</span> " + comments[i].replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>") + "<br>";
			}
			output += "<span class=\"bold\">Justification:</span> " + justifications[i].replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>") + "<br><br>";
		}
		
		return output;
	}
	
	
	private static String formatStudentEvalEditHandlerServletData(Hashtable<String, String[]> parameterTable){
		String teamName, fromEmail, courseId, evaluationName;
		String[] points, comments, justifications, toEmails;
		
		try{
			teamName = parameterTable.get(Common.PARAM_TEAM_NAME)[0];
			fromEmail = parameterTable.get(Common.PARAM_FROM_EMAIL)[0];
			courseId = parameterTable.get(Common.PARAM_COURSE_ID)[0];
			evaluationName = parameterTable.get(Common.PARAM_EVALUATION_NAME)[0];
			toEmails = parameterTable.get(Common.PARAM_TO_EMAIL);
			points = parameterTable.get(Common.PARAM_POINTS);
			comments = parameterTable.get(Common.PARAM_COMMENTS);
			justifications = parameterTable.get(Common.PARAM_JUSTIFICATION);
		} catch (NullPointerException e){
			return "";
		}
		
		String output = "(" + teamName + ")" + fromEmail + "'s Submission for Evaluation (" + evaluationName + ") for Course [" + courseId + "] edited.<br><br>";
		
		for (int i = 0; i < toEmails.length; i++){
			output += "<span class=\"bold\">To:</span> " + toEmails[i] + "<br>";
			output += "<span class=\"bold\">Points:</span> " + points[i] + "<br>";
			if (comments == null){	//p2pDisabled
				output += "<span class=\"bold\">Comments: </span>Disabled<br>";
			} else {
				output += "<span class=\"bold\">Comments:</span> " + comments[i].replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>") + "<br>";
			}
			output += "<span class=\"bold\">Justification:</span> " + justifications[i].replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>") + "<br><br>";
		}
		
		return output;
	}
	
	private static String formatEvaluationClosingRemindersServletData(Hashtable<String, String[]> parameterTable){
		String[] emails;
		
		try{
			emails = parameterTable.get("targets");
		} catch (NullPointerException e){
			return "Unable to retrieve Email targets";
		}
		
		String output = "<span class=\"bold\">Emails sent to:</span><br>";
		for (int i = 0; i < emails.length; i++){
			output += emails[i] + "<br>";
		}
		
		return output;
	}
	
	
	private static String formatEvaluationOpeningRemindersServletData(Hashtable<String, String[]> parameterTable){
		String[] emails;
		
		try{
			emails = parameterTable.get("targets");
		} catch (NullPointerException e){
			return "Unable to retrieve Email targets";
		}
		
		String output = "<span class=\"bold\">Emails sent to:</span><br>";
		for (int i = 0; i < emails.length; i++){
			output += emails[i] + "<br>";
		}
		
		return output;
	}
	
	
	private static String formatStudentCourseJoinServletData(Hashtable<String, String[]> parameterTable){
		String registrationKey;
		
		try{
			registrationKey = parameterTable.get(Common.PARAM_REGKEY)[0];
		} catch(NullPointerException e){
			return "";
		}
		
		return "Student joined course with registration key: " + registrationKey;
	}
	
	
	/*
	 * Maps the Servlet Name to the action the servlet does
	 */
	private static String servletToAction(String servletName){
		if(servletName.equals(Common.INSTRUCTOR_COURSE_SERVLET)){
			return Common.INSTRUCTOR_COURSE_SERVLET_ACTION;
		} else if (servletName.equals(Common.INSTRUCTOR_COURSE_ENROLL_SERVLET)){
			return Common.INSTRUCTOR_COURSE_ENROLL_SERVLET_ACTION;
		} else if (servletName.equals(Common.INSTRUCTOR_COURSE_EDIT_SERVLET)){
			return Common.INSTRUCTOR_COURSE_EDIT_SERVLET_ACTION;
		} else if (servletName.equals(Common.INSTRUCTOR_COURSE_DELETE_SERVLET)){
			return Common.INSTRUCTOR_COURSE_DELETE_SERVLET_ACTION;
		} else if (servletName.equals(Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET)) {
			return Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET_ACTION;
		} else if (servletName.equals(Common.INSTRUCTOR_COURSE_STUDENT_DELETE_SERVLET)) {
			return Common.INSTRUCTOR_COURSE_STUDENT_DELETE_SERVLET_ACTION;
		} else if (servletName.equals(Common.INSTRUCTOR_COURSE_REMIND_SERVLET)) {
			return Common.INSTRUCTOR_COURSE_REMIND_SERVLET_ACTION;
		} else if (servletName.equals(Common.INSTRUCTOR_EVAL_SERVLET)) {
			return Common.INSTRUCTOR_EVAL_SERVLET_ACTION;
		} else if (servletName.equals(Common.INSTRUCTOR_EVAL_EDIT_SERVLET)) {
			return Common.INSTRUCTOR_EVAL_EDIT_SERVLET_ACTION;
		} else if (servletName.equals(Common.INSTRUCTOR_EVAL_DELETE_SERVLET)) {
			return Common.INSTRUCTOR_EVAL_DELETE_SERVLET_ACTION;
		} else if (servletName.equals(Common.INSTRUCTOR_EVAL_REMIND_SERVLET)) {
			return Common.INSTRUCTOR_EVAL_REMIND_SERVLET_ACTION;
		} else if (servletName.equals(Common.INSTRUCTOR_EVAL_PUBLISH_SERVLET)) {
			return Common.INSTRUCTOR_EVAL_PUBLISH_SERVLET_ACTION;
		} else if (servletName.equals(Common.INSTRUCTOR_EVAL_UNPUBLISH_SERVLET)) {
			return Common.INSTRUCTOR_EVAL_UNPUBLISH_SERVLET_ACTION;
		} else if (servletName.equals(Common.INSTRUCTOR_EVAL_SUBMISSION_EDIT_HANDLER_SERVLET)) {
			return Common.INSTRUCTOR_EVAL_SUBMISSION_EDIT_HANDLER_SERVLET_ACTION;
		} else if (servletName.equals(Common.STUDENT_EVAL_EDIT_HANDLER_SERVLET)) {
			return Common.STUDENT_EVAL_EDIT_HANDLER_SERVLET_ACTION;
		} else if (servletName.equals(Common.EVALUATION_CLOSING_REMINDERS_SERVLET)) {
			return Common.EVALUATION_CLOSING_REMINDERS_SERVLET_ACTION;
		} else if (servletName.equals(Common.EVALUATION_OPENING_REMINDERS_SERVLET)) {
			return Common.EVALUATION_OPENING_REMINDERS_SERVLET_ACTION;
		} else if (servletName.equals(Common.STUDENT_COURSE_JOIN_SERVLET)) {
			return Common.STUDENT_COURSE_JOIN_SERVLET_ACTION;
		} else {
			return "Unknown: " + servletName;
		}
	}
	
	/*
	 * Generates a table of parameter names and values from the requestParameters string
	 */
	private static Hashtable<String, String[]> generateTable(String requestParams){
		//request parameters are in the format name1::value1//value2//value3, name2::value1//value2, ....
		
		Hashtable<String, String[]> table = new Hashtable<String, String[]>();
		String[] parameters = requestParams.split(", ", -1);
		
		for (int i = 0; i < parameters.length; i++){
			String[] pair = parameters[i].split("::");		//pair[0] = parameter name, pair[1] = parameter values
			String[] values;
			try {
				values = pair[1].split("//", -1);
			} catch (ArrayIndexOutOfBoundsException e) {
				values = new String[1];				
			}
			table.put(pair[0], values);
		}
		
		return table;
	}
	
	
}
