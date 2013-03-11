package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Add Course and Display Courses action
 */
public class InstructorCourseServlet extends ActionServlet<InstructorCourseHelper> {

	@Override
	protected InstructorCourseHelper instantiateHelper() {
		return new InstructorCourseHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, InstructorCourseHelper helper)
			throws EntityDoesNotExistException {
		boolean createCourse = false;
		String url = getRequestedURL(req);

		helper.courseID = req.getParameter(Common.PARAM_COURSE_ID);
		helper.courseName = req.getParameter(Common.PARAM_COURSE_NAME);
		helper.instructorList = req.getParameter(Common.PARAM_COURSE_INSTRUCTOR_LIST);

		if (helper.courseID != null && helper.courseName != null && helper.instructorList != null) {
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(helper.courseID);
			data.add(helper.courseName);
			data.add(helper.instructorList);
			createCourse(helper);
			
			activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_SERVLET, Common.INSTRUCTOR_COURSE_SERVLET_ADD_COURSE,
					true, helper, url, data);
			createCourse = true;
		}
		
		HashMap<String, CourseData> courses = helper.server
				.getCourseListForInstructor(helper.userId);
		helper.courses = new ArrayList<CourseData>(courses.values());
		
		sortCourses(helper.courses);	
		setStatus(helper);
		
		if (!createCourse) {
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(helper.courses.size());
			activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_SERVLET, Common.INSTRUCTOR_COURSE_SERVLET_PAGE_LOAD,
					true, helper, url, data);
		}
	}

	private void createCourse(InstructorCourseHelper helper) {
		String courseId = helper.courseID;
		String courseInstitute = helper.account.institute;	// Assumption: Logged in user is from the Institute of the course, not App Admin
		try {
			helper.server.createCourse(helper.userId, helper.courseID,
					helper.courseName);
			helper.courseID = null;
			helper.courseName = null;
			helper.statusMessage = Common.MESSAGE_COURSE_ADDED;
		} catch (EntityAlreadyExistsException e) {
			helper.statusMessage = Common.MESSAGE_COURSE_EXISTS;
			helper.error = true;
			
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
		if(helper.error){
			return;
		}
		try{
			helper.server.updateCourseInstructors(courseId, helper.instructorList, courseInstitute);
		} catch (InvalidParametersException e){
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
	}

	//TODO: unit test this
	private void setStatus(InstructorCourseHelper helper) {
		if (helper.courses.size() == 0
				&& !helper.error
				&& !noCoursesVisibleDueToEventualConsistency(helper)) {
			if (helper.statusMessage == null){
				helper.statusMessage = "";
			}else{
				helper.statusMessage += "<br />";
			}
			helper.statusMessage += Common.MESSAGE_COURSE_EMPTY;
		}
	}
	
	private boolean noCoursesVisibleDueToEventualConsistency(InstructorCourseHelper helper) {
		return helper.statusMessage != null
				&& helper.statusMessage.equals(Common.MESSAGE_COURSE_ADDED)
				&& helper.courses.size()==0;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE;
	}


	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		
		String message;
		
		if(action.equals(Common.INSTRUCTOR_COURSE_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else if (action.equals(Common.INSTRUCTOR_COURSE_SERVLET_ADD_COURSE)){
			message = generateAddCourseMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "instructorCourse Page Load<br>";
			message += "Total courses: " +(Integer)data.get(0);;
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
	
	private String generateAddCourseMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "A New Course <span class=\"bold\">[" + (String)data.get(0) + "] " + (String)data.get(1) + "</span> has been created.<br>";
			
		} catch (NullPointerException e){
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
