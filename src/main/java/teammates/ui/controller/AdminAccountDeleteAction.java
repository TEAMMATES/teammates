package teammates.ui.controller;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.logic.GateKeeper;

public class AdminAccountDeleteAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException,
			InvalidParametersException {
		
		new GateKeeper().verifyAdminPrivileges(account);
		
		String instructorId = getRequestParam(Config.PARAM_INSTRUCTOR_ID);
		String studentId = getRequestParam(Config.PARAM_STUDENT_ID);
		String courseId = getRequestParam(Config.PARAM_COURSE_ID);
		String account = getRequestParam("account");
		
		ActionResult result = null;
		
		//TODO: We should extract these into separate actions e.g., AdminInstructorDowngradeAction
		if(courseId == null && account == null){	
			//delete instructor status
			logic.downgradeInstructorToStudentCascade(instructorId);
			statusToUser.add(Config.MESSAGE_INSTRUCTOR_STATUS_DELETED);
			statusToAdmin = "Instructor Status for <span class=\"bold\">" + instructorId + "</span> has been deleted.";
			result = createRedirectResult(Config.PAGE_ADMIN_ACCOUNT_MANAGEMENT);
			
		} else if (courseId == null && account != null){
			//delete entire account
			logic.deleteAccount(instructorId);
			statusToUser.add(Config.MESSAGE_INSTRUCTOR_ACCOUNT_DELETED);
			statusToAdmin = "Instructor Account for <span class=\"bold\">" + instructorId + "</span> has been deleted.";
			result = createRedirectResult(Config.PAGE_ADMIN_ACCOUNT_MANAGEMENT);
			
		} else if (courseId != null && instructorId != null){
			//remove instructor from course
			logic.deleteInstructor(courseId, instructorId);
			statusToUser.add(Config.MESSAGE_INSTRUCTOR_REMOVED_FROM_COURSE);
			statusToAdmin = "Instructor <span class=\"bold\">" + instructorId + 
					"</span> has been deleted from Course<span class=\"bold\">[" + courseId + "]</span>"; 
			result = createRedirectResult(Config.PAGE_ADMIN_ACCOUNT_DETAILS + "?instructorid=" + instructorId);
			
		} else if (courseId != null && studentId != null) {
			//remove student from course
			StudentAttributes student = logic.getStudentForGoogleId(courseId, studentId);
			logic.deleteStudent(courseId, student.email);
			statusToUser.add(Config.MESSAGE_INSTRUCTOR_REMOVED_FROM_COURSE);
			statusToAdmin = "Instructor <span class=\"bold\">" + instructorId + 
					"</span>'s student status in Course<span class=\"bold\">[" + courseId + "]</span> has been deleted"; 
			result = createRedirectResult(Config.PAGE_ADMIN_ACCOUNT_DETAILS + "?instructorid=" + studentId);
		}		
		
		return result;
	}

}
