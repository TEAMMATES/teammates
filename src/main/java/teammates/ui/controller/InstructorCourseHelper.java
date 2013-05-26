package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

public class InstructorCourseHelper extends Helper{
	//TODO: change to courseId
	public String courseID;
	public String courseName;
	public String instructorList;
	public List<CourseDetailsBundle> courses;
	
	//TODO: unit test this class
	
	public void setStatus() {
		//TODO: implement better mechanism for multiple status messages.
		if (courses.size() == 0
				&& !error
				&& !noCoursesVisibleDueToEventualConsistency()) {
			if (statusMessage == null){
				statusMessage = "";
			}else{
				statusMessage += "<br />";
			}
			statusMessage += Common.MESSAGE_COURSE_EMPTY;
		}
	}
	
	public void createCourse(HttpServletRequest req){
		courseID = req.getParameter(Common.PARAM_COURSE_ID);
		courseName = req.getParameter(Common.PARAM_COURSE_NAME);
		instructorList = req.getParameter(Common.PARAM_COURSE_INSTRUCTOR_LIST);
		createCourse();
	}
	
	public void loadCourseList() throws EntityDoesNotExistException {
		courses = new ArrayList<CourseDetailsBundle>(
				server.getCourseSummariesForInstructor(userId).values());
		ActionServlet.sortDetailedCourses(courses);
	}
	
	public static String generateActivityLogEntryMessageForCourseList(String servletName, String action, ArrayList<Object> data) {
		 
		String message;
		
		if(action.equals(Common.INSTRUCTOR_COURSE_SERVLET_PAGE_LOAD)){
			message = InstructorCourseHelper.generatePageLoadMessage(servletName, action, data);
		} else {
			message = ActionServlet.generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	public static String generateActivityLogEntryMessageForCourseAdd(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if (action.equals(Common.INSTRUCTOR_COURSE_SERVLET_ADD_COURSE)){
			message = generateAddCourseMessage(servletName, action, data);
		} else {
			message = ActionServlet.generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	private void createCourse() {
		//TODO: make this method atomic.
		String courseInstitute = account.institute;
		String courseIdForNewCourse = courseID;
		
		try {
			server.createCourseAndInstructor(userId, courseID, courseName);
			courseID = null;
			courseName = null;
			statusMessage = Common.MESSAGE_COURSE_ADDED;
		} catch (EntityAlreadyExistsException e) {
			statusMessage = Common.MESSAGE_COURSE_EXISTS;
			error = true;
		} catch (InvalidParametersException e) {
			statusMessage = e.getMessage();
			error = true;
		}
		
		if(error){
			return;
		}
		
		try{
			server.updateCourseInstructors(courseIdForNewCourse, instructorList, courseInstitute);
		} catch (InvalidParametersException e){
			statusMessage = e.getMessage();
			error = true;
		} catch (EntityDoesNotExistException e) {
			Assumption.fail("The course created did not persist properly :"+ courseID);
		}
	}

	private boolean noCoursesVisibleDueToEventualConsistency() {
		return statusMessage != null
				&& statusMessage.equals(Common.MESSAGE_COURSE_ADDED)
				&& courses.size()==0;
	}

	private static String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "instructorCourse Page Load<br>";
			message += "Total courses: " +(Integer)data.get(0);;
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
	
	private static String generateAddCourseMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "A New Course <span class=\"bold\">[" + (String)data.get(0) + "] " + (String)data.get(1) + "</span> has been created.<br>";
			message += "Instructor List:<br>";
			String[] instructors = ((String)data.get(2)).split("\n", -1);
			for (String instructor : instructors){
				message += "  - " + instructor + "<br>";
			}
		} catch (NullPointerException e){
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}

}
