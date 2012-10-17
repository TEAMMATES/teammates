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
			 BackDoorLogic.generateAssertionError();
		 }else if(error.equals(EntityDoesNotExistException.class.getSimpleName())) {
			 BackDoorLogic.generateEntityDoesNotExistException();
		 }else if(error.equals(UnauthorizedAccessException.class.getSimpleName())) {
			 BackDoorLogic.generateUnauthorizedAccessException();
		 }else if(error.equals(NullPointerException.class.getSimpleName())) {
			 BackDoorLogic.generateNullPointerException();
		 }
		 
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_HOME;
	}
	
	

}
