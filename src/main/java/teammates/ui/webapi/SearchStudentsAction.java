package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.output.StudentData;
import teammates.ui.output.StudentsData;

/**
 * Action for searching for students.
 */
class SearchStudentsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        // Only instructors and admins can search for student
        if (!userInfo.isInstructor && !userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Instructor or Admin privilege is required to access this resource.");
        }
    }

    private String getInstituteFromCourseId(String courseId) {
        String instructorForCourseGoogleId = findAvailableInstructorGoogleIdForCourse(courseId);
        if (instructorForCourseGoogleId == null) {
            return null;
        }

        AccountAttributes account = logic.getAccount(instructorForCourseGoogleId);
        if (account == null) {
            return null;
        }

        return StringHelper.isEmpty(account.institute) ? "None" : account.institute;
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
                    || instructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR))) {
                return instructor.googleId;
            }

        }

        return "";
    }

    @Override
    JsonResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        String entity = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);
        List<StudentAttributes> students;

        // Search for students
        if (userInfo.isInstructor && entity.equals(Const.EntityType.INSTRUCTOR)) {
            List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(userInfo.id);
            students = logic.searchStudents(searchKey, instructors).studentList;
        } else if (userInfo.isAdmin && entity.equals(Const.EntityType.ADMIN)) {
            students = logic.searchStudentsInWholeSystem(searchKey).studentList;
        } else {
            throw new InvalidHttpParameterException("Invalid entity type for search");
        }

        List<StudentData> studentDataList = new ArrayList<>();
        for (StudentAttributes s : students) {
            StudentData studentData = new StudentData(s);

            if (userInfo.isAdmin && entity.equals(Const.EntityType.ADMIN)) {
                studentData.addAdditionalInformationForAdminSearch(
                        StringHelper.encrypt(s.getKey()),
                        getInstituteFromCourseId(s.getCourse()),
                        s.getGoogleId()
                );
            }
            studentData.hideLastName();

            studentDataList.add(studentData);
        }
        StudentsData studentsData = new StudentsData();
        studentsData.setStudents(studentDataList);

        return new JsonResult(studentsData);
    }
}
