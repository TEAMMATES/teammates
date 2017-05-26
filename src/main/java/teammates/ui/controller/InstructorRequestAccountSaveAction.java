package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorAccountRequestResultPageData;

/**
 * Action: saving the details of the new applicant-instructor.
 */
public class InstructorRequestAccountSaveAction extends Action {

	 @Override
	 public ActionResult execute() throws EntityDoesNotExistException {
		//TODO: to be added in const.ParamsNames instead of the plain text
	     String name = getRequestParamValue("fullname");
	     Assumption.assertPostParamNotNull("fullname", name);
	     String university = getRequestParamValue("university");
	     Assumption.assertPostParamNotNull("university", university);
	     String country = getRequestParamValue("country");
	     String email = getRequestParamValue("email");
	     Assumption.assertPostParamNotNull("email", email);
	     String url = getRequestParamValue("URL");
	     String comments = getRequestParamValue("comments");

	     /* Setup data for process results */
	     try {
	         InstructorAccountRequestResultPageData pageData = new InstructorAccountRequestResultPageData(name,
	                                                                 university, country, email, url, comments);

	         return createShowPageResult(Const.ViewURIs.INSTRUCTOR_ACCOUNT_REQUEST_RESULT, pageData);
	    } finally {
	    }
	 }
}
