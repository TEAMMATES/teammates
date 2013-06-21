package teammates.ui.controller;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.logic.GateKeeper;

public class StudentCourseDetailsPageAction extends Action {
	
	private StudentCourseDetailsPageData data;
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		if(notYetJoinedCourse(courseId, account.googleId)){
			return createPleaseJoinCourseResponse(courseId);
		}

		new GateKeeper().verifyStudentOfCourse(account.googleId, courseId);

		data = new StudentCourseDetailsPageData(account);

		data.courseDetails = logic.getCourseDetails(courseId);
		data.instructors = logic.getInstructorsForCourse(courseId);

		data.student = logic.getStudentForGoogleId(courseId, account.googleId);
		data.team = getTeam(logic.getTeamsForCourse(courseId), data.student);

		statusToAdmin = "studentCourseDetails Page Load<br>" +
				"Viewing team details for <span class=\"bold\">[" + courseId + "] " +
				data.courseDetails.course.name + "</span>";

		ShowPageResult response = createShowPageResult(
				Common.JSP_STUDENT_COURSE_DETAILS, data);
		return response;

	}
	
	private TeamDetailsBundle getTeam(CourseDetailsBundle course, StudentAttributes student){
		if(student.team == null || student.team.trim().isEmpty()){
			return null;
		}
		for(TeamDetailsBundle team: course.teams){
			if(team.name.equals(student.team)){
				return team;
			}
		}
		return null;
	}
	

}
