package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Instructor Course Edit action
 *
 */
public class InstructorCourseEditServlet extends ActionServlet<InstructorCourseEditHelper> {

	@Override
	protected InstructorCourseEditHelper instantiateHelper() {
		return new InstructorCourseEditHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, InstructorCourseEditHelper helper){
		boolean isSubmit = isPost;
		
		if(isSubmit){
			String instructorList = req.getParameter(Common.PARAM_COURSE_INSTRUCTOR_LIST);
			String courseID = req.getParameter(Common.PARAM_COURSE_ID);
			String institute = helper.account.institute;
			try{
				helper.server.updateCourseInstructors(courseID, instructorList, institute);				
				helper.statusMessage = Common.MESSAGE_COURSE_EDITED;
				helper.redirectUrl = Common.PAGE_INSTRUCTOR_COURSE;
				
			} catch (InvalidParametersException e){
				helper.statusMessage = e.getMessage();
				helper.error = true;
			}
			
		} else {
			String courseID = req.getParameter(Common.PARAM_COURSE_ID);
			
			if(courseID!=null){
				helper.course = helper.server.getCourse(courseID);
				helper.instructorList = helper.server.getInstructorsByCourseId(courseID);
				if(helper.course == null || helper.instructorList == null){
					helper.statusMessage = "Invalid Course " + courseID + " specified";
					helper.error = true;
					helper.redirectUrl = Common.PAGE_INSTRUCTOR_COURSE;
				}
			} else {
				helper.redirectUrl = Common.PAGE_INSTRUCTOR_COURSE;
			}
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE_EDIT;
	}
}