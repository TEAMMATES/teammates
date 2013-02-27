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
		String action = Common.INSTRUCTOR_COURSE_SERVLET_PAGE_LOAD;
		helper.courseID = req.getParameter(Common.PARAM_COURSE_ID);
		helper.courseName = req.getParameter(Common.PARAM_COURSE_NAME);
		helper.instructorList = req.getParameter(Common.PARAM_COURSE_INSTRUCTOR_LIST);

		if (helper.courseID != null && helper.courseName != null && helper.courseName != null) {
			createCourse(helper);
			action = Common.INSTRUCTOR_COURSE_SERVLET_ADD_COURSE;
		}

		HashMap<String, CourseData> courses = helper.server
				.getCourseListForInstructor(helper.userId);
		helper.courses = new ArrayList<CourseData>(courses.values());
		
		sortCourses(helper.courses);
		
		setStatus(helper);
		activityLog = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_SERVLET, action, true, helper);
	}

	private void createCourse(InstructorCourseHelper helper) {
		helper.formCourseID = helper.courseID;
		helper.formCourseName = helper.courseName;
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
			helper.server.updateCourseInstructors(helper.formCourseID, helper.instructorList);
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
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper) {
		InstructorCourseHelper h = (InstructorCourseHelper) helper;
		String params;
		
		h.user = helper.server.getLoggedInUser();
		h.account = helper.server.getAccount(h.user.id);
		
		if(action == Common.INSTRUCTOR_COURSE_SERVLET_PAGE_LOAD){
			try {
				params = "instructorCourse Page Load<br>";
				for(CourseData course: h.courses){
					params += " - [" + course.id + "] " + course.name + "<br>";
				}
			} catch (NullPointerException e) {
				params = "<span class=\"colour_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else if (action == Common.INSTRUCTOR_COURSE_SERVLET_ADD_COURSE){
			try {
				params = "A New Course [" + h.formCourseID + "] : " + h.formCourseName + " has been created.<br>";
				params += "Instructor List:<br>";
				String[] instructors = h.instructorList.split("\n", -1);
				for (String instructor : instructors){
					params += "  - " + instructor + "<br>";
				}
			} catch (NullPointerException e){
				params = "<span class=\"colour_red\">Null variables detected in " + servletName + ": " + action + ".</span>"; 
			}
		} else {
			params = "<span class=\"colour_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
			
		return new ActivityLogEntry(servletName, action, true, h.account, params);
	}
}
