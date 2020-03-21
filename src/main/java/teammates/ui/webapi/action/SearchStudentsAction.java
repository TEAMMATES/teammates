package teammates.ui.webapi.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.output.StudentData;
import teammates.ui.webapi.output.StudentsData;

/**
 * Action for searching for students.
 */
public class SearchStudentsAction extends Action {

    private Set<String> courseIds = new HashSet<>();
    private Map<String, String> courseIdToInstituteMap = new HashMap<>();
    private Map<String, String> courseIdToInstructorGoogleIdMap = new HashMap<>();

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Only instructors and admins can search for student
        if (userInfo.isStudent && !userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor or Admin privilege is required to access this resource.");
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

    private void populateCourseIdToInstructorGoogleIdMap() {
        for (String courseId : courseIds) {
            courseIdToInstructorGoogleIdMap.put(courseId, findAvailableInstructorGoogleIdForCourse(courseId));
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
                return instructor.googleId;
            }

        }

        return "";
    }

    @Override
    public ActionResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        List<StudentAttributes> students;
        StudentsData studentsData;

        // Search for students
        if (userInfo.isAdmin) {
            students = logic.searchStudentsInWholeSystem(searchKey).studentList;
        } else {
            List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(userInfo.id);
            students = logic.searchStudents(searchKey, instructors).studentList;
        }
        studentsData = new StudentsData(students);
        for (StudentAttributes s : students) {
            courseIds.add(s.getCourse());
        }
        populateCourseIdToInstructorGoogleIdMap();
        populateCourseIdToInstituteMap();
        studentsData.getStudents().forEach((StudentData data) -> {
            data.setInstitute(courseIdToInstituteMap.get(data.getCourseId()));
        });

        // Hide information
        studentsData.getStudents().forEach(StudentData::hideLastName);
        if (userInfo.isAdmin) {
            // Set the key
            studentsData.getStudents().forEach((StudentData data) -> {
                data.setKey(StringHelper.encrypt(students.stream()
                        .filter((StudentAttributes s) -> s.getGoogleId().equals(data.getGoogleId()))
                        .collect(Collectors.toList()).get(0).getKey()));
                data.setCourseIdToInstructorGoogleIdMap(courseIdToInstructorGoogleIdMap);
            });
        } else {
            studentsData.getStudents().forEach(StudentData::hideInformationForInstructor);
        }

        return new JsonResult(studentsData);
    }
}
