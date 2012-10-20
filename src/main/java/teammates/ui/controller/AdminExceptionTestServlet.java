package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.logic.backdoor.BackDoorLogic;

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
			 generateAssertionError();
		 }else if(error.equals(EntityDoesNotExistException.class.getSimpleName())) {
			 generateEntityDoesNotExistException();
		 }else if(error.equals(UnauthorizedAccessException.class.getSimpleName())) {
			 generateUnauthorizedAccessException();
		 }else if(error.equals(NullPointerException.class.getSimpleName())) {
			 generateNullPointerException();
		 }
		 
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_HOME;
	}
	private void generateAssertionError() throws AssertionError {
		throw new AssertionError("AssertionError Testing");
	}
	
	private void generateEntityDoesNotExistException() throws EntityDoesNotExistException {
		throw new EntityDoesNotExistException("EntityDoesNotExistException Testing");
	}
	
	private void generateUnauthorizedAccessException() throws UnauthorizedAccessException {
		throw new UnauthorizedAccessException();
	}
	private void generateNullPointerException() throws NullPointerException {
		Object o = null;
		o.toString();
	}
	

}
