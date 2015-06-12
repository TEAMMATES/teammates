package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

/**
 * Action: loading of the 'Courses' page for an instructor.
 */
public class InstructorCoursesPageAction extends Action {
    /* Explanation: Get a logger to be used for any logging */
    protected static final Logger log = Utils.getLogger();
    
    
    @Override
    public ActionResult execute() 
            throws EntityDoesNotExistException {
        /* Explanation: First, we extract any parameters from the request object.
         * e.g., idOfCourseToDelete = getRequestParam(Const.ParamsNames.COURSE_ID);
         * After that, we may verify parameters.
         * e.g. Assumption.assertNotNull(courseId);
         * In this Action, there are no parameters.*/
        
        /* Explanation: Next, check if the user has rights to execute the action.*/
        new GateKeeper().verifyInstructorPrivileges(account);
        
        /* Explanation: This is a 'show page' type action. Therefore, we 
         * prepare the matching PageData object, accessing the Logic 
         * component if necessary.*/
        
        ArrayList<CourseDetailsBundle> allCourses = new ArrayList<CourseDetailsBundle>(
                logic.getCourseSummariesForInstructor(account.googleId).values());
        
        List<CourseDetailsBundle> activeCourses = logic.extractActiveCourses(allCourses, account.googleId);
        List<CourseDetailsBundle> archivedCourses = logic.extractArchivedCourses(allCourses, account.googleId);
        
        InstructorCoursesPageData data = new InstructorCoursesPageData(account);
        
        List<CourseAttributes> courseList = logic.getCoursesForInstructor(data.account.googleId);
        HashMap<String, InstructorAttributes> instructorsForCourses = new HashMap<String, InstructorAttributes>();
        for (CourseAttributes course : courseList) {
            instructorsForCourses.put(course.id, logic.getInstructorForGoogleId(course.id, data.account.googleId));
        }
        
        data.init(activeCourses, archivedCourses, instructorsForCourses);
        
        /* Explanation: Set any status messages that should be shown to the user.*/
        if (allCourses.size() == 0 ){
            statusToUser.add(Const.StatusMessages.COURSE_EMPTY);
        }
        
        /* Explanation: We must set this variable. It is the text that will 
         * represent this particular execution of this action in the
         * 'admin activity log' page.*/
        statusToAdmin = "instructorCourse Page Load<br>Total courses: " + allCourses.size();
        
        /* Explanation: Create the appropriate result object and return it.*/
        ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSES, data);
        return response;
    }
    
}
