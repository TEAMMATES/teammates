package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;
import teammates.ui.datatransfer.InstructorStudentListPageCourseData;

public class InstructorStudentListPageAction extends Action {

    private InstructorStudentListPageData data;

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        new GateKeeper().verifyInstructorPrivileges(account);

        String searchKey = getRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        Boolean displayArchive = getRequestParamAsBoolean(Const.ParamsNames.DISPLAY_ARCHIVE);
        Map<String, InstructorAttributes> instructors = new HashMap<String, InstructorAttributes>();
        Map<String, String> numStudents = new HashMap<String, String>();
        List<CourseAttributes> courses = logic.getCoursesForInstructor(account.googleId);
        for (CourseAttributes course : courses) {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(course.id, account.googleId);
            instructors.put(course.id, instructor);
            int numStudentsInCourse = logic.getStudentsForCourse(course.id).size();
            numStudents.put(course.id, String.valueOf(numStudentsInCourse));
        }
        Collections.sort(courses, new Comparator<CourseAttributes>() {
            @Override
            public int compare(CourseAttributes c1, CourseAttributes c2) {
                return c1.createdAt.compareTo(c2.createdAt);
            }
        });

        if (courses.size() == 0) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.INSTRUCTOR_NO_COURSE_AND_STUDENTS, StatusMessageColor.WARNING));
        }

        statusToAdmin = "instructorStudentList Page Load<br>" + "Total Courses: " + courses.size();
        
        List<InstructorStudentListPageCourseData> coursesToDisplay = new ArrayList<InstructorStudentListPageCourseData>();
        for (CourseAttributes course: courses) {
            InstructorAttributes instructor = instructors.get(course.id);
            boolean isInstructorAllowedToModify = instructor.isAllowedForPrivilege(
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
            boolean isCourseArchived = Logic.isCourseArchived(course, instructor);
            boolean isCourseDisplayed = displayArchive || !isCourseArchived;
            if (isCourseDisplayed) {
                coursesToDisplay.add(new InstructorStudentListPageCourseData(course, isCourseArchived,
                                                                             isInstructorAllowedToModify));
            }
        }

        data = new InstructorStudentListPageData(account, searchKey, displayArchive, numStudents, coursesToDisplay);

        ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_STUDENT_LIST, data);
        return response;

    }

}
