package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.StringHelper;
import teammates.ui.pagedata.InstructorCoursesPageData;

/**
 * Action: adding a course for an instructor.
 */
public class InstructorCourseAddAction extends Action {
    private InstructorCoursesPageData data;

    @Override
    public ActionResult execute() {
        String newCourseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, newCourseId);
        String newCourseName = getRequestParamValue(Const.ParamsNames.COURSE_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_NAME, newCourseName);
        String newCourseTimeZone = getRequestParamValue(Const.ParamsNames.COURSE_TIME_ZONE);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_TIME_ZONE, newCourseTimeZone);

        /* Check if user has the right to execute the action */
        gateKeeper.verifyInstructorPrivileges(account);

        /* Create a new course in the database */
        data = new InstructorCoursesPageData(account, sessionToken);

        createCourse(newCourseId, newCourseName, newCourseTimeZone);

        /* Prepare data for the refreshed page after executing the adding action */
        Map<String, InstructorAttributes> instructorsForCourses = new HashMap<>();
        List<CourseAttributes> activeCourses = new ArrayList<>();
        List<CourseAttributes> archivedCourses = new ArrayList<>();

        // Get list of InstructorAttributes that belong to the user.
        List<InstructorAttributes> instructorList = logic.getInstructorsForGoogleId(data.account.googleId);
        for (InstructorAttributes instructor : instructorList) {
            instructorsForCourses.put(instructor.courseId, instructor);
        }

        // Get corresponding courses of the instructors.
        List<CourseAttributes> allCourses = logic.getCoursesForInstructor(instructorList);

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

        String courseIdToShowParam = "";
        String courseNameToShowParam = "";

        if (isError) { // there is error in adding the course
            courseIdToShowParam = SanitizationHelper.sanitizeForHtml(newCourseId);
            courseNameToShowParam = SanitizationHelper.sanitizeForHtml(newCourseName);

            List<String> statusMessageTexts = new ArrayList<>();

            for (StatusMessage msg : statusToUser) {
                statusMessageTexts.add(msg.getText());
            }

            statusToAdmin = StringHelper.toString(statusMessageTexts, "<br>");
        } else {
            statusToAdmin = "Course added : " + newCourseId;
            statusToAdmin += "<br>Total courses: " + allCourses.size();
        }

        data.init(activeCourses, archivedCourses, instructorsForCourses, courseIdToShowParam, courseNameToShowParam);

        return isError ? createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSES, data)
                : createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
    }

    private void createCourse(String newCourseId, String newCourseName, String newCourseTimeZone) {
        try {
            logic.createCourseAndInstructor(data.account.googleId, newCourseId, newCourseName, newCourseTimeZone);
            String statusMessage = Const.StatusMessages.COURSE_ADDED.replace("${courseEnrollLink}",
                    data.getInstructorCourseEnrollLink(newCourseId)).replace("${courseEditLink}",
                    data.getInstructorCourseEditLink(newCourseId));
            statusToUser.add(new StatusMessage(statusMessage, StatusMessageColor.SUCCESS));
            isError = false;

        } catch (EntityAlreadyExistsException e) {
            setStatusForException(e, Const.StatusMessages.COURSE_EXISTS);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }
    }

}
