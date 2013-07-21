package teammates.ui.controller;

import java.util.ArrayList;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;

public class InstructorCourseAddAction extends Action {
	
	private InstructorCoursesPageData data;
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		
		String newCourseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(newCourseId);
		String newCourseName = getRequestParam(Const.ParamsNames.COURSE_NAME);
		Assumption.assertNotNull(newCourseName);
		String newCourseInstructorList = getRequestParam(Const.ParamsNames.COURSE_INSTRUCTOR_LIST);
		Assumption.assertNotNull(newCourseInstructorList);
		
		new GateKeeper().verifyInstructorPrivileges(account);
		
		data = new InstructorCoursesPageData(account);
		
		data.newCourse = new CourseAttributes();
		data.newCourse.id = newCourseId;
		data.newCourse.name = newCourseName;
		createCourse(data.newCourse, newCourseInstructorList);
		
		if(isError){ 
			data.instructorListToShow = newCourseInstructorList;
			data.courseIdToShow = data.newCourse.id;
			data.courseNameToShow = data.newCourse.name;
			statusToAdmin = StringHelper.toString(statusToUser, "<br>");
		} else {
			data.instructorListToShow = data.account.googleId + "|" + data.account.name + "|" + data.account.email;
			data.courseIdToShow = "";
			data.courseNameToShow = "";
			statusToAdmin = "Course added : "+data.newCourse.id; 
		}
		
		data.currentCourses = new ArrayList<CourseDetailsBundle>(
				logic.getCourseSummariesForInstructor(data.account.googleId).values());
		CourseDetailsBundle.sortDetailedCoursesByCourseId(data.currentCourses);
		
		statusToAdmin +=  "<br>Total courses: " + data.currentCourses.size();
		
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSES, data);
	}


	private void createCourse(CourseAttributes course,
			String instructorListForNewCourse) {
		
		try {
			logic.createCourseAndInstructor(data.account.googleId, course.id, course.name);
			statusToUser.add(Const.StatusMessages.COURSE_ADDED);
			isError = false;
			
		} catch (EntityAlreadyExistsException e) {
			setStatusForException(e, Const.StatusMessages.COURSE_EXISTS);

		} catch (InvalidParametersException e) {
			setStatusForException(e);
		}
		
		if(isError){ 
			return; 
		}
		
		try{
			logic.updateCourseInstructors(data.newCourse.id, instructorListForNewCourse, data.account.institute);
		} catch (InvalidParametersException e){
			setStatusForException(e);
		} catch (EntityDoesNotExistException e) {
			Assumption.fail("The course created did not persist properly :"+ data.newCourse.id);
		}
		
	}
	

}
