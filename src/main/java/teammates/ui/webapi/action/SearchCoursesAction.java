package teammates.ui.webapi.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.output.SearchCoursesCommonData;
import teammates.ui.webapi.output.SearchCoursesData;

/**
 * Searches Courses.
 */
public class SearchCoursesAction extends Action {

    private Set<String> courseIds = new HashSet<>();
    private Map<String, String> courseIdToCourseNameMap = new HashMap<>();
    private Map<String, String> courseIdToInstituteMap = new HashMap<>();
    private Map<String, String> courseIdToInstructorGoogleIdMap = new HashMap<>();

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Only admins can get accounts directly
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_KEY);

        List<StudentAttributes> students = logic.searchStudentsInWholeSystem(searchKey).studentList;
        List<InstructorAttributes> instructors = logic.searchInstructorsInWholeSystem(searchKey).instructorList;
        List<SearchCoursesCommonData> studentsBundle = new ArrayList<>();
        List<SearchCoursesCommonData> instructorsBundle = new ArrayList<>();
        populateCourseIds(students, instructors);
        populateCourseIdToCourseNameMap();
        populateCourseIdToInstituteMap();

        for (StudentAttributes s : students) {
            SearchCoursesCommonData sb = new SearchCoursesCommonData();
            sb.setEmail(s.email);
            sb.setCourseId(s.course);
            sb.setCourseName(courseIdToCourseNameMap.get(s.course));
            sb.setInstitute(courseIdToInstituteMap.get(s.course));
            studentsBundle.add(sb);
        }

        for (InstructorAttributes i : instructors) {
            SearchCoursesCommonData ib = new SearchCoursesCommonData();
            ib.setEmail(i.email);
            ib.setCourseId(i.courseId);
            ib.setCourseName(courseIdToCourseNameMap.get(i.courseId));
            ib.setInstitute(courseIdToInstituteMap.get(i.courseId));
            instructorsBundle.add(ib);
        }

        SearchCoursesData result = new SearchCoursesData(studentsBundle, instructorsBundle);
        return new JsonResult(result);
    }

    private void populateCourseIds(List<StudentAttributes> students, List<InstructorAttributes> instructors) {
        for (StudentAttributes student : students) {
            if (student.course != null) {
                courseIds.add(student.course);
            }
        }
        for (InstructorAttributes instructor : instructors) {
            if (instructor.courseId != null) {
                courseIds.add(instructor.courseId);
            }
        }
    }

    private void populateCourseIdToCourseNameMap() {
        for (String courseId : courseIds) {
            CourseAttributes course = logic.getCourse(courseId);
            if (course != null) {
                courseIdToCourseNameMap.put(courseId, course.getName());
            }
        }
    }

    private void populateCourseIdToInstituteMap() {
        for (String courseId : courseIds) {
            String instructorForCourseGoogleId = findAvailableInstructorGoogleIdForCourse(courseId);
            AccountAttributes account = logic.getAccount(instructorForCourseGoogleId);
            if (account == null) {
                continue;
            }

            String institute = StringHelper.isEmpty(account.institute) ? "None" : account.institute;
            courseIdToInstituteMap.put(courseId, institute);
        }
    }

    /**
     * Finds the googleId of a registered instructor with co-owner privileges.
     * If there is no such instructor, finds the googleId of a registered
     * instructor with the privilege to modify instructors.
     *
     * @param courseId
     *            the ID of the course
     * @return the googleId of a suitable instructor if found, otherwise an
     *         empty string
     */
    private String findAvailableInstructorGoogleIdForCourse(String courseId) {
        List<InstructorAttributes> instructorList = logic.getInstructorsForCourse(courseId);

        for (InstructorAttributes instructor : instructorList) {
            if (instructor.isRegistered()
                    && (instructor.hasCoownerPrivileges()
                    || instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR))) {
                courseIdToInstructorGoogleIdMap.put(courseId, instructor.googleId);
                return instructor.googleId;
            }

        }

        return "";
    }
}
