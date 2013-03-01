package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.TeamData;
import teammates.common.datatransfer.UserType;
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
		// Get parameters
		String courseId = req.getParameter(Common.PARAM_COURSE_ID);
		if (courseId == null) {
			helper.redirectUrl = Common.PAGE_STUDENT_HOME;
			return;
		}
		
		helper.course = helper.server.getCourseDetails(courseId);
		helper.instructors = helper.server.getInstructorsByCourseId(courseId);
		
		helper.student = helper.server.getStudentInCourseForGoogleId(courseId, helper.userId);
		helper.team = getTeam(helper.server.getTeamsForCourse(courseId),helper.student);
		
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        }
		activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_COURSE_DETAILS_SERVLET, Common.STUDENT_COURSE_DETAILS_SERVLET_PAGE_LOAD,
				true, helper, url, null);
	}
	
	/**
	 * Returns the TeamData object of the student in the course
	 * @param course
	 * @param student
	 * @return
	 */
	private TeamData getTeam(CourseData course, StudentData student){
		for(TeamData team: course.teams){
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
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		StudentCourseDetailsHelper h = (StudentCourseDetailsHelper) helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.INSTRUCTOR_COURSE_SERVLET_PAGE_LOAD){
			try {
				params = "studentCourseDetails Page Load<br>";
				params += "Viewing team details for <span class=\"bold\">[" + h.course.id + "] " + h.course.name + "</span>";   
			} catch (NullPointerException e) {
				params = "<span class=\"colour_red\">Null variables detected in " + servletName + ": " + action + ".</span>";  
			}
		} else {
			params = "<span class=\"colour_red\">Unknown Action - " + servletName + ": " + action + ".</span>";      
		}
			
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}

}
