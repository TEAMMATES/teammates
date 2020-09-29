package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.output.StudentData;
import teammates.ui.output.StudentsData;

/**
 * Get a list of students.
 */
class GetStudentsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String teamName = getRequestParamValue(Const.ParamsNames.TEAM_NAME);
        if (teamName == null) {
            // request to get all students of a course by instructor
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId));
        } else {
            // request to get team member by current student
            StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.id);
            if (student == null || !teamName.equals(student.getTeam())) {
                throw new UnauthorizedAccessException("You are not part of the team");
            }
        }
    }

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String teamName = getRequestParamValue(Const.ParamsNames.TEAM_NAME);

        if (teamName == null) {
            // request to get all students of a course by instructor
            List<StudentAttributes> studentsForCourse = logic.getStudentsForCourse(courseId);
            return new JsonResult(new StudentsData(studentsForCourse));
        } else {
            // request to get team members by current student
            List<StudentAttributes> studentsForTeam = logic.getStudentsForTeam(teamName, courseId);
            StudentsData studentsData = new StudentsData(studentsForTeam);
            studentsData.getStudents().forEach(StudentData::hideInformationForStudent);
            return new JsonResult(studentsData);
        }

    }

}
