package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.UserType;
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
		
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        }
		if(isSubmit){
			String instructorList = req.getParameter(Common.PARAM_COURSE_INSTRUCTOR_LIST);
			String courseID = req.getParameter(Common.PARAM_COURSE_ID);
			try{
				helper.server.updateCourseInstructors(courseID, instructorList);				
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
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(courseID);
			
			activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_EDIT_SERVLET, Common.INSTRUCTOR_COURSE_EDIT_SERVLET_PAGE_LOAD,
	        		true, helper, url, data);
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE_EDIT;
	}



	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		InstructorCourseEditHelper h = (InstructorCourseEditHelper) helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.INSTRUCTOR_COURSE_EDIT_SERVLET_PAGE_LOAD){
			try {
				params = "instructorCourseEdit Page Load<br>";
				params += "Editing information for Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
			} catch (NullPointerException e) {
				params = "<span class=\"colour_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else if (action == Common.INSTRUCTOR_COURSE_EDIT_SERVLET_EDIT_COURSE_INFO){
			try {
				params = "Course <span class=\"bold\">[" + (String)data.get(0) + "]</span> edited.<br>New Instructor List: <br> - " + ((String)data.get(1)).replace("\n", "<br> - ");
			} catch (NullPointerException e){
				params = "<span class=\"colour_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else {
			params = "<span class=\"colour_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
				
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}
}