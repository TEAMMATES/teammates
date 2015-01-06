package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;

/**
 * Action: adding a course for an instructor
 */
public class InstructorCourseAddAction extends Action {
    private InstructorCoursesPageData data;

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        String newCourseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(newCourseId);
        String newCourseName = getRequestParamValue(Const.ParamsNames.COURSE_NAME);
        Assumption.assertNotNull(newCourseName);

        /* Check if user has the right to execute the action */
        new GateKeeper().verifyInstructorPrivileges(account);

        /* Create a new course in the database */
        data = new InstructorCoursesPageData(account);
        data.newCourse = new CourseAttributes(newCourseId, newCourseName);
        createCourse(data.newCourse);

        /* Prepare data for the refreshed page after executing the adding action */
        data.allCourses = new ArrayList<CourseDetailsBundle>(
                logic.getCourseSummariesForInstructor(data.account.googleId)
                        .values());
        data.archivedCourses = extractArchivedCourses(data.allCourses);
        CourseDetailsBundle.sortDetailedCoursesByCourseId(data.allCourses);
        data.instructors = new HashMap<String, InstructorAttributes>();
        
        for (CourseDetailsBundle courseDetails : data.allCourses) {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseDetails.course.id, account.googleId);
            data.instructors.put(courseDetails.course.id, instructor);
        }
        if (isError) { // there is error in adding the course
            data.courseIdToShow = data.newCourse.id;
            data.courseNameToShow = data.newCourse.name;
            statusToAdmin = StringHelper.toString(statusToUser, "<br>");
        } else {
            data.courseIdToShow = "";
            data.courseNameToShow = "";
            statusToAdmin = "Course added : " + data.newCourse.id;
            statusToAdmin += "<br>Total courses: " + data.allCourses.size();
        }

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSES, data);
    }

    private void createCourse(CourseAttributes course) {

        try {
            logic.createCourseAndInstructor(data.account.googleId, course.id,
                    course.name);
            String statusMessage = Const.StatusMessages.COURSE_ADDED.replace("${courseEnrollLink}",
                    data.getInstructorCourseEnrollLink(course.id))
                    .replace("${courseEditLink}",data.getInstructorCourseEditLink(course.id));
            statusToUser.add(statusMessage);
            isError = false;

        } catch (EntityAlreadyExistsException e) {
            setStatusForException(e, Const.StatusMessages.COURSE_EXISTS);
        } catch (InvalidParametersException e) {
            setStatusForException(e, Const.StatusMessages.COURSE_INVALID_ID);
        }

        if (isError) {
            return;
        }
    }

    private List<CourseAttributes> extractArchivedCourses(
            List<CourseDetailsBundle> courseBundles) {
        ArrayList<CourseAttributes> archivedCourses = new ArrayList<CourseAttributes>();

        for (CourseDetailsBundle courseBundle : courseBundles) {
            CourseAttributes course = courseBundle.course;

            InstructorAttributes curInstructor = logic.getInstructorForGoogleId(course.id, account.googleId);

            if (logic.isCourseArchived(course.id, curInstructor.googleId)) {
                archivedCourses.add(course);
            }
        }

        return archivedCourses;
    }

}
