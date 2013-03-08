package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.UserType;
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
		String url = req.getRequestURI();
		if (req.getQueryString() != null){
			url += "?" + req.getQueryString();
		}
		
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
		
		activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_SERVLET, action,
				true, helper, url, null);
	}

	private void createCourse(InstructorCourseHelper helper) {
		helper.formCourseId = helper.courseID;
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
			helper.server.updateCourseInstructors(helper.formCourseId, helper.instructorList);
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
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		InstructorCourseHelper h = (InstructorCourseHelper) helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.INSTRUCTOR_COURSE_SERVLET_PAGE_LOAD){
			try {
				params = "instructorCourse Page Load<br>";
				params += "Total courses: " + h.courses.size();
			} catch (NullPointerException e) {
				params = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else if (action == Common.INSTRUCTOR_COURSE_SERVLET_ADD_COURSE){
			try {
				params = "A New Course <span class=\"bold\">[" + h.formCourseId + "] " + h.formCourseName + "</span> has been created.<br>";
				params += "Instructor List:<br>";
				String[] instructors = h.instructorList.split("\n", -1);
				for (String instructor : instructors){
					params += "  - " + instructor + "<br>";
				}
			} catch (NullPointerException e){
				params = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else if (action == Common.LOG_SERVLET_ACTION_FAILURE) {
            String e = (String)data.get(0);
            params = "<span class=\"color_red\">Servlet Action failure in " + servletName + "<br>";
            params += e + "</span>";
        } else {
			params = "<span class=\"color_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
				
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}
}
