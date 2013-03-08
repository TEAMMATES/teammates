package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.InstructorData;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
public class AdminAccountManagementServlet extends ActionServlet<AdminAccountManagementHelper>{

	@Override
	protected AdminAccountManagementHelper instantiateHelper() {
		return new AdminAccountManagementHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req,
			AdminAccountManagementHelper helper)
			throws EntityDoesNotExistException, InvalidParametersException {
		helper.instructorList = helper.server.getAllInstructors();
		
		//Sort the list of instructors
		Collections.sort(helper.instructorList, new Comparator<InstructorData>(){
			public int compare(InstructorData i1, InstructorData i2){
				return i1.googleId.compareTo(i2.googleId);
			}
		});
				
		helper.accountList = new HashMap<Integer, List<String>>();
		String key = "";
		List<String> courses = null;
		int i;
		for (i = 0; i < helper.instructorList.size(); i++){
			InstructorData instructor = helper.instructorList.get(i);
			if (!instructor.googleId.equals(key)){
				//Save existing account if any
				if(courses != null){
					helper.accountList.put(i - 1, courses);
				}
				
				//Start new account
				key = instructor.googleId;
				courses = new ArrayList<String>();
				courses.add(instructor.courseId);
			} else {
				courses.add(instructor.courseId);
			}
		}
		if(courses != null){
			helper.accountList.put(i - 1, courses);
		}
		
		String url = req.getRequestURI();
		if (req.getQueryString() != null){
			url += "?" + req.getQueryString();
		}
		activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_ACCOUNT_MANAGEMENT_SERVLET, Common.ADMIN_ACCOUNT_MANAGEMENT_SERVLET_PAGE_LOAD,
				true, helper, url, null);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_ACCOUNT_MANAGEMENT;
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		AdminAccountManagementHelper h = (AdminAccountManagementHelper) helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.ADMIN_ACCOUNT_MANAGEMENT_SERVLET_PAGE_LOAD){
			try {
				params = "Admin Account Management Page Load<br>";
				params += "<span class=\"bold\">Total Instructors:</span> " + h.accountList.size();
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
