package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

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

		String newCourseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(newCourseId);
		String newCourseName = getRequestParamValue(Const.ParamsNames.COURSE_NAME);
		Assumption.assertNotNull(newCourseName);

		new GateKeeper().verifyInstructorPrivileges(account);

		data = new InstructorCoursesPageData(account);

		data.newCourse = new CourseAttributes(newCourseId, newCourseName);
		createCourse(data.newCourse);

		if (isError) {
			data.courseIdToShow = data.newCourse.id;
			data.courseNameToShow = data.newCourse.name;
			data.allCourses = new ArrayList<CourseDetailsBundle>(
					logic.getCourseSummariesForInstructor(data.account.googleId).values());
			data.archivedCourses = extractArchivedCourses(data.allCourses);
			CourseDetailsBundle.sortDetailedCoursesByCourseId(data.allCourses);
			
			statusToAdmin = StringHelper.toString(statusToUser, "<br>");
			
			return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSES, data);
		} else {
			data.courseIdToShow = "";
			data.courseNameToShow = "";
			data.allCourses = new ArrayList<CourseDetailsBundle>(
                    logic.getCourseSummariesForInstructor(data.account.googleId).values());
			data.archivedCourses = extractArchivedCourses(data.allCourses);
			CourseDetailsBundle.sortDetailedCoursesByCourseId(data.allCourses);
			
			statusToAdmin = "Course added : " + data.newCourse.id;
			statusToAdmin += "<br>Total courses: " + data.allCourses.size();
			
			return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSES, data);
		}
	}

	private void createCourse(CourseAttributes course) {

		try {
			logic.createCourseAndInstructor(data.account.googleId, course.id,
					course.name);
			String statusMessage = Const.StatusMessages.COURSE_ADDED.replace("${courseEnrollLink}",
					data.getInstructorCourseEnrollLink(course.id))
					.replace("${courseEditLink}", data.getInstructorCourseEditLink(course.id));
			statusToUser.add(statusMessage);
			isError = false;

		} catch (EntityAlreadyExistsException e) {
			setStatusForException(e, Const.StatusMessages.COURSE_EXISTS);

		} catch (InvalidParametersException e) {
			setStatusForException(e);
		}

		if (isError) {
			return;
		}
	}
	
	private List<CourseAttributes> extractArchivedCourses(List<CourseDetailsBundle> courseBundles) {
		ArrayList<CourseAttributes> archivedCourses = new ArrayList<CourseAttributes>();
		
		for (CourseDetailsBundle courseBundle : courseBundles) {
			CourseAttributes course = courseBundle.course;
			
			if (course.isArchived) {
				archivedCourses.add(course);
			}
		}
		
		return archivedCourses;
	}

}
