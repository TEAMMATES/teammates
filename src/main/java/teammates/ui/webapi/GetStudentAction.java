package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.StudentData;

/**
 * Get the information of a student by user ID.
 */
public class GetStudentAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            return;
        }

        UUID studentId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        gateKeeper.verifyInstructorInSameCourseAsStudent(requestContext, studentId);
    }

    @Override
    public JsonResult execute() {
        UUID studentId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        Student student = logic.getStudent(studentId);
        if (student == null) {
            throw new EntityNotFoundException("No student found");
        }

        StudentData studentData = new StudentData(student);

        return new JsonResult(studentData);
    }
}
