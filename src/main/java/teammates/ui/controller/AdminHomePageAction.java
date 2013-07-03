package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Constants;
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
		
		return createShowPageResult(Constants.VIEW_ADMIN_HOME, data);
	}

}
