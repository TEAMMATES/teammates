package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;

/** This action can be used when the user request for a non-existent action*/
public class NonExistentAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		return createRedirectResult(Const.ViewURIs.ACTION_NOT_FOUND_PAGE);
	}

}
