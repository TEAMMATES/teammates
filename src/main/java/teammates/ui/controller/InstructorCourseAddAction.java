package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.StatusMessage;
import teammates.common.util.StringHelper;
import teammates.common.util.Const.StatusMessageColor;
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
        CourseAttributes newCourse = new CourseAttributes(newCourseId, newCourseName);
        createCourse(newCourse);

        /* Prepare data for the refreshed page after executing the adding action */
        List<CourseDetailsBundle> allCourses = new ArrayList<CourseDetailsBundle>(logic.getCourseSummariesForInstructor(
                                                                    data.account.googleId).values());
        CourseDetailsBundle.sortDetailedCoursesByCourseId(allCourses);
        List<CourseDetailsBundle> activeCourses = logic.extractActiveCourses(allCourses, data.account.googleId);
        List<CourseDetailsBundle> archivedCourses = logic.extractArchivedCourses(allCourses, data.account.googleId);
                                        
        String CourseIdToShowParam = "";
        String CourseNameToShowParam = "";
        
        if (isError) { // there is error in adding the course
            CourseIdToShowParam = Sanitizer.sanitizeForHtml(newCourse.id);
            CourseNameToShowParam = Sanitizer.sanitizeForHtml(newCourse.name);
            
            List<String> statusMessageTexts = new ArrayList<String>();
            
            for (StatusMessage msg : statusToUser) {
                statusMessageTexts.add(msg.getText());
            }
            
            statusToAdmin = StringHelper.toString(statusMessageTexts, "<br>");
        } else {
            statusToAdmin = "Course added : " + newCourse.id;
            statusToAdmin += "<br>Total courses: " + allCourses.size();
        }
        
        List<CourseAttributes> courseList = logic.getCoursesForInstructor(data.account.googleId);
        Map<String, InstructorAttributes> instructorsForCourses = new HashMap<String, InstructorAttributes>();
        for (CourseAttributes course : courseList) {
            instructorsForCourses.put(course.id, logic.getInstructorForGoogleId(course.id, data.account.googleId));
        }
        
        data.init(activeCourses, archivedCourses, instructorsForCourses, CourseIdToShowParam, CourseNameToShowParam);
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSES, data);
    }

    private void createCourse(CourseAttributes course) {
        try {
            logic.createCourseAndInstructor(data.account.googleId, course.id, course.name);
            String statusMessage = Const.StatusMessages.COURSE_ADDED.replace("${courseEnrollLink}",
                    data.getInstructorCourseEnrollLink(course.id)).replace("${courseEditLink}",
                    data.getInstructorCourseEditLink(course.id));
            statusToUser.add(new StatusMessage(statusMessage, StatusMessageColor.SUCCESS));
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

}
