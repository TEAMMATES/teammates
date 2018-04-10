package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorCoursesPageData;

/**
 * Action: loading of the 'Courses' page for an instructor.
 */
public class InstructorCoursesPageAction extends Action {

    @Override
    public ActionResult execute() {
        /* Explanation: First, we extract any parameters from the request object.
         * e.g., idOfCourseToDelete = getRequestParam(Const.ParamsNames.COURSE_ID);
         * After that, we may verify parameters.
         * e.g. Assumption.assertNotNull(courseId);
         * In this Action, there are no parameters.*/

        /* Explanation: Next, check if the user has rights to execute the action.*/
        gateKeeper.verifyInstructorPrivileges(account);

        /* Explanation: This is a 'show page' type action. Therefore, we
         * prepare the matching PageData object, accessing the Logic
         * component if necessary.*/
        InstructorCoursesPageData data = new InstructorCoursesPageData(account, sessionToken);
        String isUsingAjax = getRequestParamValue(Const.ParamsNames.IS_USING_AJAX);
        data.setUsingAjax(isUsingAjax != null);

        Map<String, InstructorAttributes> instructorsForCourses = new HashMap<>();
        List<CourseAttributes> allCourses = new ArrayList<>();
        List<CourseAttributes> activeCourses = new ArrayList<>();
        List<CourseAttributes> archivedCourses = new ArrayList<>();

        if (data.isUsingAjax()) {
            // Get list of InstructorAttributes that belong to the user.
            List<InstructorAttributes> instructorList = logic.getInstructorsForGoogleId(data.account.googleId);
            for (InstructorAttributes instructor : instructorList) {
                instructorsForCourses.put(instructor.courseId, instructor);
            }

            // Get corresponding courses of the instructors.
            allCourses = logic.getCoursesForInstructor(instructorList);

            List<String> archivedCourseIds = logic.getArchivedCourseIds(allCourses, instructorsForCourses);
            for (CourseAttributes course : allCourses) {
                if (archivedCourseIds.contains(course.getId())) {
                    archivedCourses.add(course);
                } else {
                    activeCourses.add(course);
                }
            }

            // Sort CourseDetailsBundle lists by course id
            CourseAttributes.sortById(activeCourses);
            CourseAttributes.sortById(archivedCourses);
        }

        data.init(activeCourses, archivedCourses, instructorsForCourses);

        /* Explanation: Set any status messages that should be shown to the user.*/
        if (data.isUsingAjax() && allCourses.isEmpty()) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_EMPTY, StatusMessageColor.WARNING));
        }

        /* Explanation: We must set this variable. It is the text that will
         * represent this particular execution of this action in the
         * 'admin activity log' page.*/
        statusToAdmin = "instructorCourse Page Load<br>Total courses: " + allCourses.size();

        /* Explanation: Create the appropriate result object and return it.*/
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSES, data);
    }
}
