package teammates.ui.controller;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.RequestPageData;

/**
 * Action: showing page to request an account for an instructor.
 */

public class InstructorAccountRequestPageAction extends Action {
	
	@Override
    public ActionResult execute() {
        String name = getRequestParamValue("fullname");
        String university = getRequestParamValue("university");
        String country = getRequestParamValue("country");
        String url = getRequestParamValue("URL");
        String email = getRequestParamValue("email");
        String comments = getRequestParamValue("comments");

        Assumption.assertNotNull(name);
        Assumption.assertNotNull(university);
        Assumption.assertNotNull(email);

        //InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        //gateKeeper.verifyAccessible(
          //      instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);

        /* Setup page data for 'Request Account' page for an instructor */
        RequestPageData pageData = new RequestPageData(name, university, country, url, email, comments);
       
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_ACCOUNT_REQUEST, pageData);
    }

}
