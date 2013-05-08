package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.TeamResultBundle;
import teammates.common.exception.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle student details (showing team members, and in future for Team Forming as well)
 * @author Aldrian Obaja
 *
 */
public class StudentCourseDetailsServlet extends ActionServlet<StudentCourseDetailsHelper> {

	@Override
	protected StudentCourseDetailsHelper instantiateHelper() {
		return new StudentCourseDetailsHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, StudentCourseDetailsHelper helper)
			throws EntityDoesNotExistException {
		String url = getRequestedURL(req);
        
		// Get parameters
		String courseId = req.getParameter(Common.PARAM_COURSE_ID);
		if (courseId == null) {
			helper.redirectUrl = Common.PAGE_STUDENT_HOME;
			
			ArrayList<Object> data = new ArrayList<Object>();
	        data.add("Course Id is null");	                        
	        activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_COURSE_DETAILS_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		true, helper, url, data);
			return;
		}
		
		helper.courseDetails = helper.server.getCourseDetails(courseId);
		helper.instructors = helper.server.getInstructorsOfCourse(courseId);
		
		helper.student = helper.server.getStudentInCourseForGoogleId(courseId, helper.userId);
		helper.team = getTeam(helper.server.getTeamsForCourse(courseId),helper.student);
		
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(helper.courseDetails.course.id);
		data.add(helper.courseDetails.course.name);
		activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_COURSE_DETAILS_SERVLET, Common.STUDENT_COURSE_DETAILS_SERVLET_PAGE_LOAD,
				true, helper, url, data);
	}
	
	/**
	 * Returns the TeamData object of the student in the course
	 * @param course
	 * @param student
	 * @return
	 */
	private TeamDetailsBundle getTeam(CourseDetailsBundle course, StudentAttributes student){
		for(TeamDetailsBundle team: course.teams){
			if(team.name.equals(student.team)){
				return team;
			}
		}
		return null;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_STUDENT_COURSE_DETAILS;
	}


	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_COURSE_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
			
		return message;
	}
	
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "studentCourseDetails Page Load<br>";
			message += "Viewing team details for <span class=\"bold\">[" + (String)data.get(0) + "] " + (String)data.get(1) + "</span>";   
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";  
		}
		
		return message;
	}

}
