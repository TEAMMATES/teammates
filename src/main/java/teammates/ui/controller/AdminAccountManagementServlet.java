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
		List<InstructorData> allInstructorsList = helper.server.getAllInstructors();
		List<AccountData> allInstructorAccountsList = helper.server.getInstructorAccounts();
		
		helper.instructorCoursesTable = new HashMap<String, ArrayList<InstructorData>>();
		helper.instructorAccountsTable = new HashMap<String, AccountData>();
		
		for(AccountData acc : allInstructorAccountsList){
			helper.instructorAccountsTable.put(acc.googleId, acc);
		}
		
		for(InstructorData instructor : allInstructorsList){
			ArrayList<InstructorData> courseList = helper.instructorCoursesTable.get(instructor.googleId);
			if (courseList == null){
				courseList = new ArrayList<InstructorData>();
				helper.instructorCoursesTable.put(instructor.googleId, courseList);
			}
			courseList.add(instructor);
		}
			
		String url = getRequestedURL(req);
		
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(helper.instructorAccountsTable.size());
		activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_ACCOUNT_MANAGEMENT_SERVLET, Common.ADMIN_ACCOUNT_MANAGEMENT_SERVLET_PAGE_LOAD,
				false, helper, url, data);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_ACCOUNT_MANAGEMENT;
	}

	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.ADMIN_ACCOUNT_MANAGEMENT_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
			
		return message;
	}
	
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "Admin Account Management Page Load<br>";
			message += "<span class=\"bold\">Total Instructors:</span> " + (Integer)data.get(0);
		} catch (NullPointerException e){
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
