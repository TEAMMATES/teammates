package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.datatransfer.InstructorStudentListPageCourseData;
import teammates.ui.pagedata.InstructorStudentListPageData;

public class InstructorStudentListPageAction extends Action {

    @Override
    public ActionResult execute() {

        gateKeeper.verifyInstructorPrivileges(account);

        String searchKey = getRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        Boolean displayArchive = getRequestParamAsBoolean(Const.ParamsNames.DISPLAY_ARCHIVE);
        Map<String, InstructorAttributes> instructors = new HashMap<>();

        List<CourseAttributes> courses = logic.getCoursesForInstructor(account.googleId);
        // Sort by creation date
        courses.sort(Comparator.comparing(course -> course.createdAt));

        // Get instructor attributes
        List<InstructorAttributes> instructorList = logic.getInstructorsForGoogleId(account.googleId);

        for (InstructorAttributes instructor : instructorList) {
            instructors.put(instructor.courseId, instructor);
        }

        if (courses.isEmpty()) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.INSTRUCTOR_NO_COURSE_AND_STUDENTS,
                                               StatusMessageColor.WARNING));
        }

        statusToAdmin = "instructorStudentList Page Load<br>" + "Total Courses: " + courses.size();

        List<InstructorStudentListPageCourseData> coursesToDisplay = new ArrayList<>();
        for (CourseAttributes course : courses) {
            InstructorAttributes instructor = instructors.get(course.getId());
            boolean isInstructorAllowedToModify = instructor.isAllowedForPrivilege(
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);

            boolean isCourseDisplayed = displayArchive || !instructor.isArchived;
            if (isCourseDisplayed) {
                coursesToDisplay.add(new InstructorStudentListPageCourseData(course, instructor.isArchived,
                                                                             isInstructorAllowedToModify));
            }
        }

        InstructorStudentListPageData data =
                new InstructorStudentListPageData(account, sessionToken, searchKey, displayArchive, coursesToDisplay);

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_STUDENT_LIST, data);
    }

}
