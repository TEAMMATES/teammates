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
	
	 private static final Logger log = Logger.getLogger();

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
	     
	     /* Process enrollment list and setup data for page result */
	     try {
	         List<StudentAttributes>[] students = enrollAndProcessResultForDisplay(studentsInfo, courseId);
	         boolean hasSection = hasSections(students);

	         InstructorAccountRequestResultPageData pageData = new InstructorAccountRequestResultPageData(name,
	                                                                 university, country, email, url, comments);

	            statusToAdmin = "Students Enrolled in Course <span class=\"bold\">["
	                            + courseId + "]:</span><br>" + sanitizedStudentsInfo.replace("\n", "<br>");

	            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_ACCOUNT_REQUEST_RESULT, pageData);

	        } catch (EnrollException | InvalidParametersException e) {
	            setStatusForException(e);

	            statusToAdmin += "<br>Enrollment string entered by user:<br>" + sanitizedStudentsInfo.replace("\n", "<br>");

	            InstructorCourseEnrollPageData pageData = new InstructorCourseEnrollPageData(account, courseId, studentsInfo);

	            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, pageData);
	        } catch (EntityAlreadyExistsException e) {
	            setStatusForException(e);

	            statusToUser.add(
	                    new StatusMessage("The enrollment failed, possibly because some students were re-enrolled before "
	                                      + "the previous enrollment action was still being processed by TEAMMATES database "
	                                      + "servers. Please try again after about 10 minutes. If the problem persists, "
	                                      + "please contact TEAMMATES support", StatusMessageColor.DANGER));

	            InstructorCourseEnrollPageData pageData = new InstructorCourseEnrollPageData(account, courseId, studentsInfo);

	            log.severe("Entity already exists exception occurred when updating student: " + e.getMessage());
	            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, pageData);
	        }
	    }


}
