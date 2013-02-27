package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.Common;
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
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName,
			String action, boolean toShow, Helper helper) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
