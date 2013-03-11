package teammates.ui.controller;

import java.util.ArrayList;

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
		
		String url = getRequestedURL(req);
        
		if(isSubmit){
			String instructorList = req.getParameter(Common.PARAM_COURSE_INSTRUCTOR_LIST);
			String courseID = req.getParameter(Common.PARAM_COURSE_ID);
			String institute = helper.account.institute;
			try{
				helper.server.updateCourseInstructors(courseID, instructorList, institute);				
				helper.statusMessage = Common.MESSAGE_COURSE_EDITED;
				helper.redirectUrl = Common.PAGE_INSTRUCTOR_COURSE;
				
				ArrayList<Object> data = new ArrayList<Object>();
				data.add(courseID);
				data.add(instructorList);								    
		        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_EDIT_SERVLET, Common.INSTRUCTOR_COURSE_EDIT_SERVLET_EDIT_COURSE_INFO,
		        		true, helper, url, data);
		        
			} catch (InvalidParametersException e){
				helper.statusMessage = e.getMessage();
				helper.error = true;
				
				ArrayList<Object> data = new ArrayList<Object>();
		        data.add(helper.statusMessage);		                        
		        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_EDIT_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
		        		true, helper, url, data);
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
					
					ArrayList<Object> data = new ArrayList<Object>();
			        data.add(helper.statusMessage);			                        
			        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_EDIT_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
			        		true, helper, url, data);
			        
				} else {
					ArrayList<Object> data = new ArrayList<Object>();
					data.add(courseID);					
					activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_EDIT_SERVLET, Common.INSTRUCTOR_COURSE_EDIT_SERVLET_PAGE_LOAD,
			        		true, helper, url, data);
				}
				
			} else {
				helper.redirectUrl = Common.PAGE_INSTRUCTOR_COURSE;
				
				ArrayList<Object> data = new ArrayList<Object>();
		        data.add("Course Id is null");		                        
		        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_EDIT_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
		        		true, helper, url, data);
			}
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE_EDIT;
	}



	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_COURSE_EDIT_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else if (action.equals(Common.INSTRUCTOR_COURSE_EDIT_SERVLET_EDIT_COURSE_INFO)){
			message = generateEditCourseInfoMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		try {
			message = "instructorCourseEdit Page Load<br>";
			message += "Editing information for Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
	
	private String generateEditCourseInfoMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		try {
			message = "Course <span class=\"bold\">[" + (String)data.get(0) + "]</span> edited.<br>New Instructor List: <br> - " + ((String)data.get(1)).replace("\n", "<br> - ");
		} catch (NullPointerException e){
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}