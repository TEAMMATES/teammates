package teammates.ui.webapi;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.StudentData;
import teammates.ui.output.StudentsData;

/**
 * Get a list of students.
 */
public class GetStudentsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        UUID teamId = getNullableUuidRequestParamValue(Const.ParamsNames.TEAM_ID);

        if (teamId == null) {
            // request to get all students of a course by instructor
            gateKeeper.verifyInstructorHasPrivilege(requestContext, courseId,
                    Const.InstructorPermissions.CAN_VIEW_STUDENT);
        } else {
            // request to get team member by current student
            Student student = getStudentFromRequest(courseId);
            if (student == null || !teamId.equals(student.getTeamId())) {
                throw new UnauthorizedAccessException("You are not part of the team");
            }
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        UUID teamId = getNullableUuidRequestParamValue(Const.ParamsNames.TEAM_ID);

        Instructor instructor = requestContext.isAdmin()
                ? null
                : getInstructorFromRequest(courseId);
        String privilegeName = Const.InstructorPermissions.CAN_VIEW_STUDENT;
        boolean hasCoursePrivilege = instructor != null
                && logic.hasInstructorPermissions(instructor, privilegeName);
        boolean hasSectionPrivilege = instructor != null
                && !logic.getSectionsWithInstructorPermission(instructor, privilegeName).isEmpty();

        if (requestContext.isAdmin() || teamId == null && hasCoursePrivilege) {
            // request to get all course students by instructor with course privilege
            List<Student> studentsForCourse = logic.getStudentsForCourse(courseId);

            return new JsonResult(new StudentsData(studentsForCourse));
        } else if (teamId == null && hasSectionPrivilege) {
            // request to get students by instructor with section privilege
            List<Student> studentsForCourse = logic.getStudentsForCourse(courseId);
            List<Student> studentsToReturn = new LinkedList<>();
            Set<UUID> sectionsWithViewPrivileges =
                    logic.getSectionsWithInstructorPermission(instructor, privilegeName).keySet();

            studentsForCourse.forEach(student -> {
                if (sectionsWithViewPrivileges.contains(student.getSectionId())) {
                    studentsToReturn.add(student);
                }
            });

            return new JsonResult(new StudentsData(studentsToReturn));
        } else {
            // request to get team members by current student
            List<Student> studentsForTeam = logic.getStudentsByTeamId(teamId, courseId);
            StudentsData data = new StudentsData(studentsForTeam);

            data.getStudents().forEach(StudentData::hideInformationForStudent);

            return new JsonResult(data);
        }
    }
}
