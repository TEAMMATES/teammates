package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;

@SuppressWarnings("serial")
public class AdminExceptionTestServlet extends ActionServlet<AdminHomeHelper> {
	
	@Override
	protected AdminHomeHelper instantiateHelper() {
		return new AdminHomeHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, AdminHomeHelper helper) throws EntityDoesNotExistException {
		 String error = req.getParameter(Common.PARAM_ERROR);
		
		 String url = req.getRequestURI();
			if (req.getQueryString() != null){
				url += "?" + req.getQueryString();
			}
		 activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_EXCEPTION_TEST_SERVLET, Common.ADMIN_EXCEPTION_TEST_SERVLET_PAGE_LOAD,
				 false, helper, url, null);
		 
		 Common.getLogger().info("Generate Exception : " + error);
		 if(error.equals(AssertionError.class.getSimpleName())) {
			 
				throw new AssertionError("AssertionError Testing");
				
		 }else if(error.equals(EntityDoesNotExistException.class.getSimpleName())) {
			 
				throw new EntityDoesNotExistException("EntityDoesNotExistException Testing");
				
		 }else if(error.equals(UnauthorizedAccessException.class.getSimpleName())) {

				throw new UnauthorizedAccessException();

		 }else if(error.equals(NullPointerException.class.getSimpleName())) {
			 
				throw new NullPointerException();
		 }else if(error.equals(DeadlineExceededException.class.getSimpleName())) {
			 
			   	throw new DeadlineExceededException();
		 }
		 
		 
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_HOME;
	}


	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		AdminHomeHelper h = (AdminHomeHelper) helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.ADMIN_EXCEPTION_TEST_SERVLET_PAGE_LOAD){
			params = "adminExceptionTest";
		} else {
			params = "<span class=\"colour_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
			
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}
	

}
