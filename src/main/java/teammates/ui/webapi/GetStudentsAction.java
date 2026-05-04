package teammates.ui.webapi;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import teammates.common.util.Const;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Student;
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
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String teamName = getRequestParamValue(Const.ParamsNames.TEAM_NAME);

        if (teamName == null) {
            // request to get all students of a course by instructor
            Instructor instructor = logic.getInstructorByGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId),
                    Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS);
        } else {
            // request to get team member by current student
            Student student = logic.getStudentByGoogleId(courseId, userInfo.id);
            if (student == null || !teamName.equals(student.getTeamName())) {
                throw new UnauthorizedAccessException("You are not part of the team");
            }
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String teamName = getRequestParamValue(Const.ParamsNames.TEAM_NAME);

        Instructor instructor = logic.getInstructorByGoogleId(courseId, userInfo.id);
        String privilegeName = Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS;
        boolean hasCoursePrivilege = instructor != null
                && instructor.isAllowedForPrivilege(privilegeName);
        boolean hasSectionPrivilege = instructor != null
                && !instructor.getSectionsWithPrivilege(privilegeName).isEmpty();

        if (teamName == null && hasCoursePrivilege) {
            // request to get all course students by instructor with course privilege
            List<Student> studentsForCourse = logic.getStudentsForCourse(courseId);

            return new JsonResult(new StudentsData(studentsForCourse));
        } else if (teamName == null && hasSectionPrivilege) {
            // request to get students by instructor with section privilege
            List<Student> studentsForCourse = logic.getStudentsForCourse(courseId);
            List<Student> studentsToReturn = new LinkedList<>();
            Set<String> sectionsWithViewPrivileges = instructor
                    .getSectionsWithPrivilege(privilegeName).keySet();

            studentsForCourse.forEach(student -> {
                if (sectionsWithViewPrivileges.contains(student.getSectionName())) {
                    studentsToReturn.add(student);
                }
            });

            return new JsonResult(new StudentsData(studentsToReturn));
        } else {
            // request to get team members by current student
            List<Student> studentsForTeam = logic.getStudentsByTeamName(teamName, courseId);
            StudentsData data = new StudentsData(studentsForTeam);

            data.getStudents().forEach(StudentData::hideInformationForStudent);

            return new JsonResult(data);
        }
    }
}
