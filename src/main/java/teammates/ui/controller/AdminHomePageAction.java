package teammates.ui.controller;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;

public class AdminHomePageAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException,
			InvalidParametersException {
		
		new GateKeeper().verifyAdminPrivileges(account);
		
		AdminHomePageData data = new AdminHomePageData(account);
		
		data.instructorId = "";
		data.instructorName = "";
		data.instructorEmail = "";
		data.instructorInstitution = "";
		
		statusToAdmin = "Admin Home Page Load";
		
		return createShowPageResult(Common.JSP_ADMIN_HOME, data);
	}

}
