package teammates.ui.webapi;

import java.util.List;

import teammates.common.util.Const;
import teammates.storage.entity.Student;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.StudentData;
import teammates.ui.output.StudentsData;

/**
 * Get the list of students in the current student's own team.
 */
public class GetOwnTeamStudentsAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyStudentInCourse(requestContext, courseId);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        Student student = getStudentFromRequest(courseId);

        List<Student> studentsForTeam = logic.getStudentsByTeamId(student.getTeamId(), courseId);
        StudentsData data = new StudentsData(studentsForTeam);

        data.getStudents().forEach(StudentData::hideInformationForStudent);

        return new JsonResult(data);
    }
}
