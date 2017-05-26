package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorAccountRequestResultPageData;
import teammates.ui.pagedata.InstructorCourseEnrollPageData;
import teammates.ui.pagedata.InstructorCourseEnrollResultPageData;

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
